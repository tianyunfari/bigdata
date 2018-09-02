/**
 * MyJDBCUtil.java
 * com.hadoop.hbase.exercise
 * Copyright (c) 2018, 版权所有.
*/

package com.hadoop.hbase.exercise;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * 连接数据库工具类
 * <p>
 * @author    wuchangyuan
 * @Date	 2018-8-24 	 
 */
public class MyJDBCUtil {
	public static Connection getConnectionByJDBC() {
		Connection conn = null;
		if (conn == null) {
			try {
				Class.forName(Constant.MYSQL_DRIVER);
				conn = DriverManager.getConnection(Constant.MYSQL_URL, Constant.MYSQL_USERNAME, Constant.MYSQL_PASSWORD);
				conn.setAutoCommit(false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return conn;
	}
	
	public static int getAllRecords(String sql) {
		Connection conn = MyJDBCUtil.getConnectionByJDBC();
		Statement stmt;
		int count = 0;
		
		try {
			stmt = conn.createStatement();
			ResultSet set = stmt.executeQuery(sql);
			set.next();
			count = set.getInt(1);
			System.out.println(count);
		} catch (SQLException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return count;
	}
	
	public static void main(String[] args) {
		String sql = "select count(*) from task";
//		String sql = "select * from task limit 10";
		MyJDBCUtil.getAllRecords(sql);
//		JDBCUtil.queryData(sql);

	}
}

