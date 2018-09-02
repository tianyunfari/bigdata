package com.hadoop.flume;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.interceptor.RegexExtractorInterceptorPassThroughSerializer;
import org.apache.flume.interceptor.RegexExtractorInterceptorSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

public class MyRegexInterceptor implements Interceptor {
	static final String REGEX = "regex";
	static final String SERIALIZERS = "serializers";
	
	private static final Logger logger = LoggerFactory.getLogger(MyRegexInterceptor.class);
	
	private final Pattern regex;
	private final List<NameAndSerializer> serializers;
	
	//extractorHeader 为true则提取header
	static final String EXTRACTOR_HEADER = "extractorHeader"; //必需和配置文件的值一样
	static final boolean DEFAULT_EXTRACTOR_HEADER = false;
	static final String EXTRACTOR_HEADER_KEY = "extractorHeaderKey";
	
	private final boolean extractorHeader;
	private final String extractorHeaderKey;
	
	private MyRegexInterceptor (Pattern regex, 
			List<NameAndSerializer> serializers, boolean extractorHeader, 
			String extractorHeaderKey) {
		this.regex = regex;
		this.serializers = serializers;
		this.extractorHeader = extractorHeader;
		this.extractorHeaderKey = extractorHeaderKey;
	}
	
	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Event intercept(Event event) {
		// TODO Auto-generated method stub
		String tmpStr;
		if (extractorHeader) {
			tmpStr = event.getHeaders().get(extractorHeaderKey);
		}else{
			tmpStr = new String(event.getBody(), Charsets.UTF_8);
		}
		//通过正则解析出web.log.2018-06-27
		Matcher matcher = regex.matcher(tmpStr);
		Map<String, String> headers = event.getHeaders();
		
		if (matcher.find()) {
			for (int group = 0, count = matcher.groupCount(); group < count; group++) {
				int groupIndex = group + 1;
				if (groupIndex > serializers.size()) {
					//debug
					if (logger.isDebugEnabled()) {
						logger.debug("skipping group{} to {} due to missing serializer", 
								group, count);
					}
					break;
				}
				NameAndSerializer serializer = serializers.get(group);
				
				if (logger.isDebugEnabled()) {
					logger.debug("serializing {} using {}", serializer.headerName,
							serializer.serializer);
				}
				//正则表达式提取的信息，分别赋值给s1.name s2.name s3.name
				headers.put(serializer.headerName, 
						serializer.serializer.serialize(matcher.group(groupIndex)));
			}
		}
		
		return event;
	}

	@Override
	public List<Event> intercept(List<Event> events) {
		// TODO Auto-generated method stub
		List<Event> intercepted = Lists.newArrayListWithCapacity(events.size());
		for (Event event : events) {
			Event interceptedEvent = intercept(event);
			if (interceptedEvent != null) {
				intercepted.add(interceptedEvent);
			}
		}
		return intercepted;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	//第一步执行
	public static class Builder implements Interceptor.Builder {
		private Pattern regex;
		private List<NameAndSerializer> serializerList;
		
		private boolean extractorHeader;
		private String extractorHeaderKey;
		
		private final RegexExtractorInterceptorSerializer defaultSerializer = 
				new RegexExtractorInterceptorPassThroughSerializer();
		
		public void configure(Context context) {
			//提取正则REGEX字符串
			String regexString = context.getString(REGEX);
			Preconditions.checkArgument(!StringUtils.isEmpty(regexString), 
					"Must supply a valid regex string");
			regex = Pattern.compile(regexString);
			regex.pattern();
			regex.matcher("").groupCount();
			
			configureSerializers(context);
			extractorHeader = context.getBoolean(EXTRACTOR_HEADER, DEFAULT_EXTRACTOR_HEADER);
			
			if (extractorHeader) {
				extractorHeaderKey = context.getString(EXTRACTOR_HEADER_KEY);
				Preconditions.checkArgument(  
		                  !StringUtils.isEmpty(extractorHeaderKey),  
		                  "Must supply header key");  
			}
		}
		
		private void configureSerializers(Context context) {
			//通过参数serializers获取s1 s2 s3字符串
			String serializerListStr = context.getString(SERIALIZERS);
			Preconditions.checkArgument(!StringUtils.isEmpty(serializerListStr), 
					"Must supply at least one name and serializer");
			//分割成数组
			String[] serializerNames = serializerListStr.split("\\s+");
			//得到serializers子属性的上下文context
			Context serializerContexts = 
					new Context(context.getSubProperties(SERIALIZERS + "."));
			
			serializerList = Lists.newArrayListWithCapacity(serializerNames.length);
			
			for (String serializerName : serializerNames) {
				//获取name子属性上下文context
				Context serializerContext = new Context(
						serializerContexts.getSubProperties(serializerName + "."));
				//获取type属性
				String type = serializerContext.getString("type", "DEFAULT");
				//获取name属性
				String name = serializerContext.getString("name");
				Preconditions.checkArgument(!StringUtils.isEmpty(name),
			            "Supplied name cannot be empty.");
				
				if ("DEFAULT".equals(type)) {
					serializerList.add(new NameAndSerializer(name, defaultSerializer));
				}else {
					serializerList.add(new NameAndSerializer(name, getCustomSerializer(
							type, serializerContext)));
				}
			}
		}

		private RegexExtractorInterceptorSerializer getCustomSerializer(
				String clazzName, Context context) {
			try {
				RegexExtractorInterceptorSerializer serializer = 
						(RegexExtractorInterceptorSerializer)Class.forName(clazzName).newInstance(); 
				serializer.configure(context);
				return serializer;
			} catch (Exception e) {
				logger.error("Could not instantiate event serializer.", e);
				Throwables.propagate(e);
			}
			return defaultSerializer;
		} 
		
		@Override
		public Interceptor build() {
			// TODO Auto-generated method stub
		      //检查regex正则是否配置错误
			Preconditions.checkArgument(regex != null,
			          "Regex pattern was misconfigured");
			  //检查serializerList(serializers=s1 s2 s3)是否有效
			Preconditions.checkArgument(serializerList.size() > 0,
			          "Must supply a valid group match id list");
			  //返回自定义拦截器对象
			return new MyRegexInterceptor(regex, serializerList,extractorHeader, extractorHeaderKey);
		}
	}
	
	static class NameAndSerializer {
		private final String headerName;
		private final RegexExtractorInterceptorSerializer serializer;
		
		public NameAndSerializer (String headerName,
				RegexExtractorInterceptorSerializer serializer) {
			this.headerName = headerName;
			this.serializer = serializer;
		}
	}
}
