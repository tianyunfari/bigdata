/**
 * RedisRepositoryService.java
 * com.bigdata.myspider.service.impl
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.myspider.service.impl;


import org.apache.commons.lang.StringUtils;

import com.bigdata.myspider.service.RepositoryService;
import com.bigdata.myspider.util.RedisUtil;

/**
 * redis数据库实现类
 * @author    wuchangyuan
 * @Date	 2018-8-20 	 
 */
public class RedisRepositoryService implements RepositoryService {
	RedisUtil redisUtil = new RedisUtil();
	
	@Override
	public String poll() {
		// TODO Auto-generated method stub
		String urlString = redisUtil.poll(redisUtil.highkey);
		if(StringUtils.isBlank(urlString)){
			urlString = redisUtil.poll(redisUtil.lowkey);
		}
		
		return urlString;		
	}

	@Override
	public void addHighLevel(String url) {
		// TODO Auto-generated method stub
		redisUtil.add(redisUtil.highkey, url);
	}

	@Override
	public void addLowLevel(String url) {
		// TODO Auto-generated method stub
		redisUtil.add(redisUtil.lowkey, url);
	}

}

