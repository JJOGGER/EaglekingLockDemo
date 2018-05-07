package cn.jcyh.eaglekinglockdemo.ui.fragment;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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
import cn.jcyh.eaglekinglockdemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.utils.Timber;

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
            case 4:
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
}
