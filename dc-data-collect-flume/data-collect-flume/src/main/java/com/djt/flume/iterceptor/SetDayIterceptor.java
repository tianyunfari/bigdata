package com.djt.flume.iterceptor;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.util.List;

public class SetDayIterceptor implements Interceptor
{
  @Override
  public void initialize()
  {
  }

  @Override
  public Event intercept(Event event)
  {
    if (event == null) {
      return null;
    }

    try {
      if (event.getHeaders() == null || event.getHeaders().size() == 0 || StringUtils.isEmpty(event.getHeaders().get("basename"))) {
        return event;
      }

      String basename = event.getHeaders().get("basename");

      String day = basename.substring(0, 8);
      String onlyHour = basename.substring(8, 10) ;

      event.getHeaders().put("day", day);
      event.getHeaders().put("onlyhour", onlyHour);

      return event;
    } catch (Exception e) {
      return event;
    }
  }

  @Override
  public List<Event> intercept(List<Event> events)
  {
    List<Event> out = Lists.newArrayList();
    for (Event event : events) {
      Event outEvent = intercept(event);
      if (outEvent != null) { out.add(outEvent); }
    }
    return out;
  }

  @Override
  public void close()
  {
  }

  public static class SetDayIterceptorBuilder implements Interceptor.Builder
  {
    @Override
    public void configure(Context context)
    {
    }

    public Interceptor build()
    {
      return new SetDayIterceptor();
    }
  }
}
