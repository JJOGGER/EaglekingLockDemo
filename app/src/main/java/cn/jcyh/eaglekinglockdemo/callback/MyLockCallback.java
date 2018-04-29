package cn.jcyh.eaglekinglockdemo.callback;

import com.lock.bl.sdk.callback.LockCallback;
import com.lock.bl.sdk.entity.Error;
import com.lock.bl.sdk.scanner.ExtendedBluetoothDevice;

/**
 * Created by jogger on 2018/4/27.
 */

public class MyLockCallback implements LockCallback {
    @Override
    public void onFoundDevice(ExtendedBluetoothDevice var1) {

    }

    @Override
    public void onDeviceConnected(ExtendedBluetoothDevice var1) {

    }

    @Override
    public void onDeviceDisconnected(ExtendedBluetoothDevice var1) {

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
}
