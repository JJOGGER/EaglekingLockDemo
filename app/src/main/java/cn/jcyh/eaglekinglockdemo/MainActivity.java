package cn.jcyh.eaglekinglockdemo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.base.BaseFragment;
import cn.jcyh.eaglekinglockdemo.constant.Constants;
import cn.jcyh.eaglekinglockdemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.entity.LockKey;
import cn.jcyh.eaglekinglockdemo.entity.LockUser;
import cn.jcyh.eaglekinglockdemo.entity.SyncData;
import cn.jcyh.eaglekinglockdemo.http.LockHttpAction;
import cn.jcyh.eaglekinglockdemo.http.MyLockAPI;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestCallback;
import cn.jcyh.eaglekinglockdemo.ui.activity.AuthActivity;
import cn.jcyh.eaglekinglockdemo.ui.activity.ChooseLockActivity;
import cn.jcyh.eaglekinglockdemo.ui.fragment.LockListFragment;
import cn.jcyh.eaglekinglockdemo.ui.fragment.LockMainFragment;
import cn.jcyh.utils.L;
import cn.jcyh.utils.SPUtil;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends BaseActivity {
    private FragmentManager mFragmentManager;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public int immersiveColor() {
        return getResources().getColor(R.color.colorAccent);
    }

    @Override
    protected void init() {
        MyLockAPI.getLockAPI().startBleService(this);
        RxPermissions rxPermissions = new RxPermissions(this);
        Disposable subscribe = rxPermissions
                .request(android.Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (!bluetoothAdapter.isEnabled())
                            bluetoothAdapter.enable();
                        MyLockAPI.getLockAPI().startBTDeviceScan();
                    }
                });
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.fl_container, new LockListFragment(), LockListFragment.class.getName())
                .commit();
        syncData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLockAPI.getLockAPI().stopBleService(this);
    }

    @OnClick({R.id.tv_logout, R.id.tv_add_lock})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_logout:
                startNewActivity(AuthActivity.class);
                finish();
                break;
            case R.id.tv_add_lock:
                startNewActivity(ChooseLockActivity.class);
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mFragmentManager.beginTransaction()
                .replace(R.id.fl_container, new LockListFragment(), LockListFragment.class.getName())
                .commit();
        syncData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        L.e("--------------onActivityResult");
        syncData();
    }

    private void syncData() {
        final LockUser user = ControlCenter.getControlCenter(this).getUserInfo();
        long lastUpdateDate = SPUtil.getInstance().getLong(Constants.LAST_UPDATE_DATE, 0);
        //记录此次请求的时间，下次可直接传该时间做增量更新
        if (user != null) {
            LockHttpAction.getHttpAction(this).syncData(0, user.getAccess_token(), new OnHttpRequestCallback<SyncData>() {

                @Override
                public void onFailure(int errorCode, String desc) {
                    cancelProgressDialog();
                }

                @Override
                public void onSuccess(SyncData syncData) {
                    cancelProgressDialog();
                    L.e("------sync:" + syncData);
                    if (syncData != null && syncData.getKeyList() != null) {
                        for (int i = 0; i < syncData.getKeyList().size(); i++) {
                            LockKey key = syncData.getKeyList().get(i);
                            key.setAccessToken(user.getAccess_token());
                        }
                        SPUtil.getInstance().put(Constants.LAST_UPDATE_DATE, syncData.getLastUpdateDate());
                        ControlCenter.getControlCenter(getApplicationContext()).saveLockKeys(syncData.getKeyList());
                        BaseFragment fragment = (BaseFragment) mFragmentManager.findFragmentByTag(LockListFragment.class.getName());
                        if (fragment != null)
                            fragment.loadData();
                        fragment = (BaseFragment) mFragmentManager.findFragmentByTag(LockMainFragment.class.getName());
                        if (fragment != null)
                            fragment.loadData();
                    }
                }
            });
        }
    }
}
