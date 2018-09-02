package com.bigdata.model;

public class NginxLogModel {
    private String ip;
    private String requestTime;
    private String url;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "NginxLogModel{" +
                "ip='" + ip + '\'' +
                ", requestTime='" + requestTime + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
