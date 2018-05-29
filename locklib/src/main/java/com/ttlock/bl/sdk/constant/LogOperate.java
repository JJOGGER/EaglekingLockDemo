//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.constant;

public class LogOperate {
    public static final byte OPERATE_TYPE_MOBILE_UNLOCK = 1;
    public static final byte OPERATE_TYPE_SERVER_UNLOCK = 3;
    public static final byte OPERATE_TYPE_KEYBOARD_PASSWORD_UNLOCK = 4;
    public static final byte OPERATE_TYPE_KEYBOARD_MODIFY_PASSWORD = 5;
    public static final byte OPERATE_TYPE_KEYBOARD_REMOVE_SINGLE_PASSWORD = 6;
    public static final byte OPERATE_TYPE_ERROR_PASSWORD_UNLOCK = 7;
    public static final byte OPERATE_TYPE_KEYBOARD_REMOVE_ALL_PASSWORDS = 8;
    public static final byte OPERATE_TYPE_KEYBOARD_PASSWORD_KICKED = 9;
    public static final byte OPERATE_TYPE_USE_DELETE_CODE = 10;
    public static final byte OPERATE_TYPE_PASSCODE_EXPIRED = 11;
    public static final byte OPERATE_TYPE_SPACE_INSUFFICIENT = 12;
    public static final byte OPERATE_TYPE_PASSCODE_IN_BLACK_LIST = 13;
    public static final byte OPERATE_TYPE_DOOR_REBOOT = 14;
    public static final byte OPERATE_TYPE_ADD_IC = 15;
    public static final byte OPERATE_TYPE_CLEAR_IC_SUCCEED = 16;
    public static final byte OPERATE_TYPE_IC_UNLOCK_SUCCEED = 17;
    public static final byte OPERATE_TYPE_DELETE_IC_SUCCEED = 18;
    public static final byte OPERATE_TYPE_BONG_UNLOCK_SUCCEED = 19;
    public static final byte OPERATE_TYPE_FR_UNLOCK_SUCCEED = 20;
    public static final byte OPERATE_TYPE_ADD_FR = 21;
    public static final byte OPERATE_TYPE_FR_UNLOCK_FAILED = 22;
    public static final byte OPERATE_TYPE_DELETE_FR_SUCCEED = 23;
    public static final byte OPERATE_TYPE_CLEAR_FR_SUCCEED = 24;
    public static final byte OPERATE_TYPE_IC_UNLOCK_FAILED = 25;
    public static final byte OPERATE_BLE_LOCK = 26;
    public static final byte OPERATE_KEY_UNLOCK = 27;
    public static final byte GATEWAY_UNLOCK = 28;
    public static final byte ILLAGEL_UNLOCK = 29;
    public static final byte DOOR_SENSOR_LOCK = 30;
    public static final byte DOOR_SENSOR_UNLOCK = 31;
    public static final byte DOOR_GO_OUT = 32;
    private int recordType;
    private int recordId;
    private int uid;
    private String password;
    private String newPassword;
    private long operateDate;
    private long deleteDate;
    private int electricQuantity;

    public LogOperate() {
    }

    public int getRecordType() {
        return this.recordType;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }

    public int getRecordId() {
        return this.recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getUid() {
        return this.uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public long getOperateDate() {
        return this.operateDate;
    }

    public void setOperateDate(long operateDate) {
        this.operateDate = operateDate;
    }

    public long getDeleteDate() {
        return this.deleteDate;
    }

    public void setDeleteDate(long deleteDate) {
        this.deleteDate = deleteDate;
    }

    public int getElectricQuantity() {
        return this.electricQuantity;
    }

    public void setElectricQuantity(int electricQuantity) {
        this.electricQuantity = electricQuantity;
    }
}
