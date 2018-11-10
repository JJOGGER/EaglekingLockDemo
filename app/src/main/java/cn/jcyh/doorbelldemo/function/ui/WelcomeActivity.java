package cn.jcyh.doorbelldemo.function.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import cn.jcyh.doorbelldemo.base.BaseDoorbellActivity;
import cn.jcyh.doorbelldemo.control.ControlCenter;
import cn.jcyh.doorbelldemo.function.contract.WelcomeContract;
import cn.jcyh.doorbelldemo.function.presenter.WelcomePresenter;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.entity.UserRequest;
import cn.jcyh.utils.T;


public class WelcomeActivity extends BaseDoorbellActivity<WelcomePresenter> implements WelcomeContract.View {
    private static final int REQUEST_CODE_WRITE_SETTINGS = 1;

    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected WelcomePresenter createPresenter() {
        return new WelcomePresenter();
    }

    @Override
    protected void init() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPermission();
            }
        }, 1000);
    }

    @SuppressLint("CheckResult")
    private void checkPermission() {
//        RxPermissions rxPermissions = new RxPermissions(this);
//        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.RECORD_AUDIO,
//                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.CAMERA)
//                .subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//                        intoMain();
//                    }
//                });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Settings.System.canWrite(this)) {
                checkPermission();
            } else {
                T.show(R.string.auth_failure);
                finish();
            }
        }
    }

    /**
     * 进入主页
     */
    private void intoMain() {
        //判断是否自动登录
        UserRequest userRequest = ControlCenter.getUserManager().getUser();
        if (userRequest == null || TextUtils.isEmpty(userRequest.getUserId()) || TextUtils.isEmpty(userRequest.getAccessToken())) {
            startNewActivity(LoginActivity.class);
            finish();
        } else {
            mPresenter.login(userRequest);
        }
    }

    @Override
    public void loginSuccess() {
        startNewActivity(DoorbellHomeActivity.class);
        finish();
    }

    @Override
    public void loginFail() {
        startNewActivity(LoginActivity.class);
    }
}

