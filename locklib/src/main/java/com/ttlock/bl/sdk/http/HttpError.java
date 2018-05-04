package com.ttlock.bl.sdk.http;

/**
 * Created by jogger on 2018/5/4.
 */

public class HttpError {
    private int errcode;
    private String errmsg;
    private String description;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "HttpError{" +
                "errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
