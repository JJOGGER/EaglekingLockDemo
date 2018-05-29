package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ttlock.bl.sdk.bean.LockKey;
import com.ttlock.bl.sdk.constant.Operation;
import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.http.LockHttpAction;
import com.ttlock.bl.sdk.http.OnHttpRequestCallback;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.MainActivity;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.http.MyLockAPI;
import cn.jcyh.eaglekinglockdemo.ui.dialog.CommonEditDialog;
import cn.jcyh.eaglekinglockdemo.utils.ToastUtil;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_lock_id)
    TextView tvLockId;
    @BindView(R.id.tv_lock_mac)
    TextView tvLockMac;
    @BindView(R.id.tv_lock_power)
    TextView tvLockPower;
    @BindView(R.id.tv_lock_date)
    TextView tvLockDate;
    @BindView(R.id.tv_lock_name)
    TextView tvLockName;
    @BindView(R.id.tv_lock_pwd)
    TextView tvLockPwd;
    private LockKey mLockKey;
    private MyReceiver mReceiver;


    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void init() {
        mLockKey = getIntent().getParcelableExtra("key");
        tvLockId.setText(mLockKey.getLockName());
        tvLockName.setText(mLockKey.getLockAlias() + "");
        if (mLockKey.getEndDate() == 0)
            tvLockDate.setText("永久");
        else if (mLockKey.getEndDate() == 1)
            tvLockDate.setText("单次");
        else {
            Date date = new Date();
            date.setTime(mLockKey.getStartDate());
            String startDate = SimpleDateFormat.getInstance().format(date);
            date.setTime(mLockKey.getEndDate());
            String endDate = SimpleDateFormat.getInstance().format(date);
            tvLockDate.setText(startDate + "-" + endDate);
        }
        tvLockMac.setText(mLockKey.getLockMac());
        tvLockPower.setText(mLockKey.getElectricQuantity() + "");
        tvLockPwd.setText(mLockKey.getNoKeyPwd());
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(LockConstant.ACTION_RESET_LOCK);
        intentFilter.addAction(LockConstant.ACTION_SET_ADMIN_PWD);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
    }

    @OnClick({R.id.ibtn_back, R.id.rl_lock_name, R.id.rl_admin_pwd, R.id.rl_lock_clock, R.id.tv_delete})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.rl_lock_name:
                rename();
                break;
            case R.id.rl_admin_pwd:
                adminLockPwd();
                break;
            case R.id.rl_lock_clock:
                startNewActivity(LockClockActivity.class, "key", mLockKey);
                break;
            case R.id.tv_delete:
                deleteLock();
                break;
        }
    }

    private void adminLockPwd() {
        final CommonEditDialog commonEditDialog = new CommonEditDialog();
        commonEditDialog.setOnDialogListener(new CommonEditDialog.OnCommonEditDialogListener() {
            @Override
            public void onConfirm(boolean isConfirm) {
                commonEditDialog.dismiss();
                if (isConfirm) {
                    showProgressDialog();
                    String content = commonEditDialog.getEditText();
                    if (MyLockAPI.getLockAPI().isConnected(mLockKey.getLockMac())) {
                        MyLockAPI.getLockAPI().setAdminKeyboardPassword(null, mLockKey, content);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("password", content);
                        MyLockAPI.sBleSession.setArgments(bundle);
                        MyLockAPI.getLockAPI().connect(mLockKey.getLockMac(), Operation.SET_ADMIN_KEYBOARD_PASSWORD);
                    }
                }
            }
        });
        commonEditDialog.show(getSupportFragmentManager(), CommonEditDialog.class.getName());
    }

    /**
     * 重命名
     */
    private void rename() {
        final CommonEditDialog commonEditDialog = new CommonEditDialog();
        commonEditDialog.setHintContent("请输入名称");
        commonEditDialog.setInputType(InputType.TYPE_CLASS_TEXT);
        commonEditDialog.setOnDialogListener(new CommonEditDialog.OnCommonEditDialogListener() {
            @Override
            public void onConfirm(boolean isConfirm) {
                commonEditDialog.dismiss();
                if (isConfirm) {
                    final String content = commonEditDialog.getEditText();
                    LockHttpAction.getHttpAction(getApplicationContext()).lockRename(mLockKey.getLockId(), content, new OnHttpRequestCallback<Boolean>() {
                        @Override
                        public void onFailure(int errorCode) {
                            ToastUtil.showToast(getApplicationContext(), "修改失败" + errorCode);
                        }

                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            ToastUtil.showToast(getApplicationContext(), "修改成功");
                            setResult(RESULT_OK);
                            tvLockName.setText(content);
                        }
                    });
                }
            }
        });
        commonEditDialog.show(getSupportFragmentManager(), CommonEditDialog.class.getName());
    }

    private void deleteLock() {
        //必须先重锁中删除
        showProgressDialog();
        MyLockAPI lockAPI = MyLockAPI.getLockAPI();
        if (lockAPI.isConnected(mLockKey.getLockMac())) {
            lockAPI.resetLock(null, mLockKey.getOpenid(), mLockKey.getLockVersion(),
                    mLockKey.getAdminPwd(), mLockKey.getLockKey(), mLockKey.getLockFlagPos(), mLockKey.getAesKeystr());
        } else {
            lockAPI.connect(mLockKey.getLockMac(), Operation.RESET_LOCK);
        }
    }

    private void deleteFromServer() {
        LockHttpAction.getHttpAction(this).delKey(mLockKey.getKeyId(), new OnHttpRequestCallback<Boolean>() {
            @Override
            public void onFailure(int errorCode) {
                ToastUtil.showToast(getApplicationContext(), "删除失败" + errorCode);
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                ToastUtil.showToast(getApplicationContext(), "删除成功");
                startNewActivity(MainActivity.class);
                finish();
            }
        });
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
                case LockConstant.ACTION_RESET_LOCK:
                    cancelProgressDialog();
                    if (Error.SUCCESS == error) {
                        deleteFromServer();
                    } else {
                        ToastUtil.showToast(getApplicationContext(), error.getDescription());
                    }
                    break;
                case LockConstant.ACTION_SET_ADMIN_PWD:
                    cancelProgressDialog();
                    if (Error.SUCCESS == error) {
                        setAdminPwd(intent.getStringExtra("password"));
                    } else {
                        ToastUtil.showToast(getApplicationContext(), error.getDescription());
                    }
                    break;
            }
        }
    }

    private void setAdminPwd(final String password) {
        LockHttpAction.getHttpAction(getApplicationContext()).changeAdminKeyboardPwd(mLockKey.getLockId(), password, new OnHttpRequestCallback<Boolean>() {
            @Override
            public void onFailure(int errorCode) {
                ToastUtil.showToast(getApplicationContext(), "修改失败" + errorCode);
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                ToastUtil.showToast(getApplicationContext(), "修改成功");
                tvLockPwd.setText(password);
                setResult(RESULT_OK);
            }
        });
    }
}
