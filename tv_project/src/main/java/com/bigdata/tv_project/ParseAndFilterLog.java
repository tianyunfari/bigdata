/**
 * ParseAndFilterLog.java
 * com.bigdata.tv_project
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.tv_project;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * TODO(解析用户产生的数据)
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-7-23 	 
 */
public class ParseAndFilterLog extends Configured implements Tool{
	//只用写map
	public static class ExtractTVMsgLogMapper extends Mapper<LongWritable,
	Text, Text, Text> {
		protected void map(LongWritable key, Text value, 
				Context context) throws IOException ,InterruptedException {
			FileSplit inputSplit = (FileSplit)(context.getInputSplit());
			String pathName = inputSplit.getPath().toString();
			String date = pathName.substring(pathName.lastIndexOf("/") + 1, pathName.lastIndexOf("/") + 9);
			//原始数据
			String data = value.toString();
			//调用接口
			DataUtil.transData(data, context, date);
		}
	}
	
	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, arg0)
			.getRemainingArgs();
		
		if (otherArgs.length < 2) {
			System.err.println("Usage: ParseAndFilterLog [<in>...] <out>");
			System.exit(2);
		}
		
		//输出目录存在的话就删除
		Path outputPath = new Path(otherArgs[otherArgs.length - 1]);
		FileSystem fs = outputPath.getFileSystem(conf);
		if (fs.isDirectory(outputPath)) {
			fs.delete(outputPath, true);
		}
		
		Job job = Job.getInstance();
		
		job.getConfiguration().set("mapreduce.output.textoutputformat.separator", 
				"@");
		
		job.setJarByClass(ParseAndFilterLog.class);
		job.setMapperClass(ExtractTVMsgLogMapper.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		//设置输入路径
		for (int i = 0; i < otherArgs.length - 1; i++) {
			FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
		}
		
		//设置输出路径
		FileOutputFormat.setOutputPath(job, outputPath);
		
		return job.waitForCompletion(true) ? 0 : 1;
	}
	
	public static void main(String[] args) throws Exception {
		int ec = ToolRunner.run(new Configuration(), 
				new ParseAndFilterLog(), args);
		System.exit(ec);
	}
}

