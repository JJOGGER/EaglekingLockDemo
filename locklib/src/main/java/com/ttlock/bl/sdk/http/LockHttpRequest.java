package com.ttlock.bl.sdk.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.bean.KeyResult;
import com.ttlock.bl.sdk.bean.LockKey;
import com.ttlock.bl.sdk.bean.LockKeyboardPwd;
import com.ttlock.bl.sdk.bean.LockUser;
import com.ttlock.bl.sdk.bean.SyncData;
import com.ttlock.bl.sdk.config.LockConfig;
import com.ttlock.bl.sdk.util.DigitUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
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
    public void login(String account, String pwd, OnHttpRequestCallback listener) {

    }

    @Override
    public void auth(String userName, String pwd, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> auth = mRequestService.auth(LockConfig.CLIENT_ID, LockConfig.CLIENT_SECRET, "password", userName, DigitUtil.getMD5(pwd), LockConfig.REDIRECT_URI);
        enqueue(auth, LockUser.class, listener);
    }

    @Override
    public void syncData(long lastUpdateDate, String accessToken, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> syncData = mRequestService.syncData(LockConfig.CLIENT_ID, accessToken, lastUpdateDate, System.currentTimeMillis());
        enqueue(syncData, SyncData.class, listener);
    }

    @Override
    public void initLock(LockKey lockKey, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> initLock = mRequestService.initLock(
                LockConfig.CLIENT_ID,
                lockKey.getAccessToken(),
                lockKey.getLockName(),
                lockKey.getLockAlias(),
                lockKey.getLockMac(),
                lockKey.getLockKey(),
                lockKey.getLockFlagPos(),
                lockKey.getAesKeystr(),
                mGson.toJson(lockKey.getLockVersion()),
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
    public void sendKey(String accessToken, int lockId, String receiverUsername, long startDate, long endDate, String remarks, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> sendKey = mRequestService.sendKey(LockConfig.CLIENT_ID,
                accessToken, lockId, receiverUsername, startDate, endDate, remarks, date);
        enqueue(sendKey, listener);
    }

    @Override
    public void getPwd(String accessToken, int lockId, int keyboardPwdVersion, int keyboardPwdType, long startTime, long endDate, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> getPwd = mRequestService.getPwd(LockConfig.CLIENT_ID, accessToken, lockId, keyboardPwdVersion, keyboardPwdType, startTime, endDate, date);
        enqueue(getPwd, LockKeyboardPwd.class, listener);
    }

    @Override
    public void customPwd(String accessToken, int lockId, String keyboardPwd, long startDate, long endDate, int addType, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> customPwd = mRequestService.customPwd(LockConfig.CLIENT_ID, accessToken, lockId, keyboardPwd, startDate, endDate, addType, date);
        enqueue(customPwd, LockKeyboardPwd.class, listener);
    }

    @Override
    public void getLockKeys(String accessToken, int lockId, int pageNo, int pageSize, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> getLockKeys = mRequestService.getLockKeys(LockConfig.CLIENT_ID, accessToken, lockId, pageNo, pageSize, System.currentTimeMillis());
        enqueue(getLockKeys, KeyResult.class, listener);
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
    private <T> void enqueues(final Observable<Response<ResponseBody>> call, final OnHttpRequestCallback<T>
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
                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray list = jsonObject.getJSONArray("list");
                                Type type = new TypeToken<List<T>>() {
                                }.getType();
                                listener.onSuccess((T) mGson.fromJson(list.toString(), type));
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
}
