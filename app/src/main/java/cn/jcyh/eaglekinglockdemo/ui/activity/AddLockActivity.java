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
import com.ttlock.bl.sdk.constant.Operation;
import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.MainActivity;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.http.MyLockAPI;
import cn.jcyh.eaglekinglockdemo.utils.ToastUtil;

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
        MyLockAPI.getLockAPI().connect((ExtendedBluetoothDevice) adapter.getItem(position),Operation.ADD_ADMIN);
        showProgressDialog();

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(LockConstant.ACTION_BLE_DEVICE)) {
                Bundle bundle = intent.getExtras();
                ExtendedBluetoothDevice device = bundle.getParcelable(LockConstant.DEVICE);
                mAdapter.updateDevice(device);
            } else if (LockConstant.ACTION_ADD_ADMIN.equals(action)) {
                Error error = (Error) intent.getSerializableExtra(LockConstant.ERROR_MSG);
                ToastUtil.showToast(getApplicationContext(), error.getDescription());
                if (Error.SUCCESS.equals(error)) {
                    startNewActivity(MainActivity.class);
                    ToastUtil.showToast(getApplicationContext(), "添加成功");
                    cancelProgressDialog();
                    finish();
                }else {
                    ToastUtil.showToast(getApplicationContext(),error.getDescription());
                }
            }
//            else if(action.equals(BleConstant.ACTION_BLE_DISCONNECTED)) {
//                cancelProgressDialog();
//                Toast.makeText(FoundDeviceActivity.this, "蓝牙已断开,请重新添加.", Toast.LENGTH_LONG).show();
//            }
        }
    };

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
