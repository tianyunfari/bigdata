package com.bigdata.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Arrays;

public class MyJavaWordCount {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: MyJavaWordCount <input> <output> ");
            System.exit(1);
        }

        String input = args[0];
        String output = args[1];

        SparkConf conf = new SparkConf().setAppName("myjavawordcount");
        JavaSparkContext sc = new JavaSparkContext(conf);

        //读取数据
        JavaRDD<String> inputRdd = sc.textFile(input);
        //进行相关计算
        JavaRDD<String> words = inputRdd.flatMap(new FlatMapFunction<String, String>() {
            public Iterable call(String line) throws Exception {
                return Arrays.asList(line.split(" "));
            }
        });

        JavaPairRDD<String, Integer> result = words.mapToPair(new PairFunction<String, String, Integer>() {
            public Tuple2 call(String word) throws Exception {
                return new Tuple2(word, 1);
            }
        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
            public Integer call(Integer x, Integer y) throws Exception {
                return x + y;
            }
        });

        result.saveAsTextFile(output);

        sc.stop();
    }
}
