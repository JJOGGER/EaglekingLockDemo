package com.ttlock.bl.sdk.http;

import android.content.Context;

import com.ttlock.bl.sdk.bean.LockKey;
import com.ttlock.bl.sdk.bean.LockKeyboardPwd;
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

    public void initLock(LockKey lockKey, final OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.initLock(lockKey, listener);
    }

    /**
     * @param accessToken      访问令牌
     * @param lockId           锁ID
     * @param receiverUsername 接收方用户名
     * @param startDate        有效期开始时间
     * @param endDate          有效期结束时间（1为单次）
     * @param remarks          备注
     */
    public void sendKey(String accessToken, int lockId, String receiverUsername, long startDate, long endDate, String remarks, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.sendKey(accessToken, lockId, receiverUsername, startDate, endDate, remarks, System.currentTimeMillis(),listener);
    }

    /**
     * 生成密码
     *
     * @param lockId          锁ID
     *                        keyboardPwdVersion 键盘密码版本, 三代锁的密码版本为4
     * @param keyboardPwdType 键盘密码类型
     * @param startDate       有效期开始时间
     * @param endDate         有效期结束时间
     */
    public void getPwd(String accessToken, int lockId, int keyboardPwdType, long startDate, long endDate, OnHttpRequestCallback<LockKeyboardPwd> listener) {
        mHttpRequest.getPwd(accessToken, lockId, 4, keyboardPwdType, startDate, endDate, System.currentTimeMillis(), listener);
    }
}
