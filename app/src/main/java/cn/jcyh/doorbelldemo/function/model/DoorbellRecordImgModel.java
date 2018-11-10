package cn.jcyh.doorbelldemo.function.model;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jcyh.doorbelldemo.adapter.DoorbellImgRecordItemAdapter;
import cn.jcyh.doorbelldemo.adapter.DoorbellRecordItemListener;
import cn.jcyh.doorbelldemo.bean.DoorbellImgRecord;
import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.doorbelldemo.function.contract.DoorbellRecordImgContract;
import cn.jcyh.doorbelldemo.function.ui.ImgRecordDescActivity;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.utils.FileUtil;
import cn.jcyh.utils.Utils;

/**
 * Created by jogger on 2018/7/6.
 */
public class DoorbellRecordImgModel implements DoorbellRecordImgContract.Model {


    @Override
    public void getDoorbellRecordImgs(String deviceID,int page, int pageSize, OnHttpRequestListener<List<DoorbellImgRecordItemAdapter>> listener) {
        String baseRecordFileSrc = FileUtil.getDoorBellRecordFileSrc(deviceID);
        if (TextUtils.isEmpty(baseRecordFileSrc)) {
            listener.onFailure(-1,"");
            return;
        }
        File file = new File(baseRecordFileSrc);
        if (!file.exists() || file.list() == null || file.list().length == 0) {
            listener.onFailure(-1,"");
            return;
        }
        if (page * pageSize - pageSize > file.list().length) {
            listener.onFailure(-1,"");
            return;
        }
        List<DoorbellImgRecordItemAdapter> adapters = new ArrayList<>();
        int index = 0;
        for (int i = page * pageSize - pageSize; i < file.list().length; i++) {//storage/emulated/0/jcyh
            File files = new File(file.getAbsoluteFile() + File.separator + file.list()[i]);
            if (!files.exists()) {
                continue;
            }
            if (files.list() == null || files.list().length == 0) {
                continue;
            }
            final List<DoorbellImgRecord> doorbellImgRecords = new ArrayList<>();
            for (int j = 0; j < files.list().length; j++) {
                if (!files.list()[j].endsWith(".jpg") && !files.list()[j].endsWith(".png") && !files.list()[j].endsWith(".mp4"))
                    continue;
                DoorbellImgRecord doorbellImgRecord = new DoorbellImgRecord();
                doorbellImgRecord.setDate(file.list()[i]);
                File imgFile = new File(files.getAbsolutePath() + File.separator + files.list()[j]);
                doorbellImgRecord.setFilePath(imgFile.getAbsolutePath());
                doorbellImgRecords.add(doorbellImgRecord);
            }
            DoorbellImgRecordItemAdapter adapter = new DoorbellImgRecordItemAdapter(doorbellImgRecords);
            adapter.setDoorbellRecordItemListener(new DoorbellRecordItemListener() {
                @Override
                public void onItemClick(Object doorbellImgRecord, int pos) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(Constant.DOORBELL_IMG_RECORDS, (ArrayList<? extends Parcelable>) doorbellImgRecords);
                    bundle.putInt(Constant.INDEX, pos);
                    Intent intent = new Intent(Utils.getApp(), ImgRecordDescActivity.class);
                    intent.putExtras(bundle);
                    Utils.getApp().startActivity(intent);
                }
            });
            adapters.add(adapter);
            index++;
            if (index == pageSize) break;
        }
        listener.onSuccess(adapters);
    }
}
