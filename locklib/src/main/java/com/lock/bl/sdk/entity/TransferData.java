//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lock.bl.sdk.entity;

import java.util.List;
import java.util.TimeZone;

public class TransferData {
    public static byte[] aesKeyArray;
    private int APICommand;
    private byte command;
    String lockVersion;
    String adminPs;
    String unlockKey;
    int mUid;
    String pwdInfo;
    long timestamp;
    String keyboardPwd;
    String deletePwd;
    int lockFlagPos;
    String lockname;
    String originalPwd;
    String newPwd;
    long startDate;
    long endDate;
    byte keyboardPwdType;
    private long unlockDate = System.currentTimeMillis();
    private long calibationTime;
    private long timezoneOffSet = (long)TimeZone.getDefault().getOffset(System.currentTimeMillis());
    byte[] transferData;
    private long No;
    private String wristbandKey;
    private List<String> pwds;
    private int op;
    private String json;
    private short seq;
    private int opValue = -1;

    public TransferData() {
    }

    public String getLockVersion() {
        return this.lockVersion;
    }

    public void setLockVersion(String lockVersion) {
        this.lockVersion = lockVersion;
    }

    public static byte[] getAesKeyArray() {
        return aesKeyArray;
    }

    public static void setAesKeyArray(byte[] aesKeyArray) {
        TransferData.aesKeyArray = aesKeyArray;
    }

    public int getAPICommand() {
        return this.APICommand;
    }

    public void setAPICommand(int APICommand) {
        this.APICommand = APICommand;
    }

    public byte getCommand() {
        return this.command;
    }

    public void setCommand(byte command) {
        this.command = command;
    }
    public void setCommand(int command) {
        this.command = (byte) command;
    }
    public String getAdminPs() {
        return this.adminPs;
    }

    public void setAdminPs(String adminPs) {
        this.adminPs = adminPs;
    }

    public String getUnlockKey() {
        return this.unlockKey;
    }

    public void setUnlockKey(String unlockKey) {
        this.unlockKey = unlockKey;
    }

    public int getmUid() {
        return this.mUid;
    }

    public void setmUid(int mUid) {
        this.mUid = mUid;
    }

    public String getPwdInfo() {
        return this.pwdInfo;
    }

    public void setPwdInfo(String pwdInfo) {
        this.pwdInfo = pwdInfo;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getKeyboardPwd() {
        return this.keyboardPwd;
    }

    public void setKeyboardPwd(String keyboardPwd) {
        this.keyboardPwd = keyboardPwd;
    }

    public String getDeletePwd() {
        return this.deletePwd;
    }

    public void setDeletePwd(String deletePwd) {
        this.deletePwd = deletePwd;
    }

    public int getLockFlagPos() {
        return this.lockFlagPos;
    }

    public void setLockFlagPos(int lockFlagPos) {
        this.lockFlagPos = lockFlagPos;
    }

    public String getLockname() {
        return this.lockname;
    }

    public void setLockname(String lockname) {
        this.lockname = lockname;
    }

    public String getOriginalPwd() {
        return this.originalPwd;
    }

    public void setOriginalPwd(String originalPwd) {
        this.originalPwd = originalPwd;
    }

    public String getNewPwd() {
        return this.newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
        if(this.newPwd == null) {
            this.newPwd = "";
        }

    }

    public long getStartDate() {
        return this.startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return this.endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public byte getKeyboardPwdType() {
        return this.keyboardPwdType;
    }

    public void setKeyboardPwdType(byte keyboardPwdType) {
        this.keyboardPwdType = keyboardPwdType;
    }

    public long getUnlockDate() {
        return this.unlockDate;
    }

    public void setUnlockDate(long unlockDate) {
        this.unlockDate = unlockDate;
    }

    public long getCalibationTime() {
        return this.calibationTime;
    }

    public void setCalibationTime(long calibationTime) {
        this.calibationTime = calibationTime;
    }

    public byte[] getTransferData() {
        return this.transferData;
    }

    public void setTransferData(byte[] transferData) {
        this.transferData = transferData;
    }

    public long getNo() {
        return this.No;
    }

    public void setNo(long no) {
        this.No = no;
    }

    public String getWristbandKey() {
        return this.wristbandKey;
    }

    public void setWristbandKey(String wristbandKey) {
        this.wristbandKey = wristbandKey;
    }

    public long getTimezoneOffSet() {
        return this.timezoneOffSet;
    }

    public void setTimezoneOffSet(long timezoneOffSet) {
        if(timezoneOffSet != -1L) {
            this.timezoneOffSet = timezoneOffSet;
        }

    }

    public List<String> getPwds() {
        return this.pwds;
    }

    public void setPwds(List<String> pwds) {
        this.pwds = pwds;
    }

    public int getOp() {
        return this.op;
    }

    public void setOp(int op) {
        this.op = op;
    }

    public String getJson() {
        return this.json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public short getSeq() {
        return this.seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }

    public int getOpValue() {
        return this.opValue;
    }

    public void setOpValue(int opValue) {
        this.opValue = opValue;
    }
}
