package cn.jcyh.doorbelldemo.base;

/**
 * Created by jogger on 2018/6/9.
 */
public interface BaseView {
    int getLayoutId();

    void showProgressDialog();

    boolean isDialogShowing();

    void cancelProgressDialog();
}
