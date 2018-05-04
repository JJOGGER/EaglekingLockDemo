package com.ttlock.bl.sdk.http;

import android.content.Context;

import com.ttlock.bl.sdk.bean.LockKey;
import com.ttlock.bl.sdk.bean.LockUser;
import com.ttlock.bl.sdk.bean.SyncData;

/**
 * Created by jogger on 2018/1/10.网络请求
 */
@SuppressWarnings("unchecked")
public class LockHttpAction extends BaseHttpAction {
    private static LockHttpAction sHttpAction;

    private LockHttpAction(Context context) {
        super(context);
    }

    @Override
    public IHttpRequest getHttpRequest(RequestService requestService) {
        return new LockHttpRequest(requestService);
    }

    @Override
    public String getBaseUrl() {
        return RequestService.BASE_URL;
    }

    public static LockHttpAction getHttpAction(Context context) {
        if (sHttpAction == null) {
            synchronized (LockHttpAction.class) {
                if (sHttpAction == null) {
                    sHttpAction = new LockHttpAction(context);
                }
            }
        }
        return sHttpAction;
    }

    public void auth(String userName, String pwd, final OnHttpRequestCallback<LockUser> listener) {
        mHttpRequest.auth(userName, pwd, listener);
    }

    public void login(String accoount, String pwd, final OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.login(accoount, pwd, listener);
    }

    public void syncData(long lastUpdateDate, String accessToken, final OnHttpRequestCallback<SyncData> listener) {
        mHttpRequest.syncData(lastUpdateDate, accessToken, listener);
    }

    public void initLock(LockKey lockKey, final OnHttpRequestCallback<Boolean> listener){
        mHttpRequest.initLock(lockKey,listener);
    }
}
