package com.hadoop.hbase;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MapReduceReaderHbaseDriver {
	public static class MapperReadHbase extends TableMapper<Text, Text> {
		protected void map(ImmutableBytesWritable key, Result values, Context context) throws IOException ,InterruptedException {
			StringBuffer strBuff = new StringBuffer("");
			for (Map.Entry<byte[], byte[]> value : values.getFamilyMap("content".getBytes()).entrySet()) {
				String str = new String(value.getValue());
				if (str != null)
					strBuff.append(str);
				
				context.write(new Text(key.get()), new Text(new String(strBuff)));
			}
		}
	}
	
	public static class ReducerReadHbase extends Reducer<Text, Text, Text, Text> {
		private Text res = new Text();
		
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException ,InterruptedException {
			for (Text value : values) {
				res.set(value);
				context.write(key, res);
			}
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		String tableName = new String("wordcount");
		
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "bigdata,bigdata1,bigdata2");
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		conf.set("hbase.master", "bigdata:16010");

		Job job = Job.getInstance(conf, "import from hbase to hdfs");
		
		job.setJarByClass(MapReduceReaderHbaseDriver.class);
		
		TableMapReduceUtil.initTableMapperJob(tableName, new Scan(), MapperReadHbase.class, Text.class, Text.class, job, false);
		job.setReducerClass(ReducerReadHbase.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		FileOutputFormat.setOutputPath(job, new Path("hdfs://bigdata:9000/test/out"));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
