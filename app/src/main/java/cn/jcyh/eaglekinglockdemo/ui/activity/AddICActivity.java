package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.constant.Operation;
import cn.jcyh.eaglekinglockdemo.entity.LockKey;
import cn.jcyh.eaglekinglockdemo.http.LockHttpAction;
import cn.jcyh.eaglekinglockdemo.http.MyLockAPI;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestCallback;
import cn.jcyh.locklib.entity.Error;
import cn.jcyh.utils.L;
import cn.jcyh.utils.T;

public class AddICActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.sc_isforever)
    SwitchCompat scIsForever;
    @BindView(R.id.rl_start_time)
    RelativeLayout rlStartTime;
    @BindView(R.id.rl_end_time)
    RelativeLayout rlEndTime;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    private MyReceiver mReceiver;
    private long mStartTime, mEndTime;
    private TimePickerView mStartPickerView, mEndPickerView;
    private LockKey mLockKey;
    private MyLockAPI mLockAPI;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_ic;
    }

    @Override
    protected void init() {
        tvTitle.setText("添加IC卡");
        mReceiver = new MyReceiver();
        mLockKey = getIntent().getParcelableExtra(LockConstant.LOCK_KEY);
        mLockAPI = MyLockAPI.getLockAPI();
        IntentFilter intentFilter = new IntentFilter(LockConstant.ACTION_LOCK_IC_CARD);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
        scIsForever.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rlStartTime.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                rlEndTime.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });
        Date date = new Date();
        mStartTime = System.currentTimeMillis();
        mEndTime = mStartTime + 60 * 60 * 1000;
        date.setTime(mStartTime);
        String starttime = SimpleDateFormat.getInstance().format(date);
        tvStartTime.setText(starttime);
        date.setTime(mEndTime);
        String endtime = SimpleDateFormat.getInstance().format(date);
        tvEndTime.setText(endtime);
        //时间选择器
        TimePickerBuilder startBuilder = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String startTime = SimpleDateFormat.getInstance().format(date);
                L.e("---------->format" + startTime);
                mStartTime = date.getTime();
                mLockKey.setStartDate(mStartTime);
                tvStartTime.setText(startTime);

            }
        }).setType(new boolean[]{false, false, false, true, false, false});
        TimePickerBuilder endBuilder = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String startTime = SimpleDateFormat.getInstance().format(date);
                L.e("---------->format" + startTime);
                mEndTime = date.getTime();
                mLockKey.setEndDate(mEndTime);
                tvEndTime.setText(startTime);
            }
        }).setType(new boolean[]{false, false, false, true, false, false});
        mStartPickerView = startBuilder.build();
        mEndPickerView = endBuilder.build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @OnClick({R.id.ibtn_back, R.id.tv_start_time, R.id.tv_end_time, R.id.tv_confirm})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.tv_start_time:
                mStartPickerView.show();
                break;
            case R.id.tv_end_time:
                mEndPickerView.show();
                break;
            case R.id.tv_confirm:
                addIC();
                break;
        }
    }

    private void addIC() {
        showProgressDialog();
        if (mLockAPI.isConnected(mLockKey.getLockMac())) {
            mLockAPI.addICCard(null, mLockKey);
        } else {
            mLockAPI.connect(mLockKey.getLockMac(), Operation.ADD_IC_CARD);
        }
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) return;
            if (!LockConstant.ACTION_LOCK_IC_CARD.equals(action)) return;
            String type = intent.getStringExtra(LockConstant.TYPE);
            Error error = (Error) intent.getSerializableExtra(LockConstant.ERROR_MSG);
            switch (type) {
                case LockConstant.TYPE_ADD_IC_CARD:
                    if (Error.SUCCESS == error) {
                        int status = intent.getIntExtra(LockConstant.STATUS, -1);
                        if (status == 1 && getDialog() != null) {
                            getDialog().setMessage("进入添加模式，请把IC卡放置在扫描区");
                        } else if (status == 2) {
                            long cardNo = intent.getLongExtra(LockConstant.IC_CARD_NUMBER, 0);
                            if (scIsForever.isChecked()) {
                                mStartTime = 0;
                                mEndTime = 0;
                                addIC2Server(cardNo);
                            } else {
                                //修改有效期
                                mLockAPI = MyLockAPI.getLockAPI();
                                mLockKey.setStartDate(mStartTime);
                                mLockKey.setEndDate(mEndTime);
                                if (mLockAPI.isConnected(mLockKey.getLockMac())) {
                                    mLockAPI.modifyICPeriod(null, cardNo, mLockKey);
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putLong(LockConstant.IC_CARD_NUMBER, cardNo);
                                    MyLockAPI.sBleSession.setArgments(bundle);
                                    mLockAPI.connect(mLockKey.getLockMac(), Operation.MODIFY_IC_PERIOD);
                                }
                            }
                        } else {
                            T.show(error.getDescription() + status);
                            cancelProgressDialog();
                        }
                    } else {
                        T.show(error.getDescription());
                        cancelProgressDialog();
                    }
                    break;
                case LockConstant.TYPE_MODIFY_IC_CARD:
                    mStartTime = intent.getLongExtra(LockConstant.START_DATE, 0);
                    mEndTime = intent.getLongExtra(LockConstant.END_DATE, 0);
                    addIC2Server(intent.getLongExtra(LockConstant.IC_CARD_NUMBER, 0));
                    break;
                case LockConstant.TYPE_DELETE_IC_CARD:
                    break;
                case LockConstant.TYPE_CLEAR_IC_CARD:
                    break;
            }

        }
    }

    private void addIC2Server(long cardNo) {
        if (mEndTime - mStartTime < 0) {
            T.show("时间选择有误");
            return;
        }
        LockHttpAction.getHttpAction(getApplicationContext()).addIC(mLockKey.getLockId(), cardNo + "", mStartTime, mEndTime, new OnHttpRequestCallback<Boolean>() {

            @Override
            public void onFailure(int errorCode, String desc) {
                cancelProgressDialog();
                T.show("添加失败" + errorCode);
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                cancelProgressDialog();
                T.show("添加成功");
                setResult(RESULT_OK);
                finish();
            }
        });

    }
}
