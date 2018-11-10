package cn.jcyh.doorbelldemo.function.presenter;

import com.netease.nimlib.sdk.auth.LoginInfo;

import cn.jcyh.doorbelldemo.base.BasePresenter;
import cn.jcyh.doorbelldemo.function.contract.WelcomeContract;
import cn.jcyh.doorbelldemo.function.model.WelcomeModel;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.nimlib.entity.UserRequest;
import cn.jcyh.utils.T;

/**
 * Created by jogger on 2018/7/9.
 */
public class WelcomePresenter extends BasePresenter<WelcomeContract.View, WelcomeContract.Model> implements WelcomeContract.Presenter {

    @Override
    public void login(final UserRequest userRequest) {
        LoginInfo loginInfo = new LoginInfo(userRequest.getUserId(), userRequest.getAccessToken());
        mModel.login(loginInfo, new OnHttpRequestListener<LoginInfo>() {
            @Override
            public void onFailure(int errorCode, String desc) {
                T.show(desc);
                if (mView == null) return;
                mView.loginFail();
            }

            @Override
            public void onSuccess(LoginInfo loginInfo) {
                if (mView == null) return;
                mView.cancelProgressDialog();
                mView.loginSuccess();
            }
        });
    }

    @Override
    public WelcomeContract.Model attacheModel() {
        return new WelcomeModel();
    }
}
