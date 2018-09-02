/**
 * LoadPropertyUtil.java
 * com.bigdata.myspider.util
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.myspider.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 读取配置文件属性工具类
 * @author    wuchangyuan
 * @Date	 2018-8-13 	 
 */
public class LoadPropertyUtil {
	/**
	 * 
	 * // 读取优酷配置文件
	 *
	 * @param key
	 * @return TODO(这里描述每个参数,如果有返回值描述返回值,如果有异常描述异常)
	 */
	public static String getYOUKU(String key) {
		String value = "";
		Locale locale = Locale.getDefault();
		
		try {
			ResourceBundle localResource = ResourceBundle.getBundle("youku",
					locale);
			value = localResource.getString(key);
			
		} catch (MissingResourceException mre) {
			value = "";
		}
		
		return value;
	}
	
	// 读取公共配置文件
	public static String getConfig(String key) {
		String value = "";
		Locale locale = Locale.getDefault();
		try {
			ResourceBundle localResource = ResourceBundle.getBundle("config",
					locale);
			value = localResource.getString(key);
		} catch (MissingResourceException mre) {
			value = "";
		}
		return value;
	}
	
	public static void main(String[] args) {
		System.out.println(LoadPropertyUtil.getConfig("threadNum"));
	}
}

