package cn.jcyh.doorbelldemo.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import cn.jcyh.doorbelldemo.base.BaseRecordPagerAdapter;
import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.doorbelldemo.function.ui.fragment.DoorbellRecordImgFragment;
import cn.jcyh.doorbelldemo.function.ui.fragment.DoorbellRecordMsgFragment;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.entity.Doorbell;


public class DoorbellRecordPagerAdapter extends BaseRecordPagerAdapter {
    public DoorbellRecordPagerAdapter(FragmentManager fm, Context context, Doorbell doorbell) {
        super(fm, context, doorbell, new int[]{R.string.msg_record, R.string.img_record});
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.POSITION, position);
        bundle.putSerializable(Constant.DOORBELL, mDoorbell);
        switch (position) {
            case 0:
                DoorbellRecordMsgFragment doorbellRecordMsgFragment = new
                        DoorbellRecordMsgFragment();
                doorbellRecordMsgFragment.setArguments(bundle);
                return doorbellRecordMsgFragment;
            case 1:
                DoorbellRecordImgFragment doorbellRecordImgFragment = new
                        DoorbellRecordImgFragment();
                doorbellRecordImgFragment.setArguments(bundle);
                return doorbellRecordImgFragment;
        }
        return null;
    }
}
