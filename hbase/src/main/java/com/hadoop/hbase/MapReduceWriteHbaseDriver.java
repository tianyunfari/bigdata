package com.hadoop.hbase;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class MapReduceWriteHbaseDriver {
	public static class WordCountMapperHbase extends Mapper<Object, Text, ImmutableBytesWritable, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		
		protected void map(Object key, Text value, Context context)
				throws IOException ,InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken());
				context.write(new ImmutableBytesWritable(Bytes.toBytes(word.toString())), one);
			}
			
		}
	}
	
	public static class WordCountReducerHbase extends TableReducer<ImmutableBytesWritable, IntWritable, ImmutableBytesWritable> {
		private IntWritable result = new IntWritable();
		
		protected void reduce(ImmutableBytesWritable key, Iterable<IntWritable> values, Context context) throws IOException ,InterruptedException {
			int count = 0;
			for (IntWritable value : values) {
				count += value.get();
			}
			
			Put dataPut = new Put(key.get()); //put的实例化 key代表主键，每个单词存一行
			dataPut.addColumn(getBytes("content"), getBytes("count"), getBytes(String.valueOf(count)));
			context.write(key, dataPut);
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		String tableName = new String("wordcount");
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "bigdata,bigdata1,bigdata2");
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		conf.set("hbase.master", "bigdata:16010");
		
		HBaseAdmin hAdmin = new HBaseAdmin(conf);
		
		//存在及删除
		if (hAdmin.tableExists(tableName)) {
			hAdmin.disableTable(tableName);
			hAdmin.deleteTable(tableName);
		}
		
		HTableDescriptor htad = new HTableDescriptor(tableName);
		htad.addFamily(new HColumnDescriptor("content"));
		hAdmin.createTable(htad);
		
		Job job = Job.getInstance(conf, "import from hdfs to hbase");
		job.setJarByClass(MapReduceWriteHbaseDriver.class);
		
		job.setMapperClass(WordCountMapperHbase.class);
		
		//设置插入hbase时的相关操作
		TableMapReduceUtil.initTableReducerJob(tableName, WordCountReducerHbase.class, job);
		
		job.setMapOutputKeyClass(ImmutableBytesWritable.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setOutputKeyClass(ImmutableBytesWritable.class);
		job.setOutputValueClass(Put.class);
		
		FileInputFormat.addInputPaths(job, "hdfs://bigdata:9000/test/bigdata.txt");
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
	public static byte[] getBytes(String str) {
		if (str ==null) {
			str = "";
		}
		return Bytes.toBytes(str);
	}

}
