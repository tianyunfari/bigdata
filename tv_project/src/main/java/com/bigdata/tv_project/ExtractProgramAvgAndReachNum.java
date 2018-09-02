/**
 * ExtractProgramAvgAndReachNum.java
 * com.bigdata.tv_project
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.tv_project;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
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
 * TODO(针对上一次的结果统计出每个节目每分钟的平均收视人数和平均到达人数)
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-7-24 	 
 */
public class ExtractProgramAvgAndReachNum extends Configured implements Tool{
	public static class ExtractProgramAvgAndReachNumMapper extends Mapper<
	LongWritable, Text, Text, Text> {
		protected void map(LongWritable key, Text value, Context context)
				throws IOException ,InterruptedException {
			String lineStr = value.toString();
			String[] paramStr = StringUtil.split(lineStr, "@");
			
			if (paramStr.length != 7) {
				return;
			}
			
			String stbNum = paramStr[0].trim();
			String date = paramStr[1].trim();
			String programName = paramStr[3].trim();
			
			int start = TimeUtil.TimeToSecond(paramStr[4].trim());
			int end = TimeUtil.TimeToSecond(paramStr[5].trim());
			
			List<String> timeList = TimeUtil.getTimeSplit(paramStr[4], paramStr[5]);
			
			for (int i = 0; i < timeList.size(); i++) {
				//切割每分钟，23:11 23:12 
				String perMin = timeList.get(i);
				
				//定义到达人数为观看60s以上人数
				if (end - start > 60) {
					context.write(new Text(programName + "@" + date + "@" +
						perMin), new Text(stbNum + "@" + stbNum));
				} else {
					context.write(new Text(programName + "@" + date + "@" +
							perMin), new Text(stbNum + "@"));
				}
			}
		}
	}
	
	/**
	 * 
	 * TODO(reduce计算收视人数和到达人数)
	 * <p>
	 * TODO(这里描述这个类补充说明 – 可选)
	 * @author   wuchangyuan
	 * @Date	 2018-7-24
	 */
	public static class ExtractProgramAvgAndReachNumReducer extends
			Reducer<Text, Text, Text, Text> {
		private Set<String> setAvgNum = new HashSet<String>();
		private Set<String> setReachNum = new HashSet<String>();
		
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException ,InterruptedException {
			setAvgNum.clear();
			setReachNum.clear();
			
			for (Text value : values) {
				String[] arrStr = StringUtil.split(value.toString()
						, "@");
				setAvgNum.add(arrStr[0]);
				//满足reach条件
				if (arrStr.length > 1) {
					setReachNum.add(arrStr[1]);
				}
			}
			context.write(key, new Text(setAvgNum.size() + "@" + 
					setReachNum.size()));
		}
	}

	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, arg0)
			.getRemainingArgs();
		
		if (otherArgs.length < 2) {
			System.err.println("Usage: ExtractProgramAvgAndReachNum [<in>...] <out>");
			System.exit(2);
		}
		
		Path outputPath = new Path(otherArgs[otherArgs.length - 1]);
		FileSystem fs = outputPath.getFileSystem(conf);
		if (fs.isDirectory(outputPath)) {
			fs.delete(outputPath, true);
		}
		
		Job job = Job.getInstance();
		
		job.getConfiguration().set("mapreduce.output.textoutputformat.separator", "@");
		
		job.setJarByClass(ExtractProgramAvgAndReachNum.class);
		job.setMapperClass(ExtractProgramAvgAndReachNumMapper.class);
		job.setReducerClass(ExtractProgramAvgAndReachNumReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		//设置输入路径，注意length-1
		for (int i = 0; i < otherArgs.length - 1; i++) {
			FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
		}
		
		FileOutputFormat.setOutputPath(job, outputPath);
		
		return job.waitForCompletion(true) ? 0 : 1;
	}
	
	public static void main(String[] args) throws Exception {
		int ec = ToolRunner.run(new Configuration(), new 
				ExtractProgramAvgAndReachNum(), args);
		System.exit(ec);
	}
}

