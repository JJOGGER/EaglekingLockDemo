package cn.jcyh.doorbelldemo.function.presenter;

import android.text.TextUtils;

import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.LoginInfo;

import cn.jcyh.doorbelldemo.base.BasePresenter;
import cn.jcyh.doorbelldemo.control.ControlCenter;
import cn.jcyh.doorbelldemo.function.contract.LoginContract;
import cn.jcyh.doorbelldemo.function.model.LoginModel;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.nimlib.command.manager.ObserverManager;
import cn.jcyh.nimlib.entity.TokenRequest;
import cn.jcyh.nimlib.entity.TokenResponse;
import cn.jcyh.nimlib.entity.UserRequest;
import cn.jcyh.utils.L;
import cn.jcyh.utils.T;


/**
 * Created by jogger on 2018/6/26.
 */
public class LoginPresenter extends BasePresenter<LoginContract.View, LoginContract.Model> implements LoginContract.Presenter {

    @Override
    public LoginContract.Model attacheModel() {
        return new LoginModel();
    }

    @Override
    public void attachView(LoginContract.View view) {
        super.attachView(view);
        //用户状态监听
        mUserStatusObserver = new UserStatusObserver();
        ObserverManager.observeOnlineStatus(mUserStatusObserver, true);
    }

    @Override
    public void detachView() {
        super.detachView();
        //用户状态监听
        ObserverManager.observeOnlineStatus(mUserStatusObserver, false);
    }

    private UserStatusObserver mUserStatusObserver;

    class UserStatusObserver implements Observer<StatusCode> {
        @Override
        public void onEvent(StatusCode statusCode) {
            if (statusCode.wontAutoLogin()) {
                T.show(R.string.login_failure);
            }
        }
    }

    @Override
    public void initView() {
        UserRequest userRequest = ControlCenter.getUserManager().getUser();
        if (userRequest == null) {
            mView.initView("", "");
        } else {
            String userId = userRequest.getUserId();
            String token = userRequest.getAccessToken();
            mView.initView(userId, token);
        }
    }

    /**
     * 登录
     */
    @Override
    public void login() {
        final String userId = mView.getUserId();
        final String token = mView.getToken();
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(token)) {
            T.show(R.string.input_no_null);
            return;
        }
        mView.showProgressDialog();
        LoginInfo loginInfo = new LoginInfo(userId, token);
        L.e("---------loginInfo:" + loginInfo.getAccount() + ":" + loginInfo.getToken());
        mModel.login(loginInfo, new OnHttpRequestListener<LoginInfo>() {
            @Override
            public void onFailure(int errorCode, String desc) {
                if (mView == null) return;
                L.e("------------desc-" + desc+"::"+errorCode);
                mView.cancelProgressDialog();
                T.show(desc);
            }

            @Override
            public void onSuccess(LoginInfo loginInfo) {
                if (mView == null) {
                    return;
                }
                L.e("---onSuccess:" + loginInfo);
                if (loginInfo == null) {
                    T.show(R.string.login_failure);
                    mView.cancelProgressDialog();
                    return;
                }
                mView.cancelProgressDialog();
                UserRequest userRequest = new UserRequest();
                userRequest.setUserId(loginInfo.getAccount());
                userRequest.setAccessToken(loginInfo.getToken());
                userRequest.setUserName(loginInfo.getToken());
                ControlCenter.getUserManager().setUser(userRequest);
                mView.loginSuccess();
            }
        });
    }


    @Override
    public void getToken() {
        final String nickname = mView.getUserName();
        if (TextUtils.isEmpty(nickname)) {
            T.show(R.string.input_no_null);
            return;
        }
        mView.showProgressDialog();
        TokenRequest tokenRequest = new TokenRequest(nickname);
        mModel.getToken(tokenRequest, new OnHttpRequestListener<TokenResponse>() {
            @Override
            public void onFailure(int errorCode, String desc) {
                if (mView == null) return;
                mView.cancelProgressDialog();
                T.show(desc);
            }

            @Override
            public void onSuccess(TokenResponse tokenResponse) {
                if (mView == null) return;
                L.e("-------tokenResponse：" + tokenResponse);
                mView.cancelProgressDialog();
                mView.getTokenSuccess(tokenResponse);
            }
        });
    }

}
