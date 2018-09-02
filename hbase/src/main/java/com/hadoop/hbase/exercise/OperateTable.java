/**
 * OperateTable.java
 * com.hadoop.hbase.exercise
 * Copyright (c) 2018, 版权所有.
*/

package com.hadoop.hbase.exercise;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.Cell;

/**
 * 
 * test表的相关操作
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author   wuchangyuan
 * @Date	 2018-8-30
 */
public class OperateTable {
        
    private static Configuration conf = null;
    private static Connection connection;
    static {
    	conf = HBaseConfiguration.create();
    	conf.set("hbase.zookeeper.quorum","bigdata,bigdata1,bigdata2");
    	conf.set("hbase.zookeeper.property.clientPort", "2181");
            
    	try {
    		connection = ConnectionFactory.createConnection(conf);
		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
				
		}
    }


    // 创建数据库表
    public static void createTable(String tableName, String[] columnFamilys)
                    throws Exception {
            // 新建一个数据库管理员
//                HBaseAdmin hAdmin = new HBaseAdmin(conf);
    	Admin hAdmin = connection.getAdmin();

        if (hAdmin.tableExists(TableName.valueOf(tableName))) {
                System.out.println("表已经存在");
                System.exit(0);
        } else {
                // 新建一个 scores 表的描述
                HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf(tableName));
                // 在描述里添加列族
                for (String columnFamily : columnFamilys) {
                        tableDesc.addFamily(new HColumnDescriptor(columnFamily));
                }
                // 根据配置好的描述建表
                hAdmin.createTable(tableDesc);
                System.out.println("创建表成功");
        }
        hAdmin.close();
    }


    // 删除数据库表
    public static void deleteTable(String tableName) throws Exception {
            // 新建一个数据库管理员
            HBaseAdmin hAdmin = new HBaseAdmin(conf);


            if (hAdmin.tableExists(tableName)) {
                    // 关闭一个表
                    hAdmin.disableTable(tableName);
                    // 删除一个表
                    hAdmin.deleteTable(tableName);
                    System.out.println("删除表成功");
            } else {
                    System.out.println("删除的表不存在");
                    System.exit(0);
            }
            hAdmin.close();
    }


    // 添加一条数据
    public static void addRow(String tableName, String row,
                    String columnFamily, String column, String value) throws Exception {
            HTable table = new HTable(conf, tableName);
            Put put = new Put(Bytes.toBytes(row));
            // 参数出分别：列族、列、值
            put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column),
                            Bytes.toBytes(value));
            table.put(put);
            table.close();
    }


    // 删除一条数据
    public static void delRow(String tableName, String row) throws Exception {
            HTable table = new HTable(conf, tableName);
            Delete del = new Delete(Bytes.toBytes(row));
            table.delete(del);
            table.close();
    }


    // 删除多条数据
    public static void delMultiRows(String tableName, String[] rows)
                    throws Exception {
            HTable table = new HTable(conf, tableName);
            List<Delete> list = new ArrayList<Delete>();


            for (String row : rows) {
                    Delete del = new Delete(Bytes.toBytes(row));
                    list.add(del);
            }


            table.delete(list);
            table.close();
    }


    // get row
    public static void getRow(String tableName, String row) throws Exception {
            HTable table = new HTable(conf, tableName);
            Get get = new Get(Bytes.toBytes(row));
            Result result = table.get(get);
            // 输出结果
            for (Cell rowKV : result.rawCells()) {
                    System.out.print("Row Name: " + new String(CellUtil.cloneRow(rowKV)) + " ");
                    System.out.print("Timestamp: " + rowKV.getTimestamp() + " ");
                    System.out.print("column Family: " + new String(CellUtil.cloneFamily(rowKV)) + " ");
                    System.out.print("column Name:  " + new String(CellUtil.cloneQualifier(rowKV)) + " ");
                    System.out.println("Value: " + new String(CellUtil.cloneValue(rowKV)) + " ");
            }
            table.close();
    }


    // get all records
    public static void getAllRows(String tableName) throws Exception {
            HTable table = new HTable(conf, tableName);
            Scan scan = new Scan();
            ResultScanner results = table.getScanner(scan);
            // 输出结果
            for (Result result : results) {
                    for (Cell rowKV : result.rawCells()) {
                            System.out.print("Row Name: " + new String(CellUtil.cloneRow(rowKV)) + " ");
                            System.out.print("Timestamp: " + rowKV.getTimestamp() + " ");
                            System.out.print("column Family: " + new String(CellUtil.cloneFamily(rowKV)) + " ");
                            System.out.print("column Name:  " + new String(CellUtil.cloneQualifier(rowKV)) + " ");
                            System.out.println("Value: " + new String(CellUtil.cloneValue(rowKV)) + " ");
                    }
            }
            table.close();
    }


    // main
    public static void main(String[] args) {
            try {
//                String tableName = "users2";
//
//
//                // 第一步：创建数据库表：“users2”
//                String[] columnFamilys = { "info", "course" };
//                OperateTable.createTable(tableName, columnFamilys);
//
//
//                // 第二步：向数据表的添加数据
//                // 添加第一行数据
//                OperateTable.addRow(tableName, "tht", "info", "age", "20");
//                OperateTable.addRow(tableName, "tht", "info", "sex", "boy");
//                OperateTable.addRow(tableName, "tht", "course", "china", "97");
//                OperateTable.addRow(tableName, "tht", "course", "math", "128");
//                OperateTable.addRow(tableName, "tht", "course", "english", "85");
//                // 添加第二行数据
//                OperateTable.addRow(tableName, "xiaoxue", "info", "age", "19");
//                OperateTable.addRow(tableName, "xiaoxue", "info", "sex", "boy");
//                OperateTable.addRow(tableName, "xiaoxue", "course", "china", "90");
//                OperateTable.addRow(tableName, "xiaoxue", "course", "math", "120");
//                OperateTable.addRow(tableName, "xiaoxue", "course", "english", "90");
//                // 添加第三行数据
//                OperateTable.addRow(tableName, "qingqing", "info", "age", "18");
//                OperateTable.addRow(tableName, "qingqing", "info", "sex", "girl");
//                OperateTable.addRow(tableName, "qingqing", "course", "china", "100");
//                OperateTable.addRow(tableName, "qingqing", "course", "math", "100");
//                OperateTable.addRow(tableName, "qingqing", "course", "english", "99");
//                // 第三步：获取一条数据
//                System.out.println("获取一条数据");
//                OperateTable.getRow(tableName, "tht");
//                // 第四步：获取所有数据
//                System.out.println("获取所有数据");
//                OperateTable.getAllRows(tableName);
//                // 第五步：删除一条数据
//                System.out.println("删除一条数据");
//                OperateTable.delRow(tableName, "tht");
//                OperateTable.getAllRows(tableName);
//                // 第六步：删除多条数据
//                System.out.println("删除多条数据");
//                String[] rows = { "xiaoxue", "qingqing" };
//                OperateTable.delMultiRows(tableName, rows);
//                OperateTable.getAllRows(tableName);
//                // 第八步：删除数据库
//                System.out.println("删除数据库");
//                OperateTable.deleteTable(tableName);
//
            	Table table = connection.getTable(TableName.valueOf("test"));
            	
            	Scan scan = new Scan();
            	scan.setMaxVersions();
            	ResultScanner scanner = table.getScanner(scan);
            	for (Result result : scanner) {
            		List<Cell> list = result.listCells();
            		//list所有版本的数据?
            		for (Cell cell : list) {
                        System.out.print("Row Name: " + new String(CellUtil.cloneRow(cell)) + " ");
                        System.out.print("Timestamp: " + cell.getTimestamp() + " ");
                        System.out.print("column Family: " + new String(CellUtil.cloneFamily(cell)) + " ");
                        System.out.print("column Name:  " + new String(CellUtil.cloneQualifier(cell)) + " ");
                        System.out.println("Value: " + new String(CellUtil.cloneValue(cell)) + " ");
            		}
            	}
            	connection.close();
            } catch (Exception err) {
                    err.printStackTrace();
            }
    }
}
