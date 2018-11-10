package cn.jcyh.eaglekinglockdemo.entity;

/**
 * Created by jogger on 2018/1/18.
 * 网络解析基类
 */

public class HttpResult<T> {
    private T data;
    private int code;
    private String desc;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "data=" + data +
                ", code=" + code +
                '}';
    }
}
