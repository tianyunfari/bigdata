/**
 * StringUtil.java
 * com.bigdata.tv_project
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.tv_project;

/**
 * TODO(字符串工具类)
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-7-24 	 
 */
public class StringUtil {
	/**
	 * 
	 * TODO(分割字符串)
	 * <p>
	 * TODO(这里描述这个方法详情– 可选)
	 *
	 * @param str
	 * @param regex TODO(这里描述每个参数,如果有返回值描述返回值,如果有异常描述异常)
	 */
	public static String[] split(String str, String regex) {
		return str.split(regex);
	}
}

