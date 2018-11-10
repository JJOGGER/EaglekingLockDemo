package cn.jcyh.doorbelldemo.function.model;

import com.netease.nimlib.sdk.auth.LoginInfo;

import cn.jcyh.doorbelldemo.function.contract.LoginContract;
import cn.jcyh.eaglekinglockdemo.http.DoorbellHttpAction;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.nimlib.entity.TokenRequest;
import cn.jcyh.nimlib.entity.TokenResponse;

/**
 * Created by jogger on 2018/6/26.
 */
public class LoginModel implements LoginContract.Model {


    @Override
    public void login(LoginInfo loginInfo, OnHttpRequestListener<LoginInfo> listener) {
        DoorbellHttpAction.getHttpAction().login(loginInfo, listener);
    }

    @Override
    public void getToken(TokenRequest tokenRequest, OnHttpRequestListener<TokenResponse> listener) {
        DoorbellHttpAction.getHttpAction().getToken(tokenRequest, listener);
    }

}
