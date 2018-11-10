package cn.jcyh.eaglekinglockdemo.http;

/**
 * Created by jogger on 2018/1/10.
 */

public interface OnHttpRequestListener<T> {
    void onFailure(int errorCode, String desc);

    void onSuccess(T t);
}
