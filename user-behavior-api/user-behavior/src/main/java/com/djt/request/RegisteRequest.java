package com.djt.request;

import com.djt.utils.EnhancedStringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;

//@JsonPostDataRequest
public class RegisteRequest implements Serializable
{
  private static final long serialVersionUID = 1L;

  private long id;

  private long userId;

  @NotEmpty
  private String deviceManufacturer;

  private String deviceBrand;

  @NotEmpty
  private String deviceModel;

  private String lang;

  @NotEmpty
  private String os;

  private String osVersion;

  @NotEmpty
  private String sdkVersion;

  private String simoperator1;

  private String phoneNumber1;

  private String simoperator2;

  private String phoneNumber2;

  private int horizontalResolution;

  private int verticalResolution;

  private Date createDate;

  private Date modifyDate;

  @Min(1)
  private int dpi;

  @NotEmpty
  private String deviceIdType;

  /**
   * IMEI2
   */
  @NotEmpty
  private String deviceId;

  private String cpu;

  private String networkType;

  private String userIp;

  @NotEmpty
  private String packageName;

  private String clientVer;

  @Min(1)
  private int clientVc;

  @NotEmpty
  private String channel;

  private String macAddr;

  private String sn;

  private String deviceType;

  /**
   * IMEI1
   */
  private String card1deviceId;

  private String meid;

  private String romVersion;

  private String cnsAppToken;

  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public long getUserId()
  {
    return userId;
  }

  public void setUserId(long userId)
  {
    this.userId = userId;
  }

  public String getDeviceManufacturer()
  {
    return deviceManufacturer;
  }

  public void setDeviceManufacturer(String deviceManufacturer)
  {
    this.deviceManufacturer = deviceManufacturer;
  }

  public String getDeviceBrand()
  {
    return deviceBrand;
  }

  public void setDeviceBrand(String deviceBrand)
  {
    this.deviceBrand = deviceBrand;
  }

  public String getDeviceModel()
  {
    return deviceModel;
  }

  public void setDeviceModel(String deviceModel)
  {
    this.deviceModel = deviceModel;
  }

  public String getLang()
  {
    return lang;
  }

  public void setLang(String lang)
  {
    this.lang = lang;
  }

  public String getOs()
  {
    return os;
  }

  public void setOs(String os)
  {
    this.os = os;
  }

  public String getOsVersion()
  {
    return osVersion;
  }

  public void setOsVersion(String osVersion)
  {
    this.osVersion = osVersion;
  }

  public String getSdkVersion()
  {
    return sdkVersion;
  }

  public void setSdkVersion(String sdkVersion)
  {
    this.sdkVersion = sdkVersion;
  }

  public String getSimoperator1()
  {
    return simoperator1;
  }

  public void setSimoperator1(String simoperator1)
  {
    this.simoperator1 = simoperator1;
  }

  public String getPhoneNumber1()
  {
    return phoneNumber1;
  }

  public void setPhoneNumber1(String phoneNumber1)
  {
    this.phoneNumber1 = phoneNumber1;
  }

  public String getSimoperator2()
  {
    return simoperator2;
  }

  public void setSimoperator2(String simoperator2)
  {
    this.simoperator2 = simoperator2;
  }

  public String getPhoneNumber2()
  {
    return phoneNumber2;
  }

  public void setPhoneNumber2(String phoneNumber2)
  {
    this.phoneNumber2 = phoneNumber2;
  }

  public int getHorizontalResolution()
  {
    return horizontalResolution;
  }

  public void setHorizontalResolution(int horizontalResolution)
  {
    this.horizontalResolution = horizontalResolution;
  }

  public int getVerticalResolution()
  {
    return verticalResolution;
  }

  public void setVerticalResolution(int verticalResolution)
  {
    this.verticalResolution = verticalResolution;
  }

  public int getDpi()
  {
    return dpi;
  }

  public void setDpi(int dpi)
  {
    this.dpi = dpi;
  }

  public String getDeviceIdType()
  {
    return deviceIdType;
  }

  public void setDeviceIdType(String deviceIdType)
  {
    this.deviceIdType = deviceIdType;
  }

  public String getDeviceId()
  {
    return deviceId;
  }

  public void setDeviceId(String deviceId)
  {
    this.deviceId = deviceId;
  }

  public String getCpu()
  {
    return cpu;
  }

  public void setCpu(String cpu)
  {
    this.cpu = cpu;
  }

  public String getNetworkType()
  {
    return networkType;
  }

  public void setNetworkType(String networkType)
  {
    this.networkType = networkType;
  }

  public String getUserIp()
  {
    return userIp;
  }

  public void setUserIp(String userIp)
  {
    this.userIp = userIp;
  }

  public String getPackageName()
  {
    return packageName;
  }

  public void setPackageName(String packageName)
  {
    this.packageName = packageName;
  }

  public String getClientVer()
  {
    return clientVer;
  }

  public void setClientVer(String clientVer)
  {
    this.clientVer = clientVer;
  }

  public int getClientVc()
  {
    return clientVc;
  }

  public void setClientVc(int clientVc)
  {
    this.clientVc = clientVc;
  }

  public String getChannel()
  {
    return channel;
  }

  public void setChannel(String channel)
  {
    this.channel = channel;
  }

  public String getMacAddr()
  {
    return macAddr;
  }

  public void setMacAddr(String macAddr)
  {
    this.macAddr = macAddr;
  }

  public String getSn()
  {
    return sn;
  }

  public void setSn(String sn)
  {
    this.sn = sn;
  }

  public String getDeviceType()
  {
    return deviceType;
  }

  public void setDeviceType(String deviceType)
  {
    this.deviceType = deviceType;
  }

  public String getCard1deviceId()
  {
    return card1deviceId;
  }

  public void setCard1deviceId(String card1deviceId)
  {
    this.card1deviceId = card1deviceId;
  }

  public String getMeid()
  {
    return meid;
  }

  public void setMeid(String meid)
  {
    this.meid = meid;
  }

  public String getRomVersion()
  {
    return romVersion;
  }

  public void setRomVersion(String romVersion)
  {
    this.romVersion = romVersion;
  }
  
  public Date getCreateDate()
  {
    return createDate;
  }

  public void setCreateDate(Date createDate)
  {
    this.createDate = createDate;
  }

  public Date getModifyDate()
  {
    return modifyDate;
  }

  public void setModifyDate(Date modifyDate)
  {
    this.modifyDate = modifyDate;
  }

  public String getCnsAppToken()
  {
    return cnsAppToken;
  }

  public void setCnsAppToken(String cnsAppToken)
  {
    this.cnsAppToken = cnsAppToken;
  }

  @Override
  public String toString()
  {
    return EnhancedStringUtils.toString(this);
  }
}
