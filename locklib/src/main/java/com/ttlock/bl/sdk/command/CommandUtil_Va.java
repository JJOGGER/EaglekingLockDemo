//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.command;

import com.ttlock.bl.sdk.service.BluetoothLeService;
import com.ttlock.bl.sdk.util.DigitUtil;

public class CommandUtil_Va {
    public CommandUtil_Va() {
    }

    public static void addAdmin(Command command, String adminPassword, String unlockNumber) {
        byte[] values = new byte[21];
        System.arraycopy(adminPassword.getBytes(), 0, values, 0, 10);
        values[10] = 32;
        System.arraycopy(unlockNumber.getBytes(), 0, values, 11, 10);
        command.setData(values);
    }

    public static void checkAdmin(Command command, String adminPs) {
        command.setData(adminPs.getBytes());
    }

    public static void up_down(Command command, String sum) {
        byte[] data = new byte[12];
        System.arraycopy(sum.getBytes(), 0, data, 0, sum.length());
        data[10] = 32;
        data[11] = 49;
        command.setData(data);
    }

    public static void checkUserTime(Command command, String sDateStr, String eDateStr) {
        byte[] values = DigitUtil.convertTimeToByteArray(sDateStr + eDateStr);
        command.setData(values);
    }

    public static void calibationTime(Command command, String timeStr) {
        byte[] timeArray = DigitUtil.convertTimeToByteArray(timeStr);
        command.setData(timeArray);
    }

    public static void Va_Get_Lockcar_Alarm(int lockType) {
        Command command = new Command(lockType);
        command.setCommand((byte) 87);
        BluetoothLeService.getBluetoothLeService().sendCommand(command.buildCommand());
    }
}
