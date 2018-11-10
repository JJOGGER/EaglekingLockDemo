//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.api;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;
import android.util.Log;

import com.ttlock.bl.sdk.callback.LockCallback;
import com.ttlock.bl.sdk.command.CommandUtil;
import com.ttlock.bl.sdk.constant.Constant;
import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.entity.LockVersion;
import com.ttlock.bl.sdk.entity.TransferData;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;
import com.ttlock.bl.sdk.service.BluetoothLeService;
import com.ttlock.bl.sdk.util.DigitUtil;
import com.ttlock.bl.sdk.util.LogUtil;
import com.ttlock.bl.sdk.util.SharePreUtil;

import java.util.List;

public class LockAPI {
    private static boolean DBG = true;
    public static final int REQUEST_ENABLE_BT = 1;
    private BluetoothLeService mBluetoothLeService;
    public static boolean scan;
    public static LockCallback sLockCallback;
    protected static Context sContext;

    public LockAPI() {
    }
    public static void init(Context context, LockCallback lockCallback, String CLIENT_ID, String CLIENT_SECRET) {
        sContext = context.getApplicationContext();
        sLockCallback = lockCallback;
        SharePreUtil.getInstance(sContext).setString(Constant.CLIENT_ID, CLIENT_ID);
        SharePreUtil.getInstance(sContext).setString(Constant.CLIENT_SECRET, CLIENT_SECRET);
    }


    public static LockCallback getLockCallback() {
        return sLockCallback;
    }

    private boolean isBLESupported(@NonNull Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
    }

    public boolean isConnected(ExtendedBluetoothDevice extendedBluetoothDevice) {
        return this.isConnected(extendedBluetoothDevice.getAddress());
    }

    public boolean isConnected(String address) {
        if (this.mBluetoothLeService == null) {
            this.mBluetoothLeService = BluetoothLeService.getBluetoothLeService();
            LogUtil.d("mBluetoothLeService = " + this.mBluetoothLeService, DBG);
        }

        if (this.mBluetoothLeService == null) {
            LogUtil.w("mBluetoothLeService is null", DBG);
            return false;
        } else {
            return this.mBluetoothLeService.isConnected(address);
        }
    }

    @RequiresPermission("android.permission.BLUETOOTH")
    public boolean isBLEEnabled(Context context) {
        BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager.getAdapter();
        return adapter != null && adapter.isEnabled();
    }

    private void showBLEDialog(Context context) {
        Intent enableIntent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
        ((Activity) context).startActivityForResult(enableIntent, 1);
    }

    @RequiresPermission("android.permission.BLUETOOTH")
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

    public void stopBleService(Context context) {
        Intent intent = new Intent(context, BluetoothLeService.class);
        LogUtil.d("stop:" + context.stopService(intent), DBG);
        this.mBluetoothLeService = null;
    }

    @RequiresPermission("android.permission.BLUETOOTH")
    public void scanWristbandDevice() {
        this.mBluetoothLeService = BluetoothLeService.getBluetoothLeService();
        if (this.mBluetoothLeService != null) {
            this.mBluetoothLeService.setScanBongOnly(true);
            this.mBluetoothLeService.setScan(true);
            this.mBluetoothLeService.startScan();
        }

    }

    public void cancelScanWristbandDevice() {
        this.mBluetoothLeService = BluetoothLeService.getBluetoothLeService();
        if (this.mBluetoothLeService != null) {
            this.mBluetoothLeService.setScanBongOnly(false);
            this.mBluetoothLeService.startScan();
        }

    }

    @RequiresPermission("android.permission.BLUETOOTH")
    public void startBTDeviceScan() {
        scan = true;
        this.mBluetoothLeService = BluetoothLeService.getBluetoothLeService();
        if (this.mBluetoothLeService == null) {
            LogUtil.d("服务启动成功后扫描", DBG);
        } else {
            LogUtil.d("启动蓝牙扫描", DBG);
            this.mBluetoothLeService.setScan(true);
            this.mBluetoothLeService.startScan();
        }

    }

    public void stopBTDeviceScan() {
        LogUtil.d("stop scan", DBG);
        if (this.mBluetoothLeService == null) {
            this.mBluetoothLeService = BluetoothLeService.getBluetoothLeService();
            LogUtil.d("mBluetoothLeService = " + this.mBluetoothLeService, DBG);
        }

        if (this.mBluetoothLeService == null) {
            LogUtil.w("mBluetoothLeService is null", DBG);
        } else {
            this.mBluetoothLeService.setScan(false);
            this.mBluetoothLeService.stopScan();
        }

    }

    @RequiresPermission("android.permission.BLUETOOTH")
    protected synchronized void connect(String address) {
        this.mBluetoothLeService = BluetoothLeService.getBluetoothLeService();
        LogUtil.d("mBluetoothLeService = " + this.mBluetoothLeService, DBG);
        if (this.mBluetoothLeService == null) {
            LogUtil.w("mBluetoothLeService is null", DBG);
        } else {
            this.mBluetoothLeService.setNeedReCon(true);
            this.mBluetoothLeService.setConnectCnt(0);
            this.mBluetoothLeService.connect(address);
        }
    }

    public void disconnect() {
        this.mBluetoothLeService = BluetoothLeService.getBluetoothLeService();
        if (this.mBluetoothLeService != null) {
            this.mBluetoothLeService.clearTask();
            this.mBluetoothLeService.disconnect();
        }

    }

    public synchronized void connect(ExtendedBluetoothDevice device) {
        LogUtil.d(Thread.currentThread().toString(), DBG);
        this.mBluetoothLeService = BluetoothLeService.getBluetoothLeService();
        if (this.mBluetoothLeService == null) {
            LogUtil.w("mBluetoothLeService is null", DBG);
        } else {
            this.mBluetoothLeService.setNeedReCon(true);
            this.mBluetoothLeService.setConnectCnt(0);
            this.mBluetoothLeService.connect(device);
        }

    }

    public void getVersionInfo() {
        CommandUtil.E_getLockVersion(1);
    }

    public void setClientPara(String vendor) {
        Constant.VENDOR = vendor;
    }

    /**
     * 添加管理员
     */
    public void addAdministrator(ExtendedBluetoothDevice extendedBluetoothDevice) {
        int lockType = extendedBluetoothDevice.getLockType();
        if (lockType > 3 && !extendedBluetoothDevice.isSettingMode()) {
            Error adminPs1 = Error.LOCK_IS_IN_NO_SETTING_MODE;
            adminPs1.setLockmac(extendedBluetoothDevice.getAddress());
            sLockCallback.onAddAdministrator(extendedBluetoothDevice, null, null, null, null,
                    null, null, 0L, null, -1, null,
                    null, null, adminPs1);
        } else {
            String adminPs;
            String unlockKey;
            Log.e("LOCKAPI","------------LockAPI"+!LockHttpAction.isNetworkAvailable(sContext)+"-->"+lockType);
            if (!LockHttpAction.isNetworkAvailable(sContext)) {
                sLockCallback.onAddAdministrator(extendedBluetoothDevice, null, null, null, null, null, null, 0L, null,
                        -1, null, null, null, Error.LOCK_OPERATE_FAILED);
                return;
            }
            switch (lockType) {
                case 3:
                    adminPs = new String(DigitUtil.generateDynamicPassword(10));
                    unlockKey = new String(DigitUtil.generateDynamicPassword(10));
                    CommandUtil.V_addAdmin(3, adminPs, unlockKey, null);
                    break;
                case 4:
                    LogUtil.e("V2", DBG);
                    CommandUtil.getAESKey(LockVersion.lockVersion_V2S_PLUS, "", 2);
                    break;
                case 5:
                case 8:
                    LogUtil.e("V3", DBG);
                    CommandUtil.getAESKey(LockVersion.lockVersion_V3, Constant.VENDOR, 2);
                    Constant.VENDOR = Constant.SCIENER;
                    break;
                case 6:
                    adminPs = new String(DigitUtil.generateDynamicPassword(10));
                    unlockKey = new String(DigitUtil.generateDynamicPassword(10));
                    CommandUtil.V_addAdmin(6, adminPs, unlockKey, (byte[]) null);
                case 7:
                    break;
                default:
                    CommandUtil.E_getLockVersion(2);
            }

        }
    }

    public void setAdminKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr, String password) {
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        if (!DigitUtil.isNumeric(password)) {
            password = DigitUtil.decodeLockData(password);
        }

        LogUtil.d("uid=" + uid + " lockVersion=" + lockVersion + " adminPs=" + adminPs + " unlockKey" + unlockKey + " lockFlagPos=" + lockFlagPos + " aesKeyArray=" + DigitUtil.byteArrayToHexString(aesKeyArray) + "password:" + password, DBG);
        CommandUtil.A_checkAdmin(uid, lockVersion, adminPs, unlockKey, lockFlagPos, aesKeyArray, password, 5);
    }

    public void setDeletePassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr, String password) {
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        if (!DigitUtil.isNumeric(password)) {
            password = DigitUtil.decodeLockData(password);
        }

        LogUtil.d("uid=" + uid + " lockVersion=" + lockVersion + " adminPs=" + adminPs + " unlockKey" + unlockKey + " lockFlagPos=" + lockFlagPos + " aesKeyArray=" + DigitUtil.byteArrayToHexString(aesKeyArray), DBG);
        CommandUtil.A_checkAdmin(uid, lockVersion, adminPs, unlockKey, lockFlagPos, aesKeyArray, password, 12);
    }

    public void unlockByAdministrator(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, long unlockDate, String aesKeyStr, long timezoneOffset) {
        byte[] aesKeyArray = null;
        if (!TextUtils.isEmpty(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        LogUtil.d("uid=" + uid + " lockVersion=" + lockVersion + " adminPs=" + adminPs + " unlockKey" + unlockKey + " lockFlagPos=" + lockFlagPos + " aesKeyArray=" + DigitUtil.byteArrayToHexString(aesKeyArray), DBG);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(3);
        transferData.setCommand((byte) 65);
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

    public void unlockByAdministrator(int uid, LockKey lockKey) {
        if (lockKey == null)
            return;
        byte[] aesKeyArray = null;
        if (!TextUtils.isEmpty(lockKey.getAesKeystr())) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(lockKey.getAesKeystr());
        }
        TransferData transferData = new TransferData();
        transferData.setAPICommand(3);
        transferData.setCommand((byte) 65);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockKey.getLockVersion());
        transferData.setAdminPs(lockKey.getAdminPwd());
        transferData.setUnlockKey(lockKey.getLockKey());
        transferData.setLockFlagPos(lockKey.getLockFlagPos());
        transferData.setUnlockDate(System.currentTimeMillis());
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setTimezoneOffSet(lockKey.getTimezoneRawOffset());
        CommandUtil.A_checkAdmin(transferData);
    }

    public void lockByAdministrator(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) {
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        LogUtil.d("uid=" + uid + " lockVersion=" + lockVersion + " adminPs=" + adminPs + " unlockKey" + unlockKey + " lockFlagPos=" + lockFlagPos + " aesKeyArray=" + DigitUtil.byteArrayToHexString(aesKeyArray), DBG);
        CommandUtil.A_checkAdmin(uid, lockVersion, adminPs, unlockKey, lockFlagPos, aesKeyArray, 13);
    }

    public void unlockByUser(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, long startDate, long endDate, String unlockKey, int lockFlagPos, String aesKeyStr, long timezoneOffset) {
        LogUtil.d("this:" + this.toString(), DBG);
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        LogUtil.d("uid=" + uid + " lockVersion=" + lockVersion + " unlockKey" + unlockKey + " lockFlagPos=" + lockFlagPos + " aesKeyArray=" + DigitUtil.byteArrayToHexString(aesKeyArray), DBG);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(4);
        transferData.setCommand((byte) 85);
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

    public void unlockByUser(int uid, LockKey lockKey) {
        byte[] aesKeyArray = null;
        if (!TextUtils.isEmpty(lockKey.getAesKeystr())) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(lockKey.getAesKeystr());
        }
        TransferData transferData = new TransferData();
        transferData.setAPICommand(4);
        transferData.setCommand((byte) 85);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockKey.getLockVersion());
        transferData.setStartDate(lockKey.getStartDate());
        transferData.setEndDate(lockKey.getEndDate());
        transferData.setUnlockKey(lockKey.getLockKey());
        transferData.setLockFlagPos(lockKey.getLockFlagPos());
        transferData.setTimezoneOffSet(lockKey.getTimezoneRawOffset());
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.U_checkUserTime(transferData);
    }

    public void lockByUser(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, long startDate, long endDate, String unlockKey, int lockFlagPos, String aesKeyStr, long timezoneOffset) {
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        TransferData transferData = new TransferData();
        transferData.setAPICommand(14);
        transferData.setCommand((byte) 85);
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

    public void setLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String unlockKey, long date, int lockFlagPos, String aesKeyStr, long timezoneOffset) {
        LogUtil.e("校准时间", DBG);
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }
        TransferData transferData = new TransferData();
        transferData.setAPICommand(6);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setUnlockKey(unlockKey);
        transferData.setCalibationTime(date);
        transferData.setLockFlagPos(lockFlagPos);
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setTimezoneOffSet(timezoneOffset);
        CommandUtil.C_calibationTime(transferData);
    }

    public void resetKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) {
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        LogUtil.d("uid=" + uid + " lockVersion=" + lockVersion + " adminPs=" + adminPs + " unlockKey" + unlockKey + " lockFlagPos=" + lockFlagPos + " aesKeyArray=" + DigitUtil.byteArrayToHexString(aesKeyArray), DBG);
        CommandUtil.A_checkAdmin(uid, lockVersion, adminPs, unlockKey, lockFlagPos, aesKeyArray, 0, (String) null, 16);
    }

    public void resetEKey(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, @NonNull String adminPs, int lockFlagPos, String aesKeyStr) {
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        LogUtil.d("uid=" + uid + " lockVersion=" + lockVersion + " adminPs=" + adminPs + " lockFlagPos=" + lockFlagPos + " aesKeyArray=" + DigitUtil.byteArrayToHexString(aesKeyArray), DBG);
        CommandUtil.A_checkAdmin(uid, lockVersion, adminPs, (String) null, lockFlagPos, aesKeyArray, 0, (String) null, 15);
    }

    public void setLockName(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr, String lockname) {
        if (lockname.getBytes().length > 15) {
            LogUtil.d("锁名称不能超过15个字节", DBG);
        } else {
            byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
            CommandUtil.A_checkAdmin(uid, lockVersion, adminPs, unlockKey, lockFlagPos, aesKeyArray, lockname, 17);
        }
    }

    public void getLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersion, String aesKeyStr, long timezoneOffset) {
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        TransferData transferData = new TransferData();
        transferData.setLockVersion(lockVersion);
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setTimezoneOffSet(timezoneOffset);
        CommandUtil.getLockTime(transferData);
    }

    public void resetLock(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        CommandUtil.A_checkAdmin(uid, lockVersion, adminPs, unlockKey, lockFlagPos, aesKeyArray, 19);
    }

    public void getOperateLog(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersion, String aesKeyStr, long timezoneOffset) {
        LogUtil.d("this:" + this.toString(), DBG);
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        TransferData transferData = new TransferData();
        transferData.setLockVersion(lockVersion);
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setTimezoneOffSet(timezoneOffset);
        transferData.setSeq((short) -1);
        CommandUtil.getOperateLog(transferData);
    }

    public void getAllOperateLog(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersion, String aesKeyStr, long timezoneOffset) {
        LogUtil.d("this:" + this.toString(), DBG);
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        TransferData transferData = new TransferData();
        transferData.setLockVersion(lockVersion);
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setTimezoneOffSet(timezoneOffset);
        transferData.setSeq((short) 0);
        CommandUtil.getOperateLog(transferData);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void getValidKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersion, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        CommandUtil.getValidKeyboardPassword(lockVersion, (short) 0, aesKeyArray);
    }

    private void addOnceKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String password, long startDate, long endDate, String aesKeyStr, long timezoneOffset) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(20);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setStartDate(startDate);
        transferData.setEndDate(endDate);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        transferData.setOriginalPwd(password);
        transferData.setTimezoneOffSet(timezoneOffset);
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.A_checkAdmin(transferData);
    }

    private void addPermanentKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String password, long startDate, String aesKeyStr, long timezoneOffset) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(21);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setStartDate(startDate);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        transferData.setOriginalPwd(password);
        transferData.setTimezoneOffSet(timezoneOffset);
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void addPeriodKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String password, long startDate, long endDate, String aesKeyStr, long timezoneOffset) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(22);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setStartDate(startDate);
        transferData.setEndDate(endDate);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        transferData.setOriginalPwd(password);
        transferData.setTimezoneOffSet(timezoneOffset);
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void deleteOneKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, int keyboardPwdType, @NonNull String password, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        LogUtil.d("uid=" + uid + " lockVersion=" + lockVersion + " adminPs=" + adminPs + " unlockKey" + unlockKey + " lockFlagPos=" + lockFlagPos + "password=" + password + " aesKeyArray=" + DigitUtil.byteArrayToHexString(aesKeyArray), DBG);
        CommandUtil.A_checkAdmin(uid, lockVersion, adminPs, unlockKey, lockFlagPos, aesKeyArray, 0, keyboardPwdType, (String) null, password, 0L, 0L, 24);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void deleteKeyboardPasswords(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, List<String> passwords, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        LogUtil.d("uid=" + uid + " lockVersion=" + lockVersion + " adminPs=" + adminPs + " unlockKey" + unlockKey + " lockFlagPos=" + lockFlagPos + " aesKeyArray=" + DigitUtil.byteArrayToHexString(aesKeyArray), DBG);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(41);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        transferData.setPwds(passwords);
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void modifyKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, int keyboardPwdType, String originalPwd, String newPwd, long startDate, long endDate, String aesKeyStr, long timezoneOffset) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        LogUtil.d("uid=" + uid + " lockVersion=" + lockVersion + " adminPs=" + adminPs + " unlockKey" + unlockKey + " lockFlagPos=" + lockFlagPos + " aesKeyArray=" + DigitUtil.byteArrayToHexString(aesKeyArray), DBG);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(23);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        transferData.setStartDate(startDate);
        transferData.setEndDate(endDate);
        transferData.setOriginalPwd(originalPwd);
        transferData.setNewPwd(newPwd);
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setTimezoneOffSet(timezoneOffset);
        CommandUtil.A_checkAdmin(transferData);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void deleteAllKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        CommandUtil.A_checkAdmin(uid, lockVersion, adminPs, unlockKey, lockFlagPos, aesKeyArray, 0, 0, "", "", 0L, 0L, 23);
    }

    public void searchDeviceFeature(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(27);
        transferData.setCommand((byte) 65);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void searchICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr, long timezoneOffset) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(28);
        transferData.setCommand((byte) 65);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setTimezoneOffSet(timezoneOffset);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void addICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(29);
        transferData.setCommand((byte) 5);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void modifyICPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, long cardNo, long startDate, long endDate, String aesKeyStr, long timezoneOffset) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(30);
        transferData.setCommand((byte) 5);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setNo(cardNo);
        transferData.setStartDate(startDate);
        transferData.setEndDate(endDate);
        transferData.setTimezoneOffSet(timezoneOffset);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void deleteICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, long cardNo, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(31);
        transferData.setCommand((byte) 5);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setNo(cardNo);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void clearICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(32);
        transferData.setCommand((byte) 5);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void setWristbandKeyToLock(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String wristbandKey, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(33);
        transferData.setCommand((byte) 53);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        transferData.setWristbandKey(wristbandKey);
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void setWristbandKeyToDev(String wristbandKey) {
        BluetoothLeService.getBluetoothLeService().sendBongCommand(wristbandKey);
    }

    public void setWristbandRssi(int rssi) {
        BluetoothLeService.getBluetoothLeService().setBongRssi((byte) Math.abs(rssi));
    }

    public void searchFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr, long timezoneOffset) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(46);
        transferData.setCommand((byte) 65);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setTimezoneOffSet(timezoneOffset);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void addFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(34);
        transferData.setCommand((byte) 6);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void modifyFingerPrintPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, long FRNo, long startDate, long endDate, String aesKeyStr, long timezoneOffset) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(35);
        transferData.setCommand((byte) 6);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setNo(FRNo);
        transferData.setStartDate(startDate);
        transferData.setEndDate(endDate);
        transferData.setTimezoneOffSet(timezoneOffset);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void deleteFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, long cardNo, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(36);
        transferData.setCommand((byte) 6);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setNo(cardNo);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void clearFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(37);
        transferData.setCommand((byte) 6);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void searchAutoLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(38);
        transferData.setCommand((byte) 54);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void modifyAutoLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, int time, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(39);
        transferData.setCommand((byte) 54);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setCalibationTime((long) time);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void readDeviceInfo(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersion, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setLockVersion(lockVersion);
        CommandUtil.readDeviceInfo(transferData);
    }

    public void enterDFUMode(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) {
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        TransferData transferData = new TransferData();
        transferData.setAPICommand(40);
        transferData.setCommand((byte) 2);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void getLockSwitchState(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersion, String aesKeyStr) {
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        TransferData transferData = new TransferData();
        transferData.setCommand((byte) 20);
        transferData.setLockVersion(lockVersion);
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.searchBicycleStatus(transferData);
    }

    public void lock(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, long startDate, long endDate, String unlockKey, int lockFlagPos, long lockDate, String aesKeyStr, long timezoneOffset) {
        LogUtil.d("this:" + this.toString(), DBG);
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        LogUtil.d("uid=" + uid + " lockVersion=" + lockVersion + " unlockKey" + unlockKey + " lockFlagPos=" + lockFlagPos + " aesKeyArray=" + DigitUtil.byteArrayToHexString(aesKeyArray), DBG);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(42);
        transferData.setCommand((byte) 85);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setStartDate(startDate);
        transferData.setEndDate(endDate);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        transferData.setTimezoneOffSet(timezoneOffset);
        transferData.setUnlockDate(lockDate);
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.U_checkUserTime(transferData);
    }

    public void showPasswordOnScreen(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, int opType, String aesKeyStr) {
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        TransferData transferData = new TransferData();
        transferData.setAPICommand(43);
        transferData.setCommand((byte) 89);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        if (opType >= 1 && opType <= 3) {
            transferData.setOp(opType);
            TransferData.setAesKeyArray(aesKeyArray);
            CommandUtil.A_checkAdmin(transferData);
        } else {
            LogUtil.d("操作类型错误 opType:" + opType, DBG);
        }
    }

    public void recoveryData(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, int opType, String json, String aesKeyStr, long timezoneOffset) {
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }

        TransferData transferData = new TransferData();
        transferData.setAPICommand(44);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        if (opType >= 1 && opType <= 3) {
            byte command = 0;
            switch (opType) {
                case 1:
                    command = 3;
                    break;
                case 2:
                    command = 5;
                    break;
                case 3:
                    command = 6;
            }

            transferData.setCommand(command);
            transferData.setOp(opType);
            transferData.setJson(json);
            TransferData.setAesKeyArray(aesKeyArray);
            transferData.setTimezoneOffSet(timezoneOffset);
            CommandUtil.A_checkAdmin(transferData);
        } else {
            LogUtil.d("操作类型错误 opType:" + opType, DBG);
        }
    }

    public void searchPasscodeParam(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String unlockKey, int lockFlagPos, String aesKeyStr, long timezoneOffset) {
        byte[] aesKeyArray = null;
        if (aesKeyStr != null && !"".equals(aesKeyStr)) {
            aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        }
        TransferData transferData = new TransferData();
        transferData.setAPICommand(45);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        transferData.setCommand((byte) 50);
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setTimezoneOffSet(timezoneOffset);
        CommandUtil.U_checkUserTime(transferData);
    }

    public void searchPasscode(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr, long timezoneOffset) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(47);
        transferData.setCommand((byte) 65);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        TransferData.setAesKeyArray(aesKeyArray);
        transferData.setTimezoneOffSet(timezoneOffset);
        CommandUtil.A_checkAdmin(transferData);
    }

    public void operateRemoteUnlockSwitch(ExtendedBluetoothDevice extendedBluetoothDevice, int operateType, int state, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) {
        byte[] aesKeyArray = DigitUtil.convertAesKeyStrToBytes(aesKeyStr);
        TransferData transferData = new TransferData();
        transferData.setAPICommand(48);
        transferData.setCommand((byte) 65);
        transferData.setmUid(uid);
        transferData.setLockVersion(lockVersion);
        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        transferData.setLockFlagPos(lockFlagPos);
        transferData.setOp(operateType);
        transferData.setOpValue(state);
        TransferData.setAesKeyArray(aesKeyArray);
        CommandUtil.A_checkAdmin(transferData);
    }
}
