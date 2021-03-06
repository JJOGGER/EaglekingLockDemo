package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.app.ProgressDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.constant.Operate;
import cn.jcyh.eaglekinglockdemo.constant.Operation;
import cn.jcyh.eaglekinglockdemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.entity.LockKey;
import cn.jcyh.eaglekinglockdemo.entity.LockUser;
import cn.jcyh.eaglekinglockdemo.http.MyLockAPI;
import cn.jcyh.utils.L;


public class OperateActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    BaseQuickAdapter<String, BaseViewHolder> mAdapter;
    private List<String> mOperas;
    private LockKey mKey;
    private ProgressDialog mProgressDialog;
    private MyLockAPI mLockAPI;
    private LockUser mLockUser;

    @Override
    public int getLayoutId() {
        return R.layout.activity_operate;
    }

    @Override
    protected void init() {
        mOperas = new ArrayList<>();
        String[] stringArray = getResources().getStringArray(R.array.operate);
        Collections.addAll(mOperas, stringArray);
        mLockAPI = MyLockAPI.getLockAPI();
        mKey = getIntent().getParcelableExtra(LockConstant.LOCK_KEY);
        L.e("---------key:" + mKey);
        mAdapter = new BaseQuickAdapter<String, BaseViewHolder>(android.R.layout.simple_list_item_1, mOperas) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(android.R.id.text1, item);
            }
        };
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        rvContent.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("请稍后...");
        mLockUser = ControlCenter.getControlCenter(getApplicationContext()).getUserInfo();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        switch (position) {
            case Operate.CLICK_TO_UNLOCK://点击开门
                L.e("--------->点击开门:" + mLockAPI.isConnected(mKey.getLockMac()));
                if (mLockAPI.isConnected(mKey.getLockMac())) {//当前处于连接状态 直接发指令
                    L.e("-----------mKey.isAdmin():" + mKey.isAdmin());
                    if (mKey.isAdmin()) {
                        String version = new Gson().toJson(mKey.getLockVersion());
                        L.e("-------version:" + version);
                        mLockAPI.unlockByAdministrator(null,mKey);
                    } else
                        mLockAPI.unlockByUser(null,  mKey);
                } else {//未连接进行连接
                    mProgressDialog.show();
                    L.e("-----connect:" + mKey.getLockMac());
                    mLockAPI.connect(mKey.getLockMac(), Operation.LOCKCAR_DOWN);
                }
                break;
            //后面两个操作是车位锁独有操作
//            case Operate.LOCKCAR_UP://车位锁升
//                if (mLockAPI.isConnected(mKey.getLockMac())) {//当前处于连接状态 直接发指令
//                    if (mKey.isAdmin())
//                        mLockAPI.lockByAdministrator(null, openid, mKey.getLockVersion(), mKey.getAdminPs(), mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr());
//                    else
//                        mLockAPI.lockByUser(null, openid, mKey.getLockVersion(), mKey.getStartDate(), mKey.getEndDate(), mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
////                    MyApplication.mTTLockAPI.lockByUser(null, 0, mKey.getLockVersion(), 1489990922165l, 1490077322165l, mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
//                } else {
//                    showProgressDialog(getString(R.string.words_wait));
//                    mTTLockAPI.connect(mKey.getLockMac());
//                    bleSession.setOperation(Operation.LOCKCAR_UP);
//                    bleSession.setLockmac(mKey.getLockMac());
//                }
//                break;
//            case Operate.LOCKCAR_DOWN://车位锁降
//                if (mTTLockAPI.isConnected(mKey.getLockMac())) {//当前处于连接状态 直接发指令
//                    if (mKey.isAdmin())
//                        mTTLockAPI.unlockByAdministrator(null, openid, mKey.getLockVersion(), mKey.getAdminPs(), mKey.getUnlockKey(), mKey.getLockFlagPos(), System.currentTimeMillis(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
//                    else
//                        mTTLockAPI.unlockByUser(null, openid, mKey.getLockVersion(), mKey.getStartDate(), mKey.getEndDate(), mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
////                    MyApplication.mTTLockAPI.unlockByUser(null, 0, mKey.getLockVersion(), 1489990922165l, 1490077322165l, mKey.getUnlockKey(), mKey.getLockFlagPos(), mKey.getAesKeystr(), mKey.getTimezoneRawOffset());
//                } else {
//                    showProgressDialog(getString(R.string.words_wait));
//                    mTTLockAPI.connect(mKey.getLockMac());
//                    bleSession.setOperation(Operation.LOCKCAR_DOWN);
//                    bleSession.setLockmac(mKey.getLockMac());
//                }
//                break;
//            case Operate.DEVICE_FIRMWARE_UPDATE:
//                start_activity(DeviceFirmwareUpdateActivity.class);
//                break;
//            default:
//                showMyDialog(position);
//                break;
        }
    }

}
