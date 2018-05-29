package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ttlock.bl.sdk.bean.LockKey;
import com.ttlock.bl.sdk.constant.Operation;
import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.http.LockHttpAction;
import com.ttlock.bl.sdk.http.OnHttpRequestCallback;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.http.MyLockAPI;
import cn.jcyh.eaglekinglockdemo.utils.ToastUtil;

public class AddFingerprint3Activity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_fingerprint_pro)
    ImageView ivFingerprintPro;
    @BindView(R.id.tv_fingerprint_pro)
    TextView tvFingerprintPro;
    private MyReceiver mReceiver;
    private LockKey mLockKey;
    private long mStartTime, mEndTime;
    private MyLockAPI mLockAPI;
    private int mMaxVail;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_fingerprint3;
    }

    @Override
    protected void init() {
        tvTitle.setText("添加指纹");
        mLockKey = getIntent().getParcelableExtra("key");
        mLockAPI = MyLockAPI.getLockAPI();
        int maxVail = getIntent().getIntExtra("maxVail", 0);
        mStartTime = getIntent().getLongExtra("startDate", 0);
        mEndTime = getIntent().getLongExtra("endDate", 0);
        tvFingerprintPro.setText(String.format("(%d/%d)", 0, maxVail));
        ivFingerprintPro.setImageResource(R.mipmap.fingerprint0);
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(LockConstant.ACTION_LOCK_FINGERPRINT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);

    }

    @OnClick(R.id.ibtn_back)
    public void onClick(View v) {
        finish();
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
            String type = intent.getStringExtra("type");
            Error error = (Error) intent.getSerializableExtra(LockConstant.ERROR_MSG);
            switch (type) {
                case LockConstant.TYPE_ADD_FINGERPRINT:
                    if (Error.SUCCESS == error) {
                        int status = intent.getIntExtra("status", -1);
                        ivFingerprintPro.setImageResource(R.mipmap.fingerprint4);
                        tvFingerprintPro.setText(String.format("(%d/%d)", mMaxVail, mMaxVail));
                        if (status == 2) {
                            long FRNo = intent.getLongExtra("FRNo", 0);
                            //添加成功
                            showProgressDialog();
                            if (mStartTime != 0) {
                                //先要修改指纹
                                mLockKey.setStartDate(mStartTime);
                                mLockKey.setEndDate(mEndTime);
                                if (mLockAPI.isConnected(mLockKey.getLockMac())) {
                                    mLockAPI.modifyFingerPrintPeriod(null, FRNo, mLockKey);
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putLong("FRNo", FRNo);
                                    MyLockAPI.sBleSession.setArgments(bundle);
                                    mLockAPI.connect(mLockKey.getLockMac(), Operation.MODIFY_FINGERPRINT_PERIOD);
                                }
                            } else {
                                addFingerprint2Server(FRNo);
                            }
                        }
                    } else {
                        ToastUtil.showToast(getApplicationContext(), error.getDescription());
                        cancelProgressDialog();
                    }
                    break;
                case LockConstant.TYPE_COLLECTION_FINGERPRINT:
                    int vail = intent.getIntExtra("vail", 0);
                    mMaxVail = intent.getIntExtra("maxVail", 0);
                    switch (vail) {
                        case 1:
                            ivFingerprintPro.setImageResource(R.mipmap.fingerprint1);
                            break;
                        case 2:
                            ivFingerprintPro.setImageResource(R.mipmap.fingerprint2);
                            break;
                        case 3:
                            ivFingerprintPro.setImageResource(R.mipmap.fingerprint3);
                            break;
                    }
                    tvFingerprintPro.setText(String.format("(%d/%d)", vail, mMaxVail));
                    break;
                case LockConstant.TYPE_MODIFY_FINGERPRINT:
                    mStartTime = intent.getLongExtra("startDate", 0);
                    mEndTime = intent.getLongExtra("endDate", 0);
                    addFingerprint2Server(intent.getLongExtra("FRNo", 0));
                    break;
            }

        }

    }

    /**
     * 保存到服務器
     */
    private void addFingerprint2Server(long FRNo) {
        LockHttpAction.getHttpAction(this).addFingerprint(mLockKey.getLockId(), String.valueOf(FRNo), mStartTime, mEndTime, new OnHttpRequestCallback<Boolean>() {
            @Override
            public void onFailure(int errorCode) {
                cancelProgressDialog();
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                ToastUtil.showToast(getApplicationContext(), "添加成功");
                cancelProgressDialog();
                finish();
            }
        });
    }
}
