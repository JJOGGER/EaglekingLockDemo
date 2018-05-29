package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ttlock.bl.sdk.bean.LockKey;
import com.ttlock.bl.sdk.bean.LockPwdRecord;
import com.ttlock.bl.sdk.constant.Operation;
import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.http.HttpResult;
import com.ttlock.bl.sdk.http.LockHttpAction;
import com.ttlock.bl.sdk.http.OnHttpRequestCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.bean.KeyboardPasswdType;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.http.MyLockAPI;
import cn.jcyh.eaglekinglockdemo.utils.ToastUtil;

public class PwdManageActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    private LockKey mLockKey;
    private BaseQuickAdapter<LockPwdRecord, BaseViewHolder> mAdapter;
    private List<LockPwdRecord> mLockPwdRecords;
    private MyReceiver mReceiver;

    @Override
    protected void init() {
        tvTitle.setText("密码管理");
        mLockKey = getIntent().getParcelableExtra("key");
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        mLockPwdRecords = new ArrayList<>();
        mAdapter = new BaseQuickAdapter<LockPwdRecord, BaseViewHolder>(R.layout.rv_pwd_record_item, mLockPwdRecords) {
            @Override
            protected void convert(BaseViewHolder helper, LockPwdRecord item) {
                helper.setText(R.id.tv_pwd, item.getKeyboardPwd());
                Date date = new Date();
                date.setTime(item.getStartDate());
                String startDate = SimpleDateFormat.getInstance().format(date);
                date.setTime(item.getEndDate());
                String endDate = SimpleDateFormat.getInstance().format(date);
                switch (item.getKeyboardPwdType()) {
                    case KeyboardPasswdType.ONCE:
                        helper.setText(R.id.tv_type, "单次");
                        helper.setText(R.id.tv_date, startDate);
                        break;
                    case KeyboardPasswdType.PERMENANT:
                        helper.setText(R.id.tv_type, "永久");
                        helper.setText(R.id.tv_date, startDate);
                        break;
                    case KeyboardPasswdType.PERIOD:
                        helper.setText(R.id.tv_type, "限时");
                        helper.setText(R.id.tv_date, startDate + " - " + endDate);
                        break;
                    default:
                        helper.setText(R.id.tv_type, "自定义");
                        helper.setText(R.id.tv_date, startDate + " - " + endDate);
                        break;

                }
            }
        };
        rvContent.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(LockConstant.ACTION_RESET_PWD);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void loadData() {
        LockHttpAction.getHttpAction(this).getPwdsByLock(mLockKey.getLockId(), 1, 20, new OnHttpRequestCallback<HttpResult<LockPwdRecord>>() {
            @Override
            public void onFailure(int errorCode) {

            }

            @Override
            public void onSuccess(HttpResult<LockPwdRecord> httpResult) {
                if (httpResult != null && httpResult.getList() != null) {
                    mAdapter.setNewData(httpResult.getList());
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_pwd_manage;
    }

    @OnClick({R.id.ibtn_back, R.id.tv_reset, R.id.tv_send})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.tv_reset:
                resetPwd();
                break;
            case R.id.tv_send:
                startNewActivity(SendPwdActivity.class, "key", mLockKey);
                finish();
                break;
        }
    }

    /**
     * 重置密码
     */
    private void resetPwd() {
        MyLockAPI lockAPI = MyLockAPI.getLockAPI();
        showProgressDialog();
        if (lockAPI.isConnected(mLockKey.getLockMac()))
            lockAPI.resetKeyboardPassword(null, mLockKey);
        else
            lockAPI.connect(mLockKey.getLockMac(), Operation.RESET_KEYBOARD_PASSWORD);
//        LockHttpAction.getHttpAction(this).resetKey();
    }

    private void resetFromServer(String pwdInfo, long timestamp) {
        LockHttpAction.getHttpAction(this).resetPwd(mLockKey.getLockId(), pwdInfo, timestamp, new OnHttpRequestCallback<Boolean>() {
            @Override
            public void onFailure(int errorCode) {
                cancelProgressDialog();
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                cancelProgressDialog();
                ToastUtil.showToast(getApplicationContext(), "重置成功");
                loadData();
            }
        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

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
            switch (action) {
                case LockConstant.ACTION_RESET_PWD:
                    Error error = (Error) intent.getSerializableExtra(LockConstant.ERROR_MSG);
                    if (Error.SUCCESS == error) {
                        resetFromServer(intent.getStringExtra("pwdInfo"), intent.getLongExtra("timestamp", 0));
                    } else {
                        ToastUtil.showToast(getApplicationContext(), error.getDescription());
                        cancelProgressDialog();
                    }
                    break;
            }
        }
    }
}
