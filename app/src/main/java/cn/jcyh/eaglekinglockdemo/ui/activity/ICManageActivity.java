package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ttlock.bl.sdk.bean.ICard;
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
import cn.jcyh.eaglekinglockdemo.ui.dialog.HintDialog;
import cn.jcyh.eaglekinglockdemo.utils.Timber;
import cn.jcyh.eaglekinglockdemo.utils.ToastUtil;

public class ICManageActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemLongClickListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    private BaseQuickAdapter<ICard, BaseViewHolder> mAdapter;
    private List<ICard> mICards;
    private final int UPDATE_REQUEST = 0X0A;
    private LockKey mLockKey;
    private MyReceiver mReceiver;
    private MyLockAPI mLockAPI;
    private ICard mCurrentIC;

    @Override
    public int getLayoutId() {
        return R.layout.activity_icmanage;
    }

    @Override
    protected void init() {
        tvTitle.setText("IC卡");
        mLockKey = getIntent().getParcelableExtra("key");
        mICards = new ArrayList<>();
        mLockAPI = MyLockAPI.getLockAPI();
        mAdapter = new BaseQuickAdapter<ICard, BaseViewHolder>(R.layout.rv_ics_item, mICards) {
            @Override
            protected void convert(BaseViewHolder helper, ICard item) {
                helper.setText(R.id.tv_name, item.getCardId() + "");
                helper.setText(R.id.tv_card_no, item.getCardNumber());
                helper.setText(R.id.tv_date, SimpleDateFormat.getInstance().format(new Date(item.getStartDate())) + "-" +
                        SimpleDateFormat.getInstance().format(new Date(item.getEndDate())));
            }
        };
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        rvContent.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(LockConstant.ACTION_LOCK_IC_CARD);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void loadData() {
        LockHttpAction.getHttpAction(this).getICs(mLockKey.getLockId(), 1, 20, new OnHttpRequestCallback<HttpResult<ICard>>() {
            @Override
            public void onFailure(int errorCode) {
                Timber.e("----loadData--errorCode:" + errorCode);
            }

            @Override
            public void onSuccess(HttpResult<ICard> icCardHttpResult) {
                if (icCardHttpResult.getList() != null)
                    mAdapter.setNewData(icCardHttpResult.getList());
            }
        });
    }

    @OnClick({R.id.ibtn_back, R.id.tv_clear, R.id.tv_add})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.tv_clear:
                clearIC();
                break;
            case R.id.tv_add:
                Intent intent = new Intent(this, AddICActivity.class);
                intent.putExtra("key", mLockKey);
                startActivityForResult(intent, UPDATE_REQUEST);
                break;
        }
    }

    private void clearIC() {
        showProgressDialog();
        if (mLockAPI.isConnected(mLockKey.getLockMac()))
            mLockAPI.clearICCard(null, mLockKey);
        else {
            mLockAPI.connect(mLockKey.getLockMac(), Operation.CLEAR_IC_CARD);
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

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        mCurrentIC = (ICard) adapter.getItem(position);
        Timber.e("------ic:"+mCurrentIC);
        if (mCurrentIC == null) return false;
        final HintDialog hintDialog = new HintDialog();
        hintDialog.setHintContent("确定删除？");
        hintDialog.setOnHintDialogListener(new HintDialog.OnHintDialogListener() {
            @Override
            public void onConfirm(boolean isConfirm) {
                hintDialog.dismiss();
                if (isConfirm) {
                    showProgressDialog();
                    if (mLockAPI.isConnected(mLockKey.getLockMac())) {
                        mLockAPI.deleteICCard(null,Long.valueOf(mCurrentIC.getCardNumber()), mLockKey);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putLong("cardNo",Long.valueOf(mCurrentIC.getCardNumber()));
                        MyLockAPI.sBleSession.setArgments(bundle);
                        mLockAPI.connect(mLockKey.getLockMac(), Operation.DELETE_IC_CARD);
                    }
                }
            }
        });
        hintDialog.show(getSupportFragmentManager(), HintDialog.class.getName());
        return false;
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) return;
            if (!LockConstant.ACTION_LOCK_IC_CARD.equals(action)) return;
            String type = intent.getStringExtra("type");
            Error error = (Error) intent.getSerializableExtra(LockConstant.ERROR_MSG);
            switch (type) {
                case LockConstant.TYPE_MODIFY_IC_CARD:
                    break;
                case LockConstant.TYPE_DELETE_IC_CARD:
                    if (Error.SUCCESS == error) {
                        deleteFromServer(intent.getLongExtra("cardNo", 0));
                    }
                    break;
                case LockConstant.TYPE_CLEAR_IC_CARD:
                    if (Error.SUCCESS == error) {
                        clearFromServer();
                    }
                    break;
            }

        }
    }

    private void deleteFromServer(long cardNo) {
        LockHttpAction.getHttpAction(this).deleteIC(mLockKey.getLockId(), mCurrentIC.getCardId(), new OnHttpRequestCallback<Boolean>() {
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
        LockHttpAction.getHttpAction(this).clearICs(mLockKey.getLockId(), new OnHttpRequestCallback<Boolean>() {
            @Override
            public void onFailure(int errorCode) {
                cancelProgressDialog();
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                ToastUtil.showToast(getApplicationContext(), "清空成功");
                cancelProgressDialog();
                loadData();
            }
        });
    }
}
