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
import com.ttlock.bl.sdk.bean.LockFingerprint;
import com.ttlock.bl.sdk.bean.LockKey;
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

public class FingerprintManageActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    private BaseQuickAdapter<LockFingerprint, BaseViewHolder> mAdapter;
    private List<LockFingerprint> mLockFingerprints;
    private final int UPDATE_REQUEST = 0X0A;
    private LockKey mLockKey;
    private MyReceiver mReceiver;
    private MyLockAPI mLockAPI;
    private LockFingerprint mLockFingerprint;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fingerprint_manage;
    }

    @Override
    protected void init() {
        tvTitle.setText("指纹");
        mLockKey = getIntent().getParcelableExtra("key");
        mLockFingerprints = new ArrayList<>();
        mLockAPI = MyLockAPI.getLockAPI();
        mAdapter = new BaseQuickAdapter<LockFingerprint, BaseViewHolder>(R.layout.rv_fingerprint_item, mLockFingerprints) {
            @Override
            protected void convert(BaseViewHolder helper, LockFingerprint item) {
                helper.setText(R.id.tv_name, item.getFingerprintId() + "");
                helper.setText(R.id.tv_fingerprint_no, item.getFingerprintNumber());
                helper.setText(R.id.tv_date, SimpleDateFormat.getInstance().format(new Date(item.getStartDate())) + "-" +
                        SimpleDateFormat.getInstance().format(new Date(item.getEndDate())));
            }
        };
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        rvContent.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(LockConstant.ACTION_LOCK_FINGERPRINT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
    }
    public void initData(){
        LockHttpAction.getHttpAction(this).getFingerprints(mLockKey.getLockId(), 1, 20, new OnHttpRequestCallback<HttpResult<LockFingerprint>>() {
            @Override
            public void onFailure(int errorCode) {
                Timber.e("----loadData--errorCode:" + errorCode);
            }

            @Override
            public void onSuccess(HttpResult<LockFingerprint> fingerprintHttpResult) {
                if (fingerprintHttpResult.getList() != null)
                    mAdapter.setNewData(fingerprintHttpResult.getList());
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @OnClick({R.id.ibtn_back, R.id.tv_clear, R.id.tv_add})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.tv_clear:
                clearFingerprints();
                break;
            case R.id.tv_add:
                Intent intent = new Intent(this, AddFingerprintActivity.class);
                intent.putExtra("key", mLockKey);
                startActivityForResult(intent, UPDATE_REQUEST);
                break;
        }
    }

    private void clearFingerprints() {
        showProgressDialog();
        if (mLockAPI.isConnected(mLockKey.getLockMac()))
            mLockAPI.clearFingerPrint(null, mLockKey);
        else {
            mLockAPI.connect(mLockKey.getLockMac(), Operation.CLEAR_FINGERPRINTS);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
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
            if (!LockConstant.ACTION_LOCK_FINGERPRINT.equals(action)) return;
            String type = intent.getStringExtra("type");
            Error error = (Error) intent.getSerializableExtra(LockConstant.ERROR_MSG);
            switch (type) {
//                case LockConstant.TYPE_DELETE_FINGERPRINT:
//                    if (Error.SUCCESS == error) {
//                        deleteFromServer(intent.getLongExtra("FRNo", 0));
//                    }
//                    break;
                case LockConstant.TYPE_CLEAR_FINGERPRINT:
                    if (Error.SUCCESS == error) {
                        clearFromServer();
                    }
                    break;
            }

        }
    }


    private void deleteFromServer(long FRNo) {
        LockHttpAction.getHttpAction(this).deleteFingerprint(mLockKey.getLockId(), mLockFingerprint.getFingerprintId(), new OnHttpRequestCallback<Boolean>() {
            @Override
            public void onFailure(int errorCode) {
                cancelProgressDialog();
                ToastUtil.showToast(getApplicationContext(), "删除失败" + errorCode);
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                cancelProgressDialog();
                ToastUtil.showToast(getApplicationContext(), "删除成功");
                loadData();
            }
        });
    }

    private void clearFromServer() {
        LockHttpAction.getHttpAction(this).clearFingerprints(mLockKey.getLockId(), new OnHttpRequestCallback<Boolean>() {
            @Override
            public void onFailure(int errorCode) {
                cancelProgressDialog();
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                ToastUtil.showToast(getApplicationContext(), "清空成功");
                cancelProgressDialog();
                initData();
            }
        });
    }
}
