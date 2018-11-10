package cn.jcyh.eaglekinglockdemo.entity;

/**
 * Created by jogger on 2018/11/6.
 */
public class AuthKeyData {
    private String clientId;
    private String accessToken;
    private int lockId;
    private int keyId;
    private long date;

    public AuthKeyData(String clientId, String accessToken, int lockId, int keyId, long date) {
        this.clientId = clientId;
        this.accessToken = accessToken;
        this.lockId = lockId;
        this.keyId = keyId;
        this.date = date;
    }

    public int getLockId() {
        return lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
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

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
