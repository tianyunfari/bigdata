package com.djt.request;

import com.djt.model.SingleAppUseInfo;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class AppUseInfoAvatarRequest extends AppUseInfoRequest
{

  private Long userid;

  private String lenovoid;

  @JsonProperty("task_key")
  private String taskKey;

  @JsonProperty("event_date")
  private String eventDate;

  @JsonProperty("client_id")
  private String clientId;

  @JsonProperty("deviceid")
  private String deviceId;

  private Long devicedbid;

  @JsonProperty("device_type")
  private String deviceType;

  @JsonProperty("device_model")
  private String deviceModel;

  @JsonProperty("card1_deviceid")
  private String card1Deviceid;

  private String sn;
  private String mac;
  private String meid;

  @JsonProperty("client_ip")
  private String clientIp;

  @JsonProperty("networktype")
  private String networkType;

  @JsonProperty("datalist")
  private List<SingleAppUseInfo> appUseInfoList;

  public Long getUserid()
  {
    return userid;
  }

  public void setUserid(Long userid)
  {
    this.userid = userid;
  }

  public List<SingleAppUseInfo> getAppUseInfoList()
  {
    return appUseInfoList;
  }

  public void setAppUseInfoList(List<SingleAppUseInfo> appUseInfoList)
  {
    this.appUseInfoList = appUseInfoList;
  }

  public String getLenovoid()
  {
    return lenovoid;
  }

  public void setLenovoid(String lenovoid)
  {
    this.lenovoid = lenovoid;
  }

  public String getTaskKey()
  {
    return taskKey;
  }

  public void setTaskKey(String taskKey)
  {
    this.taskKey = taskKey;
  }

  public String getEventDate()
  {
    return eventDate;
  }

  public void setEventDate(String eventDate)
  {
    this.eventDate = eventDate;
  }

  public String getClientId()
  {
    return clientId;
  }

  public void setClientId(String clientId)
  {
    this.clientId = clientId;
  }

  public String getDeviceId()
  {
    return deviceId;
  }

  public void setDeviceId(String deviceId)
  {
    this.deviceId = deviceId;
  }

  public Long getDevicedbid()
  {
    return devicedbid;
  }

  public void setDevicedbid(Long devicedbid)
  {
    this.devicedbid = devicedbid;
  }

  public String getDeviceType()
  {
    return deviceType;
  }

  public void setDeviceType(String deviceType)
  {
    this.deviceType = deviceType;
  }

  public String getDeviceModel()
  {
    return deviceModel;
  }

  public void setDeviceModel(String deviceModel)
  {
    this.deviceModel = deviceModel;
  }

  public String getCard1Deviceid()
  {
    return card1Deviceid;
  }

  public void setCard1Deviceid(String card1Deviceid)
  {
    this.card1Deviceid = card1Deviceid;
  }

  public String getSn()
  {
    return sn;
  }

  public void setSn(String sn)
  {
    this.sn = sn;
  }

  public String getMac()
  {
    return mac;
  }

  public void setMac(String mac)
  {
    this.mac = mac;
  }

  public String getMeid()
  {
    return meid;
  }

  public void setMeid(String meid)
  {
    this.meid = meid;
  }

  public String getClientIp()
  {
    return clientIp;
  }

  public void setClientIp(String clientIp)
  {
    this.clientIp = clientIp;
  }

  public String getNetworkType()
  {
    return networkType;
  }

  public void setNetworkType(String networkType)
  {
    this.networkType = networkType;
  }
}
