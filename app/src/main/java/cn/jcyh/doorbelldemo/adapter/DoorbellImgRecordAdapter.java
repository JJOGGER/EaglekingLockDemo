package cn.jcyh.doorbelldemo.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.jcyh.doorbelldemo.bean.DoorbellImgRecord;
import cn.jcyh.doorbelldemo.widget.CustomGridLayoutManager;
import cn.jcyh.eaglekinglockdemo.R;


/**
 * Created by Jogger on 2018/2/12.
 */

public class DoorbellImgRecordAdapter extends BaseQuickAdapter<DoorbellImgRecordItemAdapter,
        BaseViewHolder> {

    public DoorbellImgRecordAdapter( @Nullable List<DoorbellImgRecordItemAdapter> data) {
        super(R.layout.rv_record_item, data);
    }

    public boolean isCanDelete() {
        boolean isCandelete = false;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).isCanDelete()) {
                isCandelete = true;
            }
        }
        return isCandelete;
    }

    public void isShowChoose(boolean isChoose) {
        for (DoorbellImgRecordItemAdapter adapter : mData) {
            adapter.isShowChoose(isChoose);
        }
    }

    public void isChooseAll(boolean isChooseAll) {
        for (DoorbellImgRecordItemAdapter adapter : mData) {
            adapter.isChooseAll(isChooseAll);
        }
    }

    public void deleteChoose() {
        List<DoorbellImgRecord> recordList = new ArrayList<>();
        for (DoorbellImgRecordItemAdapter adapter : mData) {
            List<DoorbellImgRecord> choosedItem = adapter.getChoosedItem();
            if (choosedItem != null && choosedItem.size() != 0) {
                recordList.addAll(choosedItem);
            }
        }
//        String data = new Gson().toJson(recordList);
//        HttpAction.getHttpAction().deleteDoorbellMsgRecords(data, new
// OnHttpRequestCallback<Boolean>() {
//            @Override
//            public void onFailure(int errorCode) {
//                T.show(mContext, mContext.getString(R.string.delete_failure) +
// errorCode);
//            }
//
//            @Override
//            public void onSuccess(Boolean aBoolean) {
//                for (DoorbellImgRecordItemAdapter adapter : mAdapters) {
//                    adapter.deleteChoosedItem();
//                }
//                notifyDataSetChanged();
//            }
//        });
    }

    @Override
    protected void convert(BaseViewHolder helper, DoorbellImgRecordItemAdapter item) {
        RecyclerView rvContent = helper.getView(R.id.rv_content);
        rvContent.setLayoutManager(new CustomGridLayoutManager(mContext, 3));
        rvContent.setAdapter(item);
    }
}
