//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.callback;

import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;

public interface LockCallback {
    void onFoundDevice(ExtendedBluetoothDevice extendedBluetoothDevice);

    void onDeviceConnected(ExtendedBluetoothDevice extendedBluetoothDevice);

    void onDeviceDisconnected(ExtendedBluetoothDevice extendedBluetoothDevice);

    void onGetLockVersion(ExtendedBluetoothDevice extendedBluetoothDevice, int protocolType, int protocolVersion, int scene, int groupId, int orgId, Error error);

    void onAddAdministrator(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersionString, String adminPs, String unlockKey, String adminKeyboardPwd, String deletePwd, String pwdInfo, long timestamp, String aesKeystr, int feature, String modelNumber, String hardwareRevision, String firmwareRevision, Error error);

    void onResetEKey(ExtendedBluetoothDevice extendedBluetoothDevice, int lockFlagPos, Error error);

    void onSetLockName(ExtendedBluetoothDevice var1, String var2, Error var3);

    void onSetAdminKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String adminKeyboardPwd, Error error);

    void onSetDeletePassword(ExtendedBluetoothDevice var1, String var2, Error var3);

    void onUnlock(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, int uniqueid, long lockTime, Error error);

    void onSetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, Error error);

    void onGetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, long lockTime, Error error);

    void onResetKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String pwdInfo, long timestamp, Error error);

    /**
     * @deprecated
     */
    @Deprecated
    void onSetMaxNumberOfKeyboardPassword(ExtendedBluetoothDevice var1, int var2, Error var3);

    void onResetKeyboardPasswordProgress(ExtendedBluetoothDevice var1, int var2, Error var3);

    void onResetLock(ExtendedBluetoothDevice extendedBluetoothDevice, Error error);

    void onAddKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int keyboardPwdType, String password, long startDate, long endDate, Error error);

    void onModifyKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int keyboardPwdType, String originPwd, String newPwd, Error error);

    void onDeleteOneKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int keyboardPwdType, String deletedPwd, Error error);

    void onDeleteAllKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, Error error);

    void onGetOperateLog(ExtendedBluetoothDevice extendedBluetoothDevice, String records, Error error);

    void onSearchDeviceFeature(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int feature, Error error);

    void onAddICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int battery, long cardNo, Error error);

    void onModifyICCardPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long cardNo, long startDate, long endDate, Error error);

    void onDeleteICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long cardNo, Error error);

    void onClearICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error);

    void onSetWristbandKeyToLock(ExtendedBluetoothDevice var1, int var2, Error var3);

    void onSetWristbandKeyToDev(Error var1);

    void onSetWristbandKeyRssi(Error var1);

    /**
     * @deprecated
     */
    @Deprecated
    void onAddFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int battery, long fingerPrintNo, Error error);

    void onAddFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int battery, long fingerPrintNo, int var6, Error var7);

    /**
     * @deprecated
     */
    @Deprecated
    void onFingerPrintCollection(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error);

    void onFingerPrintCollection(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int vail,
                                 int maxVail, Error error);

    void onModifyFingerPrintPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long FRNo, long startDate, long endDate, Error error);

    void onDeleteFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long FRNo, Error error);

    void onClearFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error);

    void onSearchAutoLockTime(ExtendedBluetoothDevice var1, int var2, int var3, int var4, int var5, Error var6);

    void onModifyAutoLockTime(ExtendedBluetoothDevice var1, int var2, int var3, Error var4);

    void onReadDeviceInfo(ExtendedBluetoothDevice var1, String var2, String var3, String var4, String var5, String var6);

    void onEnterDFUMode(ExtendedBluetoothDevice var1, Error var2);

    void onGetLockSwitchState(ExtendedBluetoothDevice var1, int var2, int var3, Error var4);

    void onLock(ExtendedBluetoothDevice var1, int var2, int var3, int var4, long var5, Error var7);

    void onScreenPasscodeOperate(ExtendedBluetoothDevice var1, int var2, int var3, Error var4);

    void onRecoveryData(ExtendedBluetoothDevice var1, int var2, Error var3);

    void onSearchICCard(ExtendedBluetoothDevice var1, int var2, String var3, Error var4);

    void onSearchFingerPrint(ExtendedBluetoothDevice var1, int var2, String var3, Error var4);

    void onSearchPasscode(ExtendedBluetoothDevice var1, String var2, Error var3);

    void onSearchPasscodeParam(ExtendedBluetoothDevice var1, int var2, String var3, long var4, Error var6);

    void onOperateRemoteUnlockSwitch(ExtendedBluetoothDevice var1, int var2, int var3, int var4, int var5, Error var6);
}
