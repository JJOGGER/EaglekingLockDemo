package cn.jcyh.doorbelldemo.function.model;


import java.util.List;

import cn.jcyh.doorbelldemo.function.contract.DoorbellParamsContract;
import cn.jcyh.eaglekinglockdemo.http.DoorbellHttpAction;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.nimlib.entity.BindDoorbellUser;
import cn.jcyh.nimlib.entity.DoorbellRequest;
import cn.jcyh.nimlib.entity.SetAdminRequest;
import cn.jcyh.nimlib.entity.UserDoorbellRequest;

/**
 * Created by jogger on 2018/7/6.
 */
public class DoorbellParamsModel implements DoorbellParamsContract.Model {

    @Override
    public void delDevice(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener<Boolean> listener) {
        DoorbellHttpAction.getHttpAction().unbindDoorbell(userDoorbellRequest, listener);
    }

    @Override
    public void getBindUsers(DoorbellRequest doorbellRequest, OnHttpRequestListener<List<BindDoorbellUser>> listener) {
        DoorbellHttpAction.getHttpAction().getBindDoorbellUsers(doorbellRequest,listener);
    }

    @Override
    public void setAdminUnbindDoorbell(SetAdminRequest setAdminRequest, OnHttpRequestListener<Boolean> listener) {
        DoorbellHttpAction.getHttpAction().setAdminUnbindDoorbell(setAdminRequest,listener);
    }

    @Override
    public void adminUnbind(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener<Boolean> listener) {
        DoorbellHttpAction.getHttpAction().adminUnbindDoorbell(userDoorbellRequest,listener);
    }
}
