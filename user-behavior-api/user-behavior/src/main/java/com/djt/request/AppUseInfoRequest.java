package com.djt.request;

import com.djt.model.SingleAppUseInfo;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class AppUseInfoRequest
{
  @JsonProperty("begintime")
  private long beginTime;

  @JsonProperty("endtime")
  private long endTime;

  @JsonProperty("data")
  private List<SingleAppUseInfo> appUseInfo;

  private long userId;

  private String day;

  public long getBeginTime() {
    return beginTime;
  }

  public void setBeginTime(long beginTime) {
    this.beginTime = beginTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public List<SingleAppUseInfo> getAppUseInfo() {
    return appUseInfo;
  }

  public void setAppUseInfo(List<SingleAppUseInfo> appUseInfo) {
    this.appUseInfo = appUseInfo;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getDay() {
    return day;
  }

  public void setDay(String day) {
    this.day = day;
  }
}
