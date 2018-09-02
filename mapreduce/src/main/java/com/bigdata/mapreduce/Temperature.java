/**
 * Temperature.java
 * com.bigdata.mapreduce
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.mapreduce;


import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import org.apache.hadoop.io.Text;

public class Temperature extends Configured implements Tool{

	public static class TemperatureMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
		public void map(LongWritable key, Text value, Context context) throws IOException,InterruptedException{
			//
			String line = value.toString();
			//get temperature
			int temperature = Integer.parseInt(line.substring(14, 19).trim());
			//get id
			if (temperature != -9999){
				FileSplit fileSplit = (FileSplit)context.getInputSplit();
				String weatherStationId = fileSplit.getPath().getName().substring(5, 10);
				
				context.write(new Text(weatherStationId), new IntWritable(temperature));
				
			}
		}
	}
	
	public static class TemperatureReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
		private IntWritable result = new IntWritable();
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException,InterruptedException{
			int sum = 0;
			int count = 0;
			
			for(IntWritable val:values){
				sum += val.get();
				count++;
			}
			result.set(sum / count);
			context.write(key, result);
		}
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String[] args0 = {"hdfs://bigdata:9000/MR/30yr_03103.dat", "hdfs://bigdata:9000/MR/weather-out"};
		
		int ec = ToolRunner.run(new Configuration(), new Temperature(), args0);
		
		System.exit(ec);
	}
	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		
		//输出路径存在则删除
		Path mypath = new Path(arg0[1]);
		FileSystem hdfs = mypath.getFileSystem(conf);
		if(hdfs.isDirectory(mypath)){
			hdfs.delete(mypath, true);
		}
		
		//构建job对象
		Job job = Job.getInstance(conf, "temperature");
		job.setJarByClass(Temperature.class);
		
		//指定路径
		FileInputFormat.addInputPath(job, new Path(arg0[0]));
		FileOutputFormat.setOutputPath(job, new Path(arg0[1]));
		
		//指定map和reduce
		job.setMapperClass(TemperatureMapper.class);
		job.setReducerClass(TemperatureReducer.class);
		
		//设置输出类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		//提交作业
		job.waitForCompletion(true);
		
		return 0;
	}
}

