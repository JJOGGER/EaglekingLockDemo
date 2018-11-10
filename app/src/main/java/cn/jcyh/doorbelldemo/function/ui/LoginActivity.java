package cn.jcyh.doorbelldemo.function.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.doorbelldemo.base.BaseDoorbellActivity;
import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.doorbelldemo.control.ui.DialogHelper;
import cn.jcyh.doorbelldemo.function.contract.LoginContract;
import cn.jcyh.doorbelldemo.function.presenter.LoginPresenter;
import cn.jcyh.doorbelldemo.function.ui.dialogs.CommonHintDialog2;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.entity.TokenResponse;

public class LoginActivity extends BaseDoorbellActivity<LoginPresenter> implements LoginContract.View, TextWatcher {
    @BindView(R.id.ll_login_container)
    LinearLayout llLoginContainer;
    @BindView(R.id.et_user_id)
    EditText etUserId;
    @BindView(R.id.et_token)
    EditText etToken;
    @BindView(R.id.tv_login)
    TextView tvLogin;

    @BindView(R.id.ll_get_token_container)
    LinearLayout llgetTokenContainer;
    @BindView(R.id.et_username)
    EditText etUserName;

    @BindView(R.id.ll_get_token_succ)
    LinearLayout llGetTokenSucc;

    private static final int LOGIN_SHOW = 0X001;
    private static final int GET_TOKEN_SHOW = 0X002;
    private static final int GET_TOKEN_SUCC_SHOW = 0X003;
    private int mCurrentShow = LOGIN_SHOW;
    private DialogHelper mLoginedDialog;
    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }


    @Override
    protected void init() {
        mPresenter.initView();
        boolean isOtherLogin = getIntent().getBooleanExtra(Constant.IS_OTHER_LOGIN, false);
        if (isOtherLogin) {
            if (mLoginedDialog == null) {
                final CommonHintDialog2 commonHintDialog2 = new CommonHintDialog2();
                commonHintDialog2.setHintContent(getString(R.string.other_login_msg));
                commonHintDialog2.setCancelable(false);
                commonHintDialog2.setOnHintDialogListener(new CommonHintDialog2.OnHintDialogListener() {
                    @Override
                    public void confirm(boolean isConfirm) {
                        commonHintDialog2.dismiss();
                    }
                });
                mLoginedDialog = new DialogHelper(this, commonHintDialog2);
            }
            mLoginedDialog.commit();
        }
    }

    @Override
    public void initView(String userId, String token) {
        etUserId.setText(userId == null ? "" : userId);
        etToken.setText(token == null ? "" : token);
    }


    @Override
    public String getUserName() {
        return etUserName.getText().toString().trim();
    }

    @Override
    public void getTokenSuccess(TokenResponse tokenResponse) {
        etUserId.setText(tokenResponse.getUserId());
        etToken.setText(tokenResponse.getAccessToken());
        mCurrentShow = GET_TOKEN_SUCC_SHOW;
        initShow();
    }

    @Override
    public String getUserId() {
        return etUserId.getText().toString().trim();
    }

    @Override
    public String getToken() {
        return etToken.getText().toString().trim();
    }


    @Override
    public void loginSuccess() {
        startNewActivity(DoorbellHomeActivity.class);
        finish();
    }

    private void initShow() {
        switch (mCurrentShow) {
            case LOGIN_SHOW:
                llLoginContainer.setVisibility(View.VISIBLE);
                llgetTokenContainer.setVisibility(View.GONE);
                llGetTokenSucc.setVisibility(View.GONE);
                break;
            case GET_TOKEN_SHOW:
                llLoginContainer.setVisibility(View.GONE);
                llgetTokenContainer.setVisibility(View.VISIBLE);
                llGetTokenSucc.setVisibility(View.GONE);
                break;
            case GET_TOKEN_SUCC_SHOW:
                llLoginContainer.setVisibility(View.GONE);
                llgetTokenContainer.setVisibility(View.GONE);
                llGetTokenSucc.setVisibility(View.VISIBLE);
                break;
        }

    }


    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @OnClick({R.id.tv_login, R.id.tv_go_get_token, R.id.tv_login2})
    public void onLoginClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login:
            case R.id.tv_login2:
                mPresenter.login();
                break;
            case R.id.tv_go_get_token:
                llgetTokenContainer.setVisibility(View.VISIBLE);
                llLoginContainer.setVisibility(View.GONE);
                mCurrentShow = GET_TOKEN_SHOW;
                break;
        }
    }


    @OnClick({R.id.tv_get_token, R.id.tv_login_direct})
    public void onRegistClick(View v) {
        switch (v.getId()) {
            case R.id.tv_get_token:
                mPresenter.getToken();
                break;
            case R.id.tv_login_direct:
                llgetTokenContainer.setVisibility(View.GONE);
                llLoginContainer.setVisibility(View.VISIBLE);
                mCurrentShow = LOGIN_SHOW;
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (llgetTokenContainer.getVisibility() == View.VISIBLE) {
            llgetTokenContainer.setVisibility(View.GONE);
            llLoginContainer.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
        mCurrentShow = LOGIN_SHOW;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        initShow();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @Override
    protected void onDestroy() {
        if (mLoginedDialog != null)
            mLoginedDialog.dismiss();
        super.onDestroy();
    }

}
