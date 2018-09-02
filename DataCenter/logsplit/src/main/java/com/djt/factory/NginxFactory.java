package com.djt.factory;

import com.djt.enums.NginxLogEnum;
import com.djt.model.NginxLogModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NginxFactory
{
  public static Logger log = Logger.getLogger(NginxFactory.class);

  public static NginxLogModel getModel(String nginxLog) {
    String patternStr = "(.*?) - (.*?) \\[(.*?)\\] \"(.*?)\" (.*?) (.*?) \"(.*?)\" \"(.*?)\" (.*?) (.*?) (.*?)$";
    List<String> dataList = getDataList(nginxLog, patternStr);

    if (dataList.size() == 0) {
      return null;
    }

    String url = getUrl(dataList.get(NginxLogEnum.URL.ordinal()));

    System.out.println(url);
    if (StringUtils.isEmpty(url)) {
      return null;
    }

    String requestTime = getRequestTime(dataList.get(NginxLogEnum.TIME.ordinal()));

    NginxLogModel model = new NginxLogModel();

    model.setIp(getIp(dataList.get(NginxLogEnum.IPS.ordinal())));
    model.setUrl(url);
    model.setRequestTime(requestTime);

    return model;
  }

  public static List<String> getDataList(String nginxLog, String patternStr) {
    Pattern p = Pattern.compile(patternStr);
    Matcher m = p.matcher(nginxLog);

    List<String> dataList = new ArrayList();

    while (m.find()) {
      for (int i = 1; i <= m.groupCount(); i++) {
        dataList.add(m.group(i));
      }
    }

    return dataList;
  }

  public static String getUrl(String orgUrl) {
    Pattern p = Pattern.compile("(.*?) (.*?) (.*?)$");
    Matcher m = p.matcher(orgUrl);

    String url="";

    if (m.find()) {
      url = m.group(2);
    }

    if (StringUtils.isEmpty(url)) {
      return "";
    }

    return url;
  }

  public static String getIp(String ips) {
    if (StringUtils.isEmpty(ips)) {
      return "";
    }

    String[] s=ips.split(", ");
    return s[0];
  }

  public static String getRequestTime(String nginxTime) {
    Date date;

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
    try {
      date = formatter.parse(nginxTime);
    } catch (Exception e) {
      return null;
    }

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return format.format(date);
  }

  public static Map<String, String> getParams(NginxLogModel model) {
    Map<String, String> map = new HashMap();

    String params = model.getUrl().split("\\?",-1)[1];
//    System.out.println(Arrays.toString(model.getUrl().split("\\?",-1)));

    String[] dataArr = params.split("&", -1);

    for (String param : dataArr) {
      String[] kv = param.split("=");
      String v = "";

      try {
        v = URLDecoder.decode(kv[1],"utf-8");
      } catch (Exception e) {
        v = "";
      }

      map.put(kv[0], v);
    }

    return map;
  }

  public static String getBaseUrl(NginxLogModel model) {
    String url = model.getUrl().split("\\?",-1)[0];

    if (StringUtils.isEmpty(url)) {
      return "";
    }

    return url;
  }

  public static void main(String[] args)
  {
    String str = "1.62.50.206 - - [24/May/2017:15:37:33 +0800] \"GET /oxeye/website/pvlog?protocol=1.2&rprotocol=3&clientFlag=2&fck=1415686896e2559&mac=68-F7-28-85-07-84&userid=3000&sid=24156931517d4da&pvid=7132593e-ba09-c8d8-a6c6-6332c68a1da2&config=index_&url=http://open.fun.tv/&referurl=http://open.fun.tv/vplay/c-116724.g-116461/&channelid=0 HTTP/1.1\" 200 51 \"\" \"HttpComponents/1.1\" 10.8.1.10 0.008 0.003";
    NginxLogModel model = NginxFactory.getModel(str);
    System.out.println(model);
    System.out.println(getParams(model));
    System.out.println(getParams(model).size());
    System.out.println(getBaseUrl(model));
  }
}
