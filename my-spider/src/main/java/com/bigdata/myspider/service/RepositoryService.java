/**
 * RepositoryService.java
 * com.bigdata.myspider.service
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.myspider.service;

/**
 * 存储URL仓库的接口
 * @author    wuchangyuan
 * @Date	 2018-8-15 	 
 */
public interface RepositoryService {
	public String poll();
	
	public void addHighLevel(String url);
	
	public void addLowLevel(String url);
}

