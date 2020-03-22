package com.ericsson.lte.session.entity;

import java.io.Serializable;

public class RequestBean implements Serializable {

    private static final long serialVersionUID = 1L;
    //可以放任何类型的数据
    private String deliverySessionId;
    private String actionType;
    private String sessionExpireTime;
    private String url;
    private String startTime;
    private String stopTime;
    private int connectionState;

    public RequestBean(String deliverySessionId, String actionType, String sessionExpireTime, String url, String startTime, String stopTime, int connectionState) {
        this.deliverySessionId = deliverySessionId;
        this.actionType = actionType;
        this.sessionExpireTime = sessionExpireTime;
        this.url = url;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.connectionState = connectionState;
    }

    public int getConnectionState() {
        return connectionState;
    }

    public String getUrl() {
        return url;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public String getDeliverySessionId() {
        return deliverySessionId;
    }

    public void setDeliverySessionId(String deliverySessionId) {
        this.deliverySessionId = deliverySessionId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getSessionExpireTime() {
        return sessionExpireTime;
    }

    public void setSessionExpireTime(String sessionExpireTime) {
        this.sessionExpireTime = sessionExpireTime;
    }
}
