package cn.jcyh.eaglekinglockdemo.http;

import java.util.List;

/**
 * Created by jogger on 2018/5/12.
 */

public class HttpResult<T> {
    private List<T> list;
    private int total;
    private int pages;
    private int pageSize;

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

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "list=" + list +
                ", total=" + total +
                ", pages=" + pages +
                ", pageSize=" + pageSize +
                '}';
    }
}
