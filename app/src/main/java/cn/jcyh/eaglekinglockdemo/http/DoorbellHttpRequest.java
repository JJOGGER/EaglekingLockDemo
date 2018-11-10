package cn.jcyh.eaglekinglockdemo.http;

import android.annotation.SuppressLint;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cn.jcyh.nimlib.entity.AddBindUserRequest;
import cn.jcyh.nimlib.entity.BindDoorbellUser;
import cn.jcyh.nimlib.entity.Doorbell;
import cn.jcyh.nimlib.entity.DoorbellAuthCode;
import cn.jcyh.nimlib.entity.DoorbellParam;
import cn.jcyh.nimlib.entity.DoorbellRecordRequest;
import cn.jcyh.nimlib.entity.DoorbellRecords;
import cn.jcyh.nimlib.entity.DoorbellRequest;
import cn.jcyh.nimlib.entity.GetDoorbellParamRequest;
import cn.jcyh.nimlib.entity.HttpResult;
import cn.jcyh.nimlib.entity.JsonDataRequest;
import cn.jcyh.nimlib.entity.ModifyNickNameRequest;
import cn.jcyh.nimlib.entity.RemoveBindUserRequest;
import cn.jcyh.nimlib.entity.SetAdminRequest;
import cn.jcyh.nimlib.entity.TokenRequest;
import cn.jcyh.nimlib.entity.TokenResponse;
import cn.jcyh.nimlib.entity.UserDoorbellRequest;
import cn.jcyh.nimlib.entity.UserRequest;
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
class DoorbellHttpRequest implements IDoorbellHttpRequest {
    private final DoorbellRequestService mRequestService;

    DoorbellHttpRequest(DoorbellRequestService requestService) {
        mRequestService = requestService;
    }


    @Override
    public void getDoorbellToken(TokenRequest tokenRequest, OnHttpRequestListener listener) {
        Observable<HttpResult<TokenResponse>> getToken = mRequestService.getToken(tokenRequest);
        enqueue2(getToken, listener);
    }

    @Override
    public void getDoorbells(UserRequest userRequest, OnHttpRequestListener listener) {
        Observable<HttpResult<List<Doorbell>>> getDoorbells = mRequestService.getDoorbells(userRequest);
        enqueue2(getDoorbells, listener);
    }

    @Override
    public void modifyNickName(ModifyNickNameRequest modifyNickNameRequest, OnHttpRequestListener listener) {
        Observable<Response<ResponseBody>> modifyNickName = mRequestService
                .modifyNickName(modifyNickNameRequest);
        enqueue(modifyNickName, listener);
    }


    @Override
    public void getDoorbellParams(GetDoorbellParamRequest getDoorbellParamRequest, OnHttpRequestListener listener) {
        Observable<HttpResult<DoorbellParam>> getDoorbellParams = mRequestService
                .getDoorbellParams(getDoorbellParamRequest);
        enqueue2(getDoorbellParams, listener);
    }


    @Override
    public void deleteDoorbellMsgRecords(JsonDataRequest jsonDataRequest, OnHttpRequestListener listener) {
        Observable<Response<ResponseBody>> deleteDoorbellMsgRecords = mRequestService
                .deleteDoorbellMsgRecords(jsonDataRequest);
        enqueue(deleteDoorbellMsgRecords, listener);
    }

    @Override
    public void getDoorbellMsgRecords(DoorbellRecordRequest doorbellRecordRequest, OnHttpRequestListener listener) {
        Observable<HttpResult<DoorbellRecords>> getDoorbellMsgRecords = mRequestService
                .getDoorbellMsgRecords(doorbellRecordRequest);
        enqueue2(getDoorbellMsgRecords, listener);
    }


    @Override
    public void bindDoorbell(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener listener) {
        Observable<Response<ResponseBody>> addDevice = mRequestService.bindDoorbell(userDoorbellRequest);
        enqueue(addDevice, listener);
    }

    @Override
    public void unbindDoorbell(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener listener) {
        Observable<Response<ResponseBody>> unDoorbell = mRequestService.unDoorbell(userDoorbellRequest);
        enqueue(unDoorbell, listener);
    }

    @Override
    public void getBindDoorbellUsers(DoorbellRequest doorbellRequest, OnHttpRequestListener listener) {
        Observable<HttpResult<List<BindDoorbellUser>>> getBindDoorbellUsers = mRequestService
                .getBindDoorbellUsers(doorbellRequest);
        enqueue2(getBindDoorbellUsers, listener);
    }

    @Override
    public void setAdminUnbindDoorbell(SetAdminRequest setAdminRequest, OnHttpRequestListener listener) {
        Observable<Response<ResponseBody>> setAdminUnbindDoorbell = mRequestService.setAdminUnbindDoorbell(setAdminRequest);
        enqueue(setAdminUnbindDoorbell, listener);
    }

    @Override
    public void adminUnbind(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener listener) {
        Observable<Response<ResponseBody>> adminUnbind = mRequestService.adminUnbindDoorbell(userDoorbellRequest);
        enqueue(adminUnbind, listener);
    }

    @Override
    public void removeBindUser(RemoveBindUserRequest removeBindUserRequest, OnHttpRequestListener listener) {
        Observable<Response<ResponseBody>> removeBindUser = mRequestService.removeBindUser(removeBindUserRequest);
        enqueue(removeBindUser, listener);
    }

    @Override
    public void getDeviceAuthCode(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener listener) {
        Observable<HttpResult<DoorbellAuthCode>> deviceAuthCode = mRequestService.getDeviceAuthCode(userDoorbellRequest);
        enqueue2(deviceAuthCode, listener);
    }

    @Override
    public void addUserDeviceBind(AddBindUserRequest addBindUserRequest, OnHttpRequestListener listener) {
        Observable<Response<ResponseBody>> addUserDeviceBind = mRequestService.addUserDeviceBind(addBindUserRequest);
        enqueue(addUserDeviceBind, listener);
    }


    @Override
    public void loginDoorbell(LoginInfo loginInfo, final OnHttpRequestListener listener) {
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo loginInfo) {
                        listener.onSuccess(loginInfo);
                    }

                    @Override
                    public void onFailed(int i) {
                        listener.onFailure(i, "");
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        listener.onFailure(-1, throwable.getMessage());
                    }
                };
        NIMClient.getService(AuthService.class).login(loginInfo)
                .setCallback(callback);
    }


    /**
     * 不需要解析实体类的回调
     */
    @SuppressLint("CheckResult")
    private void enqueue(Observable<Response<ResponseBody>> call, final
    OnHttpRequestListener<Boolean> listener) {
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<ResponseBody>>() {
                    @Override
                    public void accept(Response<ResponseBody> response) {
                        try {
                            String result = response.body().string();
                            if (listener == null) return;
                            int code = 0;
                            String desc = "";
                            try {
                                JSONObject json = new JSONObject(result);
                                code = json.getInt("code");
                                desc = json.getString("desc");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (code == 200) {
                                listener.onSuccess(true);
                            } else {
                                listener.onFailure(code, desc);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        if (listener != null)
                            listener.onFailure(-1, "");
                    }
                });
    }


    /**
     * 自动解析
     */

    @SuppressLint("CheckResult")
    private <T> void enqueue2(final Observable<HttpResult<T>> call, final OnHttpRequestListener<T> listener) {
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HttpResult<T>>() {
                    @Override
                    public void accept(HttpResult<T> tHttpResult) {
                        if (listener == null) return;
                        if (tHttpResult.getCode() == 200)
                            listener.onSuccess(tHttpResult.getData());
                        else {
                            listener.onFailure(tHttpResult.getCode(), tHttpResult.getDesc());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        if (listener != null)
                            listener.onFailure(-1, "");
                    }
                });
    }
}
