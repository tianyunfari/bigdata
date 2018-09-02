/**
 * YOUKUProcessService.java
 * com.bigdata.myspider.service.impl
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.myspider.service.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlCleanerException;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;


import com.bigdata.myspider.entity.Page;
import com.bigdata.myspider.service.IProcessService;
import com.bigdata.myspider.start.StartDSJCount;
import com.bigdata.myspider.util.HtmlUtil;
import com.bigdata.myspider.util.LoadPropertyUtil;

/**
 *(优酷页面解析实现类)
 * @author    wuchangyuan
 * @Date	 2018-8-13 	 
 */
public class YOUKUProcessService implements IProcessService {
	private static Logger logger = Logger.getLogger(YOUKUProcessService.class);
	
	@Override
	public Page process(Page page) {
		// TODO Auto-generated method stub
		String content = page.getContent();
		
		HtmlCleaner htmlCleaner = new HtmlCleaner();
		TagNode rootNode = htmlCleaner.clean(content);
		
		if (page.getUrl().startsWith(LoadPropertyUtil.getYOUKU("forGetDetailPageFront"))) {
			//当前应是视频播放页，通过获取电视剧名称的xpath来获取详情页的url
			String DetailPageURL;
			DetailPageURL = "https:" + HtmlUtil.getAttributeByName(rootNode, LoadPropertyUtil
					.getYOUKU("forGetPraseDetailPageURL"), LoadPropertyUtil.getYOUKU(
							"forGetDetailPageAttribute"));
			System.out.println("DetailPageURL:"+DetailPageURL);
			
			if (DetailPageURL.startsWith(LoadPropertyUtil.getYOUKU("detailPageFront"))) {
				StartDSJCount startDSJCount = new StartDSJCount();
				startDSJCount.setDownLoadSerivce(new HttpClientDownLoadService());
				page = startDSJCount.downloadPage(DetailPageURL);
				if (StringUtils.isNotBlank(page.getContent())) {
					try {
						rootNode = htmlCleaner.clean(page.getContent());
						parseDetail(page, rootNode);
					} catch (HtmlCleanerException e) {
						
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
				} else {
					logger.info("1111_没有指向详情页的URL！！！");
				}
			} else {
				logger.info("2_没有指向详情页的URL！！！");
			}
			
		} else if (page.getUrl().startsWith(LoadPropertyUtil.getYOUKU(
				"detailPageFront"))) {
			//解析电视剧详情页
			parseDetail(page, rootNode);
		}else {
			//解析列表页,先获取下一页的URL
			String result = HtmlUtil.getAttributeByName(rootNode, LoadPropertyUtil.
					getYOUKU("parseNextPageURL"), LoadPropertyUtil.getYOUKU("nextPageAttribute"));
			if (result != null) {
				page.addUrl("https:" + result);
			}
			
			//再获取指向详情页的URL
			try {
				Object[] evaluateXPath = rootNode.evaluateXPath(LoadPropertyUtil.getYOUKU(
						"praseDetailPageURL"));
				//list页面一页有许多个电视剧的url
				if (evaluateXPath.length > 0) {
					for (Object object : evaluateXPath) {
						TagNode node = (TagNode)object;
						result = node.getAttributeByName(LoadPropertyUtil.getYOUKU(
								"detailPageAttribute"));
						Pattern pattern = Pattern.compile("//.+", Pattern.DOTALL);
						Matcher matcher = pattern.matcher(result);
						if(matcher.find()){
//							System.out.println("getPageInfoByRegex:" + matcher.group());
							result = matcher.group();
						}
//						System.out.println(result);
						page.addUrl("https:" + result);

					}
				}
			} catch (XPatherException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		}
		
//		HtmlUtil.getFieldByRegex(rootNode, LoadPropertyUtil.getYOUKU("parseAllNumber"), LoadPropertyUtil.getYOUKU("allnumberRegex"));
		
//		try {
//			Object[] evaluateXPath = rootNode.evaluateXPath("/body/div/div/div/div/div/ul/li[11]");
//			if (evaluateXPath.length > 0) {
//				TagNode node = (TagNode)evaluateXPath[0];
//				System.out.println("processing");
//				System.out.println(node.getText().toString());
//			}
//		} catch (XPatherException e) {
//			
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			
//		}
		return page;
	}

	public void parseDetail(Page page, TagNode rootNode) {
		String tempStr;
		
		//tvname
		tempStr = HtmlUtil.getFieldByRegex(rootNode, LoadPropertyUtil.getYOUKU("parseName"),
				LoadPropertyUtil.getYOUKU("nameRegex"));
		page.setTvname(tempStr);
//		System.out.println("剧集：" + page.getTvname());

		//allnumber
		tempStr = HtmlUtil.getFieldByRegex(rootNode, LoadPropertyUtil.getYOUKU("parseAllNumber"),
				LoadPropertyUtil.getYOUKU("allnumberRegex"));
		page.setAllnumber(tempStr);
//		System.out.println("总播放数：" + page.getAllnumber());

		//commentnumber
		tempStr = HtmlUtil.getFieldByRegex(rootNode, LoadPropertyUtil.getYOUKU("parseCommentNumber"),
				LoadPropertyUtil.getYOUKU("commentnumberRegex"));
		page.setCommentnumber(tempStr);
//		System.out.println("评论：" + page.getCommentnumber());

		//supportnumber
		tempStr = HtmlUtil.getFieldByRegex(rootNode, LoadPropertyUtil.getYOUKU("parseSupportNumber"),
				LoadPropertyUtil.getYOUKU("supportnumberRegex"));
		page.setSupportnumber(tempStr);
//		System.out.println("顶：" + page.getSupportnumber());

		//tvid
		Pattern pattern = Pattern.compile(LoadPropertyUtil.getYOUKU("praseTvIdString"), Pattern.DOTALL);
		Matcher matcher = pattern.matcher(page.getUrl());
		String string = "0";
		if(matcher.find()){
			string = matcher.group(1);
		}
		page.setTvId("youku_" + string);
		System.out.println("tvid：" + page.getTvId());

	}
}

