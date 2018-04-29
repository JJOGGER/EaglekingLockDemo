//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lock.bl.sdk.callback;


import com.lock.bl.sdk.entity.Error;
import com.lock.bl.sdk.scanner.ExtendedBluetoothDevice;

public interface LockCallback {
    void onFoundDevice(ExtendedBluetoothDevice var1);

    void onDeviceConnected(ExtendedBluetoothDevice var1);

    void onDeviceDisconnected(ExtendedBluetoothDevice var1);

    void onGetLockVersion(ExtendedBluetoothDevice var1, int var2, int var3, int var4, int var5, int var6, Error var7);

    void onAddAdministrator(ExtendedBluetoothDevice var1, String var2, String var3, String var4, String var5, String var6, String var7, long var8, String var10, int var11, String var12, String var13, String var14, Error var15);

    void onResetEKey(ExtendedBluetoothDevice var1, int var2, Error var3);

    void onSetLockName(ExtendedBluetoothDevice var1, String var2, Error var3);

    void onSetAdminKeyboardPassword(ExtendedBluetoothDevice var1, String var2, Error var3);

    void onSetDeletePassword(ExtendedBluetoothDevice var1, String var2, Error var3);

    void onUnlock(ExtendedBluetoothDevice var1, int var2, int var3, long var4, Error var6);

    void onSetLockTime(ExtendedBluetoothDevice var1, Error var2);

    void onGetLockTime(ExtendedBluetoothDevice var1, long var2, Error var4);

    void onResetKeyboardPassword(ExtendedBluetoothDevice var1, String var2, long var3, Error var5);

    /**
     * @deprecated
     */
    @Deprecated
    void onSetMaxNumberOfKeyboardPassword(ExtendedBluetoothDevice var1, int var2, Error var3);

    void onResetKeyboardPasswordProgress(ExtendedBluetoothDevice var1, int var2, Error var3);

    void onResetLock(ExtendedBluetoothDevice var1, Error var2);

    void onAddKeyboardPassword(ExtendedBluetoothDevice var1, int var2, String var3, long var4, long var6, Error var8);

    void onModifyKeyboardPassword(ExtendedBluetoothDevice var1, int var2, String var3, String var4, Error var5);

    void onDeleteOneKeyboardPassword(ExtendedBluetoothDevice var1, int var2, String var3, Error var4);

    void onDeleteAllKeyboardPassword(ExtendedBluetoothDevice var1, Error var2);

    void onGetOperateLog(ExtendedBluetoothDevice var1, String var2, Error var3);

    void onSearchDeviceFeature(ExtendedBluetoothDevice var1, int var2, int var3, Error var4);

    void onAddICCard(ExtendedBluetoothDevice var1, int var2, int var3, long var4, Error var6);

    void onModifyICCardPeriod(ExtendedBluetoothDevice var1, int var2, long var3, long var5, long var7, Error var9);

    void onDeleteICCard(ExtendedBluetoothDevice var1, int var2, long var3, Error var5);

    void onClearICCard(ExtendedBluetoothDevice var1, int var2, Error var3);

    void onSetWristbandKeyToLock(ExtendedBluetoothDevice var1, int var2, Error var3);

    void onSetWristbandKeyToDev(Error var1);

    void onSetWristbandKeyRssi(Error var1);

    /**
     * @deprecated
     */
    @Deprecated
    void onAddFingerPrint(ExtendedBluetoothDevice var1, int var2, int var3, long var4, Error var6);

    void onAddFingerPrint(ExtendedBluetoothDevice var1, int var2, int var3, long var4, int var6, Error var7);

    /**
     * @deprecated
     */
    @Deprecated
    void onFingerPrintCollection(ExtendedBluetoothDevice var1, int var2, Error var3);

    void onFingerPrintCollection(ExtendedBluetoothDevice var1, int var2, int var3, int var4, Error var5);

    void onModifyFingerPrintPeriod(ExtendedBluetoothDevice var1, int var2, long var3, long var5, long var7, Error var9);

    void onDeleteFingerPrint(ExtendedBluetoothDevice var1, int var2, long var3, Error var5);

    void onClearFingerPrint(ExtendedBluetoothDevice var1, int var2, Error var3);

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
