//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.command;

import com.ttlock.bl.sdk.service.BluetoothLeService;
import com.ttlock.bl.sdk.util.DigitUtil;

public class CommandUtil_V2S {
    public CommandUtil_V2S() {
    }

    public static void addAdmin(Command command, String adminPassword, String unlockNumber) {
        byte[] values = new byte[14];
        int i = 9;

        for(int j = adminPassword.length() - 1; j >= 0; values[i--] = (byte)(adminPassword.charAt(j--) - 48)) {
            ;
        }

        while(i >= 0) {
            values[i--] = 0;
        }

        int unlock_number = Integer.valueOf(unlockNumber).intValue();
        System.arraycopy(DigitUtil.integerToByteArray(unlock_number), 0, values, 10, 4);
        command.setData(values);
    }

    public static void checkAdmin(Command command, String adminPs, int lockFlagPos) {
        byte[] password = adminPs.getBytes();
        byte[] values = new byte[13];
        int i = 9;

        for(int j = password.length - 1; j >= 0; values[i--] = (byte)(password[j--] - 48)) {
            ;
        }

        while(i >= 0) {
            values[i--] = 0;
        }

        values[10] = (byte)(lockFlagPos >> 16);
        values[11] = (byte)(lockFlagPos >> 8);
        values[12] = (byte)lockFlagPos;
        command.setData(values);
    }

    public static void unlock(Command command, String sum) {
        byte flag = 0;
        byte[] values = new byte[5];
        int sum_int = Integer.valueOf(sum).intValue();
        System.arraycopy(DigitUtil.integerToByteArray(sum_int), 0, values, 0, 4);
        values[4] = flag;
        command.setData(values);
    }

    public static void checkUserTime(Command command, String sDateStr, String eDateStr, int lockFlagPos) {
        byte[] values = new byte[13];
        byte[] time = DigitUtil.convertTimeToByteArray(sDateStr + eDateStr);
        System.arraycopy(time, 0, values, 0, 10);
        values[10] = (byte)(lockFlagPos >> 16 & 255);
        values[11] = (byte)(lockFlagPos >> 8 & 255);
        values[12] = (byte)(lockFlagPos & 255);
        command.setData(values);
    }

    public static void calibationTime_V2S(Command command, String timeStr) {
        byte[] timeArray = DigitUtil.convertTimeToByteArray(timeStr);
        command.setData(timeArray);
    }

    public static void synPwd(int lockType, String pwd, int seq) {
        Command command = new Command(lockType);
        command.setCommand(73);
        byte[] values = new byte[32];

        for(int i = 0; i < 30; ++i) {
            values[i] = (byte)(pwd.charAt(i) - 48);
        }

        values[30] = (byte)(seq >> 8 & 255);
        values[31] = (byte)(seq & 255);
        command.setData(values);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void setAdminKeyboardPwd(Command command, String adminKeyboardPwd) {
        byte[] values = new byte[10];
        int len = adminKeyboardPwd.length();

        int i;
        for(i = 0; i < len; ++i) {
            values[i] = (byte)(adminKeyboardPwd.charAt(i) - 48);
        }

        for(i = len; i < 10; ++i) {
            values[i] = -1;
        }

        command.setData(values);
    }

    public static void setDeletePwd(Command command, String deletePwd) {
        byte[] values = new byte[10];
        int len = deletePwd.length();

        int i;
        for(i = 0; i < len; ++i) {
            values[i] = (byte)(deletePwd.charAt(i) - 48);
        }

        for(i = len; i < 10; ++i) {
            values[i] = -1;
        }

        command.setData(values);
    }
}
