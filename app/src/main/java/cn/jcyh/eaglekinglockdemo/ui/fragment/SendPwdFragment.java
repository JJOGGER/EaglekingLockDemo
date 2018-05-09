package cn.jcyh.eaglekinglockdemo.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.contrarywind.view.WheelView;
import com.ttlock.bl.sdk.api.LockAPI;
import com.ttlock.bl.sdk.bean.LockKey;
import com.ttlock.bl.sdk.bean.LockKeyboardPwd;
import com.ttlock.bl.sdk.http.LockHttpAction;
import com.ttlock.bl.sdk.http.OnHttpRequestCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseFragment;
import cn.jcyh.eaglekinglockdemo.bean.KeyboardPasswdType;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.enumtype.Operation;
import cn.jcyh.eaglekinglockdemo.ui.dialog.CommonEditDialog;
import cn.jcyh.eaglekinglockdemo.utils.Timber;
import cn.jcyh.eaglekinglockdemo.utils.ToastUtil;

/**
 * Created by jogger on 2018/5/7.
 */

public class SendPwdFragment extends BaseFragment {
    @BindView(R.id.tv_pwd)
    TextView tvPwd;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    @BindView(R.id.rl_repeat)
    RelativeLayout rlRepeat;
    @BindView(R.id.rl_create_pwd)
    RelativeLayout rlCreatePwd;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.tv_repeat)
    TextView tvRepeat;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.tv_create_pwd)
    TextView tvCreatePwd;
    private LockKey mLockKey;
    private int mPos;
    private long mStartTime, mEndTime;
    private int mKeyboardPwdType;
    private String mAccessToken;
    private TimePickerView mStartPickerView, mEndPickerView;
    private AlertDialog mAlertDialog;
    private int mCurrentRepeat;
    private String[] mArray;
    private MyReceiver mReceiver;
    private String mCustomPwd;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_send_pwd;
    }

    @Override
    public void init() {
        mLockKey = getArguments().getParcelable("key");
        mPos = getArguments().getInt("pos");
        mAccessToken = ControlCenter.getControlCenter(mActivity).getUserInfo().getAccess_token();
        mStartTime = System.currentTimeMillis();
        mEndTime = mStartTime + 1000 * 60 * 60;
        Date date = new Date(mStartTime);
        tvStartTime.setText(SimpleDateFormat.getInstance().format(date));
        date.setTime(mEndTime);
        tvRepeat.setText(getResources().getStringArray(R.array.week)[0]);
        tvEndTime.setText(SimpleDateFormat.getInstance().format(date));
        //时间选择器
        TimePickerBuilder startBuilder = new TimePickerBuilder(mActivity, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String startTime = SimpleDateFormat.getInstance().format(date);
                Timber.e("---------->format" + startTime);
                mStartTime = date.getTime();
                tvStartTime.setText(startTime);

            }
        });
        TimePickerBuilder endBuilder = new TimePickerBuilder(mActivity, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String startTime = SimpleDateFormat.getInstance().format(date);
                Timber.e("---------->format" + startTime);
                mEndTime = date.getTime();
                tvEndTime.setText(startTime);
            }
        });
        switch (mPos) {
            case 0://永久
                rlCreatePwd.setVisibility(View.VISIBLE);
                break;
            case 1://限时
                llTime.setVisibility(View.VISIBLE);
                rlCreatePwd.setVisibility(View.VISIBLE);
                startBuilder.setType(new boolean[]{true, true, true, true, false, false});
                endBuilder.setType(new boolean[]{true, true, true, true, false, false});
                break;
            case 2://单次
                rlCreatePwd.setVisibility(View.VISIBLE);
                tvHint.setText("密码有效期为6小时，只能使用1次");
                break;
            case 3://清空
                startBuilder.setType(new boolean[]{true, true, true, true, false, false});
                endBuilder.setType(new boolean[]{true, true, true, true, false, false});
                rlCreatePwd.setVisibility(View.VISIBLE);
                break;
            case 4://自定义
                llTime.setVisibility(View.VISIBLE);
                rlCreatePwd.setVisibility(View.VISIBLE);
                startBuilder.setType(new boolean[]{true, true, true, true, true, false});
                endBuilder.setType(new boolean[]{true, true, true, true, true, false});
                tvCreatePwd.setText("设置密码");
                break;
            case 5://循环
                startBuilder.setType(new boolean[]{false, false, false, true, false, false});
                endBuilder.setType(new boolean[]{false, false, false, true, false, false});
                llTime.setVisibility(View.VISIBLE);
                rlCreatePwd.setVisibility(View.VISIBLE);
                rlRepeat.setVisibility(View.VISIBLE);
                break;
        }
        mStartPickerView = startBuilder.build();
        mEndPickerView = endBuilder.build();
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LockConstant.ACTION_CUSTOM_PWD);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mReceiver, intentFilter);
    }

    @OnClick({R.id.tv_create_pwd, R.id.rl_start_time, R.id.rl_end_time, R.id.rl_repeat})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_create_pwd:
                createPwd();
                break;
            case R.id.rl_start_time:
                mStartPickerView.show();
                break;
            case R.id.rl_end_time:
                mEndPickerView.show();
                break;
            case R.id.rl_repeat:
                showRepeatDialog();
                break;
        }
    }

    private void showRepeatDialog() {
        if (mAlertDialog == null) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_repeat_layout, null);
            final WheelView wvDate = view.findViewById(R.id.wv_date);
            wvDate.setCyclic(false);
            List<String> weeks = new ArrayList<>();
            final String[] array = getResources().getStringArray(R.array.week);
            Collections.addAll(weeks, array);
            wvDate.setAdapter(new ArrayWheelAdapter(weeks));
            mAlertDialog = new AlertDialog.Builder(mActivity)
                    .setView(view)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAlertDialog.dismiss();
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mCurrentRepeat = wvDate.getCurrentItem();
                            mAlertDialog.dismiss();
                            tvRepeat.setText(array[mCurrentRepeat]);
                        }
                    }).create();
        }
        mAlertDialog.show();

    }

    private void createPwd() {
        if (mPos == 4) {
            final CommonEditDialog commonEditDialog = new CommonEditDialog();
            commonEditDialog.setOnDialogListener(new CommonEditDialog.OnCommonEditDialogListener() {
                @Override
                public void onConfirm(boolean isConfirm) {
                    commonEditDialog.dismiss();
                    if (isConfirm) {
                        //先传给锁
                        showProgressDialog();
                        mCustomPwd = commonEditDialog.getEditText();
                        int openid = ControlCenter.getControlCenter(mActivity).getUserInfo().getOpenid();
                        if (LockAPI.getLockAPI(mActivity).isConnected(mLockKey.getLockMac()))
                            LockAPI.getLockAPI(mActivity).addPeriodKeyboardPassword(null,
                                    openid,
                                    mLockKey.getLockVersion(),
                                    mLockKey.getAdminPwd(),
                                    mLockKey.getLockKey(),
                                    mLockKey.getLockFlagPos(),
                                    mCustomPwd,
                                    mStartTime,
                                    mEndTime,
                                    mLockKey.getAesKeystr(),
                                    mLockKey.getTimezoneRawOffset()
                            );
                        else {
                            ControlCenter.sBleSession.setOperation(Operation.CUSTOM_PWD);
                            Bundle bundle = new Bundle();
                            bundle.putString("pwd", mCustomPwd);
                            bundle.putLong("startTime", mStartTime);
                            bundle.putLong("endTime", mEndTime);
                            ControlCenter.sBleSession.setArgments(bundle);
                            LockAPI.getLockAPI(mActivity).connect(mLockKey.getLockMac());
                        }
//                        Timber.e("----content:" + content);

                    }
                }
            });
            commonEditDialog.show(getFragmentManager(), CommonEditDialog.class.getName());
            return;
        }
        switch (mPos) {
            case 0:
                mKeyboardPwdType = KeyboardPasswdType.PERMENANT;//永久
                break;
            case 1:
                mKeyboardPwdType = KeyboardPasswdType.PERIOD;//限时
                break;
            case 2:
                mKeyboardPwdType = KeyboardPasswdType.ONCE;//单次
                break;
            case 3:
                mKeyboardPwdType = KeyboardPasswdType.DELETE;
                break;
            case 5:
                mKeyboardPwdType = mCurrentRepeat + 5;
                break;
        }
        getPwd();
    }

    private void getPwd() {
        LockHttpAction.getHttpAction(mActivity).getPwd(mAccessToken,
                mLockKey.getLockId(),
                mKeyboardPwdType,
                mStartTime,
                mEndTime,
                new OnHttpRequestCallback<LockKeyboardPwd>() {
                    @Override
                    public void onFailure(int errorCode) {
                        Timber.e("-------error:" + errorCode);
                    }

                    @Override
                    public void onSuccess(LockKeyboardPwd lockKeyboardPwd) {
                        Timber.e("--------lock:" + lockKeyboardPwd);
                        if (lockKeyboardPwd != null)
                            tvPwd.setText(lockKeyboardPwd.getKeyboardPwd());
                    }
                }
        );
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
                case LockConstant.ACTION_CUSTOM_PWD:
                    if (!isShowingDialog()) return;
                    cancelProgressDialog();
                    Timber.e("------>" + intent.getStringExtra(LockConstant.ERROR_MSG));
                    LockHttpAction.getHttpAction(mActivity).customPwd(mAccessToken, mLockKey.getLockId(), mCustomPwd, mStartTime, mEndTime, new OnHttpRequestCallback<LockKeyboardPwd>() {
                        @Override
                        public void onFailure(int errorCode) {
                            cancelProgressDialog();
                            ToastUtil.showToast(mActivity, "设置失败");
                        }

                        @Override
                        public void onSuccess(LockKeyboardPwd lockKeyboardPwd) {
                            tvPwd.setText(mCustomPwd);
                            ToastUtil.showToast(mActivity, "设置成功");
                        }
                    });
                    break;
            }
        }
    }
}
