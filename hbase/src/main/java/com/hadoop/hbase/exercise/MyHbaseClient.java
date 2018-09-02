package com.hadoop.hbase.exercise;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

public class MyHbaseClient {
	private static Log log = LogFactory.getLog(MyHbaseClient.class);
	private Connection connection;
	private static String zkString = "bigdata,bigdata1,bigdata2";
	private static String zkPort = "2181";
	
	public static final String TABLE_NAME = "task";
	
	//列簇
	public static final String COLUMNFAMILY = "cf";
	//列
	public static final String COLUMNFAMILY_UID = "uid";
	public static final String COLUMNFAMILY_SYSTASKID = "systaskid";
	public static final String COLUMNFAMILY_TASKID = "taskid";
	public static final String COLUMNFAMILY_NAME = "name";
	public static final String COLUMNFAMILY_TYPE = "type";
	public static final String COLUMNFAMILY_STATE = "state";
	//2018-02-03
	public static final String COLUMNFAMILY_STARTTIME = "starttime";
	public static final String COLUMNFAMILY_FINISHTIME = "finishtime";
	public static final String COLUMNFAMILY_RECEIVETIME = "receivedate";
	public static final String COLUMNFAMILY_ACTUALFINISHTIME = "actualfinishtime";

	private MyHbaseClient(String zookeeperQuorum, String clientPort) {
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.property.clientPort", clientPort);
		conf.set("hbase.zookeeper.quorum", zookeeperQuorum);
		try {
			connection = ConnectionFactory.createConnection(conf);
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			log.error("create hbase client error", e);
		}
	}
	
	//使用私有构造函数
	public static MyHbaseClient getMyHbaseClient() {
		MyHbaseClient hbaseClient = new MyHbaseClient(zkString, zkPort);
		return hbaseClient;
	}
	
	//获取表对象
	public Table getTable(String tableName) {
		Table table = null;
		
		try {
			table = connection.getTable(TableName.valueOf(tableName));
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			table = null;
			log.error("get habse table error,tableName=" + tableName, e);
		}
		return table;
	}
	
	//插入数据
	public void put(String tableName, String row, String columnFamily, String column, 
			String value) throws IOException {
		Table table = getTable(tableName);
		Put put = new Put(Bytes.toBytes(row));
		put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), 
				Bytes.toBytes(value));
		table.put(put);
	}
	
	//统计所有数据
	public void queryAllTask(String tableName) throws IOException {
		Table table = getTable(tableName);
		Scan s = new Scan();
		ResultScanner rsa = table.getScanner(s);
		int num = 0;
		for (Result result : rsa) {
			String rowKey = new String(result.getRow());
			num++;
			System.out.println(num + "~" + rowKey);
		}
	}
	
	//删除数据
	public void deleteRecord(String tableName, String row) throws IOException {
		Table table = getTable(tableName);
		Delete delete = new Delete(row.getBytes());
		table.delete(delete);
	}
	
	//关闭表连接
	public void closeTable(Table table) {
		if (table != null) {
			try {
				table.close();
			} catch (Exception e) {
				// TODO: handle exception
				log.error("close table error,tableName=" + table.getName(), e);
			}
		}
	}
	
	//关闭connection连接
	public void close() {
		if (connection != null && !connection.isClosed()) {
			try {
				connection.close();
			} catch (Exception e) {
				// TODO: handle exception
				log.error("close hbase connect error", e);
			}
		} 
	}
}
