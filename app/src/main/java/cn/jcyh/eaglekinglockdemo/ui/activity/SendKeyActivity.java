package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.ttlock.bl.sdk.bean.LockKey;
import com.ttlock.bl.sdk.bean.LockUser;
import com.ttlock.bl.sdk.http.LockHttpAction;
import com.ttlock.bl.sdk.http.OnHttpRequestCallback;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.utils.Timber;
import cn.jcyh.eaglekinglockdemo.utils.ToastUtil;

public class SendKeyActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.et_key_account)
    EditText etKeyAccount;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_single_hint)
    TextView tvSingleHint;
    private TimePickerView mStartPickerView, mEndPickerView;
    private LockKey mLockKey;
    private long mStartTime, mEndTime;
    private int mCurrentType;
    private PopupWindow mPopupWindow;

    @Override
    public int getLayoutId() {
        return R.layout.activity_send_key;
    }

    @Override
    protected void init() {
        super.init();
        tvTitle.setText("发送电子钥匙");
        mLockKey = getIntent().getParcelableExtra("key");
        mStartTime = System.currentTimeMillis();
        mEndTime = mStartTime + 1000 * 60 * 60;
        Date date = new Date(mStartTime);
        tvStartTime.setText(SimpleDateFormat.getInstance().format(date));
        date.setTime(mEndTime);
        tvEndTime.setText(SimpleDateFormat.getInstance().format(date));
        //时间选择器
        mStartPickerView = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String startTime = SimpleDateFormat.getInstance().format(date);
                Timber.e("---------->format" + startTime);
                mStartTime = date.getTime();
                tvStartTime.setText(startTime);

            }
        }).setType(new boolean[]{true, true, true, true, true, false})// 默认全部显示
                .build();
        mEndPickerView = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String endTime = SimpleDateFormat.getInstance().format(date);
                Timber.e("---------->format" + endTime);
                tvEndTime.setText(endTime);
                mEndTime = date.getTime();
            }
        }).setType(new boolean[]{true, true, true, true, true, false})
                .build();
    }

    @OnClick({R.id.ibtn_back, R.id.rl_key_type, R.id.rl_start_time, R.id.rl_end_time, R.id.tv_send_key})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.rl_key_type:
                chooseKeyType();
                break;
            case R.id.rl_start_time:
                mStartPickerView.show();
                break;
            case R.id.rl_end_time:
                mEndPickerView.show();
                break;
            case R.id.tv_send_key:
                sendKey();
                break;
        }
    }


    private void chooseKeyType() {
        if (mPopupWindow != null) {
            if (mPopupWindow.isShowing())
                mPopupWindow.dismiss();
            else {
                mPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
                backgroundAlpha(0.5f);
            }
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.window_key_type_layout, null);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        view.findViewById(R.id.tv_limit_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentType = 0;
                mPopupWindow.dismiss();
            }
        });
        view.findViewById(R.id.tv_forever).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentType = 1;
                mPopupWindow.dismiss();
            }
        });
        view.findViewById(R.id.tv_single).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentType = 2;
                mPopupWindow.dismiss();
            }
        });
        backgroundAlpha(0.5f);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1);
                if (mCurrentType == 0) {
                    llTime.setVisibility(View.VISIBLE);
                    tvSingleHint.setVisibility(View.GONE);
                    tvType.setText("限时");
                } else {
                    llTime.setVisibility(View.GONE);
                    if (mCurrentType == 1) {
                        tvType.setText("永久");
                        tvSingleHint.setVisibility(View.GONE);
                    } else {
                        tvType.setText("单次");
                        tvSingleHint.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    private void sendKey() {
        String account = etKeyAccount.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtil.showToast(getApplicationContext(), "输入不能为空");
            return;
        }
        if (mCurrentType == 0)
            if (mEndTime - mStartTime <= 0) {
                ToastUtil.showToast(getApplicationContext(), "时间选择有误");
                return;
            }
        showProgressDialog();
        if (mCurrentType == 1) {
            mStartTime = 0;
            mEndTime = 0;
        } else if (mCurrentType == 2) {
            mStartTime = System.currentTimeMillis();
            mEndTime = 1;
        }
        LockUser userInfo = ControlCenter.getControlCenter(this).getUserInfo();
        LockHttpAction.getHttpAction(this).sendKey(userInfo.getAccess_token(),
                mLockKey.getLockId(),
                account,
                mStartTime,
                mEndTime,
                "",
                new OnHttpRequestCallback<Boolean>() {
                    @Override
                    public void onFailure(int errorCode) {
                        ToastUtil.showToast(getApplicationContext(), "发送失败" + errorCode);
                        cancelProgressDialog();
                    }

                    @Override
                    public void onSuccess(Boolean abBoolean) {
                        ToastUtil.showToast(getApplicationContext(), "发送成功");
                        cancelProgressDialog();
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mStartPickerView != null && mStartPickerView.isShowing())
            mStartPickerView.dismiss();
        if (mEndPickerView != null && mEndPickerView.isShowing())
            mEndPickerView.dismiss();
    }
}
