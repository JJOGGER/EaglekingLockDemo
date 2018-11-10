package cn.jcyh.doorbelldemo.function.model;


import cn.jcyh.doorbelldemo.function.contract.AddDoorbellContract;
import cn.jcyh.eaglekinglockdemo.http.DoorbellHttpAction;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.nimlib.entity.UserDoorbellRequest;

/**
 * Created by jogger on 2018/7/3.
 */
public class AddDoorbellModel implements AddDoorbellContract.Model {
    @Override
    public void bindDoorbell(UserDoorbellRequest userDoorbellRequest, OnHttpRequestListener<Boolean> listener) {
        DoorbellHttpAction.getHttpAction().bindDoorbell(userDoorbellRequest, listener);
    }
}
