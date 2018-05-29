package com.ttlock.bl.sdk.bean;

/**
 * Created by jogger on 2018/5/16.
 * 指纹
 */

public class LockFingerprint {
    private int fingerprintId;
    private int lockId;
    private String fingerprintNumber;
    private long startDate;
    private long endDate;
    private long createDate;

    public int getFingerprintId() {
        return fingerprintId;
    }

    public void setFingerprintId(int fingerprintId) {
        this.fingerprintId = fingerprintId;
    }

    public int getLockId() {
        return lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }

    public String getFingerprintNumber() {
        return fingerprintNumber;
    }

    public void setFingerprintNumber(String fingerprintNumber) {
        this.fingerprintNumber = fingerprintNumber;
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

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "LockFingerprint{" +
                "fingerprintId=" + fingerprintId +
                ", lockId=" + lockId +
                ", fingerprintNumber='" + fingerprintNumber + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", createDate=" + createDate +
                '}';
    }
}
