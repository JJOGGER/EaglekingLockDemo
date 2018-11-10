//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.command;

import com.ttlock.bl.sdk.service.BluetoothLeService;
import com.ttlock.bl.sdk.util.DigitUtil;

public class CommandUtil_V2S_PLUS {
    private static boolean DBG = false;

    public CommandUtil_V2S_PLUS() {
    }

    public static void addAdmin_V2S_PLUS(Command command, String adminPassword, String unlockNumber, byte[] aesKeyArray) {
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
        command.setData(values, aesKeyArray);
    }

    public static void checkAdmin_V2S_PLUS(Command command, String adminPs, String unlockKey, int lockFlagPos, byte[] aesKeyArray, int apiCommand) {
        byte[] values = new byte[13];
        byte[] password = adminPs.getBytes();
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
        command.setData(values, aesKeyArray);
    }

    public static void setAdminKeyboardPwd(Command command, String adminKeyboardPwd, byte[] aesKeyArray) {
        byte[] values = new byte[10];
        int len = adminKeyboardPwd.length();

        int i;
        for(i = 0; i < len; ++i) {
            values[i] = (byte)(adminKeyboardPwd.charAt(i) - 48);
        }

        for(i = len; i < 10; ++i) {
            values[i] = -1;
        }

        command.setData(values, aesKeyArray);
    }

    public static void setDeletePwd(Command command, String deletePwd, byte[] aesKeyArray) {
        byte[] values = new byte[10];
        int len = deletePwd.length();

        int i;
        for(i = 0; i < len; ++i) {
            values[i] = (byte)(deletePwd.charAt(i) - 48);
        }

        for(i = len; i < 10; ++i) {
            values[i] = -1;
        }

        command.setData(values, aesKeyArray);
    }

    public static void checkUserTime_V2S_PLUS(Command command, String sDateStr, String eDateStr, String unlockKey, int lockFlagPos, byte[] aesKeyArray, int apiCommand) {
        byte[] values = new byte[13];
        byte[] time = DigitUtil.convertTimeToByteArray(sDateStr + eDateStr);
        System.arraycopy(time, 0, values, 0, 10);
        values[10] = (byte)(lockFlagPos >> 16 & 255);
        values[11] = (byte)(lockFlagPos >> 8 & 255);
        values[12] = (byte)(lockFlagPos & 255);
        command.setData(values, aesKeyArray);
    }

    public static void unlock_V2S_PLUS(Command command, String sum, byte[] aesKeyArray) {
        byte flag = 0;
        byte[] values = new byte[5];
        int sumI = Integer.valueOf(sum).intValue();
        byte[] sumByteArray = DigitUtil.integerToByteArray(sumI);
        System.arraycopy(sumByteArray, 0, values, 0, sumByteArray.length);
        values[4] = flag;
        command.setData(values, aesKeyArray);
    }

    public static void synPwd(int lockType, byte[] packet, int seq, byte[] aesKeyArray) {
        Command command = new Command(lockType);
        command.setCommand(73);
        byte[] values = new byte[30];

        for(int i = 0; i < 28; ++i) {
            values[i] = packet[i];
        }

        values[28] = (byte)seq;
        values[29] = (byte)(seq >> 8);
        command.setData(values, aesKeyArray);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }

    public static void calibationTime_V2S_PLUS(Command command, String timeStr, byte[] aesKeyArray) {
        byte[] timeArray = DigitUtil.convertTimeToByteArray(timeStr);
        command.setData(timeArray, aesKeyArray);
    }
}
