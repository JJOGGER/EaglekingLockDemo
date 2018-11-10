package cn.jcyh.eaglekinglockdemo.http;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import cn.jcyh.eaglekinglockdemo.entity.AddKeyboardPwdData;
import cn.jcyh.eaglekinglockdemo.entity.AuthKeyData;
import cn.jcyh.eaglekinglockdemo.entity.HttpResult;
import cn.jcyh.eaglekinglockdemo.entity.InitRequestData;
import cn.jcyh.eaglekinglockdemo.entity.KeyControlData;
import cn.jcyh.eaglekinglockdemo.entity.KeyboardPwdGetData;
import cn.jcyh.eaglekinglockdemo.entity.LockKey;
import cn.jcyh.eaglekinglockdemo.entity.LockKeyboardPwd;
import cn.jcyh.eaglekinglockdemo.entity.LockPwdRecord;
import cn.jcyh.eaglekinglockdemo.entity.LoginData;
import cn.jcyh.eaglekinglockdemo.entity.RegistRequestData;
import cn.jcyh.eaglekinglockdemo.entity.SendKeyData;
import cn.jcyh.locklib.entity.FR;
import cn.jcyh.locklib.entity.ICCard;
import cn.jcyh.locklib.entity.LockRecord;
import cn.jcyh.locklib.util.DigitUtil;
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
@SuppressLint("CheckResult")
class LockLockHttpRequest implements ILockHttpRequest {
    private static final String TAG = "LockLockHttpRequest";
    private final LockRequestService mLockRequestService;
    private Gson mGson;

    LockLockHttpRequest(LockRequestService lockRequestService) {
        mLockRequestService = lockRequestService;
        mGson = new Gson();
    }

    @Override
    public void regist(String clientId, String clientSecret, String userName, String pwd, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> regist = mLockRequestService.regist(clientId, clientSecret, userName, pwd, date);
        enqueueVoid(regist, listener);
    }

    @Override
    public void regist(String userName, String pwd, OnHttpRequestCallback listener) {
        RegistRequestData registRequestData = new RegistRequestData(userName, pwd);
        Observable<Response<ResponseBody>> regist = mLockRequestService.regist(registRequestData);
        enqueueVoid(regist, listener);
    }

    @Override
    public void login(String account, String pwd, OnHttpRequestCallback listener) {

    }

    @Override
    public void auth(String clientId, String clientScret, String userName, String grantType, String pwd, String redict_uri, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> auth = mLockRequestService.auth(clientId, clientScret, grantType, userName, DigitUtil.getMD5(pwd), redict_uri);
//        enqueue(auth, LockUser.class, listener);
    }

    @Override
    public void auth(String userName, String pwd, OnHttpRequestCallback listener) {
        RegistRequestData loginRequestData = new RegistRequestData(userName, DigitUtil.getMD5(pwd));
        Observable<HttpResult<LoginData>> auth = mLockRequestService.auth(loginRequestData);
        enqueue(auth, listener);
    }

    @Override
    public void syncData(String clientId, long lastUpdateDate, String accessToken, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> syncData = mLockRequestService.syncData(clientId, accessToken, lastUpdateDate, System.currentTimeMillis());
//        enqueue(syncData, SyncData.class, listener);
    }

    @Override
    public void initLock(InitRequestData initRequestData, OnHttpRequestCallback listener) {

    }

    @Override
    public void initLock(String clientId, LockKey lockKey, OnHttpRequestCallback listener) {
        InitRequestData initRequestData = new InitRequestData(clientId,
                lockKey.getAccessToken(),
                lockKey.getLockName(),
                lockKey.getLockMac(),
                lockKey.getLockKey(),
                lockKey.getLockFlagPos(),
                lockKey.getAesKeystr(),
                lockKey.getLockVersionEntity(),
                lockKey.getAdminPwd(),
                lockKey.getNoKeyPwd(),
                lockKey.getDeletePwd(),
                lockKey.getPwdInfo(),
                lockKey.getTimestamp(),
                lockKey.getSpecialValue(),
                lockKey.getElectricQuantity(),
                lockKey.getTimezoneRawOffset(),
                lockKey.getModelNumber(),
                lockKey.getHardwareRevision(),
                lockKey.getFirmwareRevision(),
                System.currentTimeMillis()
        );
        Observable<Response<ResponseBody>> initLock = mLockRequestService.initLock(
                initRequestData);
        enqueueVoid(initLock, listener);
    }

    @Override
    public void sendKey(String clientId, String accessToken, int lockId, String receiverUsername, long startDate, long endDate, String remarks, long date, OnHttpRequestCallback listener) {
        SendKeyData sendKeyData = new SendKeyData(clientId, accessToken, lockId, receiverUsername, startDate, endDate, remarks, 0, date);
        Observable<Response<ResponseBody>> sendKey = mLockRequestService.sendKey(sendKeyData);
        enqueueVoid(sendKey, listener);
    }

    @Override
    public void getPwd(String clientId, String accessToken, int lockId, int keyboardPwdVersion, int keyboardPwdType, long startTime, long endDate, long date, OnHttpRequestCallback listener) {
        KeyboardPwdGetData keyboardPwdGetData = new KeyboardPwdGetData(clientId, accessToken, lockId, keyboardPwdVersion, keyboardPwdType, startTime, endDate, date);
        Observable<Response<ResponseBody>> getPwd = mLockRequestService.getPwd(keyboardPwdGetData);
        enqueue(getPwd, LockKeyboardPwd.class, listener);
    }

    @Override
    public void customPwd(String clientId, String accessToken, int lockId, String keyboardPwd, long startDate, long endDate, int addType, long date, OnHttpRequestCallback listener) {
        AddKeyboardPwdData addKeyboardPwdData = new AddKeyboardPwdData(clientId, accessToken, lockId, keyboardPwd, startDate, endDate, addType, date);
        Observable<Response<ResponseBody>> customPwd = mLockRequestService.customPwd(addKeyboardPwdData);
        enqueue(customPwd, LockKeyboardPwd.class, listener);
    }

    @Override
    public void getLockKeys(String clientId, String accessToken, int lockId, int pageNo, int pageSize, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> getLockKeys = mLockRequestService.getLockKeys(clientId, accessToken, lockId, pageNo, pageSize, System.currentTimeMillis());
        enqueues(getLockKeys, LockKey.class, listener);
    }

    @Override
    public void delKey(String clientId, String accessToken, int keyId, long date, OnHttpRequestCallback listener) {
        KeyControlData keyControlData = new KeyControlData(clientId, accessToken, keyId, date);
        Observable<Response<ResponseBody>> delKey = mLockRequestService.delKey(keyControlData);
        enqueueVoid(delKey, listener);
    }

    @Override
    public void resetKey(String clientId, String accessToken, int keyId, long date, OnHttpRequestCallback listener) {
        KeyControlData keyControlData = new KeyControlData(clientId, accessToken, keyId, date);
        Observable<Response<ResponseBody>> resetKey = mLockRequestService.resetKey(keyControlData);
        enqueueVoid(resetKey, listener);
    }

    @Override
    public void delAllKeys(String clientId, String accessToken, int lockId, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> delAllKeys = mLockRequestService.delAllKeys(clientId, accessToken, lockId, date);
        enqueueVoid(delAllKeys, listener);
    }

    @Override
    public void getPwdsByLock(String clientId, String accessToken, int lockId, int pageNo, int pageSize, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> getPwdsByLock = mLockRequestService.getPwdsByLock(clientId, accessToken, lockId, pageNo, pageSize, date);
        enqueues(getPwdsByLock, LockPwdRecord.class, listener);
    }

    @Override
    public void resetPwd(String clientId, String accessToken, int lockId, String pwdInfo, long timestamp, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> resetPwd = mLockRequestService.resetKeyboardPwd(clientId, accessToken, lockId, pwdInfo, timestamp, date);
        enqueueVoid(resetPwd, listener);
    }

    @Override
    public void freezeKey(String clientId, String accessToken, int keyId, long date, OnHttpRequestCallback listener) {
        KeyControlData keyControlData = new KeyControlData(clientId, accessToken, keyId, date);
        Observable<Response<ResponseBody>> freezeKey = mLockRequestService.freezeKey(keyControlData);
        enqueueVoid(freezeKey, listener);
    }

    @Override
    public void unFreezeKey(String clientId, String accessToken, int keyId, long date, OnHttpRequestCallback listener) {
        KeyControlData keyControlData = new KeyControlData(clientId, accessToken, keyId, date);
        Observable<Response<ResponseBody>> freezeKey = mLockRequestService.unFreezeKey(keyControlData);
        enqueueVoid(freezeKey, listener);
    }

    @Override
    public void authKeyUser(String clientId, String accessToken, int lockId, int keyId, long date, OnHttpRequestCallback listener) {
        AuthKeyData authKeyData = new AuthKeyData(clientId, accessToken, lockId, keyId, date);
        Observable<Response<ResponseBody>> authKeyUser = mLockRequestService.authKeyUser(authKeyData);
        enqueueVoid(authKeyUser, listener);
    }

    @Override
    public void unAuthKeyUser(String clientId, String accessToken, int lockId, int keyId, long date, OnHttpRequestCallback listener) {
        AuthKeyData authKeyData = new AuthKeyData(clientId, accessToken, lockId, keyId, date);
        Observable<Response<ResponseBody>> unAuthKeyUser = mLockRequestService.unAuthKeyUser(authKeyData);
        enqueueVoid(unAuthKeyUser, listener);
    }

    @Override
    public void lockRename(String clientId, String accessToken, int lockId, String lockAlias, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> lockRename = mLockRequestService.lockRename(clientId, accessToken, lockId, lockAlias, date);
        enqueueVoid(lockRename, listener);
    }

    @Override
    public void changeAdminKeyboardPwd(String clientId, String accessToken, int lockId, String password, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> changeAdminKeyboardPwd = mLockRequestService.changeAdminKeyboardPwd(clientId, accessToken, lockId, password, date);
        enqueueVoid(changeAdminKeyboardPwd, listener);
    }

    @Override
    public void getICs(String clientId, String accessToken, int lockId, int pageNo, int pageSize, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> getICs = mLockRequestService.getICs(clientId, accessToken, lockId, pageNo, pageSize, date);
        enqueues(getICs, ICCard.class, listener);
    }

    @Override
    public void deleteIC(String clientId, String accessToken, int lockId, int cardId, int deleteType, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> deleteIC = mLockRequestService.deleteIC(clientId, accessToken, lockId, cardId, deleteType, date);
        enqueueVoid(deleteIC, listener);
    }

    @Override
    public void addIC(String clientId, String accessToken, int lockId, String cardNumber, long startDate, long endDate, int addType, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> addIC = mLockRequestService.addIC(clientId, accessToken, lockId, cardNumber, startDate, endDate, addType, date);
        enqueueVoid(addIC, listener);
    }

    @Override
    public void clearICs(String clientId, String accessToken, int lockId, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> clearICs = mLockRequestService.clearICs(clientId, accessToken, lockId, date);
        enqueueVoid(clearICs, listener);
    }

    @Override
    public void getFingerprints(String clientId, String accessToken, int lockId, int pageNo, int pageSize, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> getFingerprints = mLockRequestService.getFingerprints(clientId, accessToken, lockId, pageNo, pageSize, date);
        enqueues(getFingerprints, FR.class, listener);
    }

    @Override
    public void deleteFingerprint(String clientId, String accessToken, int lockId, int fingerprintId, int deleteType, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> deleteFingerprint = mLockRequestService.deleteFingerprint(clientId, accessToken, lockId, fingerprintId, deleteType, date);
        enqueueVoid(deleteFingerprint, listener);
    }

    @Override
    public void addFingerprint(String clientId, String accessToken, int lockId, String fingerprintNumber, long startDate, long endDate, int addType, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> addFingerprint = mLockRequestService.addFingerprint(clientId, accessToken, lockId, fingerprintNumber, startDate, endDate, addType, date);
        enqueueVoid(addFingerprint, listener);
    }

    @Override
    public void clearFingerprints(String clientId, String accessToken, int lockId, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> clearFingerprints = mLockRequestService.clearFingerprints(clientId, accessToken, lockId, date);
        enqueueVoid(clearFingerprints, listener);
    }

    @Override
    public void uploadLockRecords(String clientId, String accessToken, int lockId, String records, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> uploadLockRecords = mLockRequestService.uploadLockRecords(clientId, accessToken, lockId, records, date);
        enqueueVoid(uploadLockRecords, listener);
    }

    @Override
    public void getLockRecords(String clientId, String accessToken, int lockId, long startDate, long endDate, int pageNo, int pageSize, long date, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> getLockRecords = mLockRequestService.getLockRecords(clientId, accessToken, lockId, startDate, endDate, pageNo, pageSize, date);
        enqueues(getLockRecords, LockRecord.class, listener);
    }

    private <T> void enqueue(final Observable<Response<ResponseBody>> call, final Class<T> clazz, final OnHttpRequestCallback<T>
            listener) {
//        call.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .unsubscribeOn(Schedulers.io())
//                .subscribe(new Consumer<Response<ResponseBody>>() {
//                    @Override
//                    public void accept(Response<ResponseBody> response) throws Exception {
//                        String result = response.body().string();
//                        Log.e(TAG, "---------result:" + result);
//                        if (listener != null) {
//                            HttpError httpError = mGson.fromJson(result, HttpError.class);
//                            if (httpError.getErrcode() != 0) {
//                                listener.onFailure(httpError.getErrcode());
//                            } else {
//                                listener.onSuccess(mGson.fromJson(result, clazz));
//                            }
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Log.e(TAG, "-------onError:" + throwable + mLockRequestService);
//                        if (listener != null)
//                            listener.onFailure(-1);
//                    }
//                });
    }

    //获取列表
    private <T> void enqueues(final Observable<Response<ResponseBody>> call, final Class<T> clazz, final OnHttpRequestCallback<T>
            listener) {
//        call.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .unsubscribeOn(Schedulers.io())
//                .subscribe(new Consumer<Response<ResponseBody>>() {
//                    @Override
//                    public void accept(Response<ResponseBody> response) throws Exception {
//                        String result = response.body().string();
//                        Log.e(TAG, "---------result:" + result);
//                        if (listener != null) {
//                            HttpError httpError = mGson.fromJson(result, HttpError.class);
//                            if (httpError.getErrcode() != 0) {
//                                listener.onFailure(httpError.getErrcode());
//                            } else {
//                                HttpResult httpResult = new HttpResult();
////                                HttpResult httpResult = mGson.fromJson(result, HttpResult.class);
//                                JSONObject jsonObject = new JSONObject(result);
//                                JSONArray list_array = jsonObject.getJSONArray("list");
//
//                                ArrayList<T> list = new ArrayList<>();
//                                JsonArray array = new JsonParser().parse(list_array.toString()).getAsJsonArray();
//                                for (final JsonElement elem : array) {
//                                    list.add(mGson.fromJson(elem, clazz));
//                                }
//                                httpResult.setList(list);
//                                listener.onSuccess((T) httpResult);
//                            }
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Log.e(TAG, "-------onError:" + throwable + mLockRequestService);
//                        if (listener != null)
//                            listener.onFailure(-1);
//                    }
//                });
    }

//    private void enqueue(final Observable<Response<ResponseBody>> call, final OnHttpRequestCallback<Boolean>
//            listener) {
//        call.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .unsubscribeOn(Schedulers.io())
//                .subscribe(new Consumer<Response<ResponseBody>>() {
//                    @Override
//                    public void accept(Response<ResponseBody> response) throws Exception {
//                        String result = response.body().string();
//                        Log.e(TAG, "----------result:" + result);
//                        if (listener != null) {
//                            HttpError httpError = mGson.fromJson(result, HttpError.class);
//                            if (httpError.getErrcode() != 0) {
//                                listener.onFailure(httpError.getErrcode());
//                            } else {
//                                listener.onSuccess(true);
//                            }
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Log.e(TAG, "----------onError:" + throwable);
//                        if (listener != null)
//                            listener.onFailure(-1);
//                    }
//                });
//    }

    private <T> void enqueue(final Observable<HttpResult<T>> call, final OnHttpRequestCallback<T>
            listener) {
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Consumer<HttpResult<T>>() {
                    @Override
                    public void accept(HttpResult<T> httpResult) throws Exception {
                        Log.e(TAG, "----------result:" + httpResult);
                        if (listener != null) {
                            if (httpResult.getCode() != 200) {
                                listener.onFailure(httpResult.getCode(), httpResult.getDesc());
                            } else {
                                listener.onSuccess(httpResult.getData());
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "----------onError:" + throwable);
                        if (listener != null)
                            listener.onFailure(-1, "");
                    }
                });
    }

    @SuppressLint("CheckResult")
    private <T> void enqueueVoid(final Observable<Response<ResponseBody>> call, final OnHttpRequestCallback<Boolean>
            listener) {
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Consumer<Response<ResponseBody>>() {
                    @Override
                    public void accept(Response<ResponseBody> response) throws Exception {
                        try {
                            String result = response.body().string();
                            Log.e(TAG, "----------result:" + result);
                            if (listener != null) {
                                JSONObject json = new JSONObject(result);
                                int code = json.getInt("code");
                                String desc = json.getString("desc");
                                if (code == 200) {
                                    listener.onSuccess(true);
                                } else {
                                    listener.onFailure(code, desc);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "----------onError:" + throwable);
                        if (listener != null)
                            listener.onFailure(-1, "");
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
