package com.ericsson.lte.session.entity;

import java.io.Serializable;

public class ResponseBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int connectionState;//1 断开连接
    private String startTime;
    private String stopTime;
    private String url;

    public ResponseBean(int connectionState, String startTime,String stopTime,String url) {
        this.connectionState = connectionState;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.url = url;
    }

    public int getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(int connectionState) {
        this.connectionState = connectionState;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
