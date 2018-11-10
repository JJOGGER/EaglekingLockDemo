package cn.jcyh.eaglekinglockdemo.entity;

/**
 * Created by jogger on 2018/11/6.
 */
public class AddKeyboardPwdData {
    private String clientId;
    private String accessToken;
    private int lockId;
    private String keyboardPwd;
    private long startDate;
    private long endDate;
    private int addType;
    private long date;

    public AddKeyboardPwdData(String clientId, String accessToken, int lockId, String keyboardPwd, long startDate, long endDate, int addType, long date) {
        this.clientId = clientId;
        this.accessToken = accessToken;
        this.lockId = lockId;
        this.keyboardPwd = keyboardPwd;
        this.startDate = startDate;
        this.endDate = endDate;
        this.addType = addType;
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

    public int getLockId() {
        return lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }

    public String getKeyboardPwd() {
        return keyboardPwd;
    }

    public void setKeyboardPwd(String keyboardPwd) {
        this.keyboardPwd = keyboardPwd;
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

    public int getAddType() {
        return addType;
    }

    public void setAddType(int addType) {
        this.addType = addType;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
