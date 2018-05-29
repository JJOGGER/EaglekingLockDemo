package cn.jcyh.eaglekinglockdemo.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ttlock.bl.sdk.bean.LockKey;
import com.ttlock.bl.sdk.constant.Operation;
import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.util.DigitUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseFragment;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.http.MyLockAPI;
import cn.jcyh.eaglekinglockdemo.ui.activity.FingerprintManageActivity;
import cn.jcyh.eaglekinglockdemo.ui.activity.ICManageActivity;
import cn.jcyh.eaglekinglockdemo.ui.activity.KeyManagerActivity;
import cn.jcyh.eaglekinglockdemo.ui.activity.LockRecordActivity;
import cn.jcyh.eaglekinglockdemo.ui.activity.PwdManageActivity;
import cn.jcyh.eaglekinglockdemo.ui.activity.SendKeyActivity;
import cn.jcyh.eaglekinglockdemo.ui.activity.SendPwdActivity;
import cn.jcyh.eaglekinglockdemo.ui.activity.SettingActivity;
import cn.jcyh.eaglekinglockdemo.utils.Timber;
import cn.jcyh.eaglekinglockdemo.utils.ToastUtil;

/**
 * Created by jogger on 2018/5/4.锁主页
 */

public class LockMainFragment extends BaseFragment {
    @BindView(R.id.tv_lock_name)
    TextView tvLockName;
    @BindView(R.id.rl_admin_view)
    RelativeLayout rlAdminView;
    @BindView(R.id.rl_user_view)
    RelativeLayout rlUserView;
    @BindView(R.id.tv_ic_card)
    TextView tvICCard;
    @BindView(R.id.tv_fingerprint)
    TextView tvFingerprint;
    private MyLockAPI mLockAPI;
    private LockKey mLockKey;
    private MyReceiver mReceiver;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_lock_main;
    }

    @Override
    public void init() {
        mLockAPI = MyLockAPI.getLockAPI();
        mLockKey = getArguments().getParcelable("key");
        assert mLockKey != null;
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LockConstant.ACTION_UNLOCK);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mReceiver, intentFilter);
        tvFingerprint.setVisibility(DigitUtil.isSupportFingerPrint(mLockKey.getSpecialValue()) ? View.VISIBLE : View.GONE);
        tvICCard.setVisibility(DigitUtil.isSupportIC(mLockKey.getSpecialValue()) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void loadData() {
        super.loadData();
        Timber.e("--------------loadData");
        List<LockKey> lockKeys = ControlCenter.getControlCenter(mActivity).getLockKeys();
        if (lockKeys == null) return;
        for (int i = 0; i < lockKeys.size(); i++) {
            if (lockKeys.get(i).getLockId() == mLockKey.getLockId()) {
                mLockKey = lockKeys.get(i);
                break;
            }
        }
        tvLockName.setText(TextUtils.isEmpty(mLockKey.getLockAlias()) ? mLockKey.getLockName() + "" : mLockKey.getLockAlias());
    }

    @OnClick({R.id.ibtn_back, R.id.fl_unlock, R.id.tv_send_key, R.id.tv_send_pwd, R.id.tv_key_manage, R.id.tv_pwd_manage, R.id.tv_ic_card, R.id.tv_fingerprint, R.id.tv_record, R.id.tv_set})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                getFragmentManager().beginTransaction().remove(this).commit();
                break;
            case R.id.fl_unlock:
                unlock();
                break;
            case R.id.tv_send_key:
                startNewActivity(SendKeyActivity.class, "key", mLockKey);
                break;
            case R.id.tv_send_pwd:
                startNewActivity(SendPwdActivity.class, "key", mLockKey);
                break;
            case R.id.tv_key_manage:
                startNewActivity(KeyManagerActivity.class, "key", mLockKey);
                break;
            case R.id.tv_pwd_manage:
                startNewActivity(PwdManageActivity.class, "key", mLockKey);
                break;
            case R.id.tv_ic_card:
                startNewActivity(ICManageActivity.class, "key", mLockKey);
                break;
            case R.id.tv_fingerprint:
                startNewActivity(FingerprintManageActivity.class, "key", mLockKey);
                break;
            case R.id.tv_record:
                startNewActivity(LockRecordActivity.class,"key",mLockKey);
                break;
            case R.id.tv_set:
                Intent intent = new Intent(mActivity, SettingActivity.class);
                intent.putExtra("key", mLockKey);
                startActivityForResult(intent, 1);
                break;
        }
    }

    /**
     * 解锁
     */
    private void unlock() {
        if (mLockAPI.isConnected(mLockKey.getLockMac())) {
            if (mLockKey.isAdmin())
                mLockAPI.unlockByAdministrator(null, mLockKey);
            else
                mLockAPI.unlockByUser(null, mLockKey);
        } else {
            showProgressDialog();
            mLockAPI.connect(mLockKey.getLockMac(), Operation.LOCKCAR_DOWN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mReceiver);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) return;
            switch (action) {
                case LockConstant.ACTION_UNLOCK:
                    cancelProgressDialog();
                    Error error = (Error) intent.getSerializableExtra(LockConstant.ERROR_MSG);
                    ToastUtil.showToast(mActivity, error.getDescription());
                    break;
            }
        }
    }
}
