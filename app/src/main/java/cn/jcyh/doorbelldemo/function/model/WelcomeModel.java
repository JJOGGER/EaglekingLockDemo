package cn.jcyh.doorbelldemo.function.model;

import com.netease.nimlib.sdk.auth.LoginInfo;

import cn.jcyh.doorbelldemo.function.contract.WelcomeContract;
import cn.jcyh.eaglekinglockdemo.http.DoorbellHttpAction;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;

/**
 * Created by jogger on 2018/7/9.
 */
public class WelcomeModel implements WelcomeContract.Model {

    @Override
    public void login(LoginInfo loginInfo, OnHttpRequestListener<LoginInfo> listener) {
        DoorbellHttpAction.getHttpAction().login(loginInfo, listener);
    }
}
