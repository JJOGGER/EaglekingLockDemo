package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.http.LockHttpAction;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestCallback;
import cn.jcyh.utils.T;

public class RegistActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_pwd)
    EditText etPwd;


    @Override
    public int getLayoutId() {
        return R.layout.activity_regist;
    }

    @Override
    protected void init() {
        tvTitle.setText("注册");
    }

    @OnClick({R.id.ibtn_back, R.id.tv_regist})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.tv_regist:
                reigst();
                break;
        }
    }

    private void reigst() {
        String account = etAccount.getText().toString().trim();
        String pwd = etPwd.getText().toString().trim();
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(pwd)) {
             T.show("输入不能为空");
            return;
        }
        LockHttpAction.getHttpAction(this).regist(account, pwd, new OnHttpRequestCallback<Boolean>() {

            @Override
            public void onFailure(int errorCode, String desc) {
                T.show(desc);
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                 T.show("注册成功");
                finish();
            }
        });

    }
}
