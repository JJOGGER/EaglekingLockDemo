package cn.jcyh.eaglekinglockdemo.bean;

import java.util.List;

/**
 * Created by jogger on 2018/4/27.
 */

public class SyncData {
    private long lastUpdateDate;
    private List<LockKey> keyList;

    public long getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public List<LockKey> getKeyList() {
        return keyList;
    }

    public void setKeyList(List<LockKey> keyList) {
        this.keyList = keyList;
    }
}