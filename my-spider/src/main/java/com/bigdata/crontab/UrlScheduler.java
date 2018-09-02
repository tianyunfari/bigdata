/**
 * UrlScheduler.java
 * com.bigdata.crontab
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.crontab;

import java.util.Date;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 分类url定时抓取job
 * @author    wuchangyuan
 * @Date	 2018-8-21 	 
 */
public class UrlScheduler {
	public static void main(String[] args) {
		Date date = new Date();
		System.out.println(date);
		
		try {
			//获取默认调度器
			Scheduler defaultScheduler = StdSchedulerFactory.getDefaultScheduler();
			//开启调度器
			defaultScheduler.start();
			
			//调度的任务
			JobDetail jobDetail = new JobDetail("url-job", Scheduler.DEFAULT_GROUP, AddUrlJob.class);
			//定时任务 秒、分、时、日、月
			CronTrigger trigger = new CronTrigger("url-job", Scheduler.DEFAULT_GROUP, "40 52 10 21 8 ?");
			//添加调度任务
			defaultScheduler.scheduleJob(jobDetail , trigger);

		} catch (Exception e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
}

