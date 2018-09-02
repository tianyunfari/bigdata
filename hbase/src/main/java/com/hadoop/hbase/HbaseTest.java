package com.hadoop.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseTest {
	public static Configuration conf;
	static {
		conf = HBaseConfiguration.create();//第一步
		conf.set("hbase.zookeeper.quorum", "bigdata,bigdata1,bigdata2");
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		conf.set("hbase.master", "bigdata:16010");
	}
	
	public static void main(String[] args) throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
//		createTable("member1");
//		insertDataByPut("member1");
//		queryByGet("member1");
//		queryByScan("member1");
		deleteData("member1");
	}
	
	public static void createTable(String tableName) throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
		HBaseAdmin hbaseAdmin = new HBaseAdmin(conf); //创建HBASEadmin对象
		if (hbaseAdmin.tableExists(tableName)) {
			hbaseAdmin.disableTable(tableName);
			hbaseAdmin.deleteTable(tableName);
		}
		
		HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
		//添加family
		tableDescriptor.addFamily(new HColumnDescriptor("address"));
		tableDescriptor.addFamily(new HColumnDescriptor("info"));
		
		hbaseAdmin.createTable(tableDescriptor); //创建表
		hbaseAdmin.close(); //释放资源
	}
	
	public static void insertDataByPut(String tableName) throws IOException {
		HTable table = new HTable(conf, tableName);
		
		Put putData = new Put(getBytes("bigdata"));
		
		putData.addColumn(getBytes("address"), getBytes("country"), getBytes("China"));
		putData.addColumn(getBytes("address"), getBytes("province"), getBytes("guangdong"));
		putData.addColumn(getBytes("address"), getBytes("city"), getBytes("zhuhai"));
		
		putData.addColumn(getBytes("info"), getBytes("age"), getBytes("24"));
		putData.addColumn(getBytes("info"), getBytes("birthday"), getBytes("1999-07-01"));
		putData.addColumn(getBytes("info"), getBytes("company"), getBytes("huawei"));
		
		table.put(putData);
		
		table.close();
	}
	
	public static void queryByGet(String tableName) throws IOException {
		HTable table = new HTable(conf, tableName);
		
		Get getData = new Get(getBytes("bigdata"));
		Result data = table.get(getData);
		System.out.println("rowkey:" + new String(data.getRow()));
		
//		for (KeyValue keyValue : data.raw()) {
//			System.out.println("column:" + new String(keyValue.getFamily()) + 
//					"===cell:" + new String(keyValue.getQualifier()) + 
//					"== value:" + new String(keyValue.getValue()));
//		}
		
//		for (Cell keyValue : data.rawCells()) {
//			System.out.println("column:" + new String(keyValue.) + 
//					"===cell:" + new String(keyValue.getQualifier()) + 
//					"== value:" + new String(keyValue.getValue()));
//		}

		table.close();
	}
	
	public static void queryByScan(String tableName) throws IOException {
		HTable table = new HTable(conf, tableName);
		Scan scan = new Scan();
		scan.addColumn(getBytes("info"), getBytes("age"));
		ResultScanner scanner = table.getScanner(scan);
		
		for (Result res : scanner) {
			System.out.println("row key:" + new String(res.getRow()));
			for (KeyValue keyValue : res.raw()) {
				System.out.println("column:" + new String(keyValue.getFamily()) + 
						"==cell: " + new String( keyValue.getQualifier()) + 
						"==value:" + new String(keyValue.getValue()));
			}
		}
		
		scanner.close();
		table.close();
	}
	
	public static void deleteData(String tableName) throws IOException {
		HTable table = new HTable(conf, tableName);
		
		Delete del = new Delete(getBytes("bigdata"));
		del.deleteColumn(getBytes("info"), getBytes("age"));
		
		table.delete(del);
		table.close();
	}
	
	public static byte[] getBytes(String str) {
		if (str ==null) {
			str = "";
		}
		return Bytes.toBytes(str);
	}
}
