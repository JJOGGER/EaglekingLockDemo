package com.lock.bl.sdk.api;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lock.bl.sdk.callback.LockCallback;
import com.lock.bl.sdk.command.CommandUtil;
import com.lock.bl.sdk.entity.TransferData;
import com.lock.bl.sdk.scanner.ExtendedBluetoothDevice;
import com.lock.bl.sdk.service.BluetoothLeService;
import com.lock.bl.sdk.util.DigitUtil;
import com.lock.bl.sdk.util.Timber;

/**
 * Created by jogger on 2018/4/27.
 */

public class LockAPI {
    private static final String TAG = "LockAPI";
    private static LockAPI sAPI;
    private static LockCallback sCallback;
    private Context mContext;
    private BluetoothLeService mBluetoothLeService;
    public static final int REQUEST_ENABLE_BT = 1;
    public static boolean scan;

    public LockAPI(Context context, @NonNull LockCallback lockCallback) {
        mContext = context;
        sCallback = lockCallback;
    }

    public void requestBleEnable(Activity activity) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
            activity.startActivityForResult(enableIntent, 1);
        }
    }

    public void startBleService(Context context) {
        Intent intent = new Intent(context, BluetoothLeService.class);
        context.startService(intent);
    }

    public void startBTDeviceScan() {
        scan = true;
        this.mBluetoothLeService = BluetoothLeService.getBluetoothLeService();
        Timber.e("-------startBTDeviceScan" + mBluetoothLeService);
        if (this.mBluetoothLeService == null) {
        } else {
            this.mBluetoothLeService.setScan(true);
            this.mBluetoothLeService.startScan();
        }

    }

    public void stopBleService(Context context) {
        Intent intent = new Intent(context, BluetoothLeService.class);
        this.mBluetoothLeService = null;
    }

    public static LockCallback getLockCallback() {
        return sCallback;
    }

    public boolean isConnected(String address) {
        if (this.mBluetoothLeService == null) {
            this.mBluetoothLeService = BluetoothLeService.getBluetoothLeService();
            Timber.e("-----mBluetoothLeService = " + this.mBluetoothLeService);
        }

        if (this.mBluetoothLeService == null) {
            Timber.e("-----mBluetoothLeService is null");
            return false;
        } else {
            return this.mBluetoothLeService.isConnected(address);
        }
    }

    public void unlockByUser(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, long startDate, long endDate, String unlockKey, int lockFlagPos, String aesKeyStr, long timezoneOffset) {
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        Timber.e("--------uid=" + uid + " lockVersion=" + lockVersion + " unlockKey" + unlockKey + " lockFlagPos=" + lockFlagPos + " aesKeyArray=" + DigitUtil.byteArrayToHexString(aesKeyArray));
        TransferData transferData = new TransferData();
        transferData.setAPICommand(4);
        transferData.setCommand(85);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setStartDate(startDate);
        transferData.setEndDate(endDate);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        transferData.setTimezoneOffSet(timezoneOffset);
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.U_checkUserTime(transferData);
    }

    public void unlockByAdministrator(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, long unlockDate, String aesKeyStr, long timezoneOffset) {
        Log.e(TAG, "------this:" + this.toString());
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        Timber.e("--------uid=" + uid + " lockVersion=" + lockVersion + " adminPs=" + adminPs + " unlockKey" + unlockKey + " lockFlagPos=" + lockFlagPos
                + " aesKeyArray=" + DigitUtil.byteArrayToHexString(aesKeyArray));
        TransferData transferData = new TransferData();
        transferData.setAPICommand(3);
        transferData.setCommand(65);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        transferData.setUnlockDate(unlockDate);
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setTimezoneOffSet(timezoneOffset);
        CommandUtil.A_checkAdmin(transferData);
    }

    public synchronized void connect(String address) {
        this.mBluetoothLeService = BluetoothLeService.getBluetoothLeService();
        if (this.mBluetoothLeService == null) {
            Timber.e("---------mBluetoothLeService is null");
        } else {
            this.mBluetoothLeService.setNeedReCon(true);
            this.mBluetoothLeService.setConnectCnt(0);
            this.mBluetoothLeService.connect(address);
        }
    }

    public synchronized void connect(ExtendedBluetoothDevice device) {
        Timber.e("---------connect ...");
        this.mBluetoothLeService = BluetoothLeService.getBluetoothLeService();
        Timber.e("---------mBluetoothLeService = " + this.mBluetoothLeService);
        if (this.mBluetoothLeService == null) {
            Timber.e("-----------mBluetoothLeService is null");
        } else {
            this.mBluetoothLeService.setNeedReCon(true);
            this.mBluetoothLeService.setConnectCnt(0);
            this.mBluetoothLeService.connect(device);
        }

    }
}
