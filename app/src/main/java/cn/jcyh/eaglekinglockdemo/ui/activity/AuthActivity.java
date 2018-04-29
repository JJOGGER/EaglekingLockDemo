package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lock.bl.sdk.util.Timber;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.bean.User;
import cn.jcyh.eaglekinglockdemo.constant.Constants;
import cn.jcyh.eaglekinglockdemo.http.HttpAction;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestCallback;
import cn.jcyh.eaglekinglockdemo.utils.SharePreUtil;
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

    @OnClick(R.id.tv_auth)
    public void onClick(View v) {
        final String userName = etAuthUser.getText().toString().trim();
        String pwd = etAuthPwd.getText().toString().trim();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)) {
            ToastUtil.showToast(getApplicationContext(), "不能为空");
            return;
        }
        HttpAction.getHttpAction(this).auth(userName, pwd, new OnHttpRequestCallback<User>() {
            @Override
            public void onFailure(int errorCode) {

            }

            @Override
            public void onSuccess(User user) {
                Timber.e("----------user:" + user);
                if (user != null) {
                    tvAuthAccessToken.setText(user.getAccess_token());
                    tvAuthOpenid.setText(user.getOpenid()+"");
                }
                SharePreUtil.getInstance(getApplicationContext()).setString(Constants.USER_INFO, new Gson().toJson(user));
            }
        });
    }
}
