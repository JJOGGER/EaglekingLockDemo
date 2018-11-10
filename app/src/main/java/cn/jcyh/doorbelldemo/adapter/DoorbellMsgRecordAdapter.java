package cn.jcyh.doorbelldemo.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import cn.jcyh.doorbelldemo.widget.UnScrollLinearLayoutManager;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.http.DoorbellHttpAction;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.nimlib.entity.DoorbellMsgRecord;
import cn.jcyh.nimlib.entity.JsonDataRequest;
import cn.jcyh.utils.T;


/**
 * Created by Jogger on 2018/2/12.
 */

public class DoorbellMsgRecordAdapter extends BaseQuickAdapter<DoorbellMsgRecordItemAdapter,
        BaseViewHolder> {
    private List<DoorbellMsgRecordItemAdapter> mData;

    public DoorbellMsgRecordAdapter(@Nullable List<DoorbellMsgRecordItemAdapter> data) {
        super(R.layout.rv_record_item, data);
        mData = data;
    }

    public boolean isCanDelete() {
        boolean isCandelete = false;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).isCanDelete()) {
                isCandelete = true;
                break;
            }
        }
        return isCandelete;
    }

    public void setShowChoose(boolean isChoose) {
        for (DoorbellMsgRecordItemAdapter adapter : mData) {
            adapter.isShowChoose(isChoose);
        }
    }

    public void isChooseAll(boolean isChooseAll) {
        for (DoorbellMsgRecordItemAdapter adapter : mData) {
            adapter.isChooseAll(isChooseAll);
        }
    }

    public void deleteChoose() {
        List<DoorbellMsgRecord> recordList = new ArrayList<>();
        for (DoorbellMsgRecordItemAdapter adapter : mData) {
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
        DoorbellHttpAction.getHttpAction().deleteDoorbellMsgRecords(jsonDataRequest, new OnHttpRequestListener<Boolean>() {
            @Override
            public void onFailure(int errorCode, String desc) {
                T.show(mContext.getString(R.string.delete_failure) + errorCode);
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                for (DoorbellMsgRecordItemAdapter adapter : mData) {
                    adapter.deleteChoosedItem();
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void convert(BaseViewHolder helper, DoorbellMsgRecordItemAdapter item) {
        RecyclerView rvContent = helper.getView(R.id.rv_content);
        rvContent.setLayoutManager(new UnScrollLinearLayoutManager(mContext));
        rvContent.setAdapter(item);
    }
}
