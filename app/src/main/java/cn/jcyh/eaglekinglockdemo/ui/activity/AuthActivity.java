package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ttlock.bl.sdk.bean.LockUser;
import com.ttlock.bl.sdk.http.LockHttpAction;
import com.ttlock.bl.sdk.http.OnHttpRequestCallback;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.MainActivity;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.config.LockConfig;
import cn.jcyh.eaglekinglockdemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.utils.Timber;
import cn.jcyh.eaglekinglockdemo.utils.ToastUtil;

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
            ToastUtil.showToast(getApplicationContext(), "不能为空");
            return;
        }
        LockHttpAction.getHttpAction(this).auth("eagleking_"+userName, pwd, LockConfig.REDIRECT_URI, new OnHttpRequestCallback<LockUser>() {
            @Override
            public void onFailure(int errorCode) {
                ToastUtil.showToast(getApplicationContext(), "发生错误，错误码" + errorCode);
            }

            @Override
            public void onSuccess(LockUser user) {
                Timber.e("----------user:" + user);
                if (user != null) {
                    tvAuthAccessToken.setText(user.getAccess_token());
                    tvAuthOpenid.setText(user.getOpenid() + "");
                }
                ControlCenter.getControlCenter(getApplicationContext()).saveUserInfo(user);
                startNewActivity(MainActivity.class);
                finish();
            }
        });
    }
}
