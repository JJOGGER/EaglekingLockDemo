package cn.jcyh.doorbelldemo.function.contract;


import java.util.List;

import cn.jcyh.doorbelldemo.base.BaseModel;
import cn.jcyh.doorbelldemo.base.BaseView;
import cn.jcyh.doorbelldemo.base.IPresenter;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.nimlib.entity.BindDoorbellUser;
import cn.jcyh.nimlib.entity.Doorbell;
import cn.jcyh.nimlib.entity.DoorbellModelParam;
import cn.jcyh.nimlib.entity.DoorbellRequest;
import cn.jcyh.nimlib.entity.DoorbellSensorParam;
import cn.jcyh.nimlib.entity.SetAdminRequest;
import cn.jcyh.nimlib.entity.UserDoorbellRequest;

/**
 * Created by jogger on 2018/7/6.
 */
public interface DoorbellParamsContract {
    interface Model extends BaseModel {

        void delDevice(UserDoorbellRequest userDoorbellRequest, final OnHttpRequestListener<Boolean> listener);

        void getBindUsers(DoorbellRequest doorbellRequest, OnHttpRequestListener<List<BindDoorbellUser>> listener);

        void setAdminUnbindDoorbell(SetAdminRequest setAdminRequest, OnHttpRequestListener<Boolean> listener);

        void adminUnbind(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener<Boolean> listener);
    }

    interface View extends BaseView {
        Doorbell getDoorbell();

        void getBindUsersSuccess(List<BindDoorbellUser> bindDoorbellUsers);

        void getBindUsersFail();

        void userUnbindSuccess();

        void appointAdminAndUnbindSuccess();

        void adminUnbindSuccess();

        void getModeParamsSuccess(DoorbellModelParam doorbellModelParam);

        void getSensorParamsSuccess(DoorbellSensorParam doorbellSensorParam);

    }

    interface Presenter extends IPresenter<View, Model> {
        void getBindUsers();

        void userUnbind();

        void appointAdmin(BindDoorbellUser bindDoorbellUser);

        void adminUnbind();

        void getDoorbellParams();
    }
}
