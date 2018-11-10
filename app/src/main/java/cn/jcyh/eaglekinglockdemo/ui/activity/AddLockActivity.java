package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.constant.Operation;
import cn.jcyh.eaglekinglockdemo.entity.LockKey;
import cn.jcyh.eaglekinglockdemo.http.LockHttpAction;
import cn.jcyh.eaglekinglockdemo.http.MyLockAPI;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestCallback;
import cn.jcyh.eaglekinglockdemo.ui.dialog.ChooseFloorDialog;
import cn.jcyh.locklib.entity.Error;
import cn.jcyh.locklib.scanner.ExtendedBluetoothDevice;
import cn.jcyh.utils.L;
import cn.jcyh.utils.T;

public class AddLockActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    private MyAdapter mAdapter;
    private List<ExtendedBluetoothDevice> mBluetoothDevices;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_lock;
    }

    @Override
    protected void init() {
        mBluetoothDevices = new ArrayList<>();
        mAdapter = new MyAdapter(R.layout.rv_add_lock_item, mBluetoothDevices);
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        rvContent.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LockConstant.ACTION_BLE_DEVICE);
        intentFilter.addAction(LockConstant.ACTION_BLE_DISCONNECTED);
        intentFilter.addAction(LockConstant.ACTION_ADD_ADMIN);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
        MyLockAPI.getLockAPI().startBTDeviceScan();
    }

    @OnClick(R.id.ibtn_back)
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MyLockAPI.getLockAPI().connect((ExtendedBluetoothDevice) adapter.getItem(position), Operation.ADD_ADMIN);
        showProgressDialog();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (LockConstant.ACTION_BLE_DEVICE.equals(action)) {
                Bundle bundle = intent.getExtras();
                assert bundle != null;
                ExtendedBluetoothDevice device = bundle.getParcelable(LockConstant.DEVICE);
                mAdapter.updateDevice(device);
            } else if (LockConstant.ACTION_ADD_ADMIN.equals(action)) {
                Error error = (Error) intent.getSerializableExtra(LockConstant.ERROR_MSG);
                T.show(error.getDescription());
                if (Error.SUCCESS.equals(error)) {
//                    startNewActivity(MainActivity.class);
                    LockKey key = (LockKey) intent.getSerializableExtra(LockConstant.LOCK_KEY);
                    chooseFloorInfo(key);
                } else {
                    T.show(error.getDescription());
                }
            }
//            else if(action.equals(BleConstant.ACTION_BLE_DISCONNECTED)) {
//                cancelProgressDialog();
//                Toast.makeText(FoundDeviceActivity.this, "蓝牙已断开,请重新添加.", Toast.LENGTH_LONG).show();
//            }
        }
    };

    private void chooseFloorInfo(LockKey key) {
        ChooseFloorDialog chooseFloorDialog=new ChooseFloorDialog();
        Bundle bundle=new Bundle();
        bundle.putParcelable(LockConstant.LOCK_KEY,key);
        chooseFloorDialog.setArguments(bundle);
        T.show("锁添加成功，正在上传服务端进行初始化操作");
        LockHttpAction.getHttpAction(this).initLock(key, new OnHttpRequestCallback<Boolean>() {

            @Override
            public void onFailure(int errorCode, String desc) {
                L.e("--------------onFailure:" + desc + ":" + errorCode);
                //服务器添加失败
                T.show("服务器添加失败，请重置锁后再次尝试");
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                L.e("--------添加成功:");
                cancelProgressDialog();

                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    private class MyAdapter extends BaseQuickAdapter<ExtendedBluetoothDevice, BaseViewHolder> {

        MyAdapter(@LayoutRes int layoutResId, @Nullable List<ExtendedBluetoothDevice> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, ExtendedBluetoothDevice item) {
            helper.setText(R.id.tv_device_name, item.getName());
            helper.setVisible(R.id.iv_addable, item.isSettingMode());
        }

        void updateDevice(ExtendedBluetoothDevice device) {
            boolean contain = false;
            boolean update = false;
            for (ExtendedBluetoothDevice d : mBluetoothDevices) {
                if (d.equals(device)) {
                    contain = true;
                    if (d.isSettingMode() != device.isSettingMode()) {
                        d.setSettingMode(device.isSettingMode());
                        update = true;
                    }
                }
            }
            if (!contain) {
                mBluetoothDevices.add(device);
                update = true;
            }
            if (update)
                notifyDataSetChanged();
        }
    }

}
