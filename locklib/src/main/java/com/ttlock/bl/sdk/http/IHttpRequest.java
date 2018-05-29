package com.ttlock.bl.sdk.http;


import com.ttlock.bl.sdk.bean.LockKey;

/**
 * Created by jogger on 2018/4/14.请求接口
 */

interface IHttpRequest<T> {
    void regist(String clientId, String clientSecret, String userName, String pwd, long date, OnHttpRequestCallback<T> listener);

    void login(String account, String pwd, final OnHttpRequestCallback<T> listener);

    void auth(String clientId, String clientScret, String userName, String grantType, String pwd, String redict_uri, final OnHttpRequestCallback<T> listener);

    void syncData(String clientId, long lastUpdateDate, String accessToken, final OnHttpRequestCallback<T> listener);

    void initLock(String clientId, LockKey lockKey, final OnHttpRequestCallback<T> listener);

    void sendKey(String clientId, String accessToken, int lockId, String receiverUsername, long startDate,
                 long endDate, String remarks, long date, final OnHttpRequestCallback<T> listener);

    void getPwd(String clientId, String accessToken, int lockId, int keyboardPwdVersion, int keyboardPwdType, long startTime, long endDate, long date, final OnHttpRequestCallback<T> listener);

    void customPwd(String clientId, String accessToken, int lockId, String keyboardPwd, long startDate, long endDate, int addType, long date, OnHttpRequestCallback<T> listener);

    void getLockKeys(String clientId, String accessToken, int lockId, int pageNo, int pageSize, long date, OnHttpRequestCallback<T> listener);

    void delKey(String clientId, String accessToken, int keyId, long date, OnHttpRequestCallback<T> listener);

    void resetKey(String clientId, String accessToken, int lockId, long date, OnHttpRequestCallback<T> listener);

    void delAllKeys(String clientId, String accessToken, int lockId, long date, OnHttpRequestCallback<T> listener);

    void getPwdsByLock(String clientId, String accessToken, int lockId, int pageNo, int pageSize, long date, OnHttpRequestCallback<T> listener);

    void resetPwd(String clientId, String accessToken, int lockId, String pwdInfo, long timestamp, long date, OnHttpRequestCallback<T> listener);

    void freezeKey(String clientId, String accessToken, int keyId, long date, OnHttpRequestCallback<T> listener);

    void unFreezeKey(String clientId, String accessToken, int keyId, long date, OnHttpRequestCallback<T> listener);

    void authKeyUser(String clientId, String accessToken, int lockId, int keyId, long date, OnHttpRequestCallback<Boolean> listener);

    void unAuthKeyUser(String clientId, String accessToken, int lockId, int keyId, long date, OnHttpRequestCallback<Boolean> listener);

    void lockRename(String clientId, String accessToken, int lockId, String lockAlias, long date, OnHttpRequestCallback<T> listener);

    void changeAdminKeyboardPwd(String clientId, String accessToken, int lockId, String password, long date, OnHttpRequestCallback<T> listener);

    void getICs(String clientId, String accessToken, int lockId, int pageNo, int pageSize, long date, OnHttpRequestCallback<T> listener);

    void deleteIC(String clientId, String accessToken, int lockId, int cardId, int deleteType, long date, OnHttpRequestCallback<T> listener);

    void addIC(String clientId, String accessToken, int lockId, String cardNumber, long startDate, long endDate, int addType, long date, OnHttpRequestCallback<T> listener);

    void clearICs(String clientId, String accessToken, int lockId, long date, OnHttpRequestCallback<T> listener);

    void getFingerprints(String clientId, String accessToken, int lockId, int pageNo, int pageSize, long date, OnHttpRequestCallback<T> listener);

    void deleteFingerprint(String clientId, String accessToken, int lockId, int fingerprintId, int deleteType, long date, OnHttpRequestCallback<T> listener);

    void addFingerprint(String clientId, String accessToken, int lockId, String fingerprintNumber, long startDate, long endDate, int addType, long date, OnHttpRequestCallback<T> listener);

    void clearFingerprints(String clientId, String accessToken, int lockId, long date, OnHttpRequestCallback<T> listener);

    void uploadLockRecords(String clientId, String accessToken, int lockId, String records, long date, OnHttpRequestCallback<T> listener);

    void getLockRecords(String clientId, String accessToken, int lockId, long startDate, long endDate, int pageNo, int pageSize, long date, OnHttpRequestCallback<T> listener);

}
