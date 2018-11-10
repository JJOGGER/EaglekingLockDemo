package cn.jcyh.eaglekinglockdemo.entity;

import cn.jcyh.locklib.entity.LockVersion;

/**
 * Created by jogger on 2018/11/6.
 */
public class InitRequestData {
    private String clientId;
    private String accessToken;
    private String lockName;
    private String lockMac;
    private String lockKey;
    private int lockFlagPos;
    private String aesKeyStr;
    private LockVersion lockVersion;
    private String adminPwd;
    private String noKeyPwd;
    private String deletePwd;
    private String pwdInfo;
    private long timestamp;
    private int specialValue;
    private int electricQuantity;
    private int timezoneRawOffset;
    private String modelNum;
    private String hardwareRevision;
    private String firmwareRevision;
    private long date;

    public InitRequestData(String clientId, String accessToken, String lockName, String lockMac, String lockKey, int lockFlagPos, String aesKeyStr, LockVersion lockVersion, String adminPwd, String noKeyPwd, String deletePwd, String pwdInfo, long timestamp, int specialValue, int electricQuantity, int timezoneRawOffset, String modelNum, String hardwareRevision, String firmwareRevision, long date) {
        this.clientId = clientId;
        this.accessToken = accessToken;
        this.lockName = lockName;
        this.lockMac = lockMac;
        this.lockKey = lockKey;
        this.lockFlagPos = lockFlagPos;
        this.aesKeyStr = aesKeyStr;
        this.lockVersion = lockVersion;
        this.adminPwd = adminPwd;
        this.noKeyPwd = noKeyPwd;
        this.deletePwd = deletePwd;
        this.pwdInfo = pwdInfo;
        this.timestamp = timestamp;
        this.specialValue = specialValue;
        this.electricQuantity = electricQuantity;
        this.timezoneRawOffset = timezoneRawOffset;
        this.modelNum = modelNum;
        this.hardwareRevision = hardwareRevision;
        this.firmwareRevision = firmwareRevision;
        this.date = date;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String getLockMac() {
        return lockMac;
    }

    public void setLockMac(String lockMac) {
        this.lockMac = lockMac;
    }

    public String getLockKey() {
        return lockKey;
    }

    public void setLockKey(String lockKey) {
        this.lockKey = lockKey;
    }

    public int getLockFlagPos() {
        return lockFlagPos;
    }

    public void setLockFlagPos(int lockFlagPos) {
        this.lockFlagPos = lockFlagPos;
    }

    public String getAesKeyStr() {
        return aesKeyStr;
    }

    public void setAesKeyStr(String aesKeyStr) {
        this.aesKeyStr = aesKeyStr;
    }

    public LockVersion getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(LockVersion lockVersion) {
        this.lockVersion = lockVersion;
    }

    public String getAdminPwd() {
        return adminPwd;
    }

    public void setAdminPwd(String adminPwd) {
        this.adminPwd = adminPwd;
    }

    public String getNoKeyPwd() {
        return noKeyPwd;
    }

    public void setNoKeyPwd(String noKeyPwd) {
        this.noKeyPwd = noKeyPwd;
    }

    public String getDeletePwd() {
        return deletePwd;
    }

    public void setDeletePwd(String deletePwd) {
        this.deletePwd = deletePwd;
    }

    public String getPwdInfo() {
        return pwdInfo;
    }

    public void setPwdInfo(String pwdInfo) {
        this.pwdInfo = pwdInfo;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getSpecialValue() {
        return specialValue;
    }

    public void setSpecialValue(int specialValue) {
        this.specialValue = specialValue;
    }

    public int getElectricQuantity() {
        return electricQuantity;
    }

    public void setElectricQuantity(int electricQuantity) {
        this.electricQuantity = electricQuantity;
    }

    public int getTimezoneRawOffset() {
        return timezoneRawOffset;
    }

    public void setTimezoneRawOffset(int timezoneRawOffset) {
        this.timezoneRawOffset = timezoneRawOffset;
    }

    public String getModelNum() {
        return modelNum;
    }

    public void setModelNum(String modelNum) {
        this.modelNum = modelNum;
    }

    public String getHardwareRevision() {
        return hardwareRevision;
    }

    public void setHardwareRevision(String hardwareRevision) {
        this.hardwareRevision = hardwareRevision;
    }

    public String getFirmwareRevision() {
        return firmwareRevision;
    }

    public void setFirmwareRevision(String firmwareRevision) {
        this.firmwareRevision = firmwareRevision;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "InitRequestData{" +
                "clientId='" + clientId + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", lockName='" + lockName + '\'' +
                ", lockMac='" + lockMac + '\'' +
                ", lockKey='" + lockKey + '\'' +
                ", lockFlagPos=" + lockFlagPos +
                ", aesKeyStr='" + aesKeyStr + '\'' +
                ", lockVersion=" + lockVersion +
                ", adminPwd='" + adminPwd + '\'' +
                ", noKeyPwd='" + noKeyPwd + '\'' +
                ", deletePwd='" + deletePwd + '\'' +
                ", pwdInfo='" + pwdInfo + '\'' +
                ", timestamp=" + timestamp +
                ", specialValue=" + specialValue +
                ", electricQuantity=" + electricQuantity +
                ", timezoneRawOffset=" + timezoneRawOffset +
                ", modelNum='" + modelNum + '\'' +
                ", hardwareRevision='" + hardwareRevision + '\'' +
                ", firmwareRevision='" + firmwareRevision + '\'' +
                ", date=" + date +
                '}';
    }
}
