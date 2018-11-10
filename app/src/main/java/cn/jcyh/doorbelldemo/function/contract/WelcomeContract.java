package cn.jcyh.doorbelldemo.function.contract;

import com.netease.nimlib.sdk.auth.LoginInfo;

import cn.jcyh.doorbelldemo.base.BaseModel;
import cn.jcyh.doorbelldemo.base.BaseView;
import cn.jcyh.doorbelldemo.base.IPresenter;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.nimlib.entity.UserRequest;

/**
 * Created by jogger on 2018/7/9.
 */
public interface WelcomeContract {
    interface Model extends BaseModel {
        void login(LoginInfo loginInfo, OnHttpRequestListener<LoginInfo> listener);
    }

    interface View extends BaseView {
        void loginSuccess();

        void loginFail();

    }

    interface Presenter extends IPresenter<View, Model> {
        void login(UserRequest userRequest);

    }
}
