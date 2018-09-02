/**
 * HttpClientDownLoadService.java
 * com.bigdata.myspider.service.impl
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.myspider.service.impl;

import com.bigdata.myspider.entity.Page;
import com.bigdata.myspider.service.IDowLoadService;
import com.bigdata.myspider.util.PageDownLoadUtil;

/**
 * TODO(HttpClient页面下载实现类)
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-8-13 	 
 */
public class HttpClientDownLoadService implements IDowLoadService {

	@Override
	public Page download(String url) {
		// TODO Auto-generated method stub
		Page page = new Page();
		page.setContent(PageDownLoadUtil.getPageContent(url));
		page.setUrl(url);
		return page;
		
	}

}

