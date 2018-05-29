package cn.jcyh.eaglekinglockdemo;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.ttlock.bl.sdk.bean.LockKey;
import com.ttlock.bl.sdk.bean.LockUser;
import com.ttlock.bl.sdk.bean.SyncData;
import com.ttlock.bl.sdk.http.LockHttpAction;
import com.ttlock.bl.sdk.http.OnHttpRequestCallback;
import com.ttlock.bl.sdk.util.SharePreUtil;

import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.base.BaseFragment;
import cn.jcyh.eaglekinglockdemo.constant.Constants;
import cn.jcyh.eaglekinglockdemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.ui.activity.AuthActivity;
import cn.jcyh.eaglekinglockdemo.ui.activity.ChooseLockActivity;
import cn.jcyh.eaglekinglockdemo.ui.fragment.LockListFragment;
import cn.jcyh.eaglekinglockdemo.ui.fragment.LockMainFragment;
import cn.jcyh.eaglekinglockdemo.utils.Timber;

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
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.fl_container, new LockListFragment(), LockListFragment.class.getName())
                .commit();
        syncData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        Timber.e("--------------onActivityResult");
        syncData();
    }

    private void syncData() {
        final LockUser user = ControlCenter.getControlCenter(this).getUserInfo();
        long lastUpdateDate = SharePreUtil.getInstance(getApplicationContext()).getLong(Constants.LAST_UPDATE_DATE, 0);
        if (user != null) {
            LockHttpAction.getHttpAction(this).syncData(0, user.getAccess_token(), new OnHttpRequestCallback<SyncData>() {
                @Override
                public void onFailure(int errorCode) {
                    cancelProgressDialog();
                }

                @Override
                public void onSuccess(SyncData syncData) {
                    cancelProgressDialog();
                    Timber.e("------sync:" + syncData);
                    if (syncData != null && syncData.getKeyList() != null) {
                        for (int i = 0; i < syncData.getKeyList().size(); i++) {
                            LockKey key = syncData.getKeyList().get(i);
                            key.setAccessToken(user.getAccess_token());
                        }
                        SharePreUtil.getInstance(getApplicationContext()).setLong(Constants.LAST_UPDATE_DATE, syncData.getLastUpdateDate());
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
