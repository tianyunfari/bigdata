/**
 * SecondarySort.java
 * com.bigdata.mapreduce
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.mapreduce;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * TODO(二次排序)
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-7-26 	 
 */
public class SecondarySort extends Configured implements Tool{
	public static class SecondarySortMapper extends Mapper<
		LongWritable, Text, IntPair, IntWritable> {
		protected void map(LongWritable key, Text value, Context context)
				throws IOException ,InterruptedException {
			String line = value.toString();
			StringTokenizer tokenizer = new StringTokenizer(line);
			int left = 0;
			int right = 0;
			if (tokenizer.hasMoreTokens()) {
                left = Integer.parseInt(tokenizer.nextToken());
                if (tokenizer.hasMoreTokens())
                    right = Integer.parseInt(tokenizer.nextToken());
                context.write(new IntPair(left, right), new IntWritable(right));
            }
		}
	}
	
	public static class FirstPartitioner extends Partitioner<IntPair, IntWritable> {

		@Override
		public int getPartition(IntPair key, IntWritable value, int numPartitions) {
			// TODO Auto-generated method stub
			return Math.abs(key.getFirst() * 127) % numPartitions;
		}
	}
	
	/**
	 * 
	 * TODO(自定义GroupingComparator类，实现分区内的数据分组)
	 * <p>
	 * TODO(这里描述这个类补充说明 – 可选)
	 * @author   wuchangyuan
	 * @Date	 2018-7-26
	 */
	public static class GroupingComparator extends WritableComparator {

		public GroupingComparator() {
			super(IntPair.class, true);
//			super();
			// TODO Auto-generated constructor stub
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public int compare(WritableComparable w1, WritableComparable w2) {
			// TODO Auto-generated method stub
			IntPair ip1 = (IntPair)w1;
			IntPair ip2 = (IntPair)w2;
			
			int l = ip1.getFirst();
			int r = ip2.getFirst();
			
			return l == r ? 0 : (l < r ? -1 : 1);
		}
	}
	
	public static class SecondarySortReducer extends Reducer<
		IntPair, IntWritable, Text, IntWritable> {
		protected void reduce(IntPair key, Iterable<IntWritable> values, Context context)
				throws IOException ,InterruptedException {
			for (IntWritable value : values) {
				context.write(new Text(key.getFirst() + ""), value);
			}
		}
	}
	
	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		
		Path outputPath = new Path(arg0[1]);
		FileSystem fs = outputPath.getFileSystem(conf);
		if (fs.isDirectory(outputPath)) {
			fs.delete(outputPath, true);
		}
		
		Job job = Job.getInstance();
		
		job.setJarByClass(SecondarySort.class);
		job.setMapperClass(SecondarySortMapper.class);
		job.setReducerClass(SecondarySortReducer.class);
		
		job.setMapOutputKeyClass(IntPair.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setPartitionerClass(FirstPartitioner.class);
		job.setGroupingComparatorClass(GroupingComparator.class);
		// 本示例并没有自定义SortComparator，而是使用IntPair中compareTo方法进行排序 job.setSortComparatorClass();
		
		FileInputFormat.addInputPath(job, new Path(arg0[0]));
		FileOutputFormat.setOutputPath(job, outputPath);
		
		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		String[] arg0 = {"hdfs://bigdata:9000/MR/secondarySort.txt",
                "hdfs://bigdata:9000/MR/secondarySort-out/"};
		int ec = ToolRunner.run(new Configuration(),
				new SecondarySort(), arg0);
		System.exit(ec);
	}
	
}

