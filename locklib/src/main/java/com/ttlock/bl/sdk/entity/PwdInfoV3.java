//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.entity;

public class PwdInfoV3 {
    private int year;
    private int code;
    private String secretKey;
    private long deleteDate;

    public PwdInfoV3() {
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public static PwdInfoV3 getInstance(int year, int code, String secretKey) {
        PwdInfoV3 instance = new PwdInfoV3();
        instance.setYear(year);
        instance.setCode(code);
        if(secretKey.length() == 9) {
            secretKey = "0" + secretKey;
        }

        instance.setSecretKey(secretKey);
        return instance;
    }

    public static PwdInfoV3 getInstance(int year, int code, String secretKey, long deleteDate) {
        PwdInfoV3 instance = getInstance(year, code, secretKey);
        instance.setDeleteDate(deleteDate);
        return instance;
    }

    public long getDeleteDate() {
        return this.deleteDate;
    }

    public void setDeleteDate(long deleteDate) {
        this.deleteDate = deleteDate;
    }
}
