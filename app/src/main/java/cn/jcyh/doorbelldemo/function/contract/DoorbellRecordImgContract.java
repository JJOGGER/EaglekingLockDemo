package cn.jcyh.doorbelldemo.function.contract;


import java.util.List;

import cn.jcyh.doorbelldemo.adapter.DoorbellImgRecordItemAdapter;
import cn.jcyh.doorbelldemo.base.BaseModel;
import cn.jcyh.doorbelldemo.base.BaseView;
import cn.jcyh.doorbelldemo.base.IPresenter;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.nimlib.entity.Doorbell;

/**
 * Created by jogger on 2018/7/6.
 */
public interface DoorbellRecordImgContract {
    interface Model extends BaseModel {

        void getDoorbellRecordImgs(String deviceID, int page, int pageSize, OnHttpRequestListener<List<DoorbellImgRecordItemAdapter>> listener);
    }

    interface View extends BaseView {
        Doorbell getDoorbell();
        void getDoorbellRecordImgsSuccess(List<DoorbellImgRecordItemAdapter> adapters);

        void getDoorbellRecordImgsFail();

        void getMoreImgsSuccess(List<DoorbellImgRecordItemAdapter> adapters);

        void getMoreImgsFail();

        void deleteChooseDatasSuccess();
    }

    interface Presenter extends IPresenter<View, Model> {
        void getDoorbellRecordImgs();

        void getMoreImgs();

        void deleteChoose(List<DoorbellImgRecordItemAdapter> doorbellImgRecords);
    }
}
