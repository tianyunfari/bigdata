/**
 * ExtractProgramNumAndTimeLen.java
 * com.bigdata.tv_project
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.tv_project;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * TODO(针对ParseAndFilterLog的结果提取节目的收视人数和播放时长)
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-7-24 	 
 */
public class ExtractProgramNumAndTimeLen extends Configured implements Tool {
	public static class ExtractProgramNumAndTimeLenMapper extends Mapper<
			LongWritable, Text, Text, Text> {
		protected void map(LongWritable key, Text value, Context context)
				throws IOException ,InterruptedException {
			String[] arrStr = StringUtil.split(value.toString(), "@");
			
			if (arrStr.length != 7) {
				return;
			}
			String stbNum = arrStr[0].trim();
			String date = arrStr[1].trim();
			String programName = arrStr[3].trim();
			String duration = arrStr[6].trim();
			
			context.write(new Text(programName + "@" + date), 
					new Text(stbNum + "@" + duration));
		}
	}
	
	public static class ExtractProgramNumAndTimeLenReducer extends Reducer<
			Text, Text, Text, Text> {
		private Set<String> stbList = new HashSet<String>();
		
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException ,InterruptedException {
			stbList.clear();
			int timeLen = 0;
			for (Text value : values) {
				String[] arr = StringUtil.split(value.toString(), "@");
				stbList.add(arr[0]);
				if (arr.length > 1) {
					timeLen += Integer.parseInt(arr[1]);
				}
			}
			int num = stbList.size();
			context.write(key, new Text(num + "@" + timeLen / num));
		}
	}

	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, arg0)
			.getRemainingArgs();
		int len = otherArgs.length;
		if (len < 2) {
			System.err.println("Usage: ExtractProgramNumAndTimelen [<in>...] <out>");
			System.exit(2);
		}
		
		Path outputPath = new Path(otherArgs[len - 1]);
		FileSystem fs = outputPath.getFileSystem(conf);
		if (fs.isDirectory(outputPath)) {
			fs.delete(outputPath, true);
		}
		
		Job job = Job.getInstance();
		job.getConfiguration().set("mapreduce.output.textoutputformat.separator", "@");
		
		job.setJarByClass(ExtractProgramNumAndTimeLen.class);
		job.setMapperClass(ExtractProgramNumAndTimeLenMapper.class);
		job.setReducerClass(ExtractProgramNumAndTimeLenReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		for (int i = 0; i < len - 1; i++) {
			FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
		}
		
		FileOutputFormat.setOutputPath(job, outputPath);
		return job.waitForCompletion(true) ? 0 : 1;
	}
	
	public static void main(String[] args) throws Exception {
		int ec = ToolRunner.run(new Configuration(), 
				new ExtractProgramNumAndTimeLen(), args);
		
		System.exit(ec);
	}
}

