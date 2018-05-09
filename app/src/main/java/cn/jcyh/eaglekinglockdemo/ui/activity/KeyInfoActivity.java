package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ttlock.bl.sdk.bean.LockKey;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.utils.Timber;

//钥匙信息
public class KeyInfoActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_received_account)
    TextView tvReceivedAccount;
    @BindView(R.id.tv_send_account)
    TextView tvSendAccount;
    @BindView(R.id.tv_send_time)
    TextView tvSendTime;
    private LockKey mLockKey;

    @Override
    public int getLayoutId() {
        return R.layout.activity_key_info;
    }

    @Override
    protected void init() {
        tvTitle.setText("钥匙信息");
        mLockKey = getIntent().getParcelableExtra("key");
        Timber.e("-----lockkey:"+mLockKey);
        Date date = new Date();
        if (!TextUtils.isEmpty(mLockKey.getUsername()))
            tvName.setText(mLockKey.getUsername());
        String keyStatus = mLockKey.getKeyStatus();
        if (mLockKey.getEndDate() == 1) {
            tvDate.setText("单次");
        } else if (mLockKey.getEndDate() == 0) {
            tvDate.setText("永久");
        } else {
            date.setTime(mLockKey.getStartDate());
            String startDate = SimpleDateFormat.getInstance().format(date);
            date.setTime(mLockKey.getEndDate());
            String endDate = SimpleDateFormat.getInstance().format(date);
            tvDate.setText(startDate + "\n" + endDate);
        }
        tvReceivedAccount.setText(mLockKey.getUsername());
        tvSendAccount.setText(mLockKey.getSenderUsername());
        date.setTime(mLockKey.getTimestamp());
        String sendDate = SimpleDateFormat.getInstance().format(date);
        tvSendTime.setText(sendDate);
    }


    @OnClick({R.id.ibtn_back, R.id.rl_name, R.id.rl_record, R.id.tv_delete})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.rl_name:

                break;
            case R.id.rl_record:
                break;
            case R.id.tv_delete:
                break;
        }
    }
}
