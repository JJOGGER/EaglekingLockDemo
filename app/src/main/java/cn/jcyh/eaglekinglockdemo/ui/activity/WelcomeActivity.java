package cn.jcyh.eaglekinglockdemo.ui.activity;

import cn.jcyh.eaglekinglockdemo.entity.LockUser;

import cn.jcyh.eaglekinglockdemo.MainActivity;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.control.ControlCenter;

public class WelcomeActivity extends BaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void init() {
        super.init();
        LockUser user = ControlCenter.getControlCenter(this).getUserInfo();
        if (user == null || user.getAccess_token() == null) {
            startNewActivity(AuthActivity.class);
        } else {
            startNewActivity(MainActivity.class);
        }
        finish();
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }
}
