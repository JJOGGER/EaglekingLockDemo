//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.command;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.ttlock.bl.sdk.constant.Constant;
import com.ttlock.bl.sdk.constant.LogOperate;
import com.ttlock.bl.sdk.entity.PwdInfoV3;
import com.ttlock.bl.sdk.service.BluetoothLeService;
import com.ttlock.bl.sdk.util.DigitUtil;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.ttlock.bl.sdk.util.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TimeZone;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class CommandUtil_V3 {
    private static boolean DBG = true;

    public CommandUtil_V3() {
    }

    public static void addAdmin_V3(Command command, String adminPassword, String unlockNumber, byte[] aesKeyArray) {
        byte[] values = new byte[15];
        int adminPwd = Integer.valueOf(adminPassword).intValue();
        int unlockPwd = Integer.valueOf(unlockNumber).intValue();
        byte[] adminPwd_byte = DigitUtil.integerToByteArray(adminPwd);
        byte[] unlockPwd_byte = DigitUtil.integerToByteArray(unlockPwd);
        System.arraycopy(adminPwd_byte, 0, values, 0, adminPwd_byte.length);
        System.arraycopy(unlockPwd_byte, 0, values, 4, unlockPwd_byte.length);
        System.arraycopy(Constant.SCIENER.getBytes(), 0, values, 8, 7);
        command.setData(values, aesKeyArray);
    }

    public static void checkAdmin(Command command, int uid, String adminPs, String unlockKey, int lockFlagPos, byte[] aesKeyArray, int apiCommand) {
        byte[] adminPsByteArray = DigitUtil.integerToByteArray(Integer.valueOf(adminPs).intValue());
        byte[] lockFlagPos_byteArray = DigitUtil.integerToByteArray(lockFlagPos);
        byte[] values = new byte[11];
        LogUtil.e("lockFlagPos:" + lockFlagPos, DBG);
        LogUtil.e("lockFlagPos_byteArray:" + Arrays.toString(lockFlagPos_byteArray), DBG);
        System.arraycopy(adminPsByteArray, 0, values, 0, adminPsByteArray.length);
        System.arraycopy(lockFlagPos_byteArray, 1, values, 4, 3);
        System.arraycopy(DigitUtil.integerToByteArray(uid), 0, values, 7, 4);
        LogUtil.e("values:" + Arrays.toString(values), DBG);
        command.setData(values, aesKeyArray);
    }

    public static void setAdminKeyboardPwd(Command command, String adminKeyboardPwd, byte[] aesKeyArray) {
        int len = adminKeyboardPwd.length();
        byte[] values = new byte[len];

        for (int i = 0; i < len; ++i) {
            values[i] = (byte) (adminKeyboardPwd.charAt(i) - 48);
        }

        command.setData(values, aesKeyArray);
    }

    public static void setDeletePwd(Command command, String deletePwd, byte[] aesKeyArray) {
        byte[] values = new byte[10];
        int len = deletePwd.length();

        int i;
        for (i = 0; i < len; ++i) {
            values[i] = (byte) (deletePwd.charAt(i) - 48);
        }

        for (i = len; i < 10; ++i) {
            values[i] = -1;
        }

        command.setData(values, aesKeyArray);
    }

    public static void checkUserTime(Command command, int uid, String sDateStr, String eDateStr, String unlockKey, int lockFlagPos, byte[] aesKeyArray, int apiCommand) {
        byte[] values = new byte[17];
        byte[] time = DigitUtil.convertTimeToByteArray(sDateStr + eDateStr);
        System.arraycopy(time, 0, values, 0, 10);
        values[10] = (byte) (lockFlagPos >> 16 & 255);
        values[11] = (byte) (lockFlagPos >> 8 & 255);
        values[12] = (byte) (lockFlagPos & 255);
        byte[] uidArray = DigitUtil.integerToByteArray(uid);
        System.arraycopy(uidArray, 0, values, 13, 4);
        command.setData(values, aesKeyArray);
    }

    public static void calibationTime_V3(Command command, String timeStr, byte[] aesKeyArray) {
        byte[] timeArray = DigitUtil.convertTimeToByteArray(timeStr);
        command.setData(timeArray, aesKeyArray);
    }

    public static void unlock(Command command, String sum, long dateTime, byte[] aesKeyArray, long timezoneRawOffSet) {
        byte[] values = new byte[14];
        if (dateTime > 0L) {
            values = new byte[14];
        } else {
            values = new byte[8];
        }

        int sumI = Integer.valueOf(sum).intValue();
        byte[] sumByteArray = DigitUtil.integerToByteArray(sumI);
        System.arraycopy(sumByteArray, 0, values, 0, sumByteArray.length);
        int date = (int) (dateTime / 1000L);
        if (dateTime <= 0L) {
            date = (int) (System.currentTimeMillis() / 1000L);
        }

        System.arraycopy(DigitUtil.integerToByteArray(date), 0, values, 4, 4);
        if (dateTime > 0L) {
            dateTime = dateTime + timezoneRawOffSet - (long) TimeZone.getDefault().getOffset(System.currentTimeMillis());
            String dateStr = DigitUtil.formateDateFromLong(dateTime, "yyMMddHHmmss");
            byte[] dateByteArray = DigitUtil.convertTimeToByteArray(dateStr);
            System.arraycopy(dateByteArray, 0, values, 8, dateByteArray.length);
        }

        command.setData(values, aesKeyArray);
    }

    public static void checkRandom(Command command, String sum, byte[] aesKeyArray) {
        int sumI = Integer.valueOf(sum).intValue();
        byte[] sumByteArray = DigitUtil.integerToByteArray(sumI);
        command.setData(sumByteArray, aesKeyArray);
    }

    public static void initPasswords(int lockType, byte[] aesKeyArray, int apiCommand) {
        Command command = new Command(lockType);
        command.setCommand(49);
        byte[] values = new byte[61];
        int year = (new Date()).getYear() + 1900;
        int month = (new Date()).getMonth();
        int day = (new Date()).getDate();
        if (month == 0 && day == 1) {
            --year;
        }

        LogUtil.d("year : " + year, DBG);
        LinkedHashSet codeSet = new LinkedHashSet();

        while (codeSet.size() < 10) {
            codeSet.add(Integer.valueOf(DigitUtil.getRandomIntegerByUpperBound(1071)));
        }

        LinkedHashSet secretKeySet = new LinkedHashSet();

        while (secretKeySet.size() < 10) {
            secretKeySet.add(DigitUtil.getCheckTable());
        }

        Iterator codeIter = codeSet.iterator();
        Iterator secretKeyIter = secretKeySet.iterator();
        byte offset = 0;
        int var18 = offset + 1;
        values[offset] = (byte) (year % 100);
        ArrayList list = new ArrayList();

        for (int pwdInfoSource = 0; pwdInfoSource < 10; ++year) {
            int timestamp = ((Integer) codeIter.next()).intValue();
            String secretKey = (String) secretKeyIter.next();
            list.add(PwdInfoV3.getInstance(year, timestamp, secretKey));
            byte[] pwdInfo = DigitUtil.convertCodeAndKeyToByteArray(timestamp, secretKey);
            System.arraycopy(pwdInfo, 0, values, var18, 6);
            var18 += 6;
            ++pwdInfoSource;
        }

        initPasswords(command, values, aesKeyArray);
        String var19 = GsonUtil.toJson(list);
        long var20 = System.currentTimeMillis();
        String var21 = CommandUtil.encry(var19, var20);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand(), var21, var20, apiCommand);
    }

    public static void initPasswords(Command command, byte[] values, byte[] aesKeyArray) {
        command.setData(values, aesKeyArray);
    }

    public static void resetKeyboardPasswordCount(Command command, byte[] values, byte[] aesKeyArray) {
        command.setData(values, aesKeyArray);
    }

    public static void setLockname(Command command, String lockname, byte[] aesKeyArray) {
        command.setData(lockname.getBytes(), aesKeyArray);
    }

    public static void getOperateLog(Command command, short seq, byte[] aesKeyArray) {
        byte[] values = new byte[]{(byte) (seq >> 8), (byte) seq};
        command.setData(values, aesKeyArray);
    }

    public static void getValidKeyboardPassword(Command command, short seq, byte[] aesKeyArray) {
        byte[] values = new byte[]{(byte) (seq >> 8), (byte) seq};
        command.setData(values, aesKeyArray);
    }

    public static short parseKeyboardPwd(byte[] datas) {
        short recordTotalLen = (short) (datas[0] << 8 | datas[1] & 255);
        if (recordTotalLen == 0) {
            return recordTotalLen;
        } else {
            short nextReq = (short) (datas[2] << 8 | datas[3] & 255);
            int dataIndex = 4;

            while (dataIndex + 1 < datas.length) {
                byte recLen = datas[dataIndex++];
                byte pwdType = datas[dataIndex++];
                byte pwdLen = datas[dataIndex++];
                new String(Arrays.copyOfRange(datas, dataIndex, dataIndex + pwdLen));
                dataIndex += pwdLen;
                byte originPwdLen = datas[dataIndex++];
                new String(Arrays.copyOfRange(datas, dataIndex, dataIndex + originPwdLen));
                dataIndex += originPwdLen;
                switch (pwdType) {
                    case 2:
                        int var15 = (int) DigitUtil.fourBytesToLong(Arrays.copyOfRange(datas, dataIndex, dataIndex + 4));
                        dataIndex += 4;
                        break;
                    case 3:
                        byte year = datas[dataIndex++];
                        byte mounth = datas[dataIndex++];
                        byte day = datas[dataIndex++];
                        byte hour = datas[dataIndex++];
                        byte minute = datas[dataIndex++];
                }
            }

            return nextReq;
        }
    }

    public static void searchPwd(Command command, short seq, byte[] aesKey) {
        byte[] values = new byte[]{(byte) (seq >> 8), (byte) seq};
        command.setData(values, aesKey);
    }

    public static void manageKeyboardPassword(Command command, byte pwdOperateType, byte keyboardPwdType, int cycleType, String originalPwd, String newPwd, long startDate, long endDate, byte[] aesKeyArray, long timezoneOffset) {
        byte[] values = null;
        byte index = 0;
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getDefault();
        if (timeZone.inDaylightTime(new Date(System.currentTimeMillis()))) {
            timezoneOffset -= (long) timeZone.getDSTSavings();
        }

        timeZone.setRawOffset((int) timezoneOffset);
        calendar.setTimeZone(timeZone);
        Date date = new Date(startDate);
        calendar.setTime(date);
        LogUtil.e("timezoneOffset:" + timezoneOffset, DBG);
        LogUtil.e("startDate:" + startDate, DBG);
        LogUtil.e("endDate:" + endDate, DBG);
        LogUtil.d("pwdOperateType:" + pwdOperateType, DBG);
        byte originalPwdLen;
        byte newPwdLen;
        int var20;
        label55:
        switch (pwdOperateType) {
            case 1:
                values = new byte[1];
                var20 = index + 1;
                values[index] = pwdOperateType;
                break;
            case 2:
                originalPwdLen = (byte) originalPwd.length();
                switch (keyboardPwdType) {
                    case 1:
                        values = new byte[3 + originalPwdLen + 5];
                        var20 = index + 1;
                        values[index] = pwdOperateType;
                        values[var20++] = keyboardPwdType;
                        values[var20++] = originalPwdLen;
                        System.arraycopy(originalPwd.getBytes(), 0, values, var20, originalPwdLen);
                        var20 += originalPwdLen;
                        values[var20++] = (byte) (calendar.get(Calendar.YEAR) % 100);
                        values[var20++] = (byte) (calendar.get(Calendar.MONTH) + 1);
                        values[var20++] = (byte) calendar.get(Calendar.DAY_OF_MONTH);
                        values[var20++] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
                        values[var20++] = (byte) calendar.get(Calendar.MINUTE);
                        break label55;
                    case 2:
                    case 3:
                        values = new byte[3 + originalPwdLen + 5 + 5];
                        var20 = index + 1;
                        values[index] = pwdOperateType;
                        values[var20++] = keyboardPwdType;
                        values[var20++] = originalPwdLen;
                        System.arraycopy(originalPwd.getBytes(), 0, values, var20, originalPwdLen);
                        var20 += originalPwdLen;
                        values[var20++] = (byte) (calendar.get(Calendar.YEAR) % 100);
                        values[var20++] = (byte) (calendar.get(Calendar.MONTH) + 1);
                        values[var20++] = (byte) calendar.get(Calendar.DAY_OF_MONTH);
                        values[var20++] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
                        values[var20++] = (byte) calendar.get(Calendar.MINUTE);
                        LogUtil.d("小时:" + calendar.get(Calendar.HOUR_OF_DAY), DBG);
                        date = new Date(endDate);
                        calendar.setTime(date);
                        values[var20++] = (byte) (calendar.get(Calendar.YEAR) % 100);
                        values[var20++] = (byte) (calendar.get(Calendar.MONTH) + 1);
                        values[var20++] = (byte) calendar.get(Calendar.DAY_OF_MONTH);
                        values[var20++] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
                        values[var20++] = (byte) calendar.get(Calendar.MINUTE);
                    default:
                        break label55;
                }
            case 3:
                originalPwdLen = (byte) newPwd.length();
                values = new byte[3 + originalPwdLen];
                var20 = index + 1;
                values[index] = pwdOperateType;
                values[var20++] = keyboardPwdType;
                values[var20++] = originalPwdLen;
                System.arraycopy(newPwd.getBytes(), 0, values, var20, originalPwdLen);
            case 4:
            default:
                break;
            case 5:
                originalPwdLen = (byte) originalPwd.length();
                newPwdLen = 0;
                if (!TextUtils.isEmpty(newPwd)) {
                    newPwdLen = (byte) newPwd.length();
                }

                if (startDate > 0L && endDate > 0L) {
                    values = new byte[3 + originalPwdLen + 1 + newPwdLen + 5 + 5];
                } else {
                    values = new byte[3 + originalPwdLen + 1 + newPwdLen];
                }

                var20 = index + 1;
                values[index] = pwdOperateType;
                values[var20++] = keyboardPwdType;
                values[var20++] = originalPwdLen;
                System.arraycopy(originalPwd.getBytes(), 0, values, var20, originalPwdLen);
                var20 += originalPwdLen;
                values[var20++] = newPwdLen;
                System.arraycopy(newPwd.getBytes(), 0, values, var20, newPwdLen);
                var20 += newPwdLen;
                if (startDate > 0L && endDate > 0L) {
                    values[var20++] = (byte) (calendar.get(Calendar.YEAR) % 100);
                    values[var20++] = (byte) (calendar.get(Calendar.MONTH) + 1);
                    values[var20++] = (byte) calendar.get(Calendar.DAY_OF_MONTH);
                    values[var20++] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
                    values[var20++] = (byte) calendar.get(Calendar.MINUTE);
                    date = new Date(endDate);
                    calendar.setTime(date);
                    values[var20++] = (byte) (calendar.get(Calendar.YEAR) % 100);
                    values[var20++] = (byte) (calendar.get(Calendar.MONTH) + 1);
                    values[var20++] = (byte) calendar.get(Calendar.DAY_OF_MONTH);
                    values[var20++] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
                    values[var20++] = (byte) calendar.get(Calendar.MINUTE);
                }
                break;
            case 6:
                originalPwdLen = (byte) originalPwd.length();
                newPwdLen = 0;
                if (!TextUtils.isEmpty(newPwd)) {
                    newPwdLen = (byte) newPwd.length();
                }

                LogUtil.e("originalPwd:" + originalPwd, DBG);
                LogUtil.e("newPwd:" + newPwd, DBG);
                LogUtil.e("keyboardPwdType:" + keyboardPwdType, DBG);
                switch (keyboardPwdType) {
                    case 1:
                    case 2:
                        values = new byte[3 + originalPwdLen + 1 + newPwdLen + 5];
                        break;
                    case 3:
                        values = new byte[3 + originalPwdLen + 1 + newPwdLen + 5 + 5];
                        break;
                    case 4:
                        values = new byte[3 + originalPwdLen + 1 + newPwdLen + 5 + 2];
                }

                var20 = index + 1;
                values[index] = pwdOperateType;
                values[var20++] = keyboardPwdType;
                values[var20++] = originalPwdLen;
                System.arraycopy(originalPwd.getBytes(), 0, values, var20, originalPwdLen);
                var20 += originalPwdLen;
                values[var20++] = newPwdLen;
                System.arraycopy(newPwd.getBytes(), 0, values, var20, newPwdLen);
                var20 += newPwdLen;
                values[var20++] = (byte) (calendar.get(Calendar.YEAR) % 100);
                values[var20++] = (byte) (calendar.get(Calendar.MONTH) + 1);
                values[var20++] = (byte) calendar.get(Calendar.DAY_OF_MONTH);
                values[var20++] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
                values[var20++] = (byte) calendar.get(Calendar.MINUTE);
                switch (keyboardPwdType) {
                    case 3:
                        date = new Date(endDate);
                        calendar.setTime(date);
                        values[var20++] = (byte) (calendar.get(Calendar.YEAR) % 100);
                        values[var20++] = (byte) (calendar.get(Calendar.MONTH) + 1);
                        values[var20++] = (byte) calendar.get(Calendar.DAY_OF_MONTH);
                        values[var20++] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
                        values[var20++] = (byte) calendar.get(Calendar.MINUTE);
                        LogUtil.e("calendar.get(Calendar.DAY_OF_MONTH):" + calendar.get(Calendar.DAY_OF_MONTH), DBG);
                        break;
                    case 4:
                        values[var20++] = (byte) (cycleType >> 8);
                        values[var20++] = (byte) cycleType;
                }
        }

        command.setData(values, aesKeyArray);
    }

    public static short parseOperateLog(List<LogOperate> logOperates, byte[] datas, long timezoneOffSet) {
        LogUtil.e("begin---------------", DBG);
        LogUtil.e("datas:" + DigitUtil.byteArrayToHexString(datas), DBG);
        int recordTotalLen = (datas[0] << 8 | datas[1] & 255) & '\uffff';
        if (recordTotalLen == 0) {
            return 0;
        } else {
            short nextReq = (short) (datas[2] << 8 | datas[3] & 255);
            int dataIndex = 4;
            LogUtil.d("recordTotalLen:" + recordTotalLen, DBG);
            LogUtil.d("nextReq:" + nextReq, DBG);
            LogUtil.d("datas.length:" + datas.length, DBG);

            while (dataIndex + 1 < datas.length) {
                LogUtil.e("begin", DBG);
                LogUtil.d("dataIndex:" + dataIndex, DBG);
                LogOperate logOperate = new LogOperate();
                byte recLen = datas[dataIndex++];
                int nextRecIndex = dataIndex + recLen;
                LogUtil.d("recLen:" + recLen, DBG);
                byte operateType = datas[dataIndex++];
                LogUtil.d("operateType:" + operateType, DBG);
                logOperate.setRecordType(operateType);
                int year = datas[dataIndex++] + 2000;
                byte month = datas[dataIndex++];
                byte day = datas[dataIndex++];
                byte hour = datas[dataIndex++];
                byte minute = datas[dataIndex++];
                byte second = datas[dataIndex++];
                Calendar calendar = Calendar.getInstance();
                TimeZone timeZone = TimeZone.getDefault();
                LogUtil.d("timezoneOffSet:" + timezoneOffSet, DBG);
                if (timeZone.inDaylightTime(new Date(System.currentTimeMillis()))) {
                    timezoneOffSet -= (long) timeZone.getDSTSavings();
                }

                timeZone.setRawOffset((int) timezoneOffSet);
                calendar.setTimeZone(timeZone);
                calendar.set(year, month - 1, day, hour, minute, second);
                logOperate.setOperateDate(calendar.getTimeInMillis() / 1000L * 1000L);
                LogUtil.d("year:" + year, DBG);
                LogUtil.d("month:" + month, DBG);
                LogUtil.d("day:" + day, DBG);
                LogUtil.d("hour:" + hour, DBG);
                LogUtil.d("minute:" + minute, DBG);
                LogUtil.d("second:" + second, DBG);
                byte electricQuantity = datas[dataIndex++];
                logOperate.setElectricQuantity(electricQuantity);
                LogUtil.d("electricQuantity:" + electricQuantity, DBG);
                int var10000;
                String address;
                byte var26;
                byte var28;
                String var30;
                switch (operateType) {
                    case 1:
                    case 26:
                        int var27 = (int) DigitUtil.fourBytesToLong(Arrays.copyOfRange(datas, dataIndex, dataIndex + 4));
                        LogUtil.d("uid:" + var27, DBG);
                        dataIndex += 4;
                        int var29 = (int) DigitUtil.fourBytesToLong(Arrays.copyOfRange(datas, dataIndex, dataIndex + 4));
                        LogUtil.d("uuid:" + var29, DBG);
                        dataIndex += 4;
                        logOperate.setUid(var27);
                        logOperate.setRecordId(var29);
                    case 2:
                    case 3:
                    case 14:
                    case 16:
                    case 24:
                    default:
                        break;
                    case 4:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                        LogUtil.d("dataIndex:" + dataIndex, DBG);
                        var26 = datas[dataIndex++];
                        LogUtil.d("originalPwdLen:" + var26, DBG);
                        address = new String(Arrays.copyOfRange(datas, dataIndex, dataIndex + var26));
                        LogUtil.d("originalPwd:" + address, DBG);
                        dataIndex += var26;
                        var28 = datas[dataIndex++];
                        LogUtil.d("unlockPwdLen:" + var28, DBG);
                        var30 = new String(Arrays.copyOfRange(datas, dataIndex, dataIndex + var28));
                        LogUtil.d("unlockPwd:" + var30, DBG);
                        var10000 = dataIndex + var28;
                        logOperate.setPassword(address);
                        logOperate.setNewPassword(var30);
                        break;
                    case 5:
                        var26 = datas[dataIndex++];
                        address = new String(Arrays.copyOfRange(datas, dataIndex, dataIndex + var26));
                        dataIndex += var26;
                        var28 = datas[dataIndex++];
                        var30 = new String(Arrays.copyOfRange(datas, dataIndex, dataIndex + var28));
                        var10000 = dataIndex + var28;
                        logOperate.setPassword(address);
                        logOperate.setNewPassword(var30);
                        break;
                    case 6:
                        var26 = datas[dataIndex++];
                        address = new String(Arrays.copyOfRange(datas, dataIndex, dataIndex + var26));
                        dataIndex += var26;
                        var28 = datas[dataIndex++];
                        var30 = new String(Arrays.copyOfRange(datas, dataIndex, dataIndex + var28));
                        var10000 = dataIndex + var28;
                        logOperate.setPassword(address);
                        logOperate.setNewPassword(var30);
                        break;
                    case 7:
                        var26 = datas[dataIndex++];
                        address = new String(Arrays.copyOfRange(datas, dataIndex, dataIndex + var26));
                        var10000 = dataIndex + var26;
                        logOperate.setPassword(address);
                        break;
                    case 8:
                        byte var25 = datas[dataIndex++];
                        month = datas[dataIndex++];
                        day = datas[dataIndex++];
                        hour = datas[dataIndex++];
                        minute = datas[dataIndex++];
                        LogUtil.e("year:" + var25, DBG);
                        LogUtil.e("month:" + month, DBG);
                        LogUtil.e("day:" + day, DBG);
                        LogUtil.e("hour:" + hour, DBG);
                        LogUtil.e("minute:" + minute, DBG);
                        calendar.set(2000 + var25, month - 1, day, hour, minute);
                        logOperate.setDeleteDate(calendar.getTimeInMillis());
                        break;
                    case 9:
                        var26 = datas[dataIndex++];
                        address = new String(Arrays.copyOfRange(datas, dataIndex, dataIndex + var26));
                        dataIndex += var26;
                        var28 = datas[dataIndex++];
                        var30 = new String(Arrays.copyOfRange(datas, dataIndex, dataIndex + var28));
                        var10000 = dataIndex + var28;
                        logOperate.setPassword(address);
                        logOperate.setNewPassword(var30);
                        break;
                    case 15:
                    case 17:
                    case 18:
                    case 25:
                        Long cardNo = Long.valueOf(DigitUtil.fourBytesToLong(Arrays.copyOfRange(datas, dataIndex, dataIndex + 4)));
                        logOperate.setPassword(String.valueOf(cardNo));
                        dataIndex += 4;
                        break;
                    case 19:
                        address = DigitUtil.getMacString(Arrays.copyOfRange(datas, dataIndex, dataIndex + 6));
                        logOperate.setPassword(address);
                        dataIndex += 6;
                        break;
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                        Long FNNo = Long.valueOf(DigitUtil.sixBytesToLong(Arrays.copyOfRange(datas, dataIndex, dataIndex + 6)));
                        logOperate.setPassword(String.valueOf(FNNo));
                        dataIndex += 6;
                        if (dataIndex < nextRecIndex) {
                            byte pwdLen = datas[dataIndex++];
                            new String(Arrays.copyOfRange(datas, dataIndex, dataIndex + pwdLen));
                            var10000 = dataIndex + pwdLen;
                        }
                }

                LogUtil.e("end", DBG);
                logOperates.add(logOperate);
                dataIndex = nextRecIndex;
            }

            LogUtil.e("end---------------", DBG);
            return nextReq;
        }
    }

    public static void ICManage(Command command, int ICOp, int seq, long cardNo, long startDate, long endDate, byte[] aesKey, long timezoneOffSet) {
        ICManage(command, (byte) ICOp, (byte) seq, cardNo, startDate, endDate, aesKey, timezoneOffSet);
    }

    public static void ICManage(Command command, byte ICOp, short seq, long cardNo, long startDate, long endDate, byte[] aesKey, long timezoneOffSet) {
        byte[] values = null;
        byte[] cardBytes;
        String sDate;
        String eDate;
        byte[] time;
        switch (ICOp) {
            case 1:
                values = new byte[]{ICOp, (byte) (seq >> 8), (byte) seq};
                break;
            case 2:
                if (cardNo > 0L) {
                    values = new byte[15];
                    values[0] = ICOp;
                    if (startDate == 0L || endDate == 0L) {
                        startDate = 949338000000L;
                        endDate = 4099741200000L;
                    }

                    LogUtil.e("cardNo:" + cardNo, DBG);
                    cardBytes = DigitUtil.integerToByteArray((int) cardNo);
                    startDate = startDate + timezoneOffSet - (long) TimeZone.getDefault().getOffset(System.currentTimeMillis());
                    endDate = endDate + timezoneOffSet - (long) TimeZone.getDefault().getOffset(System.currentTimeMillis());
                    sDate = DigitUtil.formateDateFromLong(startDate, "yyMMddHHmm");
                    eDate = DigitUtil.formateDateFromLong(endDate, "yyMMddHHmm");
                    time = DigitUtil.convertTimeToByteArray(sDate + eDate);
                    LogUtil.d("sDate:" + sDate, DBG);
                    LogUtil.d("eDate:" + eDate, DBG);
                    System.arraycopy(cardBytes, 0, values, 1, 4);
                    System.arraycopy(time, 0, values, 5, 10);
                } else {
                    values = new byte[]{ICOp};
                }
                break;
            case 3:
                values = new byte[5];
                values[0] = ICOp;
                System.arraycopy(DigitUtil.integerToByteArray((int) cardNo), 0, values, 1, 4);
                break;
            case 4:
                values = new byte[]{ICOp};
                break;
            case 5:
                values = new byte[15];
                values[0] = ICOp;
                cardBytes = DigitUtil.integerToByteArray((int) cardNo);
                startDate = startDate + timezoneOffSet - (long) TimeZone.getDefault().getOffset(System.currentTimeMillis());
                endDate = endDate + timezoneOffSet - (long) TimeZone.getDefault().getOffset(System.currentTimeMillis());
                sDate = DigitUtil.formateDateFromLong(startDate, "yyMMddHHmm");
                eDate = DigitUtil.formateDateFromLong(endDate, "yyMMddHHmm");
                time = DigitUtil.convertTimeToByteArray(sDate + eDate);
                LogUtil.d("sDate:" + sDate, DBG);
                LogUtil.d("eDate:" + eDate, DBG);
                System.arraycopy(cardBytes, 0, values, 1, 4);
                System.arraycopy(time, 0, values, 5, 10);
                LogUtil.d(sDate + eDate, DBG);
        }

        LogUtil.d("arrays:" + DigitUtil.byteArrayToHexString(values), DBG);
        command.setData(values, aesKey);
    }

    public static void FRManage(Command command, int FROp, int seq, long FRNo, long startDate, long endDate, byte[] aesKey, long timezoneOffSet) {
        FRManage(command, FROp, seq, FRNo, startDate, endDate, aesKey, timezoneOffSet);
    }

    public static void FRManage(Command command, byte FROp, short seq, long FRNo, long startDate, long endDate, byte[] aesKey, long timezoneOffSet) {
        byte[] values = null;
        byte[] FRBytes;
        String sDate;
        String eDate;
        byte[] time;
        switch (FROp) {
            case 2:
                if (FRNo > 0L) {
                    values = new byte[17];
                    values[0] = FROp;
                    FRBytes = DigitUtil.longToByteArrayWithLen(FRNo, 6);
                    LogUtil.d("FRBytes:" + DigitUtil.sixBytesToLong(FRBytes), DBG);
                    if (startDate == 0L || endDate == 0L) {
                        startDate = 949338000000L;
                        endDate = 4099741200000L;
                    }

                    startDate = startDate + timezoneOffSet - (long) TimeZone.getDefault().getOffset(System.currentTimeMillis());
                    endDate = endDate + timezoneOffSet - (long) TimeZone.getDefault().getOffset(System.currentTimeMillis());
                    sDate = DigitUtil.formateDateFromLong(startDate, "yyMMddHHmm");
                    eDate = DigitUtil.formateDateFromLong(endDate, "yyMMddHHmm");
                    time = DigitUtil.convertTimeToByteArray(sDate + eDate);
                    System.arraycopy(FRBytes, 0, values, 1, 6);
                    System.arraycopy(time, 0, values, 7, 10);
                } else {
                    values = new byte[]{FROp};
                }
                break;
            case 3:
                values = new byte[7];
                values[0] = FROp;
                LogUtil.d("FRNO转换:" + DigitUtil.sixBytesToLong(DigitUtil.longToByteArrayWithLen(FRNo, 6)), DBG);
                System.arraycopy(DigitUtil.longToByteArrayWithLen(FRNo, 6), 0, values, 1, 6);
                LogUtil.d(DigitUtil.byteArrayToHexString(values), DBG);
                break;
            case 4:
                values = new byte[]{FROp};
                break;
            case 5:
                values = new byte[17];
                values[0] = FROp;
                LogUtil.d("FRNo:" + FRNo, DBG);
                FRBytes = DigitUtil.longToByteArrayWithLen(FRNo, 6);
                LogUtil.d("FRBytes:" + DigitUtil.sixBytesToLong(FRBytes), DBG);
                startDate = startDate + timezoneOffSet - (long) TimeZone.getDefault().getOffset(System.currentTimeMillis());
                endDate = endDate + timezoneOffSet - (long) TimeZone.getDefault().getOffset(System.currentTimeMillis());
                sDate = DigitUtil.formateDateFromLong(startDate, "yyMMddHHmm");
                eDate = DigitUtil.formateDateFromLong(endDate, "yyMMddHHmm");
                time = DigitUtil.convertTimeToByteArray(sDate + eDate);
                System.arraycopy(FRBytes, 0, values, 1, 6);
                System.arraycopy(time, 0, values, 7, 10);
                LogUtil.d(sDate + eDate, DBG);
                break;
            case 6:
                values = new byte[]{FROp, (byte) (seq >> 8), (byte) seq};
        }

        command.setData(values, aesKey);
    }

    public static void FRManage(Command command, int FROp, short seq, long FRNo, long startDate, long endDate, byte[] aesKey) {
        FRManage(command, (byte) FROp, seq, FRNo, startDate, endDate, aesKey, 0L);
    }

    public static void FRManage(Command command, byte FROp, short seq, long FRNo, long startDate, long endDate, byte[] aesKey) {
        FRManage(command, FROp, seq, FRNo, startDate, endDate, aesKey, 0L);
    }

    public static void setWristKey(Command command, String wristKey, byte[] aesKeyArray) {
        command.setData(wristKey.getBytes(), aesKeyArray);
    }

    public static void autoLockManage(Command command, byte op, short time, byte[] aesKey) {
        byte[] values = null;
        switch (op) {
            case 1:
                values = new byte[]{op};
                break;
            case 2:
                values = new byte[]{op, (byte) (time >> 8), (byte) time};
        }

        LogUtil.d(DigitUtil.byteArrayToHexString(values), DBG);
        command.setData(values, aesKey);
    }

    public static void lock(Command command, String sum, long dateTime, byte[] aesKeyArray) {
        byte[] values = new byte[8];
        int sumI = Integer.valueOf(sum).intValue();
        byte[] sumByteArray = DigitUtil.integerToByteArray(sumI);
        System.arraycopy(sumByteArray, 0, values, 0, sumByteArray.length);
        int date = (int) (dateTime / 1000L);
        System.arraycopy(DigitUtil.integerToByteArray(date), 0, values, 4, 4);
        command.setData(values, aesKeyArray);
    }

    public static void screenPasscodeManage(Command command, int opType, byte[] aesKeyArray) {
        byte[] values;
        if (opType == 1) {
            values = new byte[]{1};
        } else {
            values = new byte[]{2, (byte) (opType - 2)};
        }

        command.setData(values, aesKeyArray);
    }


    public static void controlRemoteUnlock(Command command, byte opType, byte opValue, byte[] aesKeyArray) {
        command.setCommand(55);
        byte[] values = null;
        switch (opType) {
            case 1:
                values = new byte[]{opType};
                break;
            case 2:
                values = new byte[]{opType, opValue};
        }

        command.setData(values, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }



}
