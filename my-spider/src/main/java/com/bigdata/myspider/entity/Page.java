/**
 * Page.java
 * com.bigdata.myspider.entity
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.myspider.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

/**
 * TODO(存储页面信息实体类)
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-8-13 	 
 */
public class Page {
	//页面内容
	private String content;
	//总播放量
	@Field
	private String allnumber;
	//每日播放增量
	@Field
	private String daynumber;
	//评论数
	@Field
	private String commentnumber;
	//收藏数
	@Field
	private String collectnumber;
	//赞
	@Field
	private String supportnumber;
	//踩
	@Field
	private String againstnumber;
	
	//电视剧名称
	@Field
	private String tvname;
	//页面url
	@Field
	private String url;
	//子集数据
	private String episodenumber;
	
	//电视剧id
	@Field
	private String tvId;
	
	//存储电视剧url（包含列表url和详情页url）
	private List<String> urlList = new ArrayList<String>();
	
	public String getTvId() {
		return tvId;
	}
	public void setTvId(String tvId) {
		this.tvId = tvId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAllnumber() {
		return allnumber;
	}
	public void setAllnumber(String allnumber) {
		this.allnumber = allnumber;
	}
	public String getDaynumber() {
		return daynumber;
	}
	public void setDaynumber(String daynumber) {
		this.daynumber = daynumber;
	}
	public String getCommentnumber() {
		return commentnumber;
	}
	public void setCommentnumber(String commentnumber) {
		this.commentnumber = commentnumber;
	}
	public String getCollectnumber() {
		return collectnumber;
	}
	public void setCollectnumber(String collectnumber) {
		this.collectnumber = collectnumber;
	}
	public String getSupportnumber() {
		return supportnumber;
	}
	public void setSupportnumber(String supportnumber) {
		this.supportnumber = supportnumber;
	}
	public String getAgainstnumber() {
		return againstnumber;
	}
	public void setAgainstnumber(String againstnumber) {
		this.againstnumber = againstnumber;
	}
	public String getTvname() {
		return tvname;
	}
	public void setTvname(String tvname) {
		this.tvname = tvname;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getEpisodenumber() {
		return episodenumber;
	}
	public void setEpisodenumber(String episodenumber) {
		this.episodenumber = episodenumber;
	}
	public List<String> getUrlList() {
		return urlList;
	}
		
	public void addUrl(String url){
		this.urlList.add(url);
	}
}

