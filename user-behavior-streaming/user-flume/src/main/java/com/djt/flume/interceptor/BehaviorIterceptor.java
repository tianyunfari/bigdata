package com.djt.flume.interceptor;

import com.djt.model.UserBehavorRequestModel;
import com.djt.utils.JSONUtil;
import com.google.common.collect.Lists;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BehaviorIterceptor implements Interceptor
{
  private Logger LOG = LoggerFactory.getLogger(BehaviorIterceptor.class);

  @Override
  public void initialize()
  {
  }

  @Override
  public Event intercept(Event event)
  {
    try {
      if (event == null || event.getBody() == null || event.getBody().length == 0) {
        return null;
      }

      System.out.println("===================body=" + new String(event.getBody()));

      long userId = 0;

      try {
        UserBehavorRequestModel model = JSONUtil.json2Object(new String(event.getBody()),UserBehavorRequestModel.class);
        userId = model.getUserId();
      } catch (Exception e) {
        LOG.error("event body is Invalid,body=" + event.getBody(), e);
      }

      if (userId == 0) {
        return null;
      }

      Map<String, String> headerMap = event.getHeaders();

      if (null == headerMap) {
        event.setHeaders(new HashMap<String, String>());
      }

      event.getHeaders().put("key", String.valueOf(userId));
      return event;
    } catch (Exception e) {
      LOG.error("intercept error,body=" + event.getBody(), e);
      return event;
    }
  }

  @Override
  public List intercept(List<Event> events)
  {
    List<Event> out = Lists.newArrayList();
    for (Event event : events) {
      Event outEvent = intercept(event);
      if (outEvent != null) {
        out.add(outEvent); }
    }
    return out;
  }

  @Override
  public void close()
  {
  }

  public static class BehaviorIterceptorBuilder implements Builder
  {
    @Override
    public void configure(Context context)
    {
    }

    @Override
    public Interceptor build()
    {
      return new BehaviorIterceptor();
    }
  }
}
