package cn.jcyh.eaglekinglockdemo.entity;

/**
 * Created by jogger on 2018/11/6.
 */
public class KeyboardPwdGetData {
    private String clientId;
    private String accessToken;
    private int lockId;
    private int keyboardPwdVersion;
    private int keyboardPwdType;
    private long startDate;
    private long endDate;
    private long date;

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

    public int getLockId() {
        return lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }

    public int getKeyboardPwdVersion() {
        return keyboardPwdVersion;
    }

    public void setKeyboardPwdVersion(int keyboardPwdVersion) {
        this.keyboardPwdVersion = keyboardPwdVersion;
    }

    public int getKeyboardPwdType() {
        return keyboardPwdType;
    }

    public void setKeyboardPwdType(int keyboardPwdType) {
        this.keyboardPwdType = keyboardPwdType;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public KeyboardPwdGetData(String clientId, String accessToken, int lockId, int keyboardPwdVersion, int keyboardPwdType, long startDate, long endDate, long date) {
        this.clientId = clientId;
        this.lockId = lockId;
        this.keyboardPwdVersion = keyboardPwdVersion;
        this.keyboardPwdType = keyboardPwdType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.date = date;
        this.accessToken = accessToken;
    }
}
