package cn.jcyh.eaglekinglockdemo.callback;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.ttlock.bl.sdk.api.LockAPI;
import com.ttlock.bl.sdk.bean.LockKey;
import com.ttlock.bl.sdk.bean.LockUser;
import com.ttlock.bl.sdk.callback.LockCallback;
import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.http.LockHttpAction;
import com.ttlock.bl.sdk.http.OnHttpRequestCallback;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;

import java.util.List;
import java.util.TimeZone;

import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.ui.activity.AuthActivity;
import cn.jcyh.eaglekinglockdemo.utils.Timber;
import cn.jcyh.eaglekinglockdemo.utils.ToastUtil;

import static cn.jcyh.eaglekinglockdemo.enumtype.Operation.UNLOCK;

/**
 * Created by jogger on 2018/4/27.
 */

public class MyLockCallback implements LockCallback {
    private Context mContext;
    private LockUser mUser;
    private LockAPI mLockAPI;
    private final ControlCenter mControlCenter;
    private Gson mGson;
    private LocalBroadcastManager mLocalBroadcastManager;

    public MyLockCallback(Context context) {
        mContext = context.getApplicationContext();
        mControlCenter = ControlCenter.getControlCenter(context);
        mLockAPI = LockAPI.getLockAPI(context);
        mGson = new Gson();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @Override
    public void onFoundDevice(ExtendedBluetoothDevice extendedBluetoothDevice) {
        mUser = mControlCenter.getUserInfo();
        if (mUser == null || TextUtils.isEmpty(mUser.getAccess_token())) {
            mContext.startActivity(new Intent(mContext, AuthActivity.class));
            return;
        }
        //发现设备并广播
        Intent intent = new Intent(LockConstant.ACTION_BLE_DEVICE);
        intent.putExtra(LockConstant.DEVICE, extendedBluetoothDevice);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
//            String accessToken = MyPreference.getStr(mContext, MyPreference.ACCESS_TOKEN);
        //根据accessToken和lockmac获取钥匙
        List<LockKey> keys = mControlCenter.getLockKeys();
        if (keys == null || keys.size() == 0) return;
        LockKey localKey = null;
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i) == null) return;
            if (TextUtils.isEmpty(keys.get(i).getAccessToken()) || TextUtils.isEmpty(keys.get(i).getLockMac()))
                continue;
            if (keys.get(i).getAccessToken().equals(mUser.getAccess_token()) && keys.get(i).getLockMac().equals(extendedBluetoothDevice.getAddress())) {
                localKey = keys.get(i);
                break;
            }
        }
        if (localKey != null) {
            switch (ControlCenter.sBleSession.getOperation()) {
                case UNLOCK:
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
                    if (extendedBluetoothDevice.getAddress().equals(ControlCenter.sBleSession.getLockmac()))
                        mLockAPI.connect(extendedBluetoothDevice);
                    break;
            }
        }
    }

    @Override
    public void onDeviceConnected(ExtendedBluetoothDevice extendedBluetoothDevice) {
        Timber.e("--------onDeviceConnected:" + ControlCenter.sBleSession.getOperation());
        mUser = mControlCenter.getUserInfo();
        if (mUser == null || TextUtils.isEmpty(mUser.getAccess_token())) {
            return;
        }
        //根据accessToken和lockmac获取钥匙
        List<LockKey> keys = mControlCenter.getLockKeys();
        if (keys == null || keys.size() == 0) return;
        LockKey localKey = null;
        Timber.e("------------user:" + mUser);
        Timber.e("----------keys:" + keys);
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i) == null) return;
            if (TextUtils.isEmpty(keys.get(i).getAccessToken()) || TextUtils.isEmpty(keys.get(i).getLockMac()))
                continue;
            if (keys.get(i).getAccessToken().equals(mUser.getAccess_token()) && keys.get(i).getLockMac().equals(extendedBluetoothDevice.getAddress())) {
                localKey = keys.get(i);
                break;
            }
        }
        switch (ControlCenter.sBleSession.getOperation()) {
            case ADD_ADMIN:
                mLockAPI.addAdministrator(extendedBluetoothDevice);
                break;
            case UNLOCK:
            case CLICK_UNLOCK:
                Timber.e("--------------CLICK_UNLOCK" + localKey);
                if (localKey == null) return;
                //本地存在锁
                if (localKey.isAdmin()) {
                    mLockAPI.unlockByAdministrator(extendedBluetoothDevice, mUser.getOpenid(), localKey.getLockVersion(),
                            localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(), System.currentTimeMillis(),
                            localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                } else {
                    mLockAPI.unlockByUser(extendedBluetoothDevice, mUser.getOpenid(),localKey.getLockVersion(),
                            localKey.getStartDate(), localKey.getEndDate(), localKey.getLockKey(),
                            localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
                }
                break;
//                case SET_ADMIN_KEYBOARD_PASSWORD://管理码
//                    mTTTTLockAPI.setAdminKeyboardPassword(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos(), curKey.getAesKeystr(), bleSession.getPassword());
//                    break;
//                case SET_DELETE_PASSWORD://删除码
//                    mTTTTLockAPI.setDeletePassword(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos(), curKey.getAesKeystr(), bleSession.getPassword());
//                    break;
//                case SET_LOCK_TIME://设置锁时间
//                    mTTTTLockAPI.setLockTime(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getUnlockKey(), System.currentTimeMillis(), curKey.getLockFlagPos(), curKey.getAesKeystr(), curKey.getTimezoneRawOffset());
//                    break;
//                case RESET_KEYBOARD_PASSWORD://重置键盘密码
//                    mTTTTLockAPI.resetKeyboardPassword(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos(), curKey.getAesKeystr());
//                    break;
//                case RESET_EKEY://重置电子钥匙 锁标志位+1
//                    mTTTTLockAPI.resetEKey(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getLockFlagPos() + 1, curKey.getAesKeystr());
//                    break;
//                case RESET_LOCK://重置锁
//                    mTTTTLockAPI.resetLock(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos(), curKey.getAesKeystr());
//                    break;
//                case GET_OPERATE_LOG://获取操作日志
//                    mTTTTLockAPI.getOperateLog(extendedBluetoothDevice, curKey.getLockVersion(), curKey.getAesKeystr(), localKey.getTimezoneRawOffset());
//                    break;
//                case GET_LOCK_TIME://获取锁时间
//                    mTTTTLockAPI.getLockTime(extendedBluetoothDevice, curKey.getLockVersion(), curKey.getAesKeystr(), localKey.getTimezoneRawOffset());
//                    break;
//                case LOCKCAR_UP://车位锁升
//                    if (localKey.isAdmin())
//                        mTTTTLockAPI.lockByAdministrator(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPs(), localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr());
//                    else
//                        mTTTTLockAPI.lockByUser(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
////                    mTTTTLockAPI.lockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), 1489990922165l, 1490077322165l, localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
//                    break;
//                case LOCKCAR_DOWN://车位锁降
//                    if (localKey.isAdmin())
//                        mTTTTLockAPI.unlockByAdministrator(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPs(), localKey.getUnlockKey(), localKey.getLockFlagPos(), System.currentTimeMillis(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
//                    else
//                        mTTTTLockAPI.unlockByUser(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getStartDate(), localKey.getEndDate(), localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
////                    mTTTTLockAPI.unlockByUser(extendedBluetoothDevice, 0, localKey.getLockVersion(), 1489990922165l, 1490077322165l, localKey.getUnlockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr(), localKey.getTimezoneRawOffset());
//                    break;
//                case DELETE_ONE_KEYBOARDPASSWORD://这里的密码类型传0
//                    mTTTTLockAPI.deleteOneKeyboardPassword(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPs(), localKey.getUnlockKey(), localKey.getLockFlagPos(), 0, bleSession.getPassword(), localKey.getAesKeystr());
//                    break;
//                case GET_LOCK_VERSION_INFO:
//                    mTTTTLockAPI.readDeviceInfo(extendedBluetoothDevice, localKey.getLockVersion(), localKey.getAesKeystr());
//                    break;
        }

    }

    @Override
    public void onDeviceDisconnected(ExtendedBluetoothDevice extendedBluetoothDevice) {
        Timber.e("-----------onDeviceDisconnected");
        //默认是开门标志
        ControlCenter.sBleSession.setOperation(UNLOCK);
        //断开连接
        Intent intent = new Intent(LockConstant.ACTION_BLE_DISCONNECTED);
        intent.putExtra(LockConstant.DEVICE, extendedBluetoothDevice);
        mLocalBroadcastManager.sendBroadcast(intent);
//            if(!operateSuccess) {
//                toast("蓝牙已断开");
//            }
//            LogUtil.d("蓝牙断开", DBG);
    }

    @Override
    public void onGetLockVersion(ExtendedBluetoothDevice var1, int var2, int var3, int var4,
                                 int var5, int var6, Error var7) {
        Timber.e("-----------onGetLockVersion");
    }

    @Override
    public void onAddAdministrator(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersionString, String adminPs, String unlockKey, String adminKeyboardPwd, String deletePwd, String pwdInfo, long timestamp, String aesKeystr, int feature, String modelNumber, String hardwareRevision, String firmwareRevision, Error error) {
        Timber.e("-----------onAddAdministrator");
        addAdmin(extendedBluetoothDevice, lockVersionString, adminPs, unlockKey, adminKeyboardPwd, deletePwd, pwdInfo, timestamp, aesKeystr, feature, modelNumber, hardwareRevision, firmwareRevision, error);
    }

    @Override
    public void onResetEKey(ExtendedBluetoothDevice var1, int var2, Error var3) {
        Timber.e("-----------onResetEKey");
    }

    @Override
    public void onSetLockName(ExtendedBluetoothDevice var1, String var2, Error var3) {
        Timber.e("-----------onSetLockName");
    }

    @Override
    public void onSetAdminKeyboardPassword(ExtendedBluetoothDevice var1, String var2, Error
            var3) {
        Timber.e("-----------onSetAdminKeyboardPassword");
    }

    @Override
    public void onSetDeletePassword(ExtendedBluetoothDevice var1, String var2, Error var3) {
        Timber.e("-----------onSetDeletePassword");
    }

    @Override
    public void onUnlock(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, int uniqueid, long lockTime, Error error) {
        Timber.e("-----------onUnlock");
        Intent intent = new Intent(LockConstant.ACTION_UNLOCK);
        intent.putExtra(LockConstant.ERROR_MSG, error.getErrorMsg());
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onSetLockTime(ExtendedBluetoothDevice var1, Error var2) {
        Timber.e("-----------onSetLockTime");
    }

    @Override
    public void onGetLockTime(ExtendedBluetoothDevice var1, long var2, Error var4) {
        Timber.e("-----------onGetLockTime");
    }

    @Override
    public void onResetKeyboardPassword(ExtendedBluetoothDevice var1, String var2,
                                        long var3, Error var5) {
        Timber.e("-----------onResetKeyboardPassword");
    }

    @Override
    public void onSetMaxNumberOfKeyboardPassword(ExtendedBluetoothDevice var1, int var2, Error
            var3) {
        Timber.e("-----------onSetMaxNumberOfKeyboardPassword");
    }

    @Override
    public void onResetKeyboardPasswordProgress(ExtendedBluetoothDevice var1, int var2, Error
            var3) {
        Timber.e("-----------onResetKeyboardPasswordProgress");
    }

    @Override
    public void onResetLock(ExtendedBluetoothDevice var1, Error var2) {
        Timber.e("-----------onResetLock");
    }

    @Override
    public void onAddKeyboardPassword(ExtendedBluetoothDevice var1, int var2, String var3,
                                      long var4, long var6, Error var8) {
        Timber.e("-----------onAddKeyboardPassword");
    }

    @Override
    public void onModifyKeyboardPassword(ExtendedBluetoothDevice var1, int var2, String
            var3, String var4, Error var5) {
        Timber.e("-----------onModifyKeyboardPassword");
    }

    @Override
    public void onDeleteOneKeyboardPassword(ExtendedBluetoothDevice var1, int var2, String
            var3, Error var4) {
        Timber.e("-----------onDeleteOneKeyboardPassword");
    }

    @Override
    public void onDeleteAllKeyboardPassword(ExtendedBluetoothDevice var1, Error var2) {
        Timber.e("-----------onDeleteAllKeyboardPassword");
    }

    @Override
    public void onGetOperateLog(ExtendedBluetoothDevice var1, String var2, Error var3) {
        Timber.e("-----------onGetOperateLog");
    }

    @Override
    public void onSearchDeviceFeature(ExtendedBluetoothDevice var1, int var2, int var3, Error
            var4) {
        Timber.e("-----------onSearchDeviceFeature");
    }

    @Override
    public void onAddICCard(ExtendedBluetoothDevice var1, int var2, int var3, long var4, Error
            var6) {
        Timber.e("-----------onAddICCard");
    }

    @Override
    public void onModifyICCardPeriod(ExtendedBluetoothDevice var1, int var2, long var3,
                                     long var5, long var7, Error var9) {
        Timber.e("-----------onModifyICCardPeriod");
    }

    @Override
    public void onDeleteICCard(ExtendedBluetoothDevice var1, int var2, long var3, Error var5) {
        Timber.e("-----------onDeleteICCard");
    }

    @Override
    public void onClearICCard(ExtendedBluetoothDevice var1, int var2, Error var3) {
        Timber.e("-----------onClearICCard");
    }

    @Override
    public void onSetWristbandKeyToLock(ExtendedBluetoothDevice var1, int var2, Error var3) {
        Timber.e("-----------onSetWristbandKeyToLock");
    }

    @Override
    public void onSetWristbandKeyToDev(Error var1) {
        Timber.e("-----------onSetWristbandKeyToDev");
    }

    @Override
    public void onSetWristbandKeyRssi(Error var1) {
        Timber.e("-----------onSetWristbandKeyRssi");
    }

    @Override
    public void onAddFingerPrint(ExtendedBluetoothDevice var1, int var2, int var3,
                                 long var4, Error var6) {
        Timber.e("-----------onAddFingerPrint");
    }

    @Override
    public void onAddFingerPrint(ExtendedBluetoothDevice var1, int var2, int var3, long var4,
                                 int var6, Error var7) {
        Timber.e("-----------onAddFingerPrint");
    }

    @Override
    public void onFingerPrintCollection(ExtendedBluetoothDevice var1, int var2, Error var3) {
        Timber.e("-----------onFingerPrintCollection");
    }

    @Override
    public void onFingerPrintCollection(ExtendedBluetoothDevice var1, int var2, int var3,
                                        int var4, Error var5) {
        Timber.e("-----------onFingerPrintCollection");
    }

    @Override
    public void onModifyFingerPrintPeriod(ExtendedBluetoothDevice var1, int var2, long var3,
                                          long var5, long var7, Error var9) {
        Timber.e("-----------onModifyFingerPrintPeriod");
    }

    @Override
    public void onDeleteFingerPrint(ExtendedBluetoothDevice var1, int var2, long var3, Error
            var5) {
        Timber.e("-----------onDeleteFingerPrint");
    }

    @Override
    public void onClearFingerPrint(ExtendedBluetoothDevice var1, int var2, Error var3) {
        Timber.e("-----------onClearFingerPrint");
    }

    @Override
    public void onSearchAutoLockTime(ExtendedBluetoothDevice var1, int var2, int var3, int var4,
                                     int var5, Error var6) {
        Timber.e("-----------onSearchAutoLockTime");
    }

    @Override
    public void onModifyAutoLockTime(ExtendedBluetoothDevice var1, int var2, int var3, Error
            var4) {
        Timber.e("-----------onModifyAutoLockTime");
    }

    @Override
    public void onReadDeviceInfo(ExtendedBluetoothDevice var1, String var2, String var3, String
            var4, String var5, String var6) {
        Timber.e("-----------onReadDeviceInfo");
    }

    @Override
    public void onEnterDFUMode(ExtendedBluetoothDevice var1, Error var2) {
        Timber.e("-----------onEnterDFUMode");
    }

    @Override
    public void onGetLockSwitchState(ExtendedBluetoothDevice var1, int var2, int var3, Error
            var4) {
        Timber.e("-----------onGetLockSwitchState");
    }

    @Override
    public void onLock(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int uid, int uniqueid, long lockTime, Error error) {
        Timber.e("-----------onLock");
        if (error == Error.SUCCESS) {
            ToastUtil.showToast(mContext, "解锁成功");
        }
    }

    @Override
    public void onScreenPasscodeOperate(ExtendedBluetoothDevice var1, int var2, int var3, Error
            var4) {
        Timber.e("-----------onScreenPasscodeOperate");
    }

    @Override
    public void onRecoveryData(ExtendedBluetoothDevice var1, int var2, Error var3) {
        Timber.e("-----------onRecoveryData");
    }

    @Override
    public void onSearchICCard(ExtendedBluetoothDevice var1, int var2, String var3, Error var4) {
        Timber.e("-----------onSearchICCard");
    }

    @Override
    public void onSearchFingerPrint(ExtendedBluetoothDevice var1, int var2, String var3, Error
            var4) {
        Timber.e("-----------onSearchFingerPrint");
    }

    @Override
    public void onSearchPasscode(ExtendedBluetoothDevice var1, String var2, Error var3) {
        Timber.e("-----------onSearchPasscode");
    }

    @Override
    public void onSearchPasscodeParam(ExtendedBluetoothDevice var1, int var2, String var3,
                                      long var4, Error var6) {
        Timber.e("-----------onSearchPasscodeParam");
    }

    @Override
    public void onOperateRemoteUnlockSwitch(ExtendedBluetoothDevice var1, int var2, int var3,
                                            int var4, int var5, Error var6) {
        Timber.e("-----------onOperateRemoteUnlockSwitch");
    }


    /**
     * 添加管理员
     */
    private void addAdmin(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersionString, String adminPs, String unlockKey, String adminKeyboardPwd, String deletePwd, String pwdInfo, long timestamp, String aesKeystr, int feature, String modelNumber, String hardwareRevision, String firmwareRevision, final Error error) {
        Timber.e("--------error:" + error + "--->" + (error == Error.SUCCESS));
        if (error == Error.SUCCESS) {
            LockKey key = new LockKey();
            key.setAccessToken(ControlCenter.getControlCenter(mContext).getUserInfo().getAccess_token());
            key.setAdmin(true);
            key.setLockVersion(lockVersionString);
            key.setLockName(extendedBluetoothDevice.getName());
            key.setLockMac(extendedBluetoothDevice.getAddress());
            key.setAdminPwd(adminPs);
            key.setLockKey(unlockKey);
            key.setNoKeyPwd(adminKeyboardPwd);
            key.setDeletePwd(deletePwd);
            key.setPwdInfo(pwdInfo);
            key.setTimestamp(timestamp);
            key.setAesKeystr(aesKeystr);
            key.setSpecialValue(feature);

            //获取当前时区偏移量
            key.setTimezoneRawOffset(TimeZone.getDefault().getOffset(System.currentTimeMillis()));
            key.setModelNumber(modelNumber);
            key.setHardwareRevision(hardwareRevision);
            key.setFirmwareRevision(firmwareRevision);
            ToastUtil.showToast(mContext, "锁添加成功，正在上传服务端进行初始化操作");
            LockHttpAction.getHttpAction(mContext).initLock(key, new OnHttpRequestCallback<Boolean>() {
                @Override
                public void onFailure(int errorCode) {
                    Timber.e("--------error:" + errorCode);

                }

                @Override
                public void onSuccess(Boolean aBoolean) {
                    Timber.e("--------添加成功:");
                    Intent intent = new Intent(LockConstant.ACTION_ADD_ADMIN);
                    intent.putExtra(LockConstant.ERROR_MSG, error.getErrorMsg());
                    mLocalBroadcastManager.sendBroadcast(intent);
                }
            });

        } else {
            Intent intent = new Intent(LockConstant.ACTION_ADD_ADMIN);
            intent.putExtra(LockConstant.ERROR_MSG, error.getErrorMsg());
            mLocalBroadcastManager.sendBroadcast(intent);
        }
    }
}