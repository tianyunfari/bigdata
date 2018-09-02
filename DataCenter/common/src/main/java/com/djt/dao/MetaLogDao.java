package com.djt.dao;

import com.djt.pojo.MetaLog;
import com.djt.enums.MetaLogEnum;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MetaLogDao<E, T> extends AbstractDMDAO<E, T> {
  private Map<String,MetaLog> map = new HashMap<String, MetaLog>();

  @Override
  public void parseDMObj(File file) throws IOException {
    BufferedReader in = null;
    try {
      in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
      String line = null;
      while ((line = in.readLine()) != null) {
        String[] lineFields = line.split(",");

        MetaLog metaLog = new MetaLog();
        metaLog.setLogId(Integer.parseInt(lineFields[MetaLogEnum.LOG_ID.ordinal()]));
        metaLog.setLogName(lineFields[MetaLogEnum.LOG_NAME.ordinal()]);
        metaLog.setLogUrl(lineFields[MetaLogEnum.LOG_URL.ordinal()]);
        metaLog.setHdfsBaseOutput(lineFields[MetaLogEnum.HDFS_BASE_OUTPUT.ordinal()]);

        map.put(metaLog.getLogUrl(), metaLog);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw (IOException) e;
    }finally {
      in.close();
    }
  }

  @Override
  public T getDMOjb(E param) throws Exception {
    String url = (String) param;
    return (T) map.get(url);
  }
}
