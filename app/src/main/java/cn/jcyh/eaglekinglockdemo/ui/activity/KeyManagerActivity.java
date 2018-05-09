package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ttlock.bl.sdk.bean.KeyResult;
import com.ttlock.bl.sdk.bean.LockKey;
import com.ttlock.bl.sdk.constant.KeyStatus;
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
import cn.jcyh.eaglekinglockdemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.utils.Timber;

//钥匙管理
public class KeyManagerActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    private BaseQuickAdapter<LockKey, BaseViewHolder> mAdapter;
    private List<LockKey> mLockKeys;

    @Override
    public int getLayoutId() {
        return R.layout.activity_key_manager;
    }

    @Override
    protected void init() {
        tvTitle.setText("电子钥匙");
        final LockKey lockKey = getIntent().getParcelableExtra("key");
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
            }
        };
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        rvContent.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        String accessToken = ControlCenter.getControlCenter(getApplicationContext()).getUserInfo().getAccess_token();
        LockHttpAction.getHttpAction(this).getLockKeys(accessToken, lockKey.getLockId(), 1, 20, new OnHttpRequestCallback<KeyResult>() {
            @Override
            public void onFailure(int errorCode) {
                Timber.e("------errorCode:" + errorCode);
            }

            @Override
            public void onSuccess(KeyResult keyResult) {
                if (keyResult.getList() != null)
                    mAdapter.setNewData(keyResult.getList());
            }
        });
    }

    @OnClick({R.id.ibtn_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        startNewActivity(KeyInfoActivity.class, "key", adapter.getItem(position));
    }
}
