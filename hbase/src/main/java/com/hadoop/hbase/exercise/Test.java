//package com.hadoop.hbase.exercise;
//
//import java.io.IOException;
//import java.net.UnknownHostException;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.hbase.HColumnDescriptor;
//import org.apache.hadoop.hbase.HTableDescriptor;
//import org.apache.hadoop.hbase.TableName;
//import org.apache.hadoop.hbase.client.HBaseAdmin;
//import org.apache.hadoop.hbase.client.HTable;
//import org.apache.hadoop.hbase.client.Put;
//import org.apache.hadoop.hbase.util.Bytes;
//import org.apache.hadoop.hbase.util.MD5Hash;
//import org.junit.Before;
//
////import com.dajiangtai.util.DateUtils;
///**
// * 模拟插入hbase数据
// * @author 大讲台
// *
// */
//public class Test {
////	@Before
//	public void test0() throws UnknownHostException {
//		
//	}
////	@org.junit.Test
//	public void testHashAndCreateTable() throws Exception{	
//		aa();
//		bb();
//	    }
//	public void aa(){
//		String fixedUid = MyStringUtil.getFixedLengthStr("11706" + "",7);
//		String rowkey = MD5Hash.getMD5AsHex(Bytes.toBytes(fixedUid)).substring(0, 8)
//				+fixedUid
//				+ DateUtils.getDateFormatFromDay(DateUtils.YMD_HMS,
//						"2016-06-16 18:23:02", DateUtils.YYYYMMDD)+ "33005";
//		System.out.println("rowkey="+rowkey);
//	}
//	
//	public void bb(){
//		String rowkey = MD5Hash.getMD5AsHex(Bytes.toBytes("0011706")).substring(0, 8)+"0011706"+"20160616"+"33005";
//		System.out.println("rowkey="+rowkey);
//	}
//
//}
