package com.ttlock.bl.sdk.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ttlock.bl.sdk.bean.ICard;
import com.ttlock.bl.sdk.bean.LockFingerprint;
import com.ttlock.bl.sdk.bean.LockKey;
import com.ttlock.bl.sdk.bean.LockKeyboardPwd;
import com.ttlock.bl.sdk.bean.LockPwdRecord;
import com.ttlock.bl.sdk.bean.LockRecord;
import com.ttlock.bl.sdk.bean.LockUser;
import com.ttlock.bl.sdk.bean.SyncData;
import com.ttlock.bl.sdk.util.DigitUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by jogger on 2018/4/14.网络请求处理
 */
@SuppressWarnings("unchecked")
class LockHttpRequest implements IHttpRequest {
    private static final String TAG = "LockHttpRequest";
    private final RequestService mRequestService;
    private Gson mGson;

    LockHttpRequest(RequestService requestService) {
        mRequestService = requestService;
        mGson = new Gson();
    }

    @Override
    public void regist(String clientId, String clientSecret, String userName, String pwd, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> regist = mRequestService.regist(clientId, clientSecret, userName, pwd, date);
        enqueue(regist, listener);
    }

    @Override
    public void login(String account, String pwd, OnHttpRequestCallback listener) {

    }

    @Override
    public void auth(String clientId, String clientScret, String userName, String grantType, String pwd, String redict_uri, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> auth = mRequestService.auth(clientId, clientScret, grantType, userName, DigitUtil.getMD5(pwd), redict_uri);
        enqueue(auth, LockUser.class, listener);
    }

    @Override
    public void syncData(String clientId, long lastUpdateDate, String accessToken, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> syncData = mRequestService.syncData(clientId, accessToken, lastUpdateDate, System.currentTimeMillis());
        enqueue(syncData, SyncData.class, listener);
    }

    @Override
    public void initLock(String clientId, LockKey lockKey, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> initLock = mRequestService.initLock(
                clientId,
                lockKey.getAccessToken(),
                lockKey.getLockName(),
                lockKey.getLockAlias(),
                lockKey.getLockMac(),
                lockKey.getLockKey(),
                lockKey.getLockFlagPos(),
                lockKey.getAesKeystr(),
                lockKey.getLockVersion(),
                lockKey.getAdminPwd(),
                lockKey.getNoKeyPwd(),
                lockKey.getDeletePwd(),
                lockKey.getPwdInfo(),
                lockKey.getTimestamp(),
                lockKey.getSpecialValue(),
                lockKey.getTimezoneRawOffset(),
                lockKey.getModelNumber(),
                lockKey.getHardwareRevision(),
                lockKey.getFirmwareRevision(),
                System.currentTimeMillis());
        enqueue(initLock, listener);
    }

    @Override
    public void sendKey(String clientId, String accessToken, int lockId, String receiverUsername, long startDate, long endDate, String remarks, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> sendKey = mRequestService.sendKey(clientId,
                accessToken, lockId, receiverUsername, startDate, endDate, remarks, date);
        enqueue(sendKey, listener);
    }

    @Override
    public void getPwd(String clientId, String accessToken, int lockId, int keyboardPwdVersion, int keyboardPwdType, long startTime, long endDate, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> getPwd = mRequestService.getPwd(clientId, accessToken, lockId, keyboardPwdVersion, keyboardPwdType, startTime, endDate, date);
        enqueue(getPwd, LockKeyboardPwd.class, listener);
    }

    @Override
    public void customPwd(String clientId, String accessToken, int lockId, String keyboardPwd, long startDate, long endDate, int addType, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> customPwd = mRequestService.customPwd(clientId, accessToken, lockId, keyboardPwd, startDate, endDate, addType, date);
        enqueue(customPwd, LockKeyboardPwd.class, listener);
    }

    @Override
    public void getLockKeys(String clientId, String accessToken, int lockId, int pageNo, int pageSize, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> getLockKeys = mRequestService.getLockKeys(clientId, accessToken, lockId, pageNo, pageSize, System.currentTimeMillis());
        enqueues(getLockKeys, LockKey.class, listener);
    }

    @Override
    public void delKey(String clientId, String accessToken, int keyId, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> delKey = mRequestService.delKey(clientId, accessToken, keyId, date);
        enqueue(delKey, listener);
    }

    @Override
    public void resetKey(String clientId, String accessToken, int keyId, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> resetKey = mRequestService.resetKey(clientId, accessToken, keyId, date);
        enqueue(resetKey, listener);
    }

    @Override
    public void delAllKeys(String clientId, String accessToken, int lockId, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> delAllKeys = mRequestService.delAllKeys(clientId, accessToken, lockId, date);
        enqueue(delAllKeys, listener);
    }

    @Override
    public void getPwdsByLock(String clientId, String accessToken, int lockId, int pageNo, int pageSize, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> getPwdsByLock = mRequestService.getPwdsByLock(clientId, accessToken, lockId, pageNo, pageSize, date);
        enqueues(getPwdsByLock, LockPwdRecord.class, listener);
    }

    @Override
    public void resetPwd(String clientId, String accessToken, int lockId, String pwdInfo, long timestamp, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> resetPwd = mRequestService.resetKeyboardPwd(clientId, accessToken, lockId, pwdInfo, timestamp, date);
        enqueue(resetPwd, listener);
    }

    @Override
    public void freezeKey(String clientId, String accessToken, int keyId, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> freezeKey = mRequestService.freezeKey(clientId, accessToken, keyId, date);
        enqueue(freezeKey, listener);
    }

    @Override
    public void unFreezeKey(String clientId, String accessToken, int keyId, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> freezeKey = mRequestService.unFreezeKey(clientId, accessToken, keyId, date);
        enqueue(freezeKey, listener);
    }

    @Override
    public void authKeyUser(String clientId, String accessToken, int lockId, int keyId, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> authKeyUser = mRequestService.authKeyUser(clientId, accessToken, lockId, keyId, date);
        enqueue(authKeyUser, listener);
    }

    @Override
    public void unAuthKeyUser(String clientId, String accessToken, int lockId, int keyId, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> unAuthKeyUser = mRequestService.unAuthKeyUser(clientId, accessToken, lockId, keyId, date);
        enqueue(unAuthKeyUser, listener);
    }

    @Override
    public void lockRename(String clientId, String accessToken, int lockId, String lockAlias, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> lockRename = mRequestService.lockRename(clientId, accessToken, lockId, lockAlias, date);
        enqueue(lockRename, listener);
    }

    @Override
    public void changeAdminKeyboardPwd(String clientId, String accessToken, int lockId, String password, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> changeAdminKeyboardPwd = mRequestService.changeAdminKeyboardPwd(clientId, accessToken, lockId, password, date);
        enqueue(changeAdminKeyboardPwd, listener);
    }

    @Override
    public void getICs(String clientId, String accessToken, int lockId, int pageNo, int pageSize, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> getICs = mRequestService.getICs(clientId, accessToken, lockId, pageNo, pageSize, date);
        enqueues(getICs, ICard.class, listener);
    }

    @Override
    public void deleteIC(String clientId, String accessToken, int lockId, int cardId, int deleteType, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> deleteIC = mRequestService.deleteIC(clientId, accessToken, lockId, cardId, deleteType, date);
        enqueue(deleteIC, listener);
    }

    @Override
    public void addIC(String clientId, String accessToken, int lockId, String cardNumber, long startDate, long endDate, int addType, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> addIC = mRequestService.addIC(clientId, accessToken, lockId, cardNumber, startDate, endDate, addType, date);
        enqueue(addIC, listener);
    }

    @Override
    public void clearICs(String clientId, String accessToken, int lockId, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> clearICs = mRequestService.clearICs(clientId, accessToken, lockId, date);
        enqueue(clearICs, listener);
    }

    @Override
    public void getFingerprints(String clientId, String accessToken, int lockId, int pageNo, int pageSize, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> getFingerprints = mRequestService.getFingerprints(clientId, accessToken, lockId, pageNo, pageSize, date);
        enqueues(getFingerprints, LockFingerprint.class, listener);
    }

    @Override
    public void deleteFingerprint(String clientId, String accessToken, int lockId, int fingerprintId, int deleteType, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> deleteFingerprint = mRequestService.deleteFingerprint(clientId, accessToken, lockId, fingerprintId, deleteType, date);
        enqueue(deleteFingerprint, listener);
    }

    @Override
    public void addFingerprint(String clientId, String accessToken, int lockId, String fingerprintNumber, long startDate, long endDate, int addType, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> addFingerprint = mRequestService.addFingerprint(clientId, accessToken, lockId, fingerprintNumber, startDate, endDate, addType, date);
        enqueue(addFingerprint, listener);
    }

    @Override
    public void clearFingerprints(String clientId, String accessToken, int lockId, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> clearFingerprints = mRequestService.clearFingerprints(clientId, accessToken, lockId, date);
        enqueue(clearFingerprints, listener);
    }

    @Override
    public void uploadLockRecords(String clientId, String accessToken, int lockId, String records, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> uploadLockRecords = mRequestService.uploadLockRecords(clientId, accessToken, lockId, records, date);
        enqueue(uploadLockRecords, listener);
    }

    @Override
    public void getLockRecords(String clientId, String accessToken, int lockId, long startDate, long endDate, int pageNo, int pageSize, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> getLockRecords = mRequestService.getLockRecords(clientId, accessToken, lockId, startDate, endDate, pageNo, pageSize, date);
        enqueues(getLockRecords, LockRecord.class, listener);
    }

    private <T> void enqueue(final Observable<Response<ResponseBody>> call, final Class<T> clazz, final OnHttpRequestCallback<T>
            listener) {
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Consumer<Response<ResponseBody>>() {
                    @Override
                    public void accept(Response<ResponseBody> response) throws Exception {
                        String result = response.body().string();
                        Log.e(TAG, "---------result:" + result);
                        if (listener != null) {
                            HttpError httpError = mGson.fromJson(result, HttpError.class);
                            if (httpError.getErrcode() != 0) {
                                listener.onFailure(httpError.getErrcode());
                            } else {
                                listener.onSuccess(mGson.fromJson(result, clazz));
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "-------onError:" + throwable + mRequestService);
                        if (listener != null)
                            listener.onFailure(-1);
                    }
                });
    }

    //获取列表
    private <T> void enqueues(final Observable<Response<ResponseBody>> call, final Class<T> clazz, final OnHttpRequestCallback<T>
            listener) {
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Consumer<Response<ResponseBody>>() {
                    @Override
                    public void accept(Response<ResponseBody> response) throws Exception {
                        String result = response.body().string();
                        Log.e(TAG, "---------result:" + result);
                        if (listener != null) {
                            HttpError httpError = mGson.fromJson(result, HttpError.class);
                            if (httpError.getErrcode() != 0) {
                                listener.onFailure(httpError.getErrcode());
                            } else {
                                HttpResult httpResult = new HttpResult();
//                                HttpResult httpResult = mGson.fromJson(result, HttpResult.class);
                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray list_array = jsonObject.getJSONArray("list");

                                ArrayList<T> list = new ArrayList<>();
                                JsonArray array = new JsonParser().parse(list_array.toString()).getAsJsonArray();
                                for (final JsonElement elem : array) {
                                    list.add(mGson.fromJson(elem, clazz));
                                }
                                httpResult.setList(list);
                                listener.onSuccess((T) httpResult);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "-------onError:" + throwable + mRequestService);
                        if (listener != null)
                            listener.onFailure(-1);
                    }
                });
    }

    private void enqueue(final Observable<Response<ResponseBody>> call, final OnHttpRequestCallback<Boolean>
            listener) {
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Consumer<Response<ResponseBody>>() {
                    @Override
                    public void accept(Response<ResponseBody> response) throws Exception {
                        String result = response.body().string();
                        Log.e(TAG, "----------result:" + result);
                        if (listener != null) {
                            HttpError httpError = mGson.fromJson(result, HttpError.class);
                            if (httpError.getErrcode() != 0) {
                                listener.onFailure(httpError.getErrcode());
                            } else {
                                listener.onSuccess(true);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "----------onError:" + throwable);
                        if (listener != null)
                            listener.onFailure(-1);
                    }
                });
    }
//    private <T> void enqueue3(final Observable<Response<ResponseBody>> call, final Class<T> clazz, final OnHttpRequestCallback<T>
//            listener) {
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
//                call.subscribeOn(Schedulers.io());
//                call.observeOn(AndroidSchedulers.mainThread());
//                call.subscribe(new Consumer<Response<ResponseBody>>() {
//                    @Override
//                    public void accept(Response<ResponseBody> response) throws Exception {
//                        String result = response.body().string();
//                        Timber.e("----------result:" + result);
//                        Class<T> type;
////                        type = (Class<T>) ((ParameterizedType) clazz.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//                        if (listener != null) {
//                            Timber.e("--------result:" + result);
//                            listener.onSuccess(mGson.fromJson(result, clazz));
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Timber.e("----------onError3:" + throwable);
//                        if (listener != null)
//                            listener.onFailure(-1);
//                    }
//                });
//            }
////        }).start();
////
////    }

    private <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz));
    }
}
