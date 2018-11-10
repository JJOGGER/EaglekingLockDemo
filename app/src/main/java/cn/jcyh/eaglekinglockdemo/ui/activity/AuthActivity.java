package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.MainActivity;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.entity.LoginData;
import cn.jcyh.eaglekinglockdemo.http.LockHttpAction;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestCallback;
import cn.jcyh.utils.L;
import cn.jcyh.utils.T;

//授权
public class AuthActivity extends BaseActivity {
    @BindView(R.id.et_auth_user)
    EditText etAuthUser;
    @BindView(R.id.et_auth_pwd)
    EditText etAuthPwd;
    @BindView(R.id.tv_auth_access_token)
    TextView tvAuthAccessToken;
    @BindView(R.id.tv_auth_openid)
    TextView tvAuthOpenid;

    @Override
    public int getLayoutId() {
        return R.layout.activity_auth;
    }

    @OnClick({R.id.tv_login, R.id.tv_regist})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login:
                login();
                break;
            case R.id.tv_regist:
                startNewActivity(RegistActivity.class);
                break;
        }
    }

    private void login() {
        final String userName = etAuthUser.getText().toString().trim();
        String pwd = etAuthPwd.getText().toString().trim();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)) {
            T.show("不能为空");
            return;
        }
        LockHttpAction.getHttpAction(this).auth(userName, pwd, new OnHttpRequestCallback<LoginData>() {

            @Override
            public void onFailure(int errorCode, String desc) {
                L.e("-------------error" + errorCode);
            }

            @Override
            public void onSuccess(LoginData loginData) {
                L.e("----------loginData:" + loginData);
                if (loginData != null) {
                    tvAuthAccessToken.setText(loginData.getSmartLockAccessToken());
                    tvAuthOpenid.setText(loginData.getSmartLockUserId() + "");
                    loginData.setAccount(userName);
                }
                ControlCenter.getControlCenter(getApplicationContext()).saveLoginData(loginData);
                startNewActivity(MainActivity.class);
                finish();
            }
        });
    }
}
