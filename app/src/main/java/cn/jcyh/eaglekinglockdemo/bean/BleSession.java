package cn.jcyh.eaglekinglockdemo.bean;


import android.os.Bundle;

import cn.jcyh.eaglekinglockdemo.enumtype.Operation;

/**
 * Created by Administrator on 2016/7/15 0015.
 */
public class BleSession {

    /**
     * 操作
     */
    private Operation operation;

    /**
     * mac地址
     */
    private String lockmac;

    /**
     * 密码
     */
    private String password;

    private Bundle argments;

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getLockmac() {
        return lockmac;
    }

    public void setLockmac(String lockmac) {
        this.lockmac = lockmac;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private BleSession() {
    }

    public void setArgments(Bundle bundle) {
        argments = bundle;
    }

    public Bundle getArgments() {
        return argments;
    }

    public static BleSession getInstance(Operation operation, String lockmac) {
        BleSession bleSession = new BleSession();
        bleSession.setOperation(operation);
        bleSession.setLockmac(lockmac);
        return bleSession;
    }
}
