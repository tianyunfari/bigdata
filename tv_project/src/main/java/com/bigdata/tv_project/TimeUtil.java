/**
 * TimeUtil.java
 * com.bigdata.tv_project
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.tv_project;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO(转换时间)
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-7-23 	 
 */
public class TimeUtil {
	/**
	 * 
	 * TODO(将时间00:00:00转换为秒 int)
	 * <p>
	 * TODO(这里描述这个方法详情– 可选)
	 *
	 * @param time
	 * @return TODO(这里描述每个参数,如果有返回值描述返回值,如果有异常描述异常)
	 */
	public static int TimeToSecond(String time) {
		if (time == null || time.equals("")) {
			return 0;
		}
		
		String[] splitStr = time.split(":");
		int hour = Integer.parseInt(splitStr[0]);
		int min = Integer.parseInt(splitStr[1]);
		int sec = Integer.parseInt(splitStr[2]);
		
		int total = hour * 3600 + min * 60 + sec;
		return total;
	}
	/**
	 * 
	 * TODO(转换时间为秒)
	 * <p>
	 * TODO(这里描述这个方法详情– 可选)
	 *
	 * @param time
	 * @return TODO(返回的是string类型)
	 */
	public static String TimeToSecond2(String time) {
		if (time == null || time.equals("")) {
			return "";
		}

		String[] splitStr = time.split(":");
		int hour = Integer.parseInt(splitStr[0]);
		int min = Integer.parseInt(splitStr[1]);
		int sec = Integer.parseInt(splitStr[2]);
		
		int total = hour * 3600 + min * 60 + sec;
		return total + "";
	}
	
	/**
	 * 
	 * TODO(求两个时间的秒的差值)
	 * <p>
	 * TODO(这里描述这个方法详情– 可选)
	 *
	 * @param a_e
	 * @param a_s
	 * @return TODO(这里描述每个参数,如果有返回值描述返回值,如果有异常描述异常)
	 */
	public static String getDuration(String a_e, String a_s) {
		if (a_e == null || a_s == null) {
			return 0 + "";
		}
		
		int ae = Integer.parseInt(a_e);
		int as = Integer.parseInt(a_s);
		return (ae - as) + "";
	}
	
	/**
	 * 
	 * TODO(将时间00:00转换为秒)
	 * <p>
	 * TODO(这里描述这个方法详情– 可选)
	 *
	 * @param time
	 * @return TODO(这里描述每个参数,如果有返回值描述返回值,如果有异常描述异常)
	 */
	public static int Time2ToSecond(String time) {
		if (time == null) {
			return 0;
		}
		String[] splitStr = time.split(":");
		int hour = Integer.parseInt(splitStr[0]);
		int min = Integer.parseInt(splitStr[1]);
		int total = hour * 3600 + min * 60;
		
		return total;
	}
	
	/**
	 * 
	 * TODO(提取两个时间段之间的分钟数)
	 * <p>
	 * TODO(这里描述这个方法详情– 可选)
	 *
	 * @param start
	 * @param end
	 * @return TODO(这里描述每个参数,如果有返回值描述返回值,如果有异常描述异常)
	 */
	public static List<String> getTimeSplit(String start, String end) {
		List<String> list = new ArrayList<String>();
		
		String[] s = start.split(":");
		int sh = Integer.parseInt(s[0]);
		int sm = Integer.parseInt(s[1]);
		String[] e = end.split(":");
		int eh = Integer.parseInt(e[0]);
		int em = Integer.parseInt(e[1]);
		
		if (eh < sh) {
			eh = 24;
		}
		if (sh == eh) {
			for (int m = sm; m <= em; m++) {
//				int am = m + 1;
//				int ah = sh;
//				if (am == 60) {
//					am = 0;
//					ah += 1;
//				}
				String hstr = "";
				String mstr = "";
				if (sh < 10) {
					hstr = "0" + sh;
				}else {
					hstr = sh + ""; 
				}
				
				if (m < 10) {
					mstr = "0" + m;
				}else {
					mstr = m + "";
				}
				String time = hstr + ":" + mstr;
				list.add(time);
			}
		} else {
			for (int h = sh; h <= eh; h++) {
				if (h == 24) {
					break;
				}
				if (h == eh) {
					for (int m = 0; m <= em; m++) {
						String hstr = "";
						String mstr = "";
						if (h < 10) {
							hstr = "0" + h;
						}else {
							hstr = h + ""; 
						}
						
						if (m < 10) {
							mstr = "0" + m;
						}else {
							mstr = m + "";
						}
						String time = hstr + ":" + mstr;
						list.add(time);
					}
				} else if (h == sh) {
					for (int m = sm; m <= 59; m++) {
						String hstr = "";
						String mstr = "";
						if (h < 10) {
							hstr = "0" + h;
						}else {
							hstr = h + ""; 
						}
						
						if (m < 10) {
							mstr = "0" + m;
						}else {
							mstr = m + "";
						}
						String time = hstr + ":" + mstr;
						list.add(time);
					}
				} else {
					for (int m = 0; m <= 59; m++) {
						String hstr = "";
						String mstr = "";
						if (h < 10) {
							hstr = "0" + h;
						}else {
							hstr = h + ""; 
						}
						
						if (m < 10) {
							mstr = "0" + m;
						}else {
							mstr = m + "";
						}
						String time = hstr + ":" + mstr;
						list.add(time);
					}
				}
			}
		}
		
		return list;
	}
	
	public static void main(String[] args) {
		//just for test
		List<String> testList = new ArrayList<String>();
//		testList = getTimeSplit("10:15", "10:59");
		testList = getTimeSplit("10:55", "12:05");
		System.out.println(testList.toString());
	}
}

