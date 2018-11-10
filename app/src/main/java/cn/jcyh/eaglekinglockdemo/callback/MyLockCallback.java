package cn.jcyh.eaglekinglockdemo.callback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import java.util.TimeZone;

import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.constant.Operation;
import cn.jcyh.eaglekinglockdemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.entity.LockKey;
import cn.jcyh.eaglekinglockdemo.entity.LockPwdRecord;
import cn.jcyh.eaglekinglockdemo.entity.LoginData;
import cn.jcyh.eaglekinglockdemo.http.LockHttpAction;
import cn.jcyh.eaglekinglockdemo.http.MyLockAPI;
import cn.jcyh.eaglekinglockdemo.ui.activity.AuthActivity;
import cn.jcyh.locklib.callback.LockCallback;
import cn.jcyh.locklib.entity.Error;
import cn.jcyh.locklib.scanner.ExtendedBluetoothDevice;
import cn.jcyh.utils.L;


/**
 * Created by jogger on 2018/4/27.
 * 锁回调
 */

public class MyLockCallback implements LockCallback {
    private Context mContext;
    private LoginData mUser;
    private final ControlCenter mControlCenter;
    private LocalBroadcastManager mLocalBroadcastManager;

    public MyLockCallback(Context context) {
        mContext = context.getApplicationContext();
        mControlCenter = ControlCenter.getControlCenter(context);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @Override
    public void onFoundDevice(ExtendedBluetoothDevice extendedBluetoothDevice) {
        L.i("------------onFoundDevice");
        mUser = mControlCenter.getLoginData();
        if (mUser == null || TextUtils.isEmpty(mUser.getSmartLockAccessToken())) {
            mContext.startActivity(new Intent(mContext, AuthActivity.class));
            return;
        }
        //发现设备并广播
        Intent intent = new Intent(LockConstant.ACTION_BLE_DEVICE);
        intent.putExtra(LockConstant.DEVICE, extendedBluetoothDevice);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
//            String accessToken = MyPreference.getStr(mContext, MyPreference.ACCESS_TOKEN);
        //根据accessToken和lockmac获取钥匙
        if (ControlCenter.sCurrentKey == null) return;
        switch (MyLockAPI.sBleSession.getOperation()) {
            case Operation.LOCKCAR_DOWN:
                if (extendedBluetoothDevice.isTouch())
                    MyLockAPI.getLockAPI().connect(extendedBluetoothDevice);
                break;
            case Operation.CUSTOM_PWD:
            case Operation.SET_ADMIN_KEYBOARD_PASSWORD:
            case Operation.SET_DELETE_PASSWORD:
            case Operation.SET_LOCK_TIME:
            case Operation.RESET_KEYBOARD_PASSWORD:
            case Operation.RESET_EKEY:
            case Operation.RESET_LOCK:
            case Operation.GET_LOCK_TIME:
            case Operation.GET_OPERATE_LOG:
                if (extendedBluetoothDevice.getAddress().equals(MyLockAPI.sBleSession.getLockmac()))
                    MyLockAPI.getLockAPI().connect(extendedBluetoothDevice);
                break;
            default:
                if (extendedBluetoothDevice.getAddress().equals(MyLockAPI.sBleSession.getLockmac()))
                    MyLockAPI.getLockAPI().connect(extendedBluetoothDevice);
                break;
        }
    }

    @Override
    public void onDeviceConnected(ExtendedBluetoothDevice extendedBluetoothDevice) {
        L.e("--------onDeviceConnected:" + MyLockAPI.sBleSession.getOperation());
        MyLockAPI lockAPI = MyLockAPI.getLockAPI();
        LockKey localKey = ControlCenter.sCurrentKey;
        String operation = MyLockAPI.sBleSession.getOperation();
        if (Operation.ADD_ADMIN.equals(operation)) {
            lockAPI.addAdministrator(extendedBluetoothDevice);
        }
        if (localKey == null) return;
        switch (operation) {
            case Operation.LOCKCAR_DOWN:
                L.e("--------------UNLOCK" + localKey);
                //本地存在锁
                if (localKey.isAdmin()) {
                    lockAPI.unlockByAdministrator(extendedBluetoothDevice, localKey);
                } else {
                    lockAPI.unlockByUser(extendedBluetoothDevice, localKey);
                }
                break;
            case Operation.SET_ADMIN_KEYBOARD_PASSWORD://管理码
                lockAPI.setAdminKeyboardPassword(extendedBluetoothDevice, localKey, MyLockAPI.sBleSession.getArgments().getString("password"));
                break;
            case Operation.CUSTOM_PWD:
                Bundle argments = MyLockAPI.sBleSession.getArgments();
                localKey.setStartDate(argments.getLong(LockConstant.START_DATE));
                localKey.setEndDate(argments.getLong(LockConstant.END_DATE));
                L.e("------CUSTOM_PWD");
                lockAPI.addPeriodKeyboardPassword(extendedBluetoothDevice, argments.getString(LockConstant.PWD), localKey);
                break;
            case Operation.DELETE_ONE_KEYBOARDPASSWORD://刪除密碼
                L.e("---------DELETE_ONE_KEYBOARDPASSWORD");
                argments = MyLockAPI.sBleSession.getArgments();
                LockPwdRecord lockPwdRecord = argments.getParcelable(LockConstant.PWD_INFO);
                if (lockPwdRecord == null) return;
                lockAPI.deleteOneKeyboardPassword(extendedBluetoothDevice, lockPwdRecord.getKeyboardPwdType(), lockPwdRecord.getKeyboardPwd(), localKey);
                break;
//                case SET_DELETE_PASSWORD://删除码
//                    mTTTTLockAPI.setDeletePassword(extendedBluetoothDevice, uid, curKey.getLockVersion(), curKey.getAdminPs(), curKey.getUnlockKey(), curKey.getLockFlagPos(), curKey.getAesKeystr(), bleSession.getPassword());
//                    break;
            case Operation.SET_LOCK_TIME://设置锁时间
                lockAPI.setLockTime(extendedBluetoothDevice, localKey);
                break;
            case Operation.RESET_KEYBOARD_PASSWORD://重置键盘密码
                lockAPI.resetKeyboardPassword(extendedBluetoothDevice, localKey);
                break;
            case Operation.RESET_EKEY://重置电子钥匙 锁标志位+1
                lockAPI.resetEKey(extendedBluetoothDevice, localKey);
                break;
            case Operation.RESET_LOCK://重置锁
                lockAPI.resetLock(extendedBluetoothDevice, localKey.getOpenid(), localKey.getLockVersion(), localKey.getAdminPwd(), localKey.getLockKey(), localKey.getLockFlagPos(), localKey.getAesKeystr());
                break;
            case Operation.ADD_IC_CARD:
                lockAPI.addICCard(extendedBluetoothDevice, localKey);
                break;
            case Operation.DELETE_IC_CARD:
                lockAPI.deleteICCard(extendedBluetoothDevice, MyLockAPI.sBleSession.getArgments().getLong(LockConstant.IC_CARD_NUMBER), localKey);
                break;
            case Operation.MODIFY_IC_PERIOD:
                lockAPI.modifyICPeriod(extendedBluetoothDevice, MyLockAPI.sBleSession.getArgments().getLong(LockConstant.IC_CARD_NUMBER), localKey);
                break;
            case Operation.SEARCH_IC_NUMBER:
                lockAPI.searchICCard(extendedBluetoothDevice, localKey);
                break;
            case Operation.CLEAR_IC_CARD:
                lockAPI.clearICCard(extendedBluetoothDevice, localKey);
                break;
            case Operation.ADD_FINGERPRINT:
                lockAPI.addFingerPrint(extendedBluetoothDevice, localKey);
                break;
            case Operation.MODIFY_FINGERPRINT_PERIOD:
                lockAPI.modifyFingerPrintPeriod(extendedBluetoothDevice, MyLockAPI.sBleSession.getArgments().getLong(LockConstant.FRNO), localKey);
                break;
            case Operation.CLEAR_FINGERPRINTS:
                lockAPI.clearFingerPrint(extendedBluetoothDevice, localKey);
                break;
            case Operation.DELETE_FINGERPRINT:
                lockAPI.deleteFingerPrint(extendedBluetoothDevice, MyLockAPI.sBleSession.getArgments().getLong(LockConstant.FRNO), localKey);
                break;
            case Operation.GET_OPERATE_LOG://获取操作日志
                lockAPI.getOperateLog(extendedBluetoothDevice, localKey);
                break;
            case Operation.GET_LOCK_TIME://获取锁时间
                lockAPI.getLockTime(extendedBluetoothDevice, localKey);
                break;
//                case DELETE_ONE_KEYBOARDPASSWORD://这里的密码类型传0
//                    mTTTTLockAPI.deleteOneKeyboardPassword(extendedBluetoothDevice, uid, localKey.getLockVersion(), localKey.getAdminPs(), localKey.getUnlockKey(), localKey.getLockFlagPos(), 0, bleSession.getPassword(), localKey.getAesKeystr());
//                    break;
//                case GET_LOCK_VERSION_INFO:
//                    mTTTTLockAPI.readDeviceInfo(extendedBluetoothDevice, localKey.getLockVersion(), localKey.getAesKeystr());
//                    break;
            default:
                break;
        }

    }

    @Override
    public void onDeviceDisconnected(ExtendedBluetoothDevice extendedBluetoothDevice) {
        L.e("-----------onDeviceDisconnected");
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
    public void onGetLockVersion(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, int var3, int var4,
                                 int var5, int var6, Error var7) {
        L.e("-----------onGetLockVersion");
    }

    @Override
    public void onAddAdministrator(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersionString, String adminPs, String unlockKey, String adminKeyboardPwd, String deletePwd, String pwdInfo, long timestamp, String aesKeystr, int feature, String modelNumber, String hardwareRevision, String firmwareRevision, Error error) {
        L.e("-----------onAddAdministrator");
        addAdmin(extendedBluetoothDevice, lockVersionString, adminPs, unlockKey, adminKeyboardPwd, deletePwd, pwdInfo, timestamp, aesKeystr, feature, modelNumber, hardwareRevision, firmwareRevision, error);
    }

    @Override
    public void onResetEKey(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, Error error) {
        L.e("-----------onResetEKey");
        Intent intent = new Intent(LockConstant.ACTION_RESET_KEY);
        intent.putExtra(LockConstant.ERROR_MSG, error);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onSetLockName(ExtendedBluetoothDevice extendedBluetoothDevice, String var2, Error var3) {
        L.e("-----------onSetLockName");
    }

    @Override
    public void onSetAdminKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String adminCode, Error
            error) {
        L.e("-----------onSetAdminKeyboardPassword" + adminCode);
        Intent intent = new Intent(LockConstant.ACTION_SET_ADMIN_PWD);
        intent.putExtra(LockConstant.ERROR_MSG, error);
        intent.putExtra(LockConstant.PWD, adminCode);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onSetDeletePassword(ExtendedBluetoothDevice extendedBluetoothDevice, String var2, Error var3) {
        L.e("-----------onSetDeletePassword");
    }

    @Override
    public void onUnlock(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, int uniqueid, long lockTime, Error error) {
        L.e("--------------onUnlock");
        Intent intent = new Intent(LockConstant.ACTION_UNLOCK);
        intent.putExtra(LockConstant.ERROR_MSG, error);
        mLocalBroadcastManager.sendBroadcast(intent);
        MyLockAPI.getLockAPI().connect(extendedBluetoothDevice, Operation.GET_OPERATE_LOG);
    }

    @Override
    public void onSetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {
        L.e("-----------onSetLockTime");
        Intent intent = new Intent(LockConstant.ACTION_LOCK_SYNC_TIME);
        intent.putExtra(LockConstant.ERROR_MSG, error);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onGetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, long date, Error error) {
        L.e("-----------onGetLockTime");
        Intent intent = new Intent(LockConstant.ACTION_LOCK_GET_TIME);
        intent.putExtra(LockConstant.ERROR_MSG, error);
        intent.putExtra(LockConstant.DATE, date);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onResetKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String pwdInfo, long timestamp, Error error) {
        L.e("-----------onResetKeyboardPassword");
        Intent intent = new Intent(LockConstant.ACTION_RESET_PWD);
        intent.putExtra(LockConstant.ERROR_MSG, error);
        intent.putExtra(LockConstant.PWD_RESET_DATA, pwdInfo);
        intent.putExtra(LockConstant.PWD_RESET_TIMESTAMP, timestamp);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onSetMaxNumberOfKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, Error
            var3) {
        L.e("-----------onSetMaxNumberOfKeyboardPassword");
    }

    @Override
    public void onResetKeyboardPasswordProgress(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, Error
            var3) {
        L.e("-----------onResetKeyboardPasswordProgress");
    }

    @Override
    public void onResetLock(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {
        L.e("-----------onResetLock");
        Intent intent = new Intent(LockConstant.ACTION_RESET_LOCK);
        intent.putExtra(LockConstant.ERROR_MSG, error);
        mLocalBroadcastManager.sendBroadcast(intent);

    }

    @Override
    public void onAddKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, String customPwd,
                                      long startTime, long endTime, Error error) {
        L.e("-----------onAddKeyboardPassword");
        Intent intent = new Intent(LockConstant.ACTION_CUSTOM_PWD);
        intent.putExtra(LockConstant.ERROR_MSG, error);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onModifyKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, String
            var3, String var4, Error var5) {
        L.e("-----------onModifyKeyboardPassword");
    }

    @Override
    public void onDeleteOneKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, String
            var3, Error var4) {
        L.e("-----------onDeleteOneKeyboardPassword");
    }

    @Override
    public void onDeleteAllKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, Error var2) {
        L.e("-----------onDeleteAllKeyboardPassword");
    }

    @Override
    public void onGetOperateLog(ExtendedBluetoothDevice extendedBluetoothDevice, String records, Error error) {
        L.e("-----------onGetOperateLog");
        if (Error.SUCCESS != error) return;
        //根据accessToken和lockmac获取钥匙
        if (ControlCenter.sCurrentKey != null && ControlCenter.sCurrentKey.getLockMac().equals(extendedBluetoothDevice.getAddress())) {
            LockHttpAction.getHttpAction(mContext).uploadLockRecords(ControlCenter.sCurrentKey.getLockId(), records, null);
        }
    }

    @Override
    public void onSearchDeviceFeature(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, int var3, Error
            var4) {
        L.e("-----------onSearchDeviceFeature");
    }

    @Override
    public void onAddICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int battery, long cardNo, Error error) {
        //onAddICCard2--9--1036620029
        L.e("-----------onAddICCard" + status + "--" + battery + "--" + cardNo);
        Intent intent = new Intent(LockConstant.ACTION_LOCK_IC_CARD);
        intent.putExtra(LockConstant.TYPE, LockConstant.TYPE_ADD_IC_CARD);
        intent.putExtra(LockConstant.ERROR_MSG, error);
        intent.putExtra(LockConstant.STATUS, status);
        intent.putExtra(LockConstant.IC_CARD_NUMBER, cardNo);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onModifyICCardPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long cardNo, long startDate, long endDate, Error error) {
        L.e("-----------onModifyICCardPeriod" + startDate + "-->" + endDate);
        Intent intent = new Intent(LockConstant.ACTION_LOCK_IC_CARD);
        intent.putExtra(LockConstant.TYPE, LockConstant.TYPE_MODIFY_IC_CARD);
        intent.putExtra(LockConstant.IC_CARD_NUMBER, cardNo);
        intent.putExtra(LockConstant.START_DATE, startDate);
        intent.putExtra(LockConstant.END_DATE, endDate);
        intent.putExtra(LockConstant.ERROR_MSG, error);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onDeleteICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long cardNo, Error error) {
        L.e("-----------onDeleteICCard");
        Intent intent = new Intent(LockConstant.ACTION_LOCK_IC_CARD);
        intent.putExtra(LockConstant.TYPE, LockConstant.TYPE_DELETE_IC_CARD);
        intent.putExtra(LockConstant.ERROR_MSG, error);
        intent.putExtra(LockConstant.IC_CARD_NUMBER, cardNo);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onClearICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error) {
        L.e("-----------onClearICCard");
        Intent intent = new Intent(LockConstant.ACTION_LOCK_IC_CARD);
        intent.putExtra(LockConstant.TYPE, LockConstant.TYPE_CLEAR_IC_CARD);
        intent.putExtra(LockConstant.ERROR_MSG, error);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onSetWristbandKeyToLock(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, Error var3) {
        L.e("-----------onSetWristbandKeyToLock");
    }

    @Override
    public void onSetWristbandKeyToDev(Error error) {
        L.e("-----------onSetWristbandKeyToDev");
    }

    @Override
    public void onSetWristbandKeyRssi(Error error) {
        L.e("-----------onSetWristbandKeyRssi");
    }

    @Override
    public void onAddFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int battery, long fingerPrintNo, Error error) {
//        L.e("-----------onAddFingerPrint:" + "status:" + status + "-->fingerPrintNo:" + fingerPrintNo + "error:" + error);
    }

    @Override
    public void onAddFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int bery, long fingerPrintNo, int maxVail, Error error) {
        L.e("-----------onAddFingerPrint:" + "status:" + status + "-->fingerPrintNo:" + fingerPrintNo + "-->maxVail:" + maxVail + "error:" + error);
        Intent intent = new Intent(LockConstant.ACTION_LOCK_FINGERPRINT);
        intent.putExtra(LockConstant.TYPE, LockConstant.TYPE_ADD_FINGERPRINT);
        intent.putExtra(LockConstant.FRNO, fingerPrintNo);
        intent.putExtra(LockConstant.STATUS, status);
        intent.putExtra(LockConstant.ERROR_MSG, error);
        intent.putExtra(LockConstant.MAX_VALIDATE, maxVail);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onFingerPrintCollection(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error) {
    }

    @Override
    public void onFingerPrintCollection(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int vail,
                                        int maxVail, Error error) {
        L.e("-----------onFingerPrintCollection" + vail + "--:" + maxVail);//1--:4
        Intent intent = new Intent(LockConstant.ACTION_LOCK_FINGERPRINT);
        intent.putExtra(LockConstant.TYPE, LockConstant.TYPE_COLLECTION_FINGERPRINT);
        intent.putExtra(LockConstant.ERROR_MSG, error);
        intent.putExtra(LockConstant.VALIDATE, vail);
        intent.putExtra(LockConstant.MAX_VALIDATE, maxVail);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onModifyFingerPrintPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long FRNo, long startDate, long endDate, Error error) {
        L.e("-----------onModifyFingerPrintPeriod");
        Intent intent = new Intent(LockConstant.ACTION_LOCK_FINGERPRINT);
        intent.putExtra(LockConstant.TYPE, LockConstant.TYPE_MODIFY_FINGERPRINT);
        intent.putExtra(LockConstant.FRNO, FRNo);
        intent.putExtra(LockConstant.START_DATE, startDate);
        intent.putExtra(LockConstant.END_DATE, endDate);
        intent.putExtra(LockConstant.ERROR_MSG, error);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onDeleteFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long FRNo, Error error) {
        L.e("-----------onDeleteFingerPrint");
    }

    @Override
    public void onClearFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error) {
        L.e("-----------onClearFingerPrint");
        Intent intent = new Intent(LockConstant.ACTION_LOCK_FINGERPRINT);
        intent.putExtra(LockConstant.TYPE, LockConstant.TYPE_CLEAR_FINGERPRINT);
        intent.putExtra(LockConstant.ERROR_MSG, error);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onSearchAutoLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, int var3, int var4,
                                     int var5, Error var6) {
        L.e("-----------onSearchAutoLockTime");
    }

    @Override
    public void onModifyAutoLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, int var3, Error
            var4) {
        L.e("-----------onModifyAutoLockTime");
    }

    @Override
    public void onReadDeviceInfo(ExtendedBluetoothDevice extendedBluetoothDevice, String var2, String var3, String
            var4, String var5, String var6) {
        L.e("-----------onReadDeviceInfo");
    }

    @Override
    public void onEnterDFUMode(ExtendedBluetoothDevice extendedBluetoothDevice, Error var2) {
        L.e("-----------onEnterDFUMode");
    }

    @Override
    public void onGetLockSwitchState(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, int var3, Error
            var4) {
        L.e("-----------onGetLockSwitchState");
    }

    @Override
    public void onLock(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int uid, int uniqueid, long lockTime, Error error) {
        L.e("-----------onLock");
    }

    @Override
    public void onScreenPasscodeOperate(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, int var3, Error
            var4) {
        L.e("-----------onScreenPasscodeOperate");
    }

    @Override
    public void onRecoveryData(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, Error var3) {
        L.e("-----------onRecoveryData");
    }

    @Override
    public void onSearchICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, String var3, Error var4) {
        L.e("-----------onSearchICCard");
    }

    @Override
    public void onSearchFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, String var3, Error
            var4) {
        L.e("-----------onSearchFingerPrint");
    }

    @Override
    public void onSearchPasscode(ExtendedBluetoothDevice extendedBluetoothDevice, String var2, Error var3) {
        L.e("-----------onSearchPasscode");
    }

    @Override
    public void onSearchPasscodeParam(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, String var3,
                                      long var4, Error var6) {
        L.e("-----------onSearchPasscodeParam");
    }

    @Override
    public void onOperateRemoteUnlockSwitch(ExtendedBluetoothDevice extendedBluetoothDevice, int var2, int var3,
                                            int var4, int var5, Error var6) {
        L.e("-----------onOperateRemoteUnlockSwitch");
    }


    /**
     * 添加管理员
     */
    private void addAdmin(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersionString, String adminPs, String unlockKey, String adminKeyboardPwd, String deletePwd, String pwdInfo, long timestamp, String aesKeystr, int feature, String modelNumber, String hardwareRevision, String firmwareRevision, final Error error) {
        L.e("--------error:" + error + "--->" + (error == Error.SUCCESS));
        Intent intent = new Intent(LockConstant.ACTION_ADD_ADMIN);
        if (error == Error.SUCCESS) {
            LockKey key = new LockKey();
            key.setAccessToken(ControlCenter.getControlCenter(mContext).getLoginData().getSmartLockAccessToken());
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
            intent.putExtra(LockConstant.LOCK_KEY, key);
        }
        intent.putExtra(LockConstant.ERROR_MSG, error);
        mLocalBroadcastManager.sendBroadcast(intent);
    }
}