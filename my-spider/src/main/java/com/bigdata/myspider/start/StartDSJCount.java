/**
 * StartDSJCount.java
 * com.bigdata.myspider.start
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.myspider.start;

import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


import com.bigdata.myspider.entity.Page;
import com.bigdata.myspider.service.IDowLoadService;
import com.bigdata.myspider.service.IProcessService;
import com.bigdata.myspider.service.RepositoryService;
import com.bigdata.myspider.service.impl.HttpClientDownLoadService;
import com.bigdata.myspider.service.impl.QueueRepositoryService;
import com.bigdata.myspider.service.impl.RedisRepositoryService;
import com.bigdata.myspider.service.impl.YOUKUProcessService;
import com.bigdata.myspider.util.LoadPropertyUtil;

/**
 * TODO(电视剧爬虫执行入口类)
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)
 * @author    wuchangyuan
 * @Date	 2018-8-13 	 
 */
public class StartDSJCount {
	private IDowLoadService downLoadSerivce;
	private IProcessService processService;
	private RepositoryService repositoryService;
	
	//线程池
	private ExecutorService threadPool = Executors.newFixedThreadPool(Integer.
			parseInt(LoadPropertyUtil.getConfig("threadNum")));

//	private Queue<String> repositoryService = new ConcurrentLinkedDeque<String>();
	
	private static Logger logger = Logger.getLogger(StartDSJCount.class);
	
	public IDowLoadService getDownLoadSerivce() {
		return downLoadSerivce;
	}

	public void setDownLoadSerivce(IDowLoadService downLoadSerivce) {
		this.downLoadSerivce = downLoadSerivce;
	}
	
	public IProcessService getProcessService() {
		return processService;
	}

	public void setProcessService(IProcessService processService) {
		this.processService = processService;
	}

	public RepositoryService getRepositoryService() {
		return repositoryService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	/**
	 * 
	 * TODO(下载页面内容)
	 *
	 * @param url
	 * @return TODO(这里描述每个参数,如果有返回值描述返回值,如果有异常描述异常)
	 */
	public Page downloadPage(String url) {
		return this.downLoadSerivce.download(url);
	}
	
	public Page processPage(Page page) {
		return this.processService.process(page);
	}
	
	@SuppressWarnings("static-access")
	public void startSpider() {
		//开启爬虫后一直执行，一旦队列中有数据就开始爬
		int num = 0;
//		String firsturl = "https://list.youku.com/category/show/c_97_a_%E5%A4%A7%E9%99%86_s_1_d_1.html?spm=a2htv.20009910.m_86809.5~5~5~1~3~A";
//		this.repositoryService.add(firsturl);
		while (true) {
			//从队列中取出URL
			String url = repositoryService.poll();
			System.out.println("urlQueue:" + url);
			
			if (StringUtils.isNotBlank(url)) {
				//页面下载
				Page page = downloadPage(url);
				logger.debug("download successful!");
				if (StringUtils.isNotBlank(page.getContent())) {
					Random random = new Random();
					try {
						Thread.currentThread().sleep(random.nextInt(1000));//毫秒
					} catch (InterruptedException e) {
						
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
//					continue;
				}
				//解析
//				System.out.println(page.getUrl());
				if(StringUtils.isBlank(page.getContent())){
					System.out.println("没有Content！！！");
					continue;
				}
				page = processPage(page);
				
				//获取所有URL,并添加到队列中
				List<String> urlList = page.getUrlList();
				for (String eachUrl : urlList) {
					//添加到队列中
					//this.repositoryService.add(eachUrl);
					//添加到高低队列中
					if (eachUrl.startsWith(LoadPropertyUtil.getYOUKU("forGetDetailPageFront")) ||
							eachUrl.startsWith(LoadPropertyUtil.getYOUKU("detailPageFront"))) {
						repositoryService.addLowLevel(eachUrl);
						System.out.println("low:" +eachUrl);
					} else {
						System.out.println("high:" +eachUrl);
						repositoryService.addHighLevel(eachUrl);
					}
				}
				
				//如果是详情页，就存储页面信息
//				System.out.println("mainurl:" + page.getUrl());
				if(page.getUrl().startsWith(LoadPropertyUtil.getYOUKU("detailPageFront"))){
//					storePageInfo(page);
					System.out.println("--------total:" + ++num);
					if (num > 30) {
						break;
					}
				}
				//延时
				try{
					Random random = new Random();
					Thread.currentThread().sleep(random.nextInt(1000));//毫秒   
					}
				catch(Exception e){} 
			} else {
				System.out.println("无URL可解析！！！");
				//延时
				try{
					Thread.currentThread().sleep(8888);//毫秒   
					}
				catch(Exception e){} 
			}
			
		}
	}
	
	public static void main(String[] args) {
		final StartDSJCount dsjCount = new StartDSJCount();
		
		dsjCount.setDownLoadSerivce(new HttpClientDownLoadService());
		dsjCount.setProcessService(new YOUKUProcessService());
//		dsjCount.setRepositoryService(new QueueRepositoryService());
		//添加起始页URL到Redis的highkey中
		dsjCount.setRepositoryService(new RedisRepositoryService());
//		String url = "http://list.youku.com/show/id_z2348efbfbd0befbfbdef.html";
//		String url = "https://list.youku.com/category/show/c_97_a_%E5%A4%A7%E9%99%86_s_1_d_1.html?spm=a2htv.20009910.m_86809.5~5~5~1~3~A";
//		
//		dsjCount.repositoryService.addHighLevel(url);
		//单线程执行
//		dsjCount.startSpider();
		//用线程池，多线程执行
		int threadNum = Integer.parseInt(LoadPropertyUtil.getConfig("threadNum"));
		while (threadNum-- > 0) {
			dsjCount.threadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					dsjCount.startSpider();
				}
			});
			System.out.println("threadNum=" + threadNum);
		}
//		Page page = dsjCount.downloadPage(url);
//		System.out.println(page.getContent());
//		dsjCount.processPage(page);
	//	System.out.println(page.getContent());
	}
}

