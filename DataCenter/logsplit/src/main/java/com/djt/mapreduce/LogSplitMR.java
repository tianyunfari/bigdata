package com.djt.mapreduce;

import com.djt.dao.AbstractDMDAO;
import com.djt.dao.MetaLogDao;
import com.djt.dao.MetaLogFieldDao;
import com.djt.factory.NginxFactory;
import com.djt.model.NginxLogModel;
import com.djt.pojo.MetaLog;
import com.djt.pojo.MetaLogField;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LogSplitMR extends Configured implements Tool {

	public static class MyMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
		private AbstractDMDAO<String, MetaLog> metaLogDao = null;
		private AbstractDMDAO<Integer, List<MetaLogField>> metaLogFieldDao = null;

		private MultipleOutputs<Text, NullWritable> multipleOutputs;
		private Text keyText = null;
		private IntWritable one = null;
		private String line = "";
		private String day;

		@Override
		protected void setup(Context context) throws IOException,	InterruptedException {
			this.metaLogDao = new MetaLogDao<>();
			this.metaLogDao.parseDMObj(new File("metalog.csv"));

			this.metaLogFieldDao = new MetaLogFieldDao<>();
			this.metaLogFieldDao.parseDMObj(new File("metalogfield.csv"));

			this.day = context.getConfiguration().get("day");

			multipleOutputs = new MultipleOutputs(context);
			keyText = new Text();
		}

		@Override
		protected void map(LongWritable key, Text value, Context context)	throws IOException, InterruptedException {
			try {
				System.out.println("key=====================" + key.toString());
				System.out.println("value=====================" + value.toString());
				line = value.toString();
				NginxLogModel nginxLogModel = NginxFactory.getModel(line);

				//解析url里面的参数为hashmap
				Map<String,String> params = NginxFactory.getParams(nginxLogModel);

				String baseUrl = NginxFactory.getBaseUrl(nginxLogModel);

				MetaLog metaLog = metaLogDao.getDMOjb(baseUrl);

				if (metaLog == null) {
					return;
				}

				//获取日志对应的字段备案列表
				List<MetaLogField> fieldList = metaLogFieldDao.getDMOjb(metaLog.getLogId());

				StringBuilder sb = new StringBuilder();

				sb.append(baseUrl + "\t");
				sb.append(nginxLogModel.getRequestTime() + "\t");
				sb.append(nginxLogModel.getIp() + "\t");

				Iterator<MetaLogField> it = fieldList.iterator();

				//按我们定义好的顺序去从URL参数的hashmap中获取值
				while (it.hasNext()) {
					MetaLogField field = it.next();
					sb.append(params.get(field.getFieldName()) + "\t");
				}

				this.keyText.set(sb.toString());
				this.multipleOutputs.write(this.keyText, NullWritable.get(), metaLog.getHdfsBaseOutput() + "/" + day + "/part");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			ToolRunner.run(new Configuration(), new LogSplitMR(), args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();

		Job job = Job.getInstance(conf, "LogSplitMR");

		String inputPathStr = job.getConfiguration().get("inputPath");
		String outputPathStr = job.getConfiguration().get("outputPath");

		System.out.println(inputPathStr);
		System.out.println(outputPathStr);

		FileInputFormat.setInputPaths(job, inputPathStr);
		FileOutputFormat.setOutputPath(job, new Path(outputPathStr));

		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		job.setMapperClass(MyMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(NullWritable.class);

		job.setJarByClass(LogSplitMR.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}
}
