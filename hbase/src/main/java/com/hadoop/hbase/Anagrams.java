package com.hadoop.hbase;

import java.io.IOException;
import java.util.Arrays;

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
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Anagrams extends Configured implements Tool{
	//map
	public static class AnagramsMapper extends Mapper<LongWritable, Text, Text, Text>{
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
			String text = value.toString();
			
			char[] textCharArray = text.toCharArray();
			Arrays.sort(textCharArray);
			
			String sortedText = new String(textCharArray);
			context.write(new Text(sortedText), value);
		}
	}
	
	//reduce
	public static class AnagramsReducer extends Reducer<Text, Text, Text, Text>{
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
			int count = 0;
			StringBuffer result = new StringBuffer();
			
			for(Text text:values){
				if(result.length() > 0){
					result.append(",");
				}
				result.append(text);
				count++;
			}
			if(count > 1){
				context.write(key, new Text(result.toString()));
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		String[] arg0 = {"hdfs://bigdata:9000/anagrams/", "hdfs://bigdata:9000/anagrams/out"};
		int ret = ToolRunner.run(new Configuration(), new Anagrams(), arg0);
		
		System.exit(ret);
		
	}

	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		
		Path outputPath = new Path(arg0[1]);
		
		FileSystem fs = outputPath.getFileSystem(conf);
		
		if(fs.isDirectory(outputPath)){
			fs.delete(outputPath, true);
		}
		
		Job job = Job.getInstance();
		
		job.setJarByClass(Anagrams.class);
		
		FileInputFormat.addInputPath(job, new Path(arg0[0]));
		FileOutputFormat.setOutputPath(job, new Path(arg0[1]));
		
		job.setMapperClass(AnagramsMapper.class);
		job.setReducerClass(AnagramsReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		
		return job.waitForCompletion(true) ? 0 : 1;
	}
}
