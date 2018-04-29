package cn.jcyh.eaglekinglockdemo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lock.bl.sdk.api.LockAPI;
import com.lock.bl.sdk.callback.LockCallback;
import com.lock.bl.sdk.entity.Error;
import com.lock.bl.sdk.scanner.ExtendedBluetoothDevice;
import com.lock.bl.sdk.util.Timber;

import java.util.List;

import cn.jcyh.eaglekinglockdemo.bean.BleSession;
import cn.jcyh.eaglekinglockdemo.bean.LockKey;
import cn.jcyh.eaglekinglockdemo.bean.User;
import cn.jcyh.eaglekinglockdemo.constant.BleConstant;
import cn.jcyh.eaglekinglockdemo.constant.Constants;
import cn.jcyh.eaglekinglockdemo.enumtype.Operation;
import cn.jcyh.eaglekinglockdemo.ui.activity.AuthActivity;
import cn.jcyh.eaglekinglockdemo.utils.SharePreUtil;

/**
 * Created by jogger on 2018/4/26.
 */

public class MyApp extends Application {
    private static final String TAG = "MyApp";
    /**
     * 通通锁
     */
    public static LockAPI mLockAPI;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        initLock();
    }

    private void initLock() {
        mLockAPI = new LockAPI(getApplicationContext(), mLockCallback);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    /**
     * 蓝牙操作
     */
    public static BleSession bleSession = BleSession.getInstance(Operation.UNLOCK, null);
    private LockCallback mLockCallback = new LockCallback() {
        private Gson mGson = new Gson();

        @Override
        public void onFoundDevice(ExtendedBluetoothDevice extendedBluetoothDevice) {
            //发现设备并广播
            broadcastUpdate(BleConstant.ACTION_BLE_DEVICE, BleConstant.DEVICE, extendedBluetoothDevice);
            User user = mGson.fromJson(SharePreUtil.getInstance(getApplicationContext()).getString(Constants.USER_INFO, ""), User.class);
            if (user == null || TextUtils.isEmpty(user.getAccess_token())) {
                startActivity(new Intent(MyApp.this, AuthActivity.class));
                return;
            }
//            String accessToken = MyPreference.getStr(mContext, MyPreference.ACCESS_TOKEN);
            //根据accessToken和lockmac获取钥匙
            List<LockKey> keys = mGson.fromJson(SharePreUtil.getInstance(getApplicationContext()).getString(Constants.KEY_LIST, ""), new TypeToken<List<LockKey>>() {
            }.getType());
            if (keys == null || keys.size() == 0) return;
            LockKey localKey = null;
            for (int i = 0; i < keys.size(); i++) {
                if (keys.get(i) == null) return;
                if (TextUtils.isEmpty(keys.get(i).getAccessToken()) || TextUtils.isEmpty(keys.get(i).getLockMac()))
                    continue;
                if (keys.get(i).getAccessToken().equals(user.getAccess_token()) && keys.get(i).getLockMac().equals(extendedBluetoothDevice.getAddress())) {
                    localKey = keys.get(i);
                    break;
                }
            }
            if (localKey != null) {
//                operateSuccess = false;
                switch (bleSession.getOperation()) {
                    case UNLOCK:
                        Timber.e("----------UNLOCK");
                        if (extendedBluetoothDevice.isTouch())
                            mLockAPI.connect(extendedBluetoothDevice);
                        break;
                    case SET_ADMIN_KEYBOARD_PASSWORD:
                    case SET_DELETE_PASSWORD:
                    case SET_LOCK_TIME:
                    case RESET_KEYBOARD_PASSWORD:
                    case RESET_EKEY:
                    case RESET_LOCK:
                    case GET_LOCK_TIME:
                    case GET_OPERATE_LOG:
                        if (extendedBluetoothDevice.getAddress().equals(bleSession.getLockmac()))
                            mLockAPI.connect(extendedBluetoothDevice);
                        break;
                }
            }
        }

        private <K, V extends Parcelable> void broadcastUpdate(String action, K key, V value) {
            final Intent intent = new Intent(action);
            if (key != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelable((String) key, value);
                intent.putExtras(bundle);
            }
            sendBroadcast(intent);
        }

        @Override
        public void onDeviceConnected(ExtendedBluetoothDevice var1) {
//            Log.e(TAG, "---------onDeviceConnected:" + bleSession.getOperation());
//            String accessToken = MyPreference.getStr(mContext, MyPreference.ACCESS_TOKEN);
//            //TODO:
//            Key localKey = DbService.getKeyByAccessTokenAndLockmac(accessToken, extendedBluetoothDevice.getAddress());
//            curKey = MainActivity.curKey;
//            //uid 等同于 openid
//            int uid = MyPreference.getOpenid(mContext, MyPreference.OPEN_ID);
////            operateSuccess = false;
//            switch (bleSession.getOperation()) {
//                case ADD_ADMIN:
//                    //TODO:判断本地是否存在
//                    mTTLockAPI.addAdministrator(extendedBluetoothDevice);
//                    break;
//                case UNLOCK:
//                case CLICK_UNLOCK:
//                    if (localKey != null) {//本地存在锁
//
//                        if (localKey.isAdmin())
//                            mTTLockAPI.unlockByAdministrator(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPs(), localKey.getUnlockKey(), localKey.getLockFlagPos(), System.currentTimeMillis(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
//                        else
//                            mTTLockAPI.unlockByUser(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
//                    }
////                    mTTLockAPI.unlockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getUnlockKey(), 0, localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
//                    break;
//                case SET_ADMIN_KEYBOARD_PASSWORD://管理码
//                    mTTLockAPI.setAdminKeyboardPassword(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos(), curKey.getAesKeystr(), bleSession.getPassword());
//                    break;
//                case SET_DELETE_PASSWORD://删除码
//                    mTTLockAPI.setDeletePassword(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos(), curKey.getAesKeystr(), bleSession.getPassword());
//                    break;
//                case SET_LOCK_TIME://设置锁时间
//                    mTTLockAPI.setLockTime(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getUnlockKey(), System.currentTimeMillis(), curKey.getLockFlagPos(), curKey.getAesKeystr(), curKey.getTimezoneRawOffset());
//                    break;
//                case RESET_KEYBOARD_PASSWORD://重置键盘密码
//                    mTTLockAPI.resetKeyboardPassword(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos(), curKey.getAesKeystr());
//                    break;
//                case RESET_EKEY://重置电子钥匙 锁标志位+1
//                    mTTLockAPI.resetEKey(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getLockFlagPos() + 1, curKey.getAesKeystr());
//                    break;
//                case RESET_LOCK://重置锁
//                    mTTLockAPI.resetLock(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos(), curKey.getAesKeystr());
//                    break;
//                case GET_OPERATE_LOG://获取操作日志
//                    mTTLockAPI.getOperateLog(extendedBluetoothDevice, curKey.getLockVersion(), curKey.getAesKeystr(), localKey.getTimezoneRawOffset());
//                    break;
//                case GET_LOCK_TIME://获取锁时间
//                    mTTLockAPI.getLockTime(extendedBluetoothDevice, curKey.getLockVersion(), curKey.getAesKeystr(), localKey.getTimezoneRawOffset());
//                    break;
//                case LOCKCAR_UP://车位锁升
//                    if (localKey.isAdmin())
//                        mTTLockAPI.lockByAdministrator(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPs(), localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr());
//                    else
//                        mTTLockAPI.lockByUser(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
////                    mTTLockAPI.lockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), 1489990922165l, 1490077322165l, localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
//                    break;
//                case LOCKCAR_DOWN://车位锁降
//                    if (localKey.isAdmin())
//                        mTTLockAPI.unlockByAdministrator(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPs(), localKey.getUnlockKey(), localKey.getLockFlagPos(), System.currentTimeMillis(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
//                    else
//                        mTTLockAPI.unlockByUser(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
////                    mTTLockAPI.unlockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), 1489990922165l, 1490077322165l, localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
//                    break;
//                case DELETE_ONE_KEYBOARDPASSWORD://这里的密码类型传0
//                    mTTLockAPI.deleteOneKeyboardPassword(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPs(), localKey.getUnlockKey(), localKey.getLockFlagPos(), 0, bleSession.getPassword(), localKey.getAesKeystr());
//                    break;
//                case GET_LOCK_VERSION_INFO:
//                    mTTLockAPI.readDeviceInfo(extendedBluetoothDevice, localKey.getLockVersion(), localKey.getAesKeystr());
//                    break;
//            }
        }

        @Override
        public void onDeviceDisconnected(ExtendedBluetoothDevice var1) {
            //默认是开门标志
//            bleSession.setOperation(Operation.UNLOCK);
////            //断开连接
////            broadcastUpdate(BleConstant.ACTION_BLE_DISCONNECTED, BleConstant.DEVICE, extendedBluetoothDevice);
////            if(!operateSuccess) {
////                toast("蓝牙已断开");
////            }
//            LogUtil.d("蓝牙断开", DBG);
//            ((BaseActivity) curActivity).cancelProgressDialog();
        }

        @Override
        public void onGetLockVersion(ExtendedBluetoothDevice var1, int var2, int var3, int var4, int var5, int var6, Error var7) {

        }

        @Override
        public void onAddAdministrator(ExtendedBluetoothDevice var1, String var2, String var3, String var4, String var5, String var6, String var7, long var8, String var10, int var11, String var12, String var13, String var14, Error var15) {

        }

        @Override
        public void onResetEKey(ExtendedBluetoothDevice var1, int var2, Error var3) {

        }

        @Override
        public void onSetLockName(ExtendedBluetoothDevice var1, String var2, Error var3) {

        }

        @Override
        public void onSetAdminKeyboardPassword(ExtendedBluetoothDevice var1, String var2, Error var3) {

        }

        @Override
        public void onSetDeletePassword(ExtendedBluetoothDevice var1, String var2, Error var3) {

        }

        @Override
        public void onUnlock(ExtendedBluetoothDevice var1, int var2, int var3, long var4, Error var6) {

        }

        @Override
        public void onSetLockTime(ExtendedBluetoothDevice var1, Error var2) {

        }

        @Override
        public void onGetLockTime(ExtendedBluetoothDevice var1, long var2, Error var4) {

        }

        @Override
        public void onResetKeyboardPassword(ExtendedBluetoothDevice var1, String var2, long var3, Error var5) {

        }

        @Override
        public void onSetMaxNumberOfKeyboardPassword(ExtendedBluetoothDevice var1, int var2, Error var3) {

        }

        @Override
        public void onResetKeyboardPasswordProgress(ExtendedBluetoothDevice var1, int var2, Error var3) {

        }

        @Override
        public void onResetLock(ExtendedBluetoothDevice var1, Error var2) {

        }

        @Override
        public void onAddKeyboardPassword(ExtendedBluetoothDevice var1, int var2, String var3, long var4, long var6, Error var8) {

        }

        @Override
        public void onModifyKeyboardPassword(ExtendedBluetoothDevice var1, int var2, String var3, String var4, Error var5) {

        }

        @Override
        public void onDeleteOneKeyboardPassword(ExtendedBluetoothDevice var1, int var2, String var3, Error var4) {

        }

        @Override
        public void onDeleteAllKeyboardPassword(ExtendedBluetoothDevice var1, Error var2) {

        }

        @Override
        public void onGetOperateLog(ExtendedBluetoothDevice var1, String var2, Error var3) {

        }

        @Override
        public void onSearchDeviceFeature(ExtendedBluetoothDevice var1, int var2, int var3, Error var4) {

        }

        @Override
        public void onAddICCard(ExtendedBluetoothDevice var1, int var2, int var3, long var4, Error var6) {

        }

        @Override
        public void onModifyICCardPeriod(ExtendedBluetoothDevice var1, int var2, long var3, long var5, long var7, Error var9) {

        }

        @Override
        public void onDeleteICCard(ExtendedBluetoothDevice var1, int var2, long var3, Error var5) {

        }

        @Override
        public void onClearICCard(ExtendedBluetoothDevice var1, int var2, Error var3) {

        }

        @Override
        public void onSetWristbandKeyToLock(ExtendedBluetoothDevice var1, int var2, Error var3) {

        }

        @Override
        public void onSetWristbandKeyToDev(Error var1) {

        }

        @Override
        public void onSetWristbandKeyRssi(Error var1) {

        }

        @Override
        public void onAddFingerPrint(ExtendedBluetoothDevice var1, int var2, int var3, long var4, Error var6) {

        }

        @Override
        public void onAddFingerPrint(ExtendedBluetoothDevice var1, int var2, int var3, long var4, int var6, Error var7) {

        }

        @Override
        public void onFingerPrintCollection(ExtendedBluetoothDevice var1, int var2, Error var3) {

        }

        @Override
        public void onFingerPrintCollection(ExtendedBluetoothDevice var1, int var2, int var3, int var4, Error var5) {

        }

        @Override
        public void onModifyFingerPrintPeriod(ExtendedBluetoothDevice var1, int var2, long var3, long var5, long var7, Error var9) {

        }

        @Override
        public void onDeleteFingerPrint(ExtendedBluetoothDevice var1, int var2, long var3, Error var5) {

        }

        @Override
        public void onClearFingerPrint(ExtendedBluetoothDevice var1, int var2, Error var3) {

        }

        @Override
        public void onSearchAutoLockTime(ExtendedBluetoothDevice var1, int var2, int var3, int var4, int var5, Error var6) {

        }

        @Override
        public void onModifyAutoLockTime(ExtendedBluetoothDevice var1, int var2, int var3, Error var4) {

        }

        @Override
        public void onReadDeviceInfo(ExtendedBluetoothDevice var1, String var2, String var3, String var4, String var5, String var6) {

        }

        @Override
        public void onEnterDFUMode(ExtendedBluetoothDevice var1, Error var2) {

        }

        @Override
        public void onGetLockSwitchState(ExtendedBluetoothDevice var1, int var2, int var3, Error var4) {

        }

        @Override
        public void onLock(ExtendedBluetoothDevice var1, int var2, int var3, int var4, long var5, Error var7) {

        }

        @Override
        public void onScreenPasscodeOperate(ExtendedBluetoothDevice var1, int var2, int var3, Error var4) {

        }

        @Override
        public void onRecoveryData(ExtendedBluetoothDevice var1, int var2, Error var3) {

        }

        @Override
        public void onSearchICCard(ExtendedBluetoothDevice var1, int var2, String var3, Error var4) {

        }

        @Override
        public void onSearchFingerPrint(ExtendedBluetoothDevice var1, int var2, String var3, Error var4) {

        }

        @Override
        public void onSearchPasscode(ExtendedBluetoothDevice var1, String var2, Error var3) {

        }

        @Override
        public void onSearchPasscodeParam(ExtendedBluetoothDevice var1, int var2, String var3, long var4, Error var6) {

        }

        @Override
        public void onOperateRemoteUnlockSwitch(ExtendedBluetoothDevice var1, int var2, int var3, int var4, int var5, Error var6) {

        }

    };
}
