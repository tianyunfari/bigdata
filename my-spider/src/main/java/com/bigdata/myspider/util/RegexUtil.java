/**
 * RegexUtil.java
 * com.bigdata.myspider.util
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.myspider.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式匹配工具
 * @author    wuchangyuan
 * @Date	 2018-8-13 	 
 */
public class RegexUtil {
	public static String getPageInfoByRegex(String content, Pattern pattern, 
			int groupNo) {
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			return matcher.group(groupNo).trim();
		}
		return "0";
	}
}

