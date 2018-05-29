package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.ttlock.bl.sdk.bean.LockKey;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.utils.Timber;

public class AddFingerprintActivity extends BaseActivity {
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

    private long mStartTime, mEndTime;
    private TimePickerView mStartPickerView, mEndPickerView;
    private LockKey mLockKey;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_fingerprint;
    }

    @Override
    protected void init() {
        tvTitle.setText("添加指纹");
        mLockKey = getIntent().getParcelableExtra("key");
        scIsForever.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mStartTime = 0;
                    mEndTime = 0;
                }
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
                Timber.e("---------->format" + startTime);
                mStartTime = date.getTime();
                tvStartTime.setText(startTime);

            }
        }).setType(new boolean[]{false, false, false, true, false, false});
        TimePickerBuilder endBuilder = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String startTime = SimpleDateFormat.getInstance().format(date);
                Timber.e("---------->format" + startTime);
                mEndTime = date.getTime();
                tvEndTime.setText(startTime);
            }
        }).setType(new boolean[]{false, false, false, true, false, false});
        mStartPickerView = startBuilder.build();
        mEndPickerView = endBuilder.build();
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
                Intent intent = new Intent(this, AddFingerprint2Activity.class);
                intent.putExtra("startDate", mStartTime);
                intent.putExtra("endDate", mEndTime);
                intent.putExtra("key",mLockKey);
                startActivity(intent);
                break;
        }
    }

}