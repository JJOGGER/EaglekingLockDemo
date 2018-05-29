package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ttlock.bl.sdk.bean.LockKey;
import com.ttlock.bl.sdk.constant.Operation;
import com.ttlock.bl.sdk.entity.Error;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.http.MyLockAPI;
import cn.jcyh.eaglekinglockdemo.utils.ToastUtil;

//锁时钟
public class LockClockActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_date)
    TextView tvDate;
    private MyLockAPI mLockAPI;
    private LockKey mLockKey;
    private MyReceiver mReceiver;

    @Override
    public int getLayoutId() {
        return R.layout.activity_lock_clock;
    }

    @Override
    protected void init() {
        mLockAPI = MyLockAPI.getLockAPI();
        mLockKey = getIntent().getParcelableExtra("key");
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(LockConstant.ACTION_LOCK_GET_TIME);
        intentFilter.addAction(LockConstant.ACTION_LOCK_SYNC_TIME);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
        showProgressDialog();
        if (mLockAPI.isConnected(mLockKey.getLockMac())) {
            mLockAPI.getLockTime(null, mLockKey);
        } else {
            mLockAPI.connect(mLockKey.getLockMac(), Operation.GET_LOCK_TIME);
        }
    }

    @OnClick({R.id.ibtn_back, R.id.tv_sync_date})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.tv_sync_date:
                showProgressDialog();
                if (mLockAPI.isConnected(mLockKey.getLockMac())) {
                    mLockAPI.setLockTime(null, mLockKey);
                } else {
                    mLockAPI.connect(mLockKey.getLockMac(), Operation.SET_LOCK_TIME);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) return;
            Error error = (Error) intent.getSerializableExtra(LockConstant.ERROR_MSG);
            switch (action) {
                case LockConstant.ACTION_LOCK_GET_TIME:
                    if (Error.SUCCESS == error) {
                        long date = intent.getLongExtra("date", 0);
                        String lockDate = SimpleDateFormat.getInstance().format(new Date(date));
                        tvDate.setText(lockDate);
                    } else {
                        ToastUtil.showToast(getApplicationContext(), error.getDescription());
                    }
                    cancelProgressDialog();
                    break;
                case LockConstant.ACTION_LOCK_SYNC_TIME:
                    if (Error.SUCCESS == error) {
                        ToastUtil.showToast(getApplicationContext(), "校准成功");
                    } else {
                        ToastUtil.showToast(getApplicationContext(), error.getDescription());
                    }
                    cancelProgressDialog();
                    break;
            }
        }
    }
}
