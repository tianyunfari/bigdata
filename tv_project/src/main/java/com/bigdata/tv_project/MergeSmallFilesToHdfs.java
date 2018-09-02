package com.bigdata.tv_project;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.IOUtils;

public class MergeSmallFilesToHdfs {
	private static FileSystem fs = null;
	private static FileSystem local = null;
	
	public static class RegexAcceptPathFilter implements PathFilter {
		private final String regex;

		public RegexAcceptPathFilter(String regex) {
			this.regex = regex;
		}
		
		@Override
		public boolean accept(Path arg0) {
			// TODO Auto-generated method stub
			return arg0.toString().matches(regex);
		}
	}

	public static class RegexExcludePathFilter implements PathFilter {
		private final String regex;

		public RegexExcludePathFilter(String regex) {
			this.regex = regex;
		}

		@Override
		public boolean accept(Path arg0) {
			// TODO Auto-generated method stub
			return !(arg0.toString().matches(regex));
		}
	}
	
	private static void list() throws URISyntaxException, IOException {
		Configuration conf = new Configuration();
		
		URI uri = new URI("hdfs://bigdata:9000");
		
		fs = FileSystem.get(uri, conf);
		
		local = FileSystem.getLocal(conf);
		
		FileStatus[] localStatus = local.globStatus(new Path(
				"E:/djt_work/tv_project/73/*"), new RegexExcludePathFilter("^.*svn$"));
		
		Path[] listedDirs = FileUtil.stat2Paths(localStatus);
		
		FSDataInputStream in = null;
		FSDataOutputStream out = null;
		for (Path p : listedDirs) {
			String fileName = p.getName().replace("-", "");
			FileStatus[] listStatus = local.globStatus(new Path(
				p + "/*"), new RegexAcceptPathFilter("^.*txt$"));
			
			Path[] txtDirs = FileUtil.stat2Paths(listStatus);
		
			Path outPath = new Path("hdfs://bigdata:9000/tv/" + 
			fileName + ".txt");
			
			out = fs.create(outPath);
			for (Path dir : txtDirs) {
				in = local.open(dir);
				IOUtils.copyBytes(in, out, 4096, false);
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}
	
	public static void main(String[] args) throws URISyntaxException, IOException {
		list();
	}
}
