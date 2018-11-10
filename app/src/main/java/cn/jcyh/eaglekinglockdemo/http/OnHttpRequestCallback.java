package cn.jcyh.eaglekinglockdemo.http;

/**
 * Created by jogger on 2018/1/10.
 */

public interface OnHttpRequestCallback<T> {
    void onFailure(int errorCode, String desc);

    void onSuccess(T t);
}
