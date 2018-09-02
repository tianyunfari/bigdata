/**
 * QueueRepositoryService.java
 * com.bigdata.myspider.service.impl
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.myspider.service.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.bigdata.myspider.service.RepositoryService;

/**
 * 高低优先级队列存储URL实现类
 * @author    wuchangyuan
 * @Date	 2018-8-15 	 
 */
public class QueueRepositoryService implements RepositoryService {
	private Queue<String> highLevelQueue= new ConcurrentLinkedDeque<String>();
	private Queue<String> lowLevelQueue= new ConcurrentLinkedDeque<String>();

	@Override
	public String poll() {
		String url = null;
//		System.out.println(Integer.valueOf(this.highLevelQueue.size()));

		if (this.highLevelQueue.size() != 0) {
			url = this.highLevelQueue.poll();
		} else {
			url = this.lowLevelQueue.poll();
		}
		return url;
	}

	@Override
	public void addHighLevel(String url) {
		this.highLevelQueue.add(url);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addLowLevel(String url) {
		this.lowLevelQueue.add(url);
		// TODO Auto-generated method stub
		
	}

}

