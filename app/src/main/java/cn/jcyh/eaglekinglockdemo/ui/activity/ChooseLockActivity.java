package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.view.View;

import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;

public class ChooseLockActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_choose_lock;
    }

    @OnClick({R.id.ibtn_back, R.id.tv_door_lock})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.tv_door_lock:
                startNewActivity(AddLockActivity.class);
                finish();
                break;
        }
    }
}
