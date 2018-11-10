package cn.jcyh.eaglekinglockdemo.entity;

/**
 * Created by jogger on 2018/11/6.
 */
public class SendKeyData {
    private String clientId;
    private String accessToken;
    private int lockId;
    private String receiverUsername;
    private long startDate;
    private long endDate;
    private String remarks;
    private int remoteEnable;
    private long date;

    public SendKeyData(String clientId, String accessToken, int lockId, String receiverUsername, long startDate, long endDate, String remarks, int remoteEnable, long date) {
        this.clientId = clientId;
        this.accessToken = accessToken;
        this.lockId = lockId;
        this.receiverUsername = receiverUsername;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remarks = remarks;
        this.remoteEnable = remoteEnable;
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

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getRemoteEnable() {
        return remoteEnable;
    }

    public void setRemoteEnable(int remoteEnable) {
        this.remoteEnable = remoteEnable;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "SendKeyData{" +
                "clientId='" + clientId + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", lockId=" + lockId +
                ", receiverUsername='" + receiverUsername + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", remarks='" + remarks + '\'' +
                ", remoteEnable=" + remoteEnable +
                ", date=" + date +
                '}';
    }
}
