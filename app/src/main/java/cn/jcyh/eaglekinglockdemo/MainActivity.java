package cn.jcyh.eaglekinglockdemo;

import android.view.View;

import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.ui.activity.AuthActivity;

public class MainActivity extends BaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        //请求打开蓝牙

    }

    @OnClick({R.id.btn_auth})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_auth:
                startNewActivity(AuthActivity.class);
                break;
        }
    }
}
