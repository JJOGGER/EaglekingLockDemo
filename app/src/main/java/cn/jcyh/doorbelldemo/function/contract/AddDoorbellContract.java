package cn.jcyh.doorbelldemo.function.contract;


import cn.jcyh.doorbelldemo.base.BaseModel;
import cn.jcyh.doorbelldemo.base.BaseView;
import cn.jcyh.doorbelldemo.base.IPresenter;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.nimlib.entity.UserDoorbellRequest;

/**
 * Created by jogger on 2018/7/3.
 */
public interface AddDoorbellContract {
    interface Model extends BaseModel {
        void bindDoorbell(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener<Boolean> listener);
    }

    interface View extends BaseView {
        String getDeviceID();

        void addDoorbellSuccess();
    }

    interface Presenter extends IPresenter<View, Model> {
        void sendBindRequest();
    }
}
