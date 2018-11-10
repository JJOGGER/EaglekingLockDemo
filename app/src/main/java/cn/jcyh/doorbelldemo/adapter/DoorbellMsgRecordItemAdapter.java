package cn.jcyh.doorbelldemo.adapter;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.entity.DoorbellMsgRecord;
import cn.jcyh.utils.ScreenUtil;
import cn.jcyh.utils.Utils;


/**
 * Created by Jogger on 2018/2/12.
 * 图片消息记录
 */

public class DoorbellMsgRecordItemAdapter extends BaseQuickAdapter<DoorbellMsgRecord, BaseViewHolder> {
    private boolean mIsChoose;
    private final View mHeader;
    private DoorbellRecordItemListener mListener;
    private boolean mIsChooseAll;

    public DoorbellMsgRecordItemAdapter(@Nullable final List<DoorbellMsgRecord> data) {
        super(R.layout.rv_record_item_item, data);
        mHeader = LayoutInflater.from(Utils.getApp()).inflate(R.layout.rv_record_item_item_header,
                null);
        mHeader.findViewById(R.id.tv_choose_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsChooseAll = !mIsChooseAll;
                if (mData == null) return;
                for (int i = 0; i < mData.size(); i++) {
                    mData.get(i).setSelect(mIsChooseAll);
                }
                notifyDataSetChanged();
            }
        });
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(mContext, 46));
        mHeader.setLayoutParams(layoutParams);
        addHeaderView(mHeader);
    }

    public void setDoorbellRecordItemListener(DoorbellRecordItemListener listener) {
        mListener = listener;
    }

    public boolean isCanDelete() {
        if (mData == null) return false;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).isSelect()) {
                return true;
            }
        }
        return false;
    }

    public void isShowChoose(boolean isChoose) {
        mIsChoose = isChoose;
        TextView tvChooseAll =  mHeader.findViewById(R.id.tv_choose_all);
        tvChooseAll.setVisibility(isChoose ? View.VISIBLE : View.GONE);
        notifyDataSetChanged();
    }

    public void isChooseAll(boolean isChooseAll) {
        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).setSelect(isChooseAll);
        }
        notifyDataSetChanged();
    }

    void deleteChoosedItem() {
        for (Iterator iter = mData.iterator(); iter.hasNext(); ) {
            if (((DoorbellMsgRecord) iter.next()).isSelect())
                iter.remove();
        }
        notifyDataSetChanged();
    }

    public List<DoorbellMsgRecord> getChoosedItem() {
        List<DoorbellMsgRecord> recordList = new ArrayList<>();
        for (DoorbellMsgRecord doorbellRecord : mData) {
            if (doorbellRecord.isSelect()) {
                recordList.add(doorbellRecord);
            }
        }
        return recordList;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final DoorbellMsgRecord item) {
        helper.setText(R.id.tv_time, item.getTime());
        helper.setText(R.id.tv_title, item.getTitle());
        ((TextView) mHeader.findViewById(R.id.tv_date)).setText(item.getDate());
        Glide.with(mContext)
                .load(item.getImageUrl())
                .error(R.mipmap.peephole_push_photo_ph)
                .into((ImageView) helper.getView(R.id.iv_img));
        helper.setText(R.id.tv_content, item.getContent());
        helper.getView(R.id.cb_select).setVisibility(mIsChoose ? View.VISIBLE : View.GONE);
//        helper.setVisible(R.id.cb_select,mIsChoose)
//        item.setSelect(mIsChooseAll);
        helper.setChecked(R.id.cb_select, item.isSelect());
        helper.setOnClickListener(R.id.rl_item, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsChoose) {
                    boolean checked = ((CheckBox) helper.getView(R.id.cb_select))
                            .isChecked();
                    helper.setChecked(R.id.cb_select, !checked);
                    item.setSelect(!checked);
                } else {
                    mListener.onItemClick(item, helper.getAdapterPosition() - 1);
                }
//                mListener.isCanDelete(isCanDelete());
            }
        });
//        mListener.isCanDelete(isCanDelete());
    }
}
