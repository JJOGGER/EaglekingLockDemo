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

import com.ttlock.bl.sdk.api.LockAPI;
import com.ttlock.bl.sdk.bean.LockKey;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseFragment;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.enumtype.Operation;
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
    private LockAPI mLockAPI;
    private LockKey mLockKey;
    private int mOpenid;
    private MyReceiver mReceiver;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_lock_main;
    }

    @Override
    public void init() {
        mLockAPI = LockAPI.getLockAPI(mActivity);
        mLockKey = getArguments().getParcelable("key");
        mOpenid = ControlCenter.getControlCenter(mActivity).getUserInfo().getOpenid();
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LockConstant.ACTION_UNLOCK);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mReceiver, intentFilter);
    }

    @OnClick({R.id.ibtn_back, R.id.fl_unlock, R.id.tv_send_key, R.id.tv_send_pwd, R.id.tv_key_manage, R.id.tv_pwd_manage, R.id.tv_ic_card, R.id.tv_finger, R.id.tv_record, R.id.tv_set})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_back:
                getFragmentManager().beginTransaction().remove(this).commit();
                break;
            case R.id.fl_unlock:
                unlock();
                break;
            case R.id.tv_send_key:
                break;
            case R.id.tv_send_pwd:
                break;
            case R.id.tv_key_manage:
                break;
            case R.id.tv_pwd_manage:
                break;
            case R.id.tv_ic_card:
                break;
            case R.id.tv_finger:
                break;
            case R.id.tv_record:
                break;
            case R.id.tv_set:
                break;
        }
    }

    /**
     * 解锁
     */
    private void unlock() {
        if (mLockAPI.isConnected(mLockKey.getLockMac())) {
            if (mLockKey.isAdmin())
                mLockAPI.unlockByAdministrator(mOpenid, mLockKey);
            else
                mLockAPI.unlockByUser(mOpenid, mLockKey);
        } else {
            showProgressDialog();
            mLockAPI.connect(mLockKey.getLockMac());
            ControlCenter.sBleSession.setOperation(Operation.CLICK_UNLOCK);
            ControlCenter.sBleSession.setLockmac(mLockKey.getLockMac());
        }
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
                    ToastUtil.showToast(mActivity, intent.getStringExtra(LockConstant.ERROR_MSG));
                    break;
            }
        }
    }
}
