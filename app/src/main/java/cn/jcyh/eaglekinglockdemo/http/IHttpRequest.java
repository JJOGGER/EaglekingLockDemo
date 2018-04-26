package cn.jcyh.eaglekinglockdemo.http;


/**
 * Created by jogger on 2018/4/14.请求接口
 */

interface IHttpRequest<T> {

    void login(String account, String pwd, final OnHttpRequestCallback<T> listener);

    void auth(String userName, String pwd, final OnHttpRequestCallback<T> lisstener);

}