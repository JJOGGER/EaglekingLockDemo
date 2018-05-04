package com.ttlock.bl.sdk.http;


import com.ttlock.bl.sdk.bean.LockKey;

/**
 * Created by jogger on 2018/4/14.请求接口
 */

interface IHttpRequest<T> {

    void login(String account, String pwd, final OnHttpRequestCallback<T> listener);

    void auth(String userName, String pwd, final OnHttpRequestCallback<T> lisstener);

    void syncData(long lastUpdateDate, String accessToken, final OnHttpRequestCallback<T> listener);

    void initLock(LockKey lockKey, final OnHttpRequestCallback<T> listener);
}
