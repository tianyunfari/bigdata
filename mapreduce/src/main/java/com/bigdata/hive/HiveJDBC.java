/**
 * HiveJDBC.java
 * com.bigdata.hive
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * hivejdbc连接
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-8-29 	 
 */
public class HiveJDBC {
	//驱动名称
	private static String driverName = "org.apache.hive.jdbc.HiveDriver";
	//连接hive2服务的连接地址，hiveserver2
	private static String url = "jdbc:hive2://bigdata3:10000/test";
	//对HDFS有操作权限的用户
	private static String user = "hadoop";
	////在非安全模式下，指定一个用户运行查询，忽略密码
	private static String password = "";
	
	private static String sql = "";
	private static ResultSet res;
	
	public static void main(String[] args) throws SQLException {
		try {
			Class.forName(driverName);
			Connection conn = DriverManager.getConnection(url, user, password);
			
			Statement stmt = conn.createStatement();
			
			String tableName = "testHiveDriverTable";
			
			//表存在就先删除
			sql = "drop table " + tableName;
			stmt.execute(sql);
			
			//创建表
			sql = "create table " + tableName + " (key int,value string)" +
					" row format delimited fields terminated by '\t' STORED" +
					" AS TEXTFILE";
			stmt.execute(sql);
			
			sql = "show tables '" + tableName + "'";
			res = stmt.executeQuery(sql);
			if (res.next()) {
				System.out.println(res.getString(1));
			}
			
			//执行"describe table"操作
			sql = "desc " + tableName;
			res = stmt.executeQuery(sql);
			while (res.next()) {
				System.out.println(res.getString(1) + "\t" + 
						res.getString(2));
			}
			
			//load 数据到表里
			String path = "/home/hadoop/data/djt.txt";
			
			sql = "load data local inpath '" + path + "' into table "
					+ tableName;
			stmt.execute(sql);
			
			// 执行“select * query”操作
	        sql = "select * from " + tableName;
	        res = stmt.executeQuery(sql);
	        while (res.next()) {
	            System.out.println(res.getInt(1) + "\t" + res.getString(2));
	        }
	        
	        conn.close();
		} catch (ClassNotFoundException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
}

