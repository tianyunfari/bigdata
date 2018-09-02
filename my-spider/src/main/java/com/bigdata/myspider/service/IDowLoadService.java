/**
 * IDowLoadService.java
 * com.bigdata.myspider.service
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.myspider.service;

import com.bigdata.myspider.entity.Page;

/**
 * TODO(页面下载接口)
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-8-13 	 
 */
public interface IDowLoadService {
	public Page download(String url);
}

