package com.djt.dao;

import com.djt.pojo.MetaLogField;
import com.djt.enums.MetaLogFieldEnum;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaLogFieldDao<E, T> extends AbstractDMDAO<E, T> {
  private Map<Integer,List<MetaLogField>> map = new HashMap();

  @Override
  public void parseDMObj(File file) throws IOException {
    BufferedReader in = null;
    try {
      in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
      String line;
      while ((line = in.readLine()) != null) {
        String[] lineFields = line.split(",");

        Integer logId = Integer.parseInt(lineFields[MetaLogFieldEnum.LOG_ID.ordinal()]);

        if (map.get(logId) == null) {
          map.put(logId, new ArrayList<MetaLogField>());
        }

        MetaLogField metaLogField = new MetaLogField();
        metaLogField.setLogId(logId);
        metaLogField.setFieldName(lineFields[MetaLogFieldEnum.FIELD_NAME.ordinal()]);
        metaLogField.setFieldDesc(lineFields[MetaLogFieldEnum.FIELD_DESC.ordinal()]);
        metaLogField.setSeq(Integer.parseInt(lineFields[MetaLogFieldEnum.SEQ.ordinal()]));

        map.get(logId).add(metaLogField);
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
    Integer logId = (Integer) param;
    return (T) map.get(logId);
  }
}
