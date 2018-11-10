package cn.jcyh.doorbelldemo.function.presenter;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import cn.jcyh.doorbelldemo.adapter.DoorbellMsgRecordItemAdapter;
import cn.jcyh.doorbelldemo.base.BasePresenter;
import cn.jcyh.doorbelldemo.control.ControlCenter;
import cn.jcyh.doorbelldemo.function.contract.DoorbellRecordMsgContract;
import cn.jcyh.doorbelldemo.function.model.DoorbellRecordMsgModel;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.nimlib.entity.DoorbellMsgRecord;
import cn.jcyh.nimlib.entity.DoorbellRecordRequest;
import cn.jcyh.nimlib.entity.DoorbellRecords;
import cn.jcyh.nimlib.entity.JsonDataRequest;
import cn.jcyh.utils.L;
import cn.jcyh.utils.T;

/**
 * Created by jogger on 2018/7/6.
 */
public class DoorbellRecordMsgPresenter extends BasePresenter<DoorbellRecordMsgContract.View, DoorbellRecordMsgContract.Model> implements DoorbellRecordMsgContract.Presenter {
    private int mPage;
    private int mTotalPage;
    private boolean mHasNext;
    private final String mUserID;

    public boolean isHasNext() {
        return mHasNext;
    }

    public DoorbellRecordMsgPresenter() {
        mUserID = ControlCenter.getUserManager().getUser().getUserId();
    }

    @Override
    public void getDoorbellMsgRecords() {
        mPage = 1;
        if (mView.getDoorbell() == null) return;
        DoorbellRecordRequest doorbellRecordRequest = new DoorbellRecordRequest(mUserID,mView.getDoorbell().getDeviceId(),mPage,20);
        mModel.getDoorbellMsgRecords(doorbellRecordRequest, new OnHttpRequestListener<DoorbellRecords>() {
            @Override
            public void onFailure(int errorCode, String desc) {
                L.e("------------errorCode" + errorCode);
                T.show(desc);
            }

            @Override
            public void onSuccess(DoorbellRecords records) {
                mTotalPage = records.getTotalPage();
                mHasNext = mPage + 1 <= mTotalPage;
                if (mView == null) return;
                mView.getDoorbellMsgRecordsSuccess(records.getRecords());
            }
        });
    }

    @Override
    public void getMoreDatas() {
        DoorbellRecordRequest doorbellRecordRequest = new DoorbellRecordRequest(mUserID,mView.getDoorbell().getDeviceId(),mPage + 1,20);
        mModel.getDoorbellMsgRecords(doorbellRecordRequest, new OnHttpRequestListener<DoorbellRecords>() {
            @Override
            public void onFailure(int errorCode, String desc) {
                if (mView == null) return;
                mView.getMoreDatasFail();
            }

            @Override
            public void onSuccess(DoorbellRecords records) {
                mPage++;
                if (mView == null) return;
                mTotalPage = records.getTotalPage();
                mHasNext = mPage + 1 <= mTotalPage;
                mView.getMoreDatasSuccess(records.getRecords());
            }
        });
    }

    @Override
    public void deleteChooseDatas(List<DoorbellMsgRecordItemAdapter> adapters) {
        List<DoorbellMsgRecord> recordList = new ArrayList<>();
        for (DoorbellMsgRecordItemAdapter adapter : adapters) {
            List<DoorbellMsgRecord> choosedItem = adapter.getChoosedItem();
            if (choosedItem != null && choosedItem.size() != 0) {
                recordList.addAll(choosedItem);
            }
        }
        Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return !"id".equals(f.getName());
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
        JsonDataRequest jsonDataRequest = new JsonDataRequest(gson.toJson(recordList));
        mModel.deleteDoorbellMsgRecords(jsonDataRequest, new OnHttpRequestListener<Boolean>() {
            @Override
            public void onFailure(int errorCode, String desc) {
                T.show(desc);
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                if (mView == null) return;
                mView.deleteDoorbellMsgRecordsSuccess();
            }
        });
    }

    @Override
    public DoorbellRecordMsgContract.Model attacheModel() {
        return new DoorbellRecordMsgModel();
    }
}
