package cn.jcyh.doorbelldemo.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.jcyh.doorbelldemo.bean.DoorbellImgRecord;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.entity.DoorbellMsgRecord;
import cn.jcyh.utils.ScreenUtil;
import cn.jcyh.utils.Utils;

/**
 * Created by Jogger on 2018/2/12.
 */

public class DoorbellImgRecordItemAdapter extends BaseQuickAdapter<DoorbellImgRecord,
        BaseViewHolder> {
    private boolean mIsChoose;
    private final View mHeader;
    private List<DoorbellImgRecord> mDoorbellImgRecords;
    private DoorbellRecordItemListener mListener;
    private boolean mIsChooseAll;


    public DoorbellImgRecordItemAdapter(@Nullable List<DoorbellImgRecord> data) {
        super(R.layout.rv_img_record_item_item, data);
        mDoorbellImgRecords = data;
        mHeader = LayoutInflater.from(Utils.getApp()).inflate(R.layout.rv_record_item_item_header,
                null);
        mHeader.findViewById(R.id.tv_choose_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsChooseAll = !mIsChooseAll;
                for (int i = 0; i < mDoorbellImgRecords.size(); i++) {
                    mDoorbellImgRecords.get(i).setSelect(mIsChooseAll);
                }
                notifyDataSetChanged();
            }
        });
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(mContext, 46));
        mHeader.setLayoutParams(layoutParams);
        addHeaderView(mHeader);
    }

    public void setDoorbellRecordItemListener(DoorbellRecordItemListener listener) {
        mListener = listener;
    }

    public boolean isCanDelete() {
        for (int i = 0; i < mDoorbellImgRecords.size(); i++) {
            if (mDoorbellImgRecords.get(i).isSelect()) {
                return true;
            }
        }
        return false;
    }

    public void isShowChoose(boolean isChoose) {
        mIsChoose = isChoose;
        TextView tvChooseAll = mHeader.findViewById(R.id.tv_choose_all);
        tvChooseAll.setVisibility(isChoose ? View.VISIBLE : View.GONE);
        notifyDataSetChanged();
    }

    public void isChooseAll(boolean isChooseAll) {
        for (int i = 0; i < mDoorbellImgRecords.size(); i++) {
            mDoorbellImgRecords.get(i).setSelect(isChooseAll);
        }
        notifyDataSetChanged();
    }

    void deleteChoosedItem() {
        for (Iterator iter = mDoorbellImgRecords.iterator(); iter.hasNext(); ) {
            if (((DoorbellMsgRecord) iter.next()).isSelect())
                iter.remove();
        }
        notifyDataSetChanged();
    }

    public List<DoorbellImgRecord> getChoosedItem() {
        List<DoorbellImgRecord> recordList = new ArrayList<>();
        for (DoorbellImgRecord doorbellRecord : mDoorbellImgRecords) {
            if (doorbellRecord.isSelect()) {
                recordList.add(doorbellRecord);
            }
        }
        return recordList;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final DoorbellImgRecord item) {
        ((TextView) mHeader.findViewById(R.id.tv_date)).setText(item.getDate());
        Glide.with(mContext)
                .load(item.getFilePath())
                .into((ImageView) helper.getView(R.id.iv_img));
//        helper.getView(R.id.iv_checked).setVisibility(mIsChoose ? ServerView.VISIBLE : ServerView.GONE);
//        helper.setVisible(R.id.cb_select,mIsChoose)
//        item.setSelect(mIsChooseAll);
        helper.setVisible(R.id.iv_type, item.getFilePath().endsWith(".mp4"));
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) helper
                .getView(R.id.rl_item).getLayoutParams();
        final int adapterPosition = helper.getAdapterPosition();
        if (adapterPosition % 3 != 1) {
            layoutParams.leftMargin = ScreenUtil.dip2px(mContext, 2);
        }
        if (adapterPosition % 3 == 1) {
            layoutParams.leftMargin = ScreenUtil.dip2px(mContext, 16);
        }
        if (adapterPosition % 3 == 0) {
            layoutParams.rightMargin = ScreenUtil.dip2px(mContext, 16);
        }
        if (adapterPosition > 3) {
            layoutParams.topMargin = ScreenUtil.dip2px(mContext, 3);
        }
        helper.getView(R.id.rl_item).setLayoutParams(layoutParams);
        if (mIsChoose) {
            helper.getView(R.id.iv_checked).setVisibility(item.isSelect() ? View.VISIBLE : View
                    .GONE);
        } else {
            helper.getView(R.id.iv_checked).setVisibility(View.GONE);
        }
        helper.setOnClickListener(R.id.rl_item, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsChoose) {
                    boolean checked = helper.getView(R.id.iv_checked).getVisibility()
                            == View.VISIBLE;
                    item.setSelect(!checked);
                    helper.getView(R.id.iv_checked).setVisibility(!checked ? View.VISIBLE : View
                            .GONE);
                } else {
                    if (mListener != null)
                        mListener.onItemClick(item, adapterPosition - 1);
                }
            }
        });
    }
}
