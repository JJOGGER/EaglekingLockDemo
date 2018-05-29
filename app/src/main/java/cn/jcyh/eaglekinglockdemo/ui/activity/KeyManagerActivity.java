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
import com.ttlock.bl.sdk.constant.KeyStatus;
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
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.http.MyLockAPI;
import cn.jcyh.eaglekinglockdemo.utils.Timber;
import cn.jcyh.eaglekinglockdemo.utils.ToastUtil;

//钥匙管理
public class KeyManagerActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    private BaseQuickAdapter<LockKey, BaseViewHolder> mAdapter;
    private List<LockKey> mLockKeys;
    private final int UPDATE_REQUEST = 0X0A;
    private LockKey mLockKey;
    private MyReceiver mReceiver;

    @Override
    public int getLayoutId() {
        return R.layout.activity_key_manager;
    }

    @Override
    protected void init() {
        tvTitle.setText("电子钥匙");
        mLockKey = getIntent().getParcelableExtra("key");
        mLockKeys = new ArrayList<>();
        mAdapter = new BaseQuickAdapter<LockKey, BaseViewHolder>(R.layout.rv_keys_item, mLockKeys) {
            @Override
            protected void convert(BaseViewHolder helper, LockKey item) {
                helper.setText(R.id.tv_user_name, item.getUsername());
                helper.setText(R.id.tv_state, KeyStatus.getStatus(item.getKeyStatus()));
                if (item.getEndDate() > 1) {
                    helper.setText(R.id.tv_type, SimpleDateFormat.getInstance().format(new Date(item.getStartDate())) + "-" +
                            SimpleDateFormat.getInstance().format(new Date(item.getEndDate())));
                } else if (item.getEndDate() == 1) {
                    helper.setText(R.id.tv_type, "单次");
                } else {
                    helper.setText(R.id.tv_type, "永久");
                }
                switch (item.getKeyStatus()){
                    case "110402":
                        helper.setText(R.id.tv_state,"");
                        break;
                    case "110405":
                        helper.setText(R.id.tv_state,"已冻结");
                        break;
                    case "110408":
                        helper.setText(R.id.tv_state,"已删除");
                        break;
                    case "110410":
                        helper.setText(R.id.tv_state,"已重置");
                        break;
                }
            }
        };
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        rvContent.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(LockConstant.ACTION_RESET_KEY);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void loadData() {
        LockHttpAction.getHttpAction(this).getLockKeys(mLockKey.getLockId(), 1, 20, new OnHttpRequestCallback<HttpResult<LockKey>>() {
            @Override
            public void onFailure(int errorCode) {
                Timber.e("----loadData--errorCode:" + errorCode);
            }

            @Override
            public void onSuccess(HttpResult<LockKey> keyResult) {
                if (keyResult.getList() != null)
                    mAdapter.setNewData(keyResult.getList());
            }
        });
    }

    @OnClick({R.id.ibtn_back, R.id.tv_clear, R.id.tv_reset})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.tv_clear:
                clearKey();
                break;
            case R.id.tv_reset:
                resetKey();
                break;
        }
    }


    private void resetKey() {
        showProgressDialog();
        MyLockAPI lockAPI = MyLockAPI.getLockAPI();
        if (lockAPI.isConnected(mLockKey.getLockMac()))
            lockAPI.resetEKey(null, mLockKey);
        else {
            lockAPI.connect(mLockKey.getLockMac(), Operation.RESET_EKEY);
        }
    }

    private void resetFromServer() {
        LockHttpAction.getHttpAction(this).resetKey(mLockKey.getLockId(), new OnHttpRequestCallback<Boolean>() {
            @Override
            public void onFailure(int errorCode) {
                cancelProgressDialog();
                ToastUtil.showToast(getApplicationContext(), "操作失败" + errorCode);
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                cancelProgressDialog();
                ToastUtil.showToast(getApplicationContext(), "重置成功");
                loadData();
            }
        });
    }

    private void clearKey() {
        Timber.e("----------mlockkey:" + mLockKey);
        LockHttpAction.getHttpAction(this).delAllKeys(
                mLockKey.getLockId(),
                new OnHttpRequestCallback<Boolean>() {
                    @Override
                    public void onFailure(int errorCode) {
                        ToastUtil.showToast(getApplicationContext(), "操作失败" + errorCode);
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        ToastUtil.showToast(getApplicationContext(), "清空成功");
                        loadData();
                    }
                });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(this, KeyInfoActivity.class);
        intent.putExtra("key", (LockKey) adapter.getItem(position));
        startActivityForResult(intent, UPDATE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UPDATE_REQUEST)
            loadData();
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
                case LockConstant.ACTION_RESET_KEY:
                    Error error = (Error) intent.getSerializableExtra(LockConstant.ERROR_MSG);
                    if (Error.SUCCESS == error) {
                        resetFromServer();
                    } else {
                        ToastUtil.showToast(getApplicationContext(), error.getDescription());
                        cancelProgressDialog();
                    }
                    break;
            }
        }
    }
}
