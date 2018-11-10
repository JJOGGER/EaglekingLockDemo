package cn.jcyh.doorbelldemo.function.contract;

import com.netease.nimlib.sdk.auth.LoginInfo;

import cn.jcyh.doorbelldemo.base.BaseModel;
import cn.jcyh.doorbelldemo.base.BaseView;
import cn.jcyh.doorbelldemo.base.IPresenter;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.nimlib.entity.TokenRequest;
import cn.jcyh.nimlib.entity.TokenResponse;

/**
 * Created by jogger on 2018/6/26.
 */
public interface LoginContract {
    interface Model extends BaseModel {

        void login(LoginInfo loginInfo, OnHttpRequestListener<LoginInfo> listener);

        void getToken(TokenRequest tokenRequest, OnHttpRequestListener<TokenResponse> listener);

    }

    interface View extends BaseView {
        void initView(String userId, String token);

        String getUserName();

        void getTokenSuccess(TokenResponse tokenResponse);

        String getUserId();

        String getToken();

        void loginSuccess();

    }

    interface Presenter extends IPresenter<View, Model> {
        void initView();

        void login();

        void getToken();

    }
}
