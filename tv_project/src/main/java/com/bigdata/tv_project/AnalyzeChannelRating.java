/**
 * AnalyzeChannelRating.java
 * com.bigdata.tv_project
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.tv_project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * TODO(统计频道每天每分钟的收视指标)
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-7-24 	 
 */
public class AnalyzeChannelRating extends Configured implements Tool {
	public static class AnalyzeChannelRatingMapper extends 
		Mapper<Object, Text, Text, Text> {
		private Map<String, String> curNumMap = new HashMap<String, String>();
		
		protected void setup(Context context)
				throws IOException ,InterruptedException {
			BufferedReader br;
			String infoAddr = null;
			
			//返回缓存文件路径
			@SuppressWarnings("deprecation")
			Path[] cacheFilesPaths = context.getLocalCacheFiles();
			for (Path tempPath : cacheFilesPaths) {
				String pathStr = tempPath.toString();
				br = new BufferedReader(new FileReader(pathStr));
				
				while (null != (infoAddr = br.readLine())) {
					String[] currentNumJoin = StringUtil.split(infoAddr,
							"@");
					if (currentNumJoin.length == 3) {
						curNumMap.put(currentNumJoin[0].trim() + "@"
								+ currentNumJoin[1].trim(), 
								currentNumJoin[2].trim());
					}
				}
			}
		}
		
		protected void map(Object key, Text value, Context context)
				throws IOException ,InterruptedException {
			String[] arrStr = StringUtil.split(value.toString(), "@");
			
			if (arrStr.length != 5) {
				return;
			}
			
			int avgNum = Integer.parseInt(arrStr[3].trim());
			int reachNum = Integer.parseInt(arrStr[4].trim());
			//当前在播人数
			int currNum = Integer.parseInt(curNumMap.get(arrStr[1].trim()
					+ "@" + arrStr[2].trim()));
			
			float tvRating = (float)avgNum / 25000 * 100;
			float marketShare = (float)avgNum / currNum * 100;
			float reachRating = (float)reachNum / 25000 * 100;
			
			context.write(value, new Text(tvRating + "@" + reachRating +
					"@" + marketShare));
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
			System.err.println("Usage: AnalyzeCountChannelRating cache in [<in>...] <out>");
			System.exit(2);
		}
		
		Path outputPath = new Path(otherArgs[len - 1]);
		FileSystem fs = outputPath.getFileSystem(conf);
		if (fs.isDirectory(outputPath)) {
			fs.delete(outputPath, true);
		}
		
		Job job = Job.getInstance();
		
		job.getConfiguration().set(
				"mapreduce.output.textoutputformat.separator", "@");
		//添加缓存文件
		FileStatus[] dirStatus = fs.listStatus(new Path(otherArgs[0]));
		for (FileStatus file : dirStatus) {
			job.addCacheFile(file.getPath().toUri());
		}
		
		job.setJarByClass(AnalyzeChannelRating.class);
		job.setMapperClass(AnalyzeChannelRatingMapper.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		for (int i = 1; i < len - 1; i++) {
			FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
		}
		
		FileOutputFormat.setOutputPath(job, outputPath);
		
		return job.waitForCompletion(true) ? 0 : 1;
	}
	
	public static void main(String[] args) throws Exception {
		int ec = ToolRunner.run(new Configuration(), 
				new AnalyzeChannelRating(), args);
		System.exit(ec);
	}
	
}

