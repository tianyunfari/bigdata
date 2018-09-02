package djt;

import com.djt.model.SingleAppUseInfo;
import com.djt.security.signature.AESSignature;
import com.djt.utils.JSONUtil;
import com.djt.utils.SignUtils;
import djt.vo.AppUseInfo;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppUseTest
{
  @Test
  public void appUseInfo()
  {
    String url = "http://hadoop1:8080/behavior/gather";
    System.out.println("junit request url==================" + url);
    PostMethod method = new PostMethod(url);

    SingleAppUseInfo appUse1 = new SingleAppUseInfo();
    appUse1.setPackageName("com.browser1");
    appUse1.setActiveTime(60000L);

    SingleAppUseInfo appUse2 = new SingleAppUseInfo();
    appUse2.setPackageName("com.browser3");
    appUse2.setActiveTime(120000L);

    List<SingleAppUseInfo> singleAppUseInfoList = new ArrayList();
    singleAppUseInfoList.add(appUse1);
    singleAppUseInfoList.add(appUse2);

    AppUseInfo appUseInfo = new AppUseInfo();
    appUseInfo.setUserId(1000);
    appUseInfo.setSingleAppUseInfoList(singleAppUseInfoList);

    try {
      String beginDateStr = "2018-05-13 07:00:00";
      String endDateStr = "2018-05-13 07:10:00";

      Date beginDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(beginDateStr);
      Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endDateStr);

      appUseInfo.setDay(beginDateStr.substring(0,10));
      appUseInfo.setBeginTime(beginDate.getTime());
      appUseInfo.setEndTime(endDate.getTime());
    } catch (ParseException e) {
      e.printStackTrace();
    }

    try {
      String json = JSONUtil.fromObject(appUseInfo);
      //json = "{\"begintime\":1468806000000,\"endtime\":1468807200000,\"data\":[{\"package\":\"com.tencent.mobileqq\",\"activetime\":283100,\"md5\":\"0441DACCCB623052C9BDCD550121B3E0\",\"versioncode\":\"358\"},{\"package\":\"com.tencent.mm\",\"activetime\":819476,\"md5\":\"96AC37E3D7144BA5D0FDF0A5C4698FD1\",\"versioncode\":\"800\"},{\"package\":\"com.zui.launcher\",\"activetime\":10288,\"md5\":\"1762F6BD5C213A31969DF12093DB08A0\",\"versioncode\":\"110057\"},{\"package\":\"com.zui.camera\",\"activetime\":61249,\"md5\":\"3620C85143A868638ED12BF0D51FC6CD\",\"versioncode\":\"21\"}],\"gsensor_is_changed\":\"1\",\"gsensor_x\":\"-0.1341\",\"gsensor_y\":\"-2.4777\",\"gsensor_z\":\"9.4875\",\"gsensor_time\":\"0\",\"is_screen_off_never_timeout\":\"0\",\"is_statistic_on\":\"1\",\"insert_time\":\"1468807403914\",\"clientid\":\"769172223_a23_480_v2000240_10000_i867695020019393_1468562440448_60\",\"rkey\":\"1468807454286_251\",\"usertoken\":\"ZAgAAAAAAAGE9MTAwNDk0ODA3ODgmYj0yJmM9MSZkPTE0NzMwJmU9RjdGQjlGNTU3N0MwQUJFMDk0RDgwRDdENTFFOTVGN0YxJmg9MTQ2ODgwNzc2ODAyNSZpPTQzMjAwJm89ODY3Njk1MDIwMDE5MzkzJnA9aW1laSZxPTAmdXNlcm5hbWU9c2luYmElNDBhbGl5dW4uY29tJmlsPWNuuVgtAnXzB6gcIfdI1cmZLQ\",\"uuid\":\"d5b5f78b-a7f5-4d51-aba9-9bd8cfbad16c\",\"requestTime\":1468807460550}";
      System.out.println("junit request json========" + json);
      FormatJsonUtil.printJson(json);

      // AES加密
      byte[] bytes = AESSignature.encrypt(json, HttpHelper.parivateKey);

      // 格式化成16进制字符串
      String requestData = SignUtils.getFormattedText(bytes);

      method.setRequestBody(requestData);

      HttpHelper.getHttpClient().executeMethod(method);
      String result = HttpHelper.getHttpRequestResult(method);
      System.out.println("junit response json========" + result);
    } catch (HttpException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}