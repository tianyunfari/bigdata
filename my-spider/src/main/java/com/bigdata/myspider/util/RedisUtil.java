/**
 * RedisUtil.java
 * com.bigdata.myspider.util
 * Copyright (c) 2018, 版权所有.
*/

package com.bigdata.myspider.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * 操作redis数据库的工具类
 * @author    wuchangyuan
 * @Date	 2018-8-20 	 
 */
public class RedisUtil {
	//redis中列表key的名称
	public static String highkey = "spider.highlevel";
	public static String lowkey = "spider.lowlevel";
	public static String starturl = "spider.starturl";
	public static String setkey = "spider.ip";
	JedisCluster cluster = null;
//	JedisPool jedisPool = null;

	public RedisUtil() {
//		JedisPoolConfig poolConfig = new JedisPoolConfig();
//		poolConfig.setMaxIdle(10);
//		poolConfig.setMaxTotal(100);
//		poolConfig.setMaxWaitMillis(10000);
//		poolConfig.setTestOnBorrow(true);
//		jedisPool = new JedisPool(poolConfig, "192.168.80.133", 7000);
		
		Set<HostAndPort> clusterNodes = new HashSet<HostAndPort>();
		clusterNodes.add(new HostAndPort("192.168.79.24", 7000));
		clusterNodes.add(new HostAndPort("192.168.79.24", 7001));
		clusterNodes.add(new HostAndPort("192.168.79.24", 7002));
		clusterNodes.add(new HostAndPort("192.168.79.24", 7003));
		clusterNodes.add(new HostAndPort("192.168.79.24", 7004));
		clusterNodes.add(new HostAndPort("192.168.79.24", 7005));
		cluster = new JedisCluster(clusterNodes);
	}
	
	/**
	 * 查询
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public List<String> lrange(String key,int start,int end){
//		Jedis resource = jedisPool.getResource();
//		List<String> list = resource.lrange(key, start, end);
//		jedisPool.returnResourceObject(resource);
		
		List<String> list = cluster.lrange(key, start, end);
		return list;
		
	}

	/**
	 * 添加一个元素到list
	 * @param Key
	 * @param url
	 */
	public void add(String key, String url) {
//		Jedis resource = jedisPool.getResource();
//		resource.lpush(Key, url);
//		jedisPool.returnResourceObject(resource);
		
		cluster.lpush(key, url);
	}
	
	/**
	 * 从list中获取一个元素
	 * @param key
	 * @return
	 */
	public String poll(String key) {
//		Jedis resource = jedisPool.getResource();
//		String result = resource.rpop(key);
//		jedisPool.returnResourceObject(resource);
		
		String result = cluster.rpop(key);
		return result;
	}

	/**
	 * 添加一个元素到set
	 * @param Key
	 * @param url
	 */
	public void addSet(String key, String value) {
		cluster.sadd(key, value);
	}
	
	/**
	 * 从set中随机获取一个元素
	 * @param key
	 * @return
	 */
	public String getSet(String key) {
		return cluster.srandmember(key);
	}

	/**
	 * 从set中删除一个指定元素
	 * @param key
	 * @return
	 */
	public void delSet(String key, String value) {
		cluster.srem(key, value);
	}
	
	/**
	 * 从set中删除所有元素
	 * @param key
	 * @return
	 */
	public void delAllSet(String key) {
		Set<String> set = cluster.smembers(key);
		for (String string : set) {
			cluster.srem(key, string);
		}
	}

	@Test
	public void testSet() {
//		delAllSet(setkey);
		
		addSet(setkey, "116.62.194.248:3128");
		addSet(setkey, "121.63.208.230:9999");
		addSet(setkey, "125.106.227.217:3128");
		addSet(setkey, "221.225.215.71:8118");
//		for (int i = 0; i < 10; i++) {
//			System.out.println(getSet(setkey));
//		}
	}
	
	public static void main(String[] args) {
		RedisUtil redisUtil = new RedisUtil();
		
//		String url = "http://www.tudou.com/s3portal/service/pianku/data.action?pageSize=90&app=mainsitepc&deviceType=1&tags=&tagType=3&firstTagId=3&areaCode=&initials=&hotSingerId=&pageNo=1&sortDesc=quality";
//		redisUtil.add(highkey, url);
//		redisUtil.add(highkey, "1");
//		redisUtil.add(highkey, "2");
//		redisUtil.add(highkey, "3");
//		redisUtil.add(highkey, "4");
//		
//		List<String> list = redisUtil.lrange(highkey, 0, 4);
//		for (String string : list) {
//			System.out.println(string);
//		}
//		redisUtil.add(starturl, "https://list.youku.com/category/show/c_97_a_%E5%A4%A7%E9%99%86_s_1_d_1.html?spm=a2htv.20009910.m_86809.5~5~5~1~3~A");
//		redisUtil.add(highkey, "https://list.youku.com/category/show/c_97_a_%E5%A4%A7%E9%99%86_s_1_d_1.html?spm=a2htv.20009910.m_86809.5~5~5~1~3~A");

//		String str = null;
		System.out.println("high:" + redisUtil.cluster.llen(highkey));
		System.out.println("low:" + redisUtil.cluster.llen(lowkey));
		System.out.println("starturl:" + redisUtil.cluster.llen(starturl));
		System.out.println("spider.ip:" + redisUtil.cluster.scard(setkey));
		
//		while(StringUtils.isNotBlank((str = redisUtil.poll(highkey)))){
//			System.out.println(str);
//		}
//		while(StringUtils.isNotBlank((str = redisUtil.poll(lowkey)))){
//			System.out.println(str);
//		}
		
	}
}

