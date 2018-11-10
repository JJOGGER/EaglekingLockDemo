package cn.jcyh.eaglekinglockdemo.http;

import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.netease.nimlib.sdk.auth.LoginInfo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jcyh.nimlib.entity.AddBindUserRequest;
import cn.jcyh.nimlib.entity.BindDoorbellUser;
import cn.jcyh.nimlib.entity.Doorbell;
import cn.jcyh.nimlib.entity.DoorbellAuthCode;
import cn.jcyh.nimlib.entity.DoorbellParam;
import cn.jcyh.nimlib.entity.DoorbellRecordRequest;
import cn.jcyh.nimlib.entity.DoorbellRecords;
import cn.jcyh.nimlib.entity.DoorbellRequest;
import cn.jcyh.nimlib.entity.GetDoorbellParamRequest;
import cn.jcyh.nimlib.entity.JsonDataRequest;
import cn.jcyh.nimlib.entity.RemoveBindUserRequest;
import cn.jcyh.nimlib.entity.SetAdminRequest;
import cn.jcyh.nimlib.entity.TokenRequest;
import cn.jcyh.nimlib.entity.TokenResponse;
import cn.jcyh.nimlib.entity.UserDoorbellRequest;
import cn.jcyh.nimlib.entity.UserRequest;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jogger on 2018/1/10.网络请求
 */
@SuppressWarnings("unchecked")
public class DoorbellHttpAction {
    private static final String TAG = DoorbellHttpAction.class.getSimpleName();
    private static DoorbellHttpAction sDoorbellHttpAction;
    private IDoorbellHttpRequest mHttpRequest;

    private DoorbellHttpAction() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);
        builder.interceptors().add(new AddCookiesInterceptor());
        //声明日志类
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i(TAG, "http message:" + message);
            }
        });
//设定日志级别
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        builder.interceptors().add(httpLoggingInterceptor);
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(DoorbellRequestService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        DoorbellRequestService requestService = retrofit.create(DoorbellRequestService.class);
        mHttpRequest = new DoorbellHttpRequest(requestService);
    }

    public static DoorbellHttpAction getHttpAction() {
        if (sDoorbellHttpAction == null) {
            synchronized (DoorbellHttpAction.class) {
                if (sDoorbellHttpAction == null) {
                    sDoorbellHttpAction = new DoorbellHttpAction();
                }
            }
        }
        return sDoorbellHttpAction;
    }

    /**
     * 注册
     */
    public void getToken(TokenRequest tokenRequest, final
    OnHttpRequestListener<TokenResponse> listener) {
        mHttpRequest.getDoorbellToken(tokenRequest, listener);
    }

    public void login(LoginInfo loginInfo, final OnHttpRequestListener<LoginInfo> listener) {
        mHttpRequest.loginDoorbell(loginInfo, listener);
    }


    /**
     * 添加猫眼
     */
    public void bindDoorbell(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener<Boolean> listener) {
        mHttpRequest.bindDoorbell(userDoorbellRequest, listener);
    }

    /**
     * 解绑猫眼
     */
    public void unbindDoorbell(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener listener) {
        mHttpRequest.unbindDoorbell(userDoorbellRequest, listener);
    }

    /**
     * 获取绑定猫眼的用户列表
     */
    public void getBindDoorbellUsers(DoorbellRequest doorbellRequest, OnHttpRequestListener<List<BindDoorbellUser>> listener) {
        mHttpRequest.getBindDoorbellUsers(doorbellRequest, listener);
    }

    /**
     * 设置管理员并解绑设备
     */
    public void setAdminUnbindDoorbell(SetAdminRequest setAdminRequest, OnHttpRequestListener<Boolean> listener) {
        mHttpRequest.setAdminUnbindDoorbell(setAdminRequest, listener);
    }

    /**
     * 管理员解绑（解绑所有用户）
     */
    public void adminUnbindDoorbell(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener<Boolean> listener) {
        mHttpRequest.adminUnbind(userDoorbellRequest, listener);
    }

    /**
     * 移除用户和猫眼的绑定关系
     */
    public void removeBindUser(RemoveBindUserRequest removeBindUserRequest, OnHttpRequestListener<Boolean> listener) {
        mHttpRequest.removeBindUser(removeBindUserRequest, listener);
    }

    /**
     * 获取猫眼设备授权码
     */
    public void getDeviceAuthCode(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener<DoorbellAuthCode> listener) {
        mHttpRequest.getDeviceAuthCode(userDoorbellRequest, listener);
    }

    /**
     * 添加用户和猫眼的绑定
     */
    public void addUserDeviceBind(AddBindUserRequest addBindUserRequest, OnHttpRequestListener<Boolean> listener) {
        mHttpRequest.addUserDeviceBind(addBindUserRequest, listener);
    }

    /**
     * 获取猫眼报警/门铃记录
     */
    public void getDoorbellMsgRecords(DoorbellRecordRequest doorbellRecordRequest, final
    OnHttpRequestListener<DoorbellRecords> listener) {
        mHttpRequest.getDoorbellMsgRecords(doorbellRecordRequest, listener);
    }

    /**
     * 删除门铃记录
     */
    public void deleteDoorbellMsgRecords(JsonDataRequest jsonDataRequest, final OnHttpRequestListener<Boolean>
            listener) {
        mHttpRequest.deleteDoorbellMsgRecords(jsonDataRequest, listener);
    }

    /**
     * 获取猫眼参数
     * <p>
     * 设置类型 mode/monitor/sensor
     */
    public void getDoorbellParams(GetDoorbellParamRequest getDoorbellParamRequest, final
    OnHttpRequestListener<DoorbellParam> listener) {
        mHttpRequest.getDoorbellParams(getDoorbellParamRequest, listener);
    }

    /**
     * 获取用户下所有的设备列表
     */
    public void getDoorbells(UserRequest userRequest, final OnHttpRequestListener<List<Doorbell>> listener) {
        mHttpRequest.getDoorbells(userRequest, listener);
    }

}
