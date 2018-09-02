/**
 * HtmlUtil.java
 * com.bigdata.myspider.util
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.myspider.util;

import java.util.regex.Pattern;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import com.bigdata.myspider.util.RegexUtil;

/**
 * 页面解析工具类
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-8-13 	 
 */
public class HtmlUtil {
	//获取标签属性值
	public static String getAttributeByName(TagNode tagNode, String xpath,
			String att) {
		String result = null;
		Object[] evaluateXPath = null;
		
		try {
			evaluateXPath = tagNode.evaluateXPath(xpath);
			if (evaluateXPath.length > 0) {
				TagNode node = (TagNode)evaluateXPath[0];
				result = node.getAttributeByName(att);
			}
		} catch (XPatherException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return result;
	}

	public static String getFieldByRegex(TagNode rootNode, String xpath,
			String regex) {
		String number = "0";
		Object[] evaluateXPath = null;
		
		try {
			evaluateXPath = rootNode.evaluateXPath(xpath);
			if (evaluateXPath.length > 0) {
				TagNode node = (TagNode)evaluateXPath[0];
				
				Pattern numberPattern = Pattern.compile(regex, Pattern.DOTALL);
//				System.out.println(node.getText().toString());
				number = RegexUtil.getPageInfoByRegex(node.getText().toString(), numberPattern, 0);
//				System.out.println(number);
			}
		} catch (XPatherException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return number;
	}
}

