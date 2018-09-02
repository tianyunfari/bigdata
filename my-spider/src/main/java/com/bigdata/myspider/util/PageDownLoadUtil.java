/**
 * PageDownLoadUtil.java
 * com.bigdata.myspider.util
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.myspider.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.bigdata.myspider.entity.Page;
import com.bigdata.myspider.service.IDowLoadService;

/**
 * TODO(页面下载工具类)
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-8-13 	 
 */
public class PageDownLoadUtil implements IDowLoadService {
	public static String getPageContent(String url) {
		HttpClientBuilder builder = HttpClients.custom();
		CloseableHttpClient client = builder.build();
		
		HttpGet request = new HttpGet(url);
		String content = null;
		try {
			CloseableHttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			content = EntityUtils.toString(entity);
		} catch (ClientProtocolException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
				
		return content;
	}
	/**
	 * TODO(这里用一句话描述这个方法的作用)
	 * <p>
	 * TODO(这里描述这个方法详情– 可选)
	 *
	 * @param args TODO(这里描述每个参数,如果有返回值描述返回值,如果有异常描述异常)
	 */

	@Override
	public Page download(String url) {
		Page page = new Page();
		page.setContent(getPageContent(url));
		
		return page;
	}

	public static void main(String[] args) {
		PageDownLoadUtil download = new PageDownLoadUtil();
		Page pagecontent = download.download("http://list.youku.com/show/id_z2348efbfbd0befbfbdef.html");
		System.out.println(pagecontent.getContent());
	}
}

