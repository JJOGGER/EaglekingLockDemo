package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.constant.Operation;
import cn.jcyh.eaglekinglockdemo.entity.LockKey;
import cn.jcyh.eaglekinglockdemo.http.MyLockAPI;
import cn.jcyh.locklib.entity.Error;
import cn.jcyh.utils.T;

public class AddFingerprint2Activity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private MyReceiver mReceiver;
    private LockKey mLockKey;
    private MyLockAPI mLockAPI;
    private long mStartTime, mEndTime;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_fingerprint2;
    }

    @Override
    protected void init() {
        tvTitle.setText("添加指纹");
        mReceiver = new MyReceiver();
        mLockAPI = MyLockAPI.getLockAPI();
        mLockKey = getIntent().getParcelableExtra(LockConstant.LOCK_KEY);
        mStartTime = getIntent().getLongExtra(LockConstant.START_DATE, 0);
        mEndTime = getIntent().getLongExtra(LockConstant.END_DATE, 0);
        IntentFilter intentFilter = new IntentFilter(LockConstant.ACTION_LOCK_FINGERPRINT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
    }

    @OnClick({R.id.ibtn_back,R.id.tv_add})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.tv_add:
                showProgressDialog();
                if (mLockAPI.isConnected(mLockKey.getLockMac()))
                    mLockAPI.addFingerPrint(null, mLockKey);
                else {
                    mLockAPI.connect(mLockKey.getLockMac(), Operation.ADD_FINGERPRINT);
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
            if (!LockConstant.ACTION_LOCK_FINGERPRINT.equals(action)) return;
            String type = intent.getStringExtra(LockConstant.TYPE);
            Error error = (Error) intent.getSerializableExtra(LockConstant.ERROR_MSG);
            switch (type) {
                case LockConstant.TYPE_ADD_FINGERPRINT:
                    if (Error.SUCCESS == error) {
                        int status = intent.getIntExtra(LockConstant.STATUS, -1);
                        if (status == 1) {
                            //进入验证
                            cancelProgressDialog();
                            Intent i = new Intent(AddFingerprint2Activity.this, AddFingerprint3Activity.class);
                            i.putExtra(LockConstant.LOCK_KEY, mLockKey);
                            i.putExtra(LockConstant.MAX_VALIDATE, intent.getIntExtra(LockConstant.MAX_VALIDATE, 0));
                            i.putExtra(LockConstant.START_DATE, mStartTime);
                            i.putExtra(LockConstant.END_DATE, mEndTime);
                            i.putExtra(LockConstant.LOCK_KEY, mLockKey);
                            startActivity(i);
                        } else {
                            T.show(error.getDescription() + status);
                            cancelProgressDialog();
                        }
                    } else {
                        T.show( error.getDescription());
                        cancelProgressDialog();
                    }
                    break;
            }

        }
    }

}
