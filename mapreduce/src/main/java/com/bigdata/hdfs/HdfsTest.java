/**
 * HdfsTest.java
 * com.bigdata.hdfs
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.hdfs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;

/**
 * @author    wuchangyuan
 * @Date	 2018-7-25 	 
 */
public class HdfsTest {
	public static FileSystem getFilesystem() throws IOException, URISyntaxException {
		Configuration conf = new Configuration();
		//集群上运行可以这样写
		//FileSystem fs = FileSystem.get(conf);
		
		//指定的文件系统地址
		URI fileURI = new URI("hdfs://bigdata:9000");
		//返回指定的文件系统，，本地运行获取文件系统，需要使用这个方法
		FileSystem fs = FileSystem.get(fileURI, conf);
		
		
		return fs;
	}
	
	public static void printDirs() throws IOException, URISyntaxException {
		FileSystem fs = getFilesystem();
		
		//列出目录内容
		FileStatus[] fileStatus = fs.listStatus(new Path(
			"hdfs://bigdata:9000/join"));
		
//		for (FileStatus dir : fileStatus) {
//			System.out.println(dir.getPath().toString());
//		}
		
		Path[] listPath = FileUtil.stat2Paths(fileStatus);
		for (Path p : listPath) {
			System.out.println(p.toString());
		}
	}
	
	public static void getFileFromHdfs() throws IOException, URISyntaxException {
		FileSystem fs = getFilesystem();
		
		Path srcPath = new Path("hdfs://bigdata:9000/MR/join/station.txt");
		
		Path dstPath = new Path("E:\\ps");
		
		fs.copyToLocalFile(srcPath, dstPath);
		if (fs != null) {
			fs.close();
		}
	} 
	
	public static void main(String[] args) {
		try {
			//printDirs();
			getFileFromHdfs();
		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (URISyntaxException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
}

