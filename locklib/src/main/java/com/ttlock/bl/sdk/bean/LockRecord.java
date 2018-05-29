package com.ttlock.bl.sdk.bean;

/**
 * Created by jogger on 2018/5/17.锁记录
 */

public class LockRecord {
    private int lockId;
    private int recordType;//记录类型：1-App开锁、2-动过车位锁、3-插座开锁、4-键盘密码开锁，5-车位锁升，6-车位锁降,7-IC卡开锁，8-指纹开锁，9-手环开锁，10-机械钥匙开锁，11-蓝牙闭锁，12-网关开锁，29-非法开锁，30-门磁合上，31-门磁打开；
    private int success;
    private String username;//操作人用户名
    private String keyboardPwd;//键盘密码的密码，或者IC卡号或者手环地址
    private long lockDate;//操作时锁上的时间
    private long serverDate;//记录上传到服务器的时间

    public int getLockId() {
        return lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }

    public int getRecordType() {
        return recordType;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKeyboardPwd() {
        return keyboardPwd;
    }

    public void setKeyboardPwd(String keyboardPwd) {
        this.keyboardPwd = keyboardPwd;
    }

    public long getLockDate() {
        return lockDate;
    }

    public void setLockDate(long lockDate) {
        this.lockDate = lockDate;
    }

    public long getServerDate() {
        return serverDate;
    }

    public void setServerDate(long serverDate) {
        this.serverDate = serverDate;
    }

    @Override
    public String toString() {
        return "LockRecord{" +
                "lockId=" + lockId +
                ", recordType=" + recordType +
                ", success=" + success +
                ", username='" + username + '\'' +
                ", keyboardPwd='" + keyboardPwd + '\'' +
                ", lockDate=" + lockDate +
                ", serverDate=" + serverDate +
                '}';
    }
}
