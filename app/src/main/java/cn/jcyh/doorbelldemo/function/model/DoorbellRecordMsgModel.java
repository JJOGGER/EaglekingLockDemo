package cn.jcyh.doorbelldemo.function.model;


import cn.jcyh.doorbelldemo.function.contract.DoorbellRecordMsgContract;
import cn.jcyh.eaglekinglockdemo.http.DoorbellHttpAction;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.nimlib.entity.DoorbellRecordRequest;
import cn.jcyh.nimlib.entity.DoorbellRecords;
import cn.jcyh.nimlib.entity.JsonDataRequest;

/**
 * Created by jogger on 2018/7/6.
 */
public class DoorbellRecordMsgModel implements DoorbellRecordMsgContract.Model {

    @Override
    public void getDoorbellMsgRecords(DoorbellRecordRequest doorbellRecordRequest, OnHttpRequestListener<DoorbellRecords> listener) {
        DoorbellHttpAction.getHttpAction().getDoorbellMsgRecords(doorbellRecordRequest, listener);
    }

    @Override
    public void deleteDoorbellMsgRecords(JsonDataRequest jsonDataRequest, OnHttpRequestListener<Boolean> listener) {
        DoorbellHttpAction.getHttpAction().deleteDoorbellMsgRecords(jsonDataRequest, listener);
    }
}
