package cn.jcyh.eaglekinglockdemo.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseFragment;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.entity.LockKey;
import cn.jcyh.eaglekinglockdemo.http.MyLockAPI;
import cn.jcyh.utils.L;

/**
 * Created by jogger on 2018/5/4.
 */

public class LockListFragment extends BaseFragment {
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    private BaseQuickAdapter<LockKey, BaseViewHolder> mAdapter;
    private List<LockKey> mLockKeys;
    private MyLockAPI mLockAPI;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_lock_list;
    }

    @Override
    public void init() {
        //请求打开蓝牙
        mLockAPI = MyLockAPI.getLockAPI();
//        mLockAPI.requestBleEnable(mActivity);
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        bluetoothAdapter.enable();
        mLockAPI.startBleService(mActivity);
        AndPermission.with(this)
                .permission(Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        L.e("------onGranted");
                        mLockAPI.startBTDeviceScan();
                    }
                }).onDenied(new Action() {
            @Override
            public void onAction(List<String> permissions) {
                L.e("------onDenied");
            }
        }).start();
        mLockKeys = new ArrayList<>();
        mAdapter = new BaseQuickAdapter<LockKey, BaseViewHolder>(android.R.layout.simple_list_item_1, mLockKeys) {
            @Override
            protected void convert(BaseViewHolder helper, LockKey item) {
                helper.setText(android.R.id.text1, TextUtils.isEmpty(item.getLockAlias()) ? item.getLockName() + "" : item.getLockAlias());
            }
        };
        rvContent.setLayoutManager(new LinearLayoutManager(mActivity));
        rvContent.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ControlCenter.sCurrentKey = (LockKey) adapter.getItem(position);
                LockMainFragment lockMainFragment = new LockMainFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(LockConstant.LOCK_KEY, (Parcelable) adapter.getItem(position));
                lockMainFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.fl_container, lockMainFragment, LockMainFragment.class.getName())
                        .commit();
            }
        });
    }

    @Override
    public void loadData() {
        super.loadData();
        List<LockKey> lockKeys = ControlCenter.getControlCenter(mActivity).getLockKeys();
        if (lockKeys != null)
            mAdapter.setNewData(lockKeys);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mLockAPI.stopBleService(mActivity);
    }
}
