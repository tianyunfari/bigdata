package com.hadoop.hbase.exercise;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;

public class MyHbaseService {
	public static MyHbaseClient myHbaseClient;
	
	public MyHbaseService() {
		myHbaseClient = MyHbaseClient.getMyHbaseClient();
	}
	public static void main(String[] args) {
		MyHbaseService myHbaseService = new MyHbaseService();
		
//		String row = MD5Hash.getMD5AsHex(Bytes.toBytes("0011706")).substring(0, 8)
//				+ "0011706"+"20160616"+"33005";
//		myHbaseService.getTaskByRowkey(row);
		
//		int uid = 11772;
//		myHbaseService.getTaskByUid(uid);
		
		// 精确到日期查询
//		 int uid = 11772;
//		 String time = "2016-07-04";
//		 myHbaseService.getTaskByUidAndTime(uid, time);

		myHbaseService.getTaskByRowFilter();
	}
	
	public void getTaskByRowkey(String row) {
		Table table = myHbaseClient.getTable(myHbaseClient.TABLE_NAME);
		
		Get get = new Get(row.getBytes());
		
		try {
			Result result = table.get(get);
			if (!result.isEmpty()) {
				String name = Bytes.toString(CellUtil.cloneValue(result.getColumnLatestCell(
						Bytes.toBytes("cf"), Bytes.toBytes("name"))));
				System.out.println(name);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (table != null) {
				myHbaseClient.closeTable(table);
			}
			myHbaseClient.close();
		}
	}
	
	public void getTaskByUid(int uid) {
		Table table = myHbaseClient.getTable(myHbaseClient.TABLE_NAME);
		String fixedUid1 = MyStringUtil.getFixedLengthStr(uid + "", 7);
		String startKey = MD5Hash.getMD5AsHex(Bytes.toBytes(fixedUid1)).substring(0, 8) +
				fixedUid1;
		
		String fixedUid2 = MyStringUtil.getFixedLengthStr(uid + 1 + "", 7);
		String endKey = MD5Hash.getMD5AsHex(Bytes.toBytes(fixedUid1)).substring(0, 8) +
				fixedUid2;
		Scan s = new Scan();
		s.setStartRow(Bytes.toBytes(startKey));
		s.setStopRow(Bytes.toBytes(endKey));
		
		ResultScanner rsa = null;
		try {
			rsa = table.getScanner(s);
			for (Result result : rsa) {
				String rowkey = new String(result.getRow());
				String name = Bytes.toString(CellUtil.cloneValue(result.getColumnLatestCell(
						Bytes.toBytes("cf"), Bytes.toBytes("name"))));
				
				System.out.println(rowkey + ":" + name);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (rsa != null) {
				rsa.close();
			}
			if (table != null) {
				myHbaseClient.closeTable(table);
			}
			myHbaseClient.close();
		}
	}
	
	public void getTaskByUidAndTime(int uid, String time) {
		Table table = myHbaseClient.getTable(myHbaseClient.TABLE_NAME);
		String fixedUid = MyStringUtil.getFixedLengthStr(uid + "", 7);
		
		String startKey = MD5Hash.getMD5AsHex(Bytes.toBytes(fixedUid))
				.substring(0, 8) + fixedUid +
				DateUtils.getDateFormatFromDay(DateUtils.YMD, time, 
						DateUtils.YYYYMMDD);
		//天数加1
		String endKey = MD5Hash.getMD5AsHex(Bytes.toBytes(fixedUid))
				.substring(0, 8) + fixedUid + 
				DateUtils.getDateBeforeOrAfter(DateUtils.YMD, time, 1,
						DateUtils.YYYYMMDD);
		Scan s = new Scan();
		s.setStartRow(Bytes.toBytes(startKey));
		s.setStopRow(Bytes.toBytes(endKey));
		
		ResultScanner rsa = null;
		ArrayList<Task> list = new ArrayList<Task>();
		
		try {
			rsa = table.getScanner(s);
			for (Result result : rsa) {
				Task task = new Task();
				String rowkey = new String(result.getRow());
				
				String name = Bytes.toString(CellUtil.cloneValue(result.getColumnLatestCell(
						Bytes.toBytes("cf"), Bytes.toBytes("name"))));
				System.out.println(rowkey + ":" + name);
				task.setName(name);
				list.add(task);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (rsa != null) {
				rsa.close();
			}
			if (table != null) {
				myHbaseClient.closeTable(table);
			}
			myHbaseClient.close();
		}
		
	}
	
	public void getTaskByRowFilter() {
		Table table = myHbaseClient.getTable(myHbaseClient.TABLE_NAME);
		
		//根据rowkey过滤
		RowFilter rowFilter = new RowFilter(CompareFilter.CompareOp.EQUAL, 
				new SubstringComparator("11772"));
		Scan s = new Scan();
		s.setFilter(rowFilter);
		ResultScanner rsa = null;
		try {
			rsa = table.getScanner(s);
			for (Result result : rsa) {
				String rowkey = new String(result.getRow());
				String name = Bytes.toString(CellUtil.cloneValue(result.getColumnLatestCell(
						Bytes.toBytes("cf"), Bytes.toBytes("name"))));
				System.out.println(rowkey + ":" + name);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (rsa != null) {
				rsa.close();
			}
			if (table != null) {
				myHbaseClient.closeTable(table);
			}
			myHbaseClient.close();
		}
	}
}
