package com.ericsson.lte.session.entity;

import java.io.Serializable;

public class ResponseResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private String message;
    private int code;//0为成功
    private ResponseBean responseBean;

    public ResponseBean getResponseBean() {
        return responseBean;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public ResponseResult code(int code) {
        this.code = code;
        return this;
    }

    public ResponseResult message(String message) {
        this.message = message;
        return this;
    }

    public ResponseResult responseBean(ResponseBean responseBean) {
        this.responseBean = responseBean;
        return this;
    }
}
