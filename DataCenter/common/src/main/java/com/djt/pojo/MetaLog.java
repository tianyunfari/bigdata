package com.djt.pojo;

public class MetaLog {
  private int logId;
  private String logName;
  private String logUrl;
  private String hdfsBaseOutput;

  public int getLogId() {
    return logId;
  }

  public void setLogId(int logId) {
    this.logId = logId;
  }

  public String getLogName() {
    return logName;
  }

  public void setLogName(String logName) {
    this.logName = logName;
  }

  public String getLogUrl() {
    return logUrl;
  }

  public void setLogUrl(String logUrl) {
    this.logUrl = logUrl;
  }

  public String getHdfsBaseOutput() {
    return hdfsBaseOutput;
  }

  public void setHdfsBaseOutput(String hdfsBaseOutput) {
    this.hdfsBaseOutput = hdfsBaseOutput;
  }
}
