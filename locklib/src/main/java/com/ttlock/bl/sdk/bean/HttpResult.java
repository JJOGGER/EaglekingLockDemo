package com.ttlock.bl.sdk.bean;

/**
 * Created by jogger on 2018/1/18.
 * 网络解析基类
 */

public class HttpResult<T,M> {
    private T data;
    private M code;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public M getCode() {
        return code;
    }

    public void setCode(M code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "data=" + data +
                ", code=" + code +
                '}';
    }
}
