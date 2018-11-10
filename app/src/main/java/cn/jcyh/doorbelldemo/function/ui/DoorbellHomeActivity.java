package cn.jcyh.doorbelldemo.function.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.doorbelldemo.base.BaseDoorbellActivity;
import cn.jcyh.doorbelldemo.base.BasePresenter;
import cn.jcyh.doorbelldemo.bus.nim.NIMFriendAction;
import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.doorbelldemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.command.ndk.CommandControl;
import cn.jcyh.nimlib.entity.Doorbell;
import cn.jcyh.nimlib.observer.event.OnlineStateChangeObserver;
import cn.jcyh.nimlib.observer.event.OnlineStateEventCache;
import cn.jcyh.nimlib.observer.event.OnlineStateEventManager;
import cn.jcyh.nimlib.utils.NetworkUtil;
import cn.jcyh.utils.L;
import cn.jcyh.utils.T;

//猫眼首页
public class DoorbellHomeActivity extends BaseDoorbellActivity implements  SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srlRefresh;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.tv_isOnline)
    TextView tvIsOnline;
    private Doorbell mCurrentDoorbell;//当前门铃

    @Override
    public int getLayoutId() {
        return R.layout.activity_doorbell_home;
    }


    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void init() {
        srlRefresh.setOnRefreshListener(this);
        setCurrentDoorbellState(mCurrentDoorbell);
        registerObservable();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
//        if (mAdapter != null && mAdapter.getData().size() > 0)
//            outState.putSerializable(Constant.DEVICE_LIST, (Serializable) mAdapter.getData());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
//        List<Doorbell> doorbells = (List<Doorbell>) savedInstanceState.getSerializable(Constant.DEVICE_LIST);
//        initView(doorbells);
    }

    private void registerObservable() {
        OnlineStateEventManager.getOnlineStateChangeObservable().registerOnlineStateChangeListeners(mOnlineStateChangeObserver, true);
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(mUserStatusObserver, true);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void unRegisterObservable() {
        OnlineStateEventManager.getOnlineStateChangeObservable().registerOnlineStateChangeListeners(mOnlineStateChangeObserver, false);
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(mUserStatusObserver, false);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterObservable();
    }

    private void setCurrentDoorbellState(Doorbell currentDoorbell) {
        if (currentDoorbell == null) {
                return;
        }
        tvDeviceName.setText(String.format(getString(R.string.current_device_),
                currentDoorbell.getDeviceName()));
        boolean isOnline = OnlineStateEventCache.isOnline(currentDoorbell.getDeviceId());
        if (isOnline) {
            //在线
            tvIsOnline.setText(R.string.online);
        } else {
            //离线
            tvIsOnline.setText(R.string.offline);
        }
        tvIsOnline.setSelected(isOnline);
    }

    /**
     * 猫眼在线监听
     */
    OnlineStateChangeObserver mOnlineStateChangeObserver = new OnlineStateChangeObserver() {
        @Override
        public void onlineStateChange(Set<String> accounts) {
            if (accounts == null || mCurrentDoorbell == null) return;
            for (String account :
                    accounts) {
                if (mCurrentDoorbell.getDeviceId().equals(account)) {
                    setCurrentDoorbellState(mCurrentDoorbell);
                    break;
                }
            }
        }
    };


    @OnClick({R.id.rl_connect_video, R.id.rl_params, R.id.rl_push_infos
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_connect_video:
                //连接视频
                if (mCurrentDoorbell == null)
                    return;
                //发起呼叫
                if (NetworkUtil.isNetworkConnected()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.DEVICE_ID, mCurrentDoorbell.getDeviceId());
                    bundle.putString(Constant.DEVICE_NAME, mCurrentDoorbell.getDeviceName());
                    startNewActivity(DoorbellVideoActivity.class, bundle);
                } else
                    T.show(R.string.check_net_error);
                break;
            case R.id.rl_params:
                if (mCurrentDoorbell == null)
                    return;
                startNewActivity(DoorbellParamsActivity.class, Constant.DOORBELL, mCurrentDoorbell);
                break;
            case R.id.rl_push_infos:
                if (mCurrentDoorbell == null)
                    return;
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.DOORBELL, mCurrentDoorbell);
                startNewActivity(DoorbellRecordActivity.class, bundle);
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFriendAction(NIMFriendAction friendAction) {
        if (NIMFriendAction.TYPE_ADD_DOORBELL.equals(friendAction.getType())) {
            String account = friendAction.getStringExtra(Constant.FROM_ACCOUNT);

        } else if (NIMFriendAction.TYPE_DELETE_DOORBELL.equals(friendAction.getType())) {
            //删除猫眼
            finish();
        }
    }

    @Override
    public void onRefresh() {
        CommandControl.sendRefreshDoorbellCommand(mCurrentDoorbell.getDeviceId());
        srlRefresh.setRefreshing(false);
    }

    private Observer<StatusCode> mUserStatusObserver = new Observer<StatusCode>() {
        @Override
        public void onEvent(StatusCode statusCode) {
            if (!statusCode.wontAutoLogin()) return;
            L.e("-----------------被其他端登录");
            ControlCenter.logout(true);
        }
    };

}
