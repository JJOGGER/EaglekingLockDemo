package cn.jcyh.doorbelldemo.function.contract;


import java.util.List;

import cn.jcyh.doorbelldemo.adapter.DoorbellMsgRecordItemAdapter;
import cn.jcyh.doorbelldemo.base.BaseModel;
import cn.jcyh.doorbelldemo.base.BaseView;
import cn.jcyh.doorbelldemo.base.IPresenter;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.nimlib.entity.Doorbell;
import cn.jcyh.nimlib.entity.DoorbellMsgRecord;
import cn.jcyh.nimlib.entity.DoorbellRecordRequest;
import cn.jcyh.nimlib.entity.DoorbellRecords;
import cn.jcyh.nimlib.entity.JsonDataRequest;

/**
 * Created by jogger on 2018/7/6.
 */
public interface DoorbellRecordMsgContract {

    interface Model extends BaseModel {
        void getDoorbellMsgRecords(DoorbellRecordRequest doorbellRecordRequest, OnHttpRequestListener<DoorbellRecords> listener);

        void deleteDoorbellMsgRecords(JsonDataRequest jsonDataRequest, final OnHttpRequestListener<Boolean>
                listener);
    }

    interface View extends BaseView {
        Doorbell getDoorbell();
        void getDoorbellMsgRecordsSuccess(List<List<DoorbellMsgRecord>> doorbellMsgRecords);

        void getMoreDatasSuccess(List<List<DoorbellMsgRecord>> doorbellMsgRecords);

        void getMoreDatasFail();

        void deleteDoorbellMsgRecordsSuccess();
    }

    interface Presenter extends IPresenter<View, Model> {
        void getDoorbellMsgRecords();

        void getMoreDatas();

        void deleteChooseDatas(List<DoorbellMsgRecordItemAdapter> adapters);
    }
}
