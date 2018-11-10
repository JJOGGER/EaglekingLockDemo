package cn.jcyh.doorbelldemo.function.presenter;

import android.os.Handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.jcyh.doorbelldemo.adapter.DoorbellImgRecordItemAdapter;
import cn.jcyh.doorbelldemo.base.BasePresenter;
import cn.jcyh.doorbelldemo.bean.DoorbellImgRecord;
import cn.jcyh.doorbelldemo.function.contract.DoorbellRecordImgContract;
import cn.jcyh.doorbelldemo.function.model.DoorbellRecordImgModel;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.utils.T;

/**
 * Created by jogger on 2018/7/6.
 */
public class DoorbellRecordImgPresenter extends BasePresenter<DoorbellRecordImgContract.View, DoorbellRecordImgContract.Model> implements DoorbellRecordImgContract.Presenter {
    private int mPage, mPageSize = 5;
    private boolean mHasNext;

    public boolean isHasNext() {
        return mHasNext;
    }

    @Override
    public void getDoorbellRecordImgs() {
        mPage = 1;
        mModel.getDoorbellRecordImgs(mView.getDoorbell().getDeviceId(), mPage, mPageSize, new OnHttpRequestListener<List<DoorbellImgRecordItemAdapter>>() {
            @Override
            public void onFailure(int errorCode, String desc) {
                if (mView == null) return;
                mView.getDoorbellRecordImgsFail();
                mHasNext = false;
            }

            @Override
            public void onSuccess(List<DoorbellImgRecordItemAdapter> adapters) {
                if (mView == null) return;
                mView.cancelProgressDialog();
                mView.getDoorbellRecordImgsSuccess(adapters);
                mHasNext = true;
            }
        });
    }

    @Override
    public void getMoreImgs() {
        mModel.getDoorbellRecordImgs(mView.getDoorbell().getDeviceId(),mPage + 1, mPageSize, new OnHttpRequestListener<List<DoorbellImgRecordItemAdapter>>() {
            @Override
            public void onFailure(int errorCode, String desc) {
                if (mView == null) return;
                mView.getMoreImgsFail();
                mHasNext = false;
            }

            @Override
            public void onSuccess(List<DoorbellImgRecordItemAdapter> adapters) {
                if (mView == null) return;
                mPage++;
                mHasNext = true;
                mView.getMoreImgsSuccess(adapters);
            }
        });
    }

    @Override
    public void deleteChoose(List<DoorbellImgRecordItemAdapter> doorbellImgRecords) {
        final List<DoorbellImgRecord> recordList = new ArrayList<>();
        for (DoorbellImgRecordItemAdapter adapter : doorbellImgRecords) {
            List<DoorbellImgRecord> choosedItem = adapter.getChoosedItem();
            if (choosedItem != null && choosedItem.size() != 0) {
                recordList.addAll(choosedItem);
            }
        }
        if (recordList.size() == 0) return;
        mView.showProgressDialog();
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Iterator<DoorbellImgRecord> iterator = recordList.iterator(); iterator.hasNext(); ) {
                    DoorbellImgRecord doorbellImgRecord = iterator.next();
                    File file = new File(doorbellImgRecord.getFilePath());
                    file.delete();
                    iterator.remove();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mView == null) return;
                        mView.cancelProgressDialog();
                        T.show(R.string.delete_succ);
                        mView.deleteChooseDatasSuccess();
                    }
                });
            }
        }).start();
    }

    @Override
    public DoorbellRecordImgContract.Model attacheModel() {
        return new DoorbellRecordImgModel();
    }
}
