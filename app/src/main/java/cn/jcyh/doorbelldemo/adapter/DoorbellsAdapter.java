package cn.jcyh.doorbelldemo.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.jcyh.doorbelldemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.entity.Doorbell;

/**
 * Created by jogger on 2018/7/3.
 */
public class DoorbellsAdapter extends BaseQuickAdapter<Doorbell, BaseViewHolder> {
    public DoorbellsAdapter(@Nullable List<Doorbell> data) {
        super(R.layout.rv_doorbells_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Doorbell item) {
        helper.setText(R.id.tv_doorbell_name, item.getDeviceName());
        helper.setText(R.id.tv_doorbell_id, item.getDeviceId());
        boolean online = ControlCenter.getDoorbellManager().isOnline(item.getDeviceId());
        helper.setText(R.id.tv_doorbell_state, online ? R.string.online
                : R.string.off_line);
        helper.getView(R.id.tv_doorbell_state).setSelected(online);
        helper.setTextColor(R.id.tv_doorbell_name, online ? mContext.getResources().getColor(R.color
                .black_333333) : mContext.getResources
                ().getColor(R.color.gray_888888));
    }

}
