package cn.jcyh.eaglekinglockdemo;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.lock.bl.sdk.api.LockAPI;
import com.lock.bl.sdk.util.Timber;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.bean.LockKey;
import cn.jcyh.eaglekinglockdemo.bean.SyncData;
import cn.jcyh.eaglekinglockdemo.bean.User;
import cn.jcyh.eaglekinglockdemo.constant.Constants;
import cn.jcyh.eaglekinglockdemo.http.HttpAction;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestCallback;
import cn.jcyh.eaglekinglockdemo.ui.activity.AuthActivity;
import cn.jcyh.eaglekinglockdemo.ui.activity.OperateActivity;
import cn.jcyh.eaglekinglockdemo.utils.SharePreUtil;

public class MainActivity extends BaseActivity {
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    private ProgressDialog mProgressDialog;
    private BaseQuickAdapter<LockKey, BaseViewHolder> mAdapter;
    private List<LockKey> mLockKeys;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        //请求打开蓝牙
        MyApp.mLockAPI.requestBleEnable(this);
        MyApp.mLockAPI.startBleService(this);
        AndPermission.with(this)
                .permission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Timber.e("-----蓝牙服务");
                        MyApp.mLockAPI.startBTDeviceScan();
                    }
                }).onDenied(new Action() {
            @Override
            public void onAction(List<String> permissions) {
                finish();
            }
        }).start();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("请稍候...");
        mLockKeys = new ArrayList<>();
        mAdapter = new BaseQuickAdapter<LockKey, BaseViewHolder>(android.R.layout.simple_list_item_1, mLockKeys) {
            @Override
            protected void convert(BaseViewHolder helper, LockKey item) {
                helper.setText(android.R.id.text1, item.getLockName() + "");
            }
        };
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        rvContent.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startNewActivity(OperateActivity.class, "key", adapter.getItem(position));
            }
        });
        syncData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        MyApp.mLockAPI.stopBleService(this);
    }

    @OnClick({R.id.btn_auth})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_auth:
                startNewActivity(AuthActivity.class);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LockAPI.REQUEST_ENABLE_BT) {
                //打开蓝牙之后启动扫描
                MyApp.mLockAPI.startBTDeviceScan();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        syncData();
    }


    private void syncData() {
        mProgressDialog.show();
        User user = new Gson().fromJson(SharePreUtil.getInstance(getApplicationContext()).getString(Constants.USER_INFO, ""), User.class);
        Timber.e("-----user:" + user);
        if (user != null)
            HttpAction.getHttpAction(this).syncData(0, user.getAccess_token(), new OnHttpRequestCallback<SyncData>() {
                @Override
                public void onFailure(int errorCode) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                }

                @Override
                public void onSuccess(SyncData syncData) {
                    Timber.e("--------sync:" + syncData);
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (syncData != null && syncData.getKeyList() != null) {
                        mAdapter.setNewData(syncData.getKeyList());
                        SharePreUtil.getInstance(getApplicationContext()).setString(Constants.KEY_LIST, new Gson().toJson(syncData.getKeyList()));
                    }
                }
            });
    }
}
