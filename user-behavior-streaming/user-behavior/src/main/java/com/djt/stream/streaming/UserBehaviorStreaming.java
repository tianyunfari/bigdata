package com.djt.stream.streaming;

import com.djt.model.SingleUserBehaviorRequestModel;
import com.djt.model.UserBehaviorStatModel;
import com.djt.model.UserBehavorRequestModel;
import com.djt.stream.common.HBaseClient;
import com.djt.stream.key.UserHourPackageKey;
import com.djt.stream.monitor.MonitorStopThread;
import com.djt.stream.service.BehaviorStatService;
import com.djt.utils.DateUtils;
import com.djt.utils.JSONUtil;
import com.djt.utils.MyStringUtil;
import com.djt.utils.PropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.*;
import scala.Tuple2;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class UserBehaviorStreaming {
    public static Logger log = Logger.getLogger(UserBehaviorStreaming.class);

    //判断是否出入了配置文件，如果没有传入则退出
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: UserBehaviorStreaming ConfigFile");
            System.exit(1);
        }

        //获取配置文件路径
        String configFile = args[0];
        final Properties serverProps = PropertiesUtil.getProperties(configFile);
        printConfig(serverProps);

        JavaStreamingContext javaStreamingContext = createContext(serverProps);
        javaStreamingContext.start();

        //每隔20秒钟监控是否有停止指令,如果有则优雅退出streaming
        Thread thread = new Thread(new MonitorStopThread(javaStreamingContext, serverProps));
        thread.start();
        javaStreamingContext.awaitTermination();
    }

    /**
     * 根据配置文件以及业务逻辑创建JavaStreamingContext
     *
     * @param serverProps
     * @return
     */
    public static JavaStreamingContext createContext(final Properties serverProps) {
        //获取配置中的topic
        final String topic = serverProps.getProperty("kafka.topic");
        Set<String> topicSet = new HashSet();
        topicSet.add(topic);

        final String groupId = serverProps.getProperty("kafka.groupId");
        //获取批次的时间间隔，比如5s
        final Long streamingInterval = Long.parseLong(serverProps.getProperty("streaming.interval"));
        //获取kafka broker列表
        final String brokerList = serverProps.getProperty("bootstrap.servers");

        //组合kafka参数
        final Map<String, Object> kafkaParams = new HashMap();
        kafkaParams.put("bootstrap.servers", brokerList);
        kafkaParams.put("group.id", groupId);
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        System.out.println(kafkaParams);

        //从hbase中获取每个分区的消费到的offset位置
        Map<TopicPartition, Long> consumerOffsetsLong = getConsumerOffsets(serverProps, topic, groupId);
        printOffset(consumerOffsetsLong);

        //创建sparkconf
        SparkConf sparkConf = new SparkConf().setAppName("UserBehaviorStreaming");
        sparkConf.set("spark.streaming.stopGracefullyOnShutdown", "true");
        sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        sparkConf.set("spark.kryo.registrator", "com.djt.stream.registrator.MyKryoRegistrator");
        sparkConf.set("spark.streaming.kafka.maxRatePerPartition", "100");

        //streamingInterval指每隔多长时间执行一个批次
        JavaStreamingContext javaStreamingContext = new JavaStreamingContext(sparkConf, Durations.seconds(streamingInterval));

        //创建kafka DStream
        JavaInputDStream<ConsumerRecord<String, String>> kafkaMessageDStream = KafkaUtils.createDirectStream(
                javaStreamingContext,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.<String, String>Assign(consumerOffsetsLong.keySet(), kafkaParams, consumerOffsetsLong)
                //ConsumerStrategies.<String, String>Subscribe(topicSet, kafkaParams)
        );

        //需要把每个批次的offset保存到此变量
        final AtomicReference<OffsetRange[]> offsetRanges = new AtomicReference();

        JavaDStream<ConsumerRecord<String, String>> kafkaMessageDStreamTransform =
                kafkaMessageDStream.transform(new Function2<JavaRDD<ConsumerRecord<String, String>>, Time, JavaRDD<ConsumerRecord<String, String>>>() {
            @Override
            public JavaRDD<ConsumerRecord<String, String>> call(JavaRDD<ConsumerRecord<String, String>> javaRDD, Time time) throws Exception {
                OffsetRange[] offsets = ((HasOffsetRanges) javaRDD.rdd()).offsetRanges();
                offsetRanges.set(offsets);

                log.info("rddid=" + javaRDD.id() + ",offsetRange:================================================");
                for (OffsetRange o : offsets) {
                    log.info("rddoffsetRange:topic=" + o.topic()
                            + ",partition=" + o.partition()
                            + ",fromOffset=" + o.fromOffset()
                            + ",untilOffset=" + o.untilOffset()
                            + ",rddpartitions=" + javaRDD.getNumPartitions()
                            + ",isempty=" + javaRDD.isEmpty());
                }

                return javaRDD;
            }
        });

        //将kafka中的消息转换成对象并过滤不合法的消息
        JavaDStream<ConsumerRecord<String, String>> kafkaMessageMessageDStreamFilter =
                kafkaMessageDStreamTransform.filter(new Function<ConsumerRecord<String, String>, Boolean>() {
            @Override
            public Boolean call(ConsumerRecord<String, String> record) throws Exception {
                try {
                    if (StringUtils.isEmpty(record.value())) {
                        return false;
                    }

                    UserBehavorRequestModel requestModel = JSONUtil.json2Object(record.value(), UserBehavorRequestModel.class);

                    if (requestModel == null ||
                            requestModel.getUserId() == 0 ||
                            requestModel.getSingleUserBehaviorRequestModelList() == null ||
                            requestModel.getSingleUserBehaviorRequestModelList().size() == 0) {
                        return false;
                    }

                    return true;
                } catch (Exception e) {
                    log.error("json to UserBehavorRequestModel error", e);
                    return false;
                }
            }
        });

        //将每条用户行为转换成键值对，键是我们自定义的key,值是使用应用的时长，并统计时长
        JavaPairDStream<UserHourPackageKey, Long> javaPairDStream =
                kafkaMessageMessageDStreamFilter.flatMapToPair(new PairFlatMapFunction<ConsumerRecord<String, String>, UserHourPackageKey, Long>() {
            @Override
            public Iterator<Tuple2<UserHourPackageKey, Long>> call(ConsumerRecord<String, String> record) throws Exception {
                List<Tuple2<UserHourPackageKey, Long>> list = new ArrayList();

                UserBehavorRequestModel requestModel;
                try {
                    requestModel = JSONUtil.json2Object(record.value(), UserBehavorRequestModel.class);
                } catch (Exception e) {
                    log.error("event body is Invalid,message=" + record.value(), e);
                    return list.iterator();
                }

                if (requestModel == null) {
                    return list.iterator();
                }

                List<SingleUserBehaviorRequestModel> singleList = requestModel.getSingleUserBehaviorRequestModelList();

                for (SingleUserBehaviorRequestModel singleModel : singleList) {
                    UserHourPackageKey key = new UserHourPackageKey();
                    key.setUserId(requestModel.getUserId());
                    key.setHour(DateUtils.getDateStringByMillisecond(DateUtils.HOUR_FORMAT, requestModel.getBeginTime()));
                    key.setPackageName(singleModel.getPackageName());

                    Tuple2<UserHourPackageKey, Long> t = new Tuple2(key, singleModel.getActiveTime() / 1000);
                    list.add(t);
                }

                return list.iterator();
            }
        }).reduceByKey(new Function2<Long, Long, Long>() {
            @Override
            public Long call(Long long1, Long long2) throws Exception {
                return long1 + long2;
            }
        });

        //将每个用户的统计时长写入hbase
        javaPairDStream.foreachRDD(new VoidFunction<JavaPairRDD<UserHourPackageKey, Long>>() {
            @Override
            public void call(JavaPairRDD<UserHourPackageKey, Long> rdd) throws Exception {
                rdd.foreachPartition(new VoidFunction<Iterator<Tuple2<UserHourPackageKey, Long>>>() {
                    @Override
                    public void call(Iterator<Tuple2<UserHourPackageKey, Long>> it) throws Exception {
                        BehaviorStatService service = BehaviorStatService.getInstance(serverProps);

                        while (it.hasNext()) {
                            Tuple2<UserHourPackageKey, Long> t = it.next();
                            UserHourPackageKey key = t._1();

                            UserBehaviorStatModel model = new UserBehaviorStatModel();
                            model.setUserId(MyStringUtil.getFixedLengthStr(String.valueOf(key.getUserId()), 10));
                            model.setHour(key.getHour());
                            model.setPackageName(key.getPackageName());
                            model.setTimeLen(t._2());

                            service.addTimeLen(model);
                        }
                    }
                });

                //kafka offset写入hbase
                offsetToHbase(serverProps, offsetRanges, topic, groupId);
            }
        });

        return javaStreamingContext;
    }

    /**
     * 讲offset写入hbase
     */
    public static void offsetToHbase(Properties props, final AtomicReference<OffsetRange[]> offsetRanges, final String topic, String groupId) {
        String tableName = "topic_offset";
        Table table = HBaseClient.getInstance(props).getTable(tableName);
        String rowKey = topic + ":" + groupId;

        for (OffsetRange or : offsetRanges.get()) {
            try {
                Put put = new Put(Bytes.toBytes(rowKey));
                put.addColumn(Bytes.toBytes("offset"), Bytes.toBytes(String.valueOf(or.partition())), Bytes.toBytes(String.valueOf(or.untilOffset())));
                table.put(put);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                HBaseClient.closeTable(table);
            }
        }
    }

    /*
    * 从hbase中获取kafka每个分区消费到的offset,以便继续消费
    * */
    public static Map<TopicPartition, Long> getConsumerOffsets(Properties props, String topic, String groupId) {
        Set<String> topicSet = new HashSet<>();
        topicSet.add(topic);
        String tableName = "topic_offset";
        Table table = HBaseClient.getInstance(props).getTable(tableName);
        String rowKey = topic + ":" + groupId;

        Map<TopicPartition, Long> map = new HashMap();

        List<PartitionInfo> list = getPartitionInfoList(props);

        for (PartitionInfo partitionInfo : list) {
            map.put(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()), 0L);
        }

        Get get = new Get(Bytes.toBytes(rowKey));
        try {
            Result result = table.get(get);
            if (result.isEmpty()) {
                return map;
            } else {
                for (Cell cell : result.rawCells()) {
                    String family = Bytes.toString(CellUtil.cloneFamily(cell));

                    if (!"offset".equals(family)) {
                        continue;
                    }

                    int partition = Integer.parseInt(Bytes.toString(CellUtil.cloneQualifier(cell)));
                    long offset = Long.parseLong(Bytes.toString(CellUtil.cloneValue(cell)));
                    TopicPartition topicPartition = new TopicPartition(topic, partition);
                    map.put(topicPartition, offset);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 打印配置文件
     *
     * @param serverProps
     */
    public static void printConfig(Properties serverProps) {
        Iterator<Map.Entry<Object, Object>> it1 = serverProps.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry<Object, Object> entry = it1.next();
            log.info(entry.getKey().toString() + "=" + entry.getValue().toString());
        }
    }

    /**
     * 打印从hbase中获取的每个分区消费到的位置
     *
     * @param consumerOffsetsLong
     */
    public static void printOffset(Map<TopicPartition, Long> consumerOffsetsLong) {

        if(consumerOffsetsLong.size() == 0) {
            System.out.println("consumerOffsetsLong size is 0");
        }

        Iterator<Map.Entry<TopicPartition, Long>> it1 = consumerOffsetsLong.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry<TopicPartition, Long> entry = it1.next();
            TopicPartition key = entry.getKey();
            log.info("hbase offset:partition=" + key.partition() + ",beginOffset=" + entry.getValue());
        }
    }

    /**
     * 获取分区列表
     * @param serverProps
     * @return
     */
    public static List<PartitionInfo> getPartitionInfoList(Properties serverProps) {
        Properties props = new Properties();
        props.put("bootstrap.servers", serverProps.getProperty("bootstrap.servers"));
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        List<PartitionInfo> partitionInfoList = producer.partitionsFor(serverProps.getProperty("kafka.topic"));

        return partitionInfoList;
    }
}
