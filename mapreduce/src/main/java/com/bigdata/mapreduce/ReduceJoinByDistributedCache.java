/**
 * ReduceJoinByDistributedCache.java
 * com.bigdata.mapreduce
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.mapreduce;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


/**
 * TODO(利用分布式缓存在reduce进行join操作)
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-7-25 	 
 */
public class ReduceJoinByDistributedCache extends Configured implements Tool {
	public static class ReduceJoinByDistributedCacheMapper extends 
		Mapper<LongWritable, Text, Text, Text> {
		protected void map(LongWritable key, Text value, Context context)
				throws IOException ,InterruptedException {
			String[] arr = value.toString().split("\\s+");
			if (arr.length == 3) {
				context.write(new Text(arr[0]), value);
			}
		}
	}
	
	public static class ReduceJoinByDistributedCacheReducer extends 
		Reducer<Text, Text, Text, Text> {
		private Map<String, String> table = new Hashtable< String, String>();
		
		protected void setup(Context context)
				throws IOException ,InterruptedException {
			@SuppressWarnings("deprecation")
			Path[] filePaths = context.getLocalCacheFiles();
			if (filePaths.length == 0) {
				throw(new FileNotFoundException("cache file not find!!!"));
			}
			
			FileSystem fs = FileSystem.getLocal(context.getConfiguration());
			FSDataInputStream in = null;
			BufferedReader br = null;
			String infoStr = null;
			for (Path path : filePaths) {
				in = fs.open(path);
				br = new BufferedReader(new InputStreamReader(in));
				
				while (null != (infoStr = br.readLine())) {
	                String[] records = infoStr.split("\t");
	                table.put(records[0], records[1]);//key为stationID，value为stationName
				}
			}
		}
		
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException ,InterruptedException {
			String stationName = table.get(key.toString());
			for (Text value : values) {
				context.write(new Text(stationName), value);
			}
		}
	}
	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub
		 Configuration conf = new Configuration();

         Path out = new Path(arg0[2]);
         FileSystem hdfs = out.getFileSystem(conf);// 创建输出路径
         if (hdfs.isDirectory(out)) {
             hdfs.delete(out, true);
         }
         Job job = Job.getInstance();//获取一个job实例
         
 		job.getConfiguration().set("mapreduce.output.textoutputformat.separator",
				"@");

         job.setJarByClass(ReduceJoinByDistributedCache.class);
         FileInputFormat.addInputPath(job,
                 new Path(arg0[0]));
         FileOutputFormat.setOutputPath(job, new Path(
        		 arg0[2]));
         //添加分布式缓存文件 station.txt
//         job.addCacheFile(new URI(arg0[1]));
         FileStatus[] fileDirs = hdfs.listStatus(new Path(arg0[1]));
         
         for (FileStatus dir : fileDirs) {
        	 job.addCacheFile(dir.getPath().toUri());
         }
         
         job.setMapperClass(ReduceJoinByDistributedCacheMapper.class);
         job.setReducerClass(ReduceJoinByDistributedCacheReducer.class);
         job.setOutputKeyClass(Text.class);// 输出key类型
         job.setOutputValueClass(Text.class);// 输出value类型
         return job.waitForCompletion(true) ? 0 : 1;
     }
	 
     public static void main(String[] args) throws Exception {
     	String[] args0 = {"hdfs://bigdata:9000/join/records.txt"
     			,"hdfs://bigdata:9000/join/station.txt"
     			,"hdfs://bigdata:9000/join/mapcache-out"
     	};
     	
     	int ec = ToolRunner.run(new Configuration(),
     			new ReduceJoinByDistributedCache(), args0);
     	System.exit(ec);
     }
}

