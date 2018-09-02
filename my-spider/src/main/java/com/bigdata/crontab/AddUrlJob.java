/**
 * AddUrlJob.java
 * com.bigdata.crontab
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.crontab;

import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bigdata.myspider.util.RedisUtil;

/**
 * 向redis 添加分类url
 * @author    wuchangyuan
 * @Date	 2018-8-21 	 
 */
public class AddUrlJob implements Job{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		RedisUtil redisUtil = new RedisUtil();
		
		list = redisUtil.lrange(RedisUtil.starturl, 0, -1);
		for (String string : list) {
			System.out.println(string);
			redisUtil.add(RedisUtil.highkey, string);
		}
	}
	
}

