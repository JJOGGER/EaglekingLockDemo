package com.ttlock.bl.sdk.bean;

import java.util.List;

/**
 * Created by jogger on 2018/5/8.
 */

public class KeyResult {
    private int total;
    private int pages;
    private int pageSize;
    private List<LockKey> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<LockKey> getList() {
        return list;
    }

    public void setList(List<LockKey> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "KeyResult{" +
                "total=" + total +
                ", pages=" + pages +
                ", pageSize=" + pageSize +
                ", list=" + list +
                '}';
    }
}
