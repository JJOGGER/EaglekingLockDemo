package cn.jcyh.doorbelldemo.avchat;

/**
 * Created by winnie on 2017/12/10.
 */

public interface AVChatControllerCallback<T> {

    void onSuccess(T t);

    void onFailed(int code, String errorMsg);
}
