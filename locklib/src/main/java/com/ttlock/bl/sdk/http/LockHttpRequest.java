package com.ttlock.bl.sdk.http;

import android.util.Log;

import com.google.gson.Gson;
import com.ttlock.bl.sdk.bean.LockKey;
import com.ttlock.bl.sdk.bean.LockUser;
import com.ttlock.bl.sdk.bean.SyncData;
import com.ttlock.bl.sdk.config.LockConfig;
import com.ttlock.bl.sdk.util.DigitUtil;

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
        Observable<Response<ResponseBody>> initLock = mRequestService.initLock(LockConfig.CLIENT_ID,
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
