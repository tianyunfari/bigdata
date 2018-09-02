package com.bigdata.spark

import org.apache.spark.{SparkConf, SparkContext}

object MyWordCount {
  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      System.err.println("Usage: MyWordCout <input> <output> ")
      System.exit(1)
    }

    val input = args(0)
    val output = args(1)

    //创建sparkcontext
    val conf = new SparkConf().setAppName("myWordcount")
    val sc = new SparkContext(conf)

    val lines = sc.textFile(input)

    val resultRdd = lines.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)
    resultRdd.saveAsTextFile(output)
    sc.stop()
  }
}
