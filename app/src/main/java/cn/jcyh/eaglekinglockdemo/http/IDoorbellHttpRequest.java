package cn.jcyh.eaglekinglockdemo.http;


import com.netease.nimlib.sdk.auth.LoginInfo;

import cn.jcyh.nimlib.entity.AddBindUserRequest;
import cn.jcyh.nimlib.entity.DoorbellRecordRequest;
import cn.jcyh.nimlib.entity.DoorbellRequest;
import cn.jcyh.nimlib.entity.GetDoorbellParamRequest;
import cn.jcyh.nimlib.entity.JsonDataRequest;
import cn.jcyh.nimlib.entity.ModifyNickNameRequest;
import cn.jcyh.nimlib.entity.RemoveBindUserRequest;
import cn.jcyh.nimlib.entity.SetAdminRequest;
import cn.jcyh.nimlib.entity.TokenRequest;
import cn.jcyh.nimlib.entity.UserDoorbellRequest;
import cn.jcyh.nimlib.entity.UserRequest;

/**
 * Created by jogger on 2018/4/14.请求接口
 */

interface IDoorbellHttpRequest<T> {

    /*猫眼接口*/
    void getDoorbellToken(TokenRequest tokenRequest, final OnHttpRequestListener<T> listener);

    void loginDoorbell(LoginInfo loginInfo, final OnHttpRequestListener<T> listener);

    void getBindDoorbellUsers(DoorbellRequest doorbellRequest, OnHttpRequestListener<T> listener);

    void setAdminUnbindDoorbell(SetAdminRequest setAdminRequest, OnHttpRequestListener<T> listener);

    void adminUnbind(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener<T> listener);

    void removeBindUser(RemoveBindUserRequest removeBindUserRequest, OnHttpRequestListener<T> listener);

    void getDeviceAuthCode(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener<T> listener);

    void addUserDeviceBind(AddBindUserRequest addBindUserRequest, OnHttpRequestListener<T> listener);

    void bindDoorbell(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener<T> listener);

    void unbindDoorbell(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener<T> listener);

    void getDoorbellMsgRecords(DoorbellRecordRequest doorbellRecordRequest, final
    OnHttpRequestListener<T> listener);

    void deleteDoorbellMsgRecords(JsonDataRequest jsonDataRequest, final OnHttpRequestListener<T>
            listener);

    void getDoorbellParams(GetDoorbellParamRequest getDoorbellParamRequest, final
    OnHttpRequestListener<T> listener);

    void getDoorbells(UserRequest userRequest, final OnHttpRequestListener<T> listener);

    void modifyNickName(ModifyNickNameRequest modifyNickNameRequest, final OnHttpRequestListener<T> listener);
}
