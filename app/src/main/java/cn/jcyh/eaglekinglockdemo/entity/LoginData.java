package cn.jcyh.eaglekinglockdemo.entity;

/**
 * Created by jogger on 2018/11/6.
 */
public class LoginData {
    private String account;
    private long mySmartxUserId;//
    private int smartLockUserId;//开锁用uid
    private String mySmartxAccessToken;
    private String smartLockUserName;
    private String smartLockAccessToken;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public long getMySmartxUserId() {
        return mySmartxUserId;
    }

    public void setMySmartxUserId(long mySmartxUserId) {
        this.mySmartxUserId = mySmartxUserId;
    }

    public String getMySmartxAccessToken() {
        return mySmartxAccessToken;
    }

    public void setMySmartxAccessToken(String mySmartxAccessToken) {
        this.mySmartxAccessToken = mySmartxAccessToken;
    }

    public String getSmartLockUserName() {
        return smartLockUserName;
    }

    public void setSmartLockUserName(String smartLockUserName) {
        this.smartLockUserName = smartLockUserName;
    }

    public String getSmartLockAccessToken() {
        return smartLockAccessToken;
    }

    public void setSmartLockAccessToken(String smartLockAccessToken) {
        this.smartLockAccessToken = smartLockAccessToken;
    }

    public int getSmartLockUserId() {
        return smartLockUserId;
    }

    public void setSmartLockUserId(int smartLockUserId) {
        this.smartLockUserId = smartLockUserId;
    }

    @Override
    public String toString() {
        return "LoginData{" +
                "smartLockUserId=" + smartLockUserId +
                "mySmartxUserId=" + mySmartxUserId +
                ", mySmartxAccessToken='" + mySmartxAccessToken + '\'' +
                ", smartLockUserName='" + smartLockUserName + '\'' +
                ", smartLockAccessToken='" + smartLockAccessToken + '\'' +
                '}';
    }
}
