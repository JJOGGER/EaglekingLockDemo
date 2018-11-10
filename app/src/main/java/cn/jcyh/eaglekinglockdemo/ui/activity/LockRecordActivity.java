package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.entity.LockKey;
import cn.jcyh.eaglekinglockdemo.http.HttpResult;
import cn.jcyh.eaglekinglockdemo.http.LockHttpAction;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestCallback;
import cn.jcyh.locklib.entity.LockRecord;

public class LockRecordActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    private BaseQuickAdapter<LockRecord, BaseViewHolder> mAdapter;
    private List<LockRecord> mLockRecords;
    private LockKey mLockKey;

    @Override
    public int getLayoutId() {
        return R.layout.activity_lock_record;
    }

    @Override
    protected void init() {
        tvTitle.setText("开锁记录");
        mLockKey = getIntent().getParcelableExtra(LockConstant.LOCK_KEY);
        mLockRecords = new ArrayList<>();
        mAdapter = new BaseQuickAdapter<LockRecord, BaseViewHolder>(R.layout.rv_lock_record_item, mLockRecords) {
            @Override
            protected void convert(BaseViewHolder helper, LockRecord item) {
                helper.setText(R.id.tv_name, item.getUsername() + "");
                Date date = new Date();
                date.setTime(item.getLockDate());
                String time = SimpleDateFormat.getInstance().format(date);
                StringBuilder sb = new StringBuilder();
                sb.append(time);
                switch (item.getRecordType()) {
                    case 1:
                        sb.append("用APP开锁");
                        break;
                    case 2:
                        sb.append("动过车位锁");
                        break;
                    case 3:
                        sb.append("插座开锁");
                        break;
                    case 4:
                        sb.append("键盘密码开锁");
                        break;
                    case 5:
                        sb.append("车位锁升");
                        break;
                    case 6:
                        sb.append("车位锁降");
                        break;
                    case 7:
                        sb.append("IC卡开锁");
                        break;
                    case 8:
                        sb.append("指纹开锁");
                        break;
                    case 9:
                        sb.append("手环开锁");
                        break;
                    case 10:
                        sb.append("机械钥匙开锁");
                        break;
                    case 11:
                        sb.append("蓝牙闭锁");
                        break;
                    case 12:
                        sb.append("网关开锁");
                        break;
                    case 29:
                        sb.append("非法开锁");
                        break;
                    case 30:
                        sb.append("门磁合上");
                        break;
                    case 31:
                        sb.append("门磁打开");
                        break;
                }
                helper.setText(R.id.tv_content, sb.toString());
            }
        };
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        rvContent.setAdapter(mAdapter);
    }

    @Override
    protected void loadData() {
        long endDate = System.currentTimeMillis();
        long startDate = endDate - 60 * 1000 * 60 * 24 * 3;
        LockHttpAction.getHttpAction(this).getLockRecords(mLockKey.getLockId(), startDate, endDate, 1, 20, new OnHttpRequestCallback<HttpResult<LockRecord>>() {

            @Override
            public void onFailure(int errorCode, String desc) {

            }

            @Override
            public void onSuccess(HttpResult<LockRecord> httpResult) {
                List<LockRecord> list = httpResult.getList();
                if (list != null)
                    mAdapter.setNewData(list);
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
}
