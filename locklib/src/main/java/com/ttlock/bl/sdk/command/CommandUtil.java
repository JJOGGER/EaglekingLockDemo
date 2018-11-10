//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.command;

import android.util.Base64;

import com.ttlock.bl.sdk.constant.Constant;
import com.ttlock.bl.sdk.entity.LockVersion;
import com.ttlock.bl.sdk.entity.TransferData;
import com.ttlock.bl.sdk.service.BluetoothLeService;
import com.ttlock.bl.sdk.util.AESUtil;
import com.ttlock.bl.sdk.util.DigitUtil;
import com.ttlock.bl.sdk.util.LogUtil;

import java.util.TimeZone;

public class CommandUtil {
    private static final boolean DBG = true;
    public static final long permanentStartDate = 949338000000L;
    public static final long permanentEndDate = 4099741200000L;
    private static final byte[] defaultAesKeyArray = new byte[]{-104, 118, 35, -24, -87, 35, -95, -69, 61, -98, 125, 3, 120, 18, 69, -120};

    public CommandUtil() {
    }

    public static void E_getLockVersion(int apiCommand) {
        Command command = new Command(5);
        command.setCommand(69);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand(), apiCommand);
    }

    public static void A_addAdmin_V2S_Plus() {
    }

    public static void A_checkAdmin(TransferData transferData) {
        Command command = new Command(transferData.getLockVersion());
        command.setCommand(65);
        String adminPs = transferData.getAdminPs();
        String unlockKey = transferData.getUnlockKey();
        if(adminPs.length() > 10) {
            adminPs = new String(DigitUtil.decodeDefaultPassword(DigitUtil.stringDividerByDotToByteArray(adminPs)));
        }

        if(adminPs.length() < 10) {
            adminPs = String.format("%10s", new Object[]{adminPs}).replace(" ", "0");
        }

        if(unlockKey != null && unlockKey.length() > 10) {
            unlockKey = new String(DigitUtil.decodeDefaultPassword(DigitUtil.stringDividerByDotToByteArray(unlockKey)));
        }

        transferData.setAdminPs(adminPs);
        transferData.setUnlockKey(unlockKey);
        switch(command.getLockType()) {
            case 2:
            case 7:
            default:
                break;
            case 3:
                CommandUtil_V2S.checkAdmin(command, adminPs, transferData.getLockFlagPos());
                break;
            case 4:
                CommandUtil_V2S_PLUS.checkAdmin_V2S_PLUS(command, adminPs, unlockKey, transferData.getLockFlagPos(), TransferData.getAesKeyArray(), transferData.getAPICommand());
                break;
            case 5:
            case 8:
                CommandUtil_V3.checkAdmin(command, transferData.getmUid(), adminPs, unlockKey, transferData.getLockFlagPos(), TransferData.getAesKeyArray(), transferData.getAPICommand());
                break;
            case 6:
                CommandUtil_Va.checkAdmin(command, adminPs);
        }

        transferData.setTransferData(command.buildCommand());
        BluetoothLeService.getBluetoothLeService().sendCommand(transferData);
    }

    public static void U_checkUserTime(TransferData transferData) {
        Command command = new Command(transferData.getLockVersion());
        command.setCommand(85);
        String unlockKey = transferData.getUnlockKey();
        long startDate = transferData.getStartDate();
        long endDate = transferData.getEndDate();
        LogUtil.d("startDate:" + startDate, true);
        LogUtil.d("endDate:" + endDate, true);
        if(unlockKey.length() > 10) {
            unlockKey = new String(DigitUtil.decodeDefaultPassword(DigitUtil.stringDividerByDotToByteArray(unlockKey)));
        }

        transferData.setUnlockKey(unlockKey);
        if(startDate == 0L || endDate == 0L) {
            startDate = 949338000000L;
            endDate = 4099741200000L;
        }

        transferData.setStartDate(startDate);
        transferData.setEndDate(endDate);
        startDate = startDate + transferData.getTimezoneOffSet() - (long)TimeZone.getDefault().getOffset(System.currentTimeMillis());
        endDate = endDate + transferData.getTimezoneOffSet() - (long)TimeZone.getDefault().getOffset(System.currentTimeMillis());
        String sDateStr = DigitUtil.formateDateFromLong(startDate, "yyMMddHHmm");
        String eDateStr = DigitUtil.formateDateFromLong(endDate, "yyMMddHHmm");
        switch(command.getLockType()) {
            case 2:
            case 7:
            default:
                break;
            case 3:
                CommandUtil_V2S.checkUserTime(command, sDateStr, eDateStr, transferData.getLockFlagPos());
                break;
            case 4:
                CommandUtil_V2S_PLUS.checkUserTime_V2S_PLUS(command, sDateStr, eDateStr, unlockKey, transferData.getLockFlagPos(), TransferData.getAesKeyArray(), transferData.getAPICommand());
                break;
            case 5:
            case 8:
                CommandUtil_V3.checkUserTime(command, transferData.getmUid(), sDateStr, eDateStr, unlockKey, transferData.getLockFlagPos(), TransferData.getAesKeyArray(), transferData.getAPICommand());
                break;
            case 6:
                CommandUtil_Va.checkUserTime(command, sDateStr, eDateStr);
        }

        transferData.setTransferData(command.buildCommand());
        BluetoothLeService.getBluetoothLeService().sendCommand(transferData);
    }

    public static void getAESKey(LockVersion lockVersion, String manufacturer, int apiCommand) {
        BluetoothLeService.aesKeyArray = defaultAesKeyArray;
        LogUtil.e("BluetoothLeService.aesKeyArray:" + BluetoothLeService.aesKeyArray, true);
        LogUtil.d(lockVersion.toString(), true);
        Command command = new Command(lockVersion);
        command.setCommand(25);
        command.setData(manufacturer.getBytes(), defaultAesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand(), apiCommand, defaultAesKeyArray);
    }

    public static void V_addAdmin(int lockType, String adminPassword, String unlockNumber, byte[] aesKeyArray) {
        LogUtil.d("lockType=" + lockType + " adminPassword=" + adminPassword + " unlockNumber=" + unlockNumber, true);
        Command command = new Command(lockType);
        command.setCommand(86);
        switch(lockType) {
            case 2:
            case 7:
            default:
                break;
            case 3:
                CommandUtil_V2S.addAdmin(command, adminPassword, unlockNumber);
                break;
            case 4:
                CommandUtil_V2S_PLUS.addAdmin_V2S_PLUS(command, adminPassword, unlockNumber, aesKeyArray);
                break;
            case 5:
            case 8:
                CommandUtil_V3.addAdmin_V3(command, adminPassword, unlockNumber, aesKeyArray);
                break;
            case 6:
                CommandUtil_Va.addAdmin(command, adminPassword, unlockNumber);
        }

        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand(), adminPassword, unlockNumber, aesKeyArray, 2);
    }

    public static void A_checkAdmin(int uid, String lockVersionString, String adminPs, String unlockKey, int lockFlagPos, byte[] aesKeyArray, String string, int apiCommand) {
        A_checkAdmin(uid, lockVersionString, adminPs, unlockKey, lockFlagPos, aesKeyArray, 0, string, apiCommand);
    }

    public static void A_checkAdmin(int uid, String lockVersionString, String adminPs, String unlockKey, int lockFlagPos, byte[] aesKeyArray, int apiCommand) {
        A_checkAdmin(uid, lockVersionString, adminPs, unlockKey, lockFlagPos, aesKeyArray, 0, (String)null, apiCommand);
    }

    public static void A_checkAdmin(int uid, String lockVersionString, String adminPs, String unlockKey, int lockFlagPos, byte[] aesKeyArray, int validPwdNum, int pwdType, String originalPwd, String string, long startDate, long endDate, int apiCommand) {
        Command command = new Command(lockVersionString);
        command.setCommand(65);
        if(adminPs.length() > 10) {
            adminPs = new String(DigitUtil.decodeDefaultPassword(DigitUtil.stringDividerByDotToByteArray(adminPs)));
        }

        if(adminPs.length() < 10) {
            adminPs = String.format("%10s", new Object[]{adminPs}).replace(" ", "0");
        }

        if(unlockKey.length() > 10) {
            unlockKey = new String(DigitUtil.decodeDefaultPassword(DigitUtil.stringDividerByDotToByteArray(unlockKey)));
        }

        switch(command.getLockType()) {
            case 2:
            case 7:
            default:
                break;
            case 3:
                CommandUtil_V2S.checkAdmin(command, adminPs, lockFlagPos);
                break;
            case 4:
                CommandUtil_V2S_PLUS.checkAdmin_V2S_PLUS(command, adminPs, unlockKey, lockFlagPos, aesKeyArray, apiCommand);
                break;
            case 5:
            case 8:
                CommandUtil_V3.checkAdmin(command, uid, adminPs, unlockKey, lockFlagPos, aesKeyArray, apiCommand);
                break;
            case 6:
                CommandUtil_Va.checkAdmin(command, adminPs);
        }

        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand(), adminPs, unlockKey, lockFlagPos, aesKeyArray, (byte)validPwdNum, (byte)pwdType, originalPwd, string, startDate, endDate, apiCommand);
    }

    public static void A_checkAdmin(int uid, String lockVersionString, String adminPs, String unlockKey, int lockFlagPos, byte[] aesKeyArray, int validPwdNum, String string, int apiCommand) {
        Command command = new Command(lockVersionString);
        command.setCommand(65);
        if(adminPs.length() > 10) {
            adminPs = new String(DigitUtil.decodeDefaultPassword(DigitUtil.stringDividerByDotToByteArray(adminPs)));
        }

        if(adminPs.length() < 10) {
            adminPs = String.format("%10s", new Object[]{adminPs}).replace(" ", "0");
        }

        if(unlockKey != null && unlockKey.length() > 10) {
            unlockKey = new String(DigitUtil.decodeDefaultPassword(DigitUtil.stringDividerByDotToByteArray(unlockKey)));
        }

        switch(command.getLockType()) {
            case 2:
            case 7:
            default:
                break;
            case 3:
                CommandUtil_V2S.checkAdmin(command, adminPs, lockFlagPos);
                break;
            case 4:
                CommandUtil_V2S_PLUS.checkAdmin_V2S_PLUS(command, adminPs, unlockKey, lockFlagPos, aesKeyArray, apiCommand);
                break;
            case 5:
            case 8:
                CommandUtil_V3.checkAdmin(command, uid, adminPs, unlockKey, lockFlagPos, aesKeyArray, apiCommand);
                break;
            case 6:
                CommandUtil_Va.checkAdmin(command, adminPs);
        }

        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand(), adminPs, unlockKey, lockFlagPos, aesKeyArray, (byte)validPwdNum, string, apiCommand);
    }

    public static void S_setAdminKeyboardPwd(int lockType, String adminKeyboardPassword, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(83);
        switch(lockType) {
            case 2:
            default:
                break;
            case 3:
                CommandUtil_V2S.setAdminKeyboardPwd(command, adminKeyboardPassword);
                break;
            case 4:
                CommandUtil_V2S_PLUS.setAdminKeyboardPwd(command, adminKeyboardPassword, aesKeyArray);
                break;
            case 5:
                CommandUtil_V3.setAdminKeyboardPwd(command, adminKeyboardPassword, aesKeyArray);
        }

        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void D_setDeletePassword(int lockType, String deletePwd, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(68);
        switch(lockType) {
            case 2:
            default:
                break;
            case 3:
                CommandUtil_V2S.setDeletePwd(command, deletePwd);
                break;
            case 4:
                CommandUtil_V2S_PLUS.setDeletePwd(command, deletePwd, aesKeyArray);
                break;
            case 5:
                CommandUtil_V3.setDeletePwd(command, deletePwd, aesKeyArray);
        }

        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    /** @deprecated */
    @Deprecated
    public static void U_checkUserTime(int uid, String lockVersionString, long startDate, long endDate, String unlockKey, int lockFlagPos, byte[] aesKeyArray, long date, int apiCommand) {
        Command command = new Command(lockVersionString);
        command.setCommand(85);
        if(unlockKey.length() > 10) {
            unlockKey = new String(DigitUtil.decodeDefaultPassword(DigitUtil.stringDividerByDotToByteArray(unlockKey)));
        }

        if(startDate == 0L) {
            startDate = 949338000000L;
        }

        if(endDate == 0L) {
            endDate = 4099741200000L;
        }

        String sDateStr = DigitUtil.formateDateFromLong(startDate, "yyMMddHHmm");
        String eDateStr = DigitUtil.formateDateFromLong(endDate, "yyMMddHHmm");
        switch(command.getLockType()) {
            case 2:
            case 7:
            default:
                break;
            case 3:
                CommandUtil_V2S.checkUserTime(command, sDateStr, eDateStr, lockFlagPos);
                break;
            case 4:
                CommandUtil_V2S_PLUS.checkUserTime_V2S_PLUS(command, sDateStr, eDateStr, unlockKey, lockFlagPos, aesKeyArray, apiCommand);
                break;
            case 5:
            case 8:
                CommandUtil_V3.checkUserTime(command, uid, sDateStr, eDateStr, unlockKey, lockFlagPos, aesKeyArray, apiCommand);
                break;
            case 6:
                CommandUtil_Va.checkUserTime(command, sDateStr, eDateStr);
        }

        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand(), uid, unlockKey, aesKeyArray, date, apiCommand);
    }

    public static void G_unlock(int lockType, String unlockKey, byte[] psFromLock, byte[] aesKeyArray, long unlockDate, long timezoneRawOffSet) {
        Command command = new Command(lockType);
        command.setCommand(71);
        long psFromLockL = 0L;
        long unlockKeyL = 0L;
        String sum = null;
        switch(lockType) {
            case 2:
                psFromLockL = Long.valueOf(new String(psFromLock)).longValue();
                unlockKeyL = Long.valueOf(unlockKey).longValue();
                DigitUtil.getUnlockPassword(psFromLockL, unlockKeyL);
                break;
            case 3:
                psFromLockL = DigitUtil.fourBytesToLong(psFromLock);
                unlockKeyL = Long.valueOf(unlockKey).longValue();
                sum = DigitUtil.getUnlockPwd_new(psFromLockL, unlockKeyL);
                CommandUtil_V2S.unlock(command, sum);
                break;
            case 4:
                psFromLockL = DigitUtil.fourBytesToLong(psFromLock);
                unlockKeyL = Long.valueOf(unlockKey).longValue();
                sum = DigitUtil.getUnlockPwd_new(psFromLockL, unlockKeyL);
                CommandUtil_V2S_PLUS.unlock_V2S_PLUS(command, sum, aesKeyArray);
                break;
            case 5:
            case 8:
                psFromLockL = DigitUtil.fourBytesToLong(psFromLock);
                unlockKeyL = Long.valueOf(unlockKey).longValue();
                sum = DigitUtil.getUnlockPwd_new(psFromLockL, unlockKeyL);
                CommandUtil_V3.unlock(command, sum, unlockDate, aesKeyArray, timezoneRawOffSet);
                break;
            case 6:
                psFromLockL = Long.valueOf(new String(psFromLock)).longValue();
                unlockKeyL = Long.valueOf(unlockKey).longValue();
                sum = DigitUtil.getUnlockPassword(psFromLockL, unlockKeyL);
                command.setCommand(76);
                CommandUtil_Va.up_down(command, sum);
            case 7:
        }

        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void L_lock(int lockType, String unlockKey, byte[] psFromLock, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(71);
        long psFromLockL = 0L;
        long unlockKeyL = 0L;
        String sum = null;
        switch(lockType) {
            case 6:
                psFromLockL = Long.valueOf(new String(psFromLock)).longValue();
                unlockKeyL = Long.valueOf(unlockKey).longValue();
                sum = DigitUtil.getUnlockPassword(psFromLockL, unlockKeyL);
                CommandUtil_Va.up_down(command, sum);
            case 7:
            default:
                BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
        }
    }

    public static void checkRandom(int lockType, String unlockKey, byte[] psFromLock, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(48);
        long psFromLockL = DigitUtil.fourBytesToLong(psFromLock);
        long unlockKeyL = Long.valueOf(unlockKey).longValue();
        String sum = DigitUtil.getUnlockPwd_new(psFromLockL, unlockKeyL);
        CommandUtil_V3.checkRandom(command, sum, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void C_calibationTime(TransferData transferData) {
        transferData.setStartDate(949338000000L);
        transferData.setEndDate(4099741200000L);
        U_checkUserTime(transferData);
    }

    public static void C_calibationTime(int lockType, long date, long timezoneRawOffSet, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(67);
        date = date + timezoneRawOffSet - (long)TimeZone.getDefault().getOffset(System.currentTimeMillis());
        LogUtil.d("timezoneRawOffSet:" + timezoneRawOffSet, true);
        LogUtil.d("TimeZone.getDefault().getOffset(System.currentTimeMillis()):" + TimeZone.getDefault().getOffset(System.currentTimeMillis()), true);
        LogUtil.d("date:" + date, true);
        String timeStr = DigitUtil.formateDateFromLong(date, "yyMMddHHmmss");
        LogUtil.d("timeStr:" + timeStr, true);
        switch(lockType) {
            case 2:
                CommandUtil_V2.calibationTime_V2(command, timeStr.substring(0, timeStr.length() - 2));
                break;
            case 3:
                CommandUtil_V2S.calibationTime_V2S(command, timeStr.substring(0, timeStr.length() - 2));
                break;
            case 4:
                CommandUtil_V2S_PLUS.calibationTime_V2S_PLUS(command, timeStr.substring(0, timeStr.length() - 2), aesKeyArray);
                break;
            case 5:
            case 8:
                CommandUtil_V3.calibationTime_V3(command, timeStr, aesKeyArray);
                break;
            case 6:
                CommandUtil_Va.calibationTime(command, timeStr.substring(0, timeStr.length() - 2));
            case 7:
        }

        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void N_setLockname(int lockType, String lockname, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(78);
        CommandUtil_V3.setLockname(command, lockname, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void AT_setLockname(int lockType, String lockname, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(-1);
        command.setData(("AT+NAME=" + lockname).getBytes(), aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void R_resetLock(int lockType) {
        Command command = new Command(lockType);
        command.setCommand(82);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void resetKeyboardPasswordCount(int lockType, byte validPwdNum, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(51);
        byte[] values = new byte[]{validPwdNum};
        switch(lockType) {
            case 5:
                CommandUtil_V3.resetKeyboardPasswordCount(command, values, aesKeyArray);
            default:
                BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
        }
    }

    /** @deprecated */
    @Deprecated
    public static void getLockTime(String lockVersion) {
        Command command = new Command(lockVersion);
        command.setCommand(52);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void getLockTime(TransferData transferData) {
        Command command = new Command(transferData.getLockVersion());
        command.setCommand(52);
        transferData.setTransferData(command.buildCommand());
        BluetoothLeService.getBluetoothLeService().sendCommand(transferData);
    }

    /** @deprecated */
    @Deprecated
    public static void getOperateLog(String lockVersion, byte[] aesKeyArray) {
        Command command = new Command(lockVersion);
        switch(command.getLockType()) {
            case 5:
            case 8:
                command.setCommand(37);
                CommandUtil_V3.getOperateLog(command, (short) -1, aesKeyArray);
                break;
            case 6:
                command.setCommand(87);
            case 7:
        }

        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand(), 26);
    }

    public static void getOperateLog(TransferData transferData) {
        Command command = new Command(transferData.getLockVersion());
        switch(command.getLockType()) {
            case 5:
            case 8:
                command.setCommand(37);
                CommandUtil_V3.getOperateLog(command, transferData.getSeq(), TransferData.getAesKeyArray());
                break;
            case 6:
                command.setCommand(87);
            case 7:
        }

        transferData.setAPICommand(26);
        transferData.setTransferData(command.buildCommand());
        BluetoothLeService.getBluetoothLeService().sendCommand(transferData);
    }

    public static void getOperateLog(String lockVersion, short seq, byte[] aesKeyArray) {
        Command command = new Command(lockVersion);
        command.setCommand(37);
        CommandUtil_V3.getOperateLog(command, seq, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand(), 26);
    }

    public static void getOperateLog(int lockType, short seq, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(37);
        CommandUtil_V3.getOperateLog(command, seq, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void getValidKeyboardPassword(String lockVersion, short seq, byte[] aesKeyArray) {
        Command command = new Command(lockVersion);
        command.setCommand(4);
        CommandUtil_V3.getValidKeyboardPassword(command, seq, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void getValidKeyboardPassword(int lockType, short seq, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(4);
        CommandUtil_V3.getValidKeyboardPassword(command, seq, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void manageKeyboardPassword(int lockType, byte pwdOperateType, byte keyboardPwdType, String originalPwd, String newPwd, long startDate, long endDate, byte[] defaultAesKeyArray) {
        manageKeyboardPassword(lockType, pwdOperateType, keyboardPwdType, 0, originalPwd, newPwd, startDate, endDate, defaultAesKeyArray, -1L);
    }

    public static void manageKeyboardPassword(int lockType, byte pwdOperateType, byte keyboardPwdType, int circleType, String originalPwd, String newPwd, long startDate, long endDate, byte[] defaultAesKeyArray) {
        manageKeyboardPassword(lockType, pwdOperateType, keyboardPwdType, circleType, originalPwd, newPwd, startDate, endDate, defaultAesKeyArray, -1L);
    }

    public static void manageKeyboardPassword(int lockType, byte pwdOperateType, byte keyboardPwdType, String originalPwd, String newPwd, long startDate, long endDate, byte[] defaultAesKeyArray, long timezoneOffset) {
        manageKeyboardPassword(lockType, pwdOperateType, keyboardPwdType, 0, originalPwd, newPwd, startDate, endDate, defaultAesKeyArray, timezoneOffset);
    }

    public static void manageKeyboardPassword(int lockType, byte pwdOperateType, byte keyboardPwdType, int circleType, String originalPwd, String newPwd, long startDate, long endDate, byte[] defaultAesKeyArray, long timezoneOffset) {
        Command command = new Command(lockType);
        command.setCommand(3);
        CommandUtil_V3.manageKeyboardPassword(command, pwdOperateType, keyboardPwdType, circleType, originalPwd, newPwd, startDate, endDate, defaultAesKeyArray, timezoneOffset);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static String encry(String pwdInfoSource, long timestamp) {
        String timestampStr = String.format("%-16d", new Object[]{Long.valueOf(timestamp)}).replace(" ", "0").substring(0, 16);
        LogUtil.d(pwdInfoSource + " " + timestampStr, true);
        byte[] encryRes = AESUtil.aesEncrypt(pwdInfoSource.getBytes(), timestampStr.getBytes());
        return Base64.encodeToString(encryRes, 2);
    }

    public static void operateFinished(int lockType) {
        Command command = new Command(lockType);
        command.setCommand(87);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void searchDeviceFeature(int lockType) {
        Command command = new Command(lockType);
        command.setCommand(1);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void searchICCardNo(int lockType, short seq, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(5);
        CommandUtil_V3.ICManage(command, 1, seq, 0L, 0L, 0L, aesKeyArray, 0L);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void addICCard(int lockType, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(5);
        CommandUtil_V3.ICManage(command, 2, 0, 0L, 0L, 0L, aesKeyArray, 0L);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void recoveryICCardPeriod(int lockType, long cardNo, long startDate, long endDate, byte[] aesKeyArray, long timezoneOffSet) {
        Command command = new Command(lockType);
        command.setCommand(5);
        CommandUtil_V3.ICManage(command, 2, 0, cardNo, startDate, endDate, aesKeyArray, timezoneOffSet);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void modifyICCardPeriod(int lockType, long cardNo, long startDate, long endDate, byte[] aesKeyArray, long timezoneOffSet) {
        Command command = new Command(lockType);
        command.setCommand(5);
        CommandUtil_V3.ICManage(command, 5, 0, cardNo, startDate, endDate, aesKeyArray, timezoneOffSet);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void deleteICCard(int lockType, long cardNo, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(5);
        CommandUtil_V3.ICManage(command, 3, 0, cardNo, 0L, 0L, aesKeyArray, 0L);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void clearICCard(int lockType, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(5);
        CommandUtil_V3.ICManage(command, 4, 0, 0L, 0L, 0L, aesKeyArray, 0L);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void setWristbandKey(int lockType, String wristKey, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(53);
        CommandUtil_V3.setWristKey(command, wristKey, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void searchPasscode(int lockType, short seq, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(7);
        CommandUtil_V3.searchPwd(command, seq, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void searchFRNo(int lockType, short seq, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(6);
        CommandUtil_V3.FRManage(command, 6, seq, 0L, 0L, 0L, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void addFR(int lockType, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(6);
        CommandUtil_V3.FRManage(command, 2, (short) 0, 0L, 0L, 0L, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void modifyFRPeriod(int lockType, long FRNo, long startDate, long endDate, byte[] aesKeyArray, long timezoneOffSet) {
        Command command = new Command(lockType);
        command.setCommand(6);
        CommandUtil_V3.FRManage(command, 5, 0, FRNo, startDate, endDate, aesKeyArray, timezoneOffSet);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void recoveryFRPeriod(int lockType, long FRNo, long startDate, long endDate, byte[] aesKeyArray, long timezoneOffSet) {
        Command command = new Command(lockType);
        command.setCommand(6);
        CommandUtil_V3.FRManage(command, 2, 0, FRNo, startDate, endDate, aesKeyArray, timezoneOffSet);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void deleteFR(int lockType, long FRNo, byte[] aesKeyArray) {
        LogUtil.d("FRNo:" + FRNo, true);
        Command command = new Command(lockType);
        command.setCommand(6);
        CommandUtil_V3.FRManage(command, 3, (short) 0, FRNo, 0L, 0L, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void clearFR(int lockType, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(6);
        CommandUtil_V3.ICManage(command, 4, 0, 0L, 0L, 0L, aesKeyArray, 0L);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void searchAutoLockTime(int lockType, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(54);
        CommandUtil_V3.autoLockManage(command, (byte) 1, (short) 0, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void modifyAutoLockTime(int lockType, short time, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(54);
        CommandUtil_V3.autoLockManage(command, (byte) 2, time, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void readDeviceInfo(TransferData transferData) {
        Command command = new Command(transferData.getLockVersion());
        command.setCommand(-112);
        command.setData(new byte[]{1}, TransferData.getAesKeyArray());
        transferData.setTransferData(command.buildCommand());
        BluetoothLeService.getBluetoothLeService().sendCommand(transferData);
    }

    public static void readDeviceInfo(int lockType, byte opType, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(-112);
        command.setData(new byte[]{opType}, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void enterDFUMode(int lockType, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(2);
        command.setData(Constant.SCIENER.getBytes(), aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void searchBicycleStatus(TransferData transferData) {
        Command command = new Command(transferData.getLockVersion());
        command.setCommand(20);
        command.setData(Constant.SCIENER.getBytes(), TransferData.getAesKeyArray());
        transferData.setTransferData(command.buildCommand());
        BluetoothLeService.getBluetoothLeService().sendCommand(transferData);
    }

    public static void lock(int lockType, String unlockKey, byte[] psFromLock, byte[] aesKeyArray, long unlockDate) {
        Command command = new Command(lockType);
        command.setCommand(88);
        long psFromLockL = 0L;
        long unlockKeyL = 0L;
        String sum = null;
        psFromLockL = DigitUtil.fourBytesToLong(psFromLock);
        unlockKeyL = Long.valueOf(unlockKey).longValue();
        sum = DigitUtil.getUnlockPwd_new(psFromLockL, unlockKeyL);
        CommandUtil_V3.lock(command, sum, unlockDate, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void screenPasscodeManage(int lockType, int opType, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(89);
        CommandUtil_V3.screenPasscodeManage(command, opType, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void readPwdPara(int lockType, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(50);
        command.setData("".getBytes(), aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }
}
