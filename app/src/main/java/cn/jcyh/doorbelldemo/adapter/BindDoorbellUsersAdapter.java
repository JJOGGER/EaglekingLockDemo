package cn.jcyh.doorbelldemo.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.jcyh.doorbelldemo.control.ControlCenter;
import cn.jcyh.doorbelldemo.widget.SlideDelete;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.entity.BindDoorbellUser;

/**
 * Created by jogger on 2018/1/30.
 */

public class BindDoorbellUsersAdapter extends BaseQuickAdapter<BindDoorbellUser, BaseViewHolder> {
    private List<SlideDelete> mSlideDeleteArrayList = new ArrayList<>();
    private OnItemClickCallback mListener;
    private String mUserID;
    private boolean mIsAdmin;

    public BindDoorbellUsersAdapter(@Nullable List<BindDoorbellUser> data) {
        super(R.layout.rv_bind_doorbell_users_item, data);
        mUserID = ControlCenter.getUserManager().getUser().getUserId();
        assert data != null;
        for (BindDoorbellUser user :
                data) {
            if (user.getUserId().equals(mUserID) && user.isAdmin()) {
                mIsAdmin = true;
                break;
            }
        }
    }

    public void setOnItemClickCallback(OnItemClickCallback listener) {
        mListener = listener;
    }

    public interface OnItemClickCallback {
        void onItemClick(BindDoorbellUser item);
    }

    @Override
    protected void convert(BaseViewHolder helper, final BindDoorbellUser item) {
        final SlideDelete mySlide = helper.getView(R.id.my_slide_delete);
        helper.setText(R.id.tv_account, item.getNickName()
                + " " + item.getUserName());
        if (item.isAdmin()) {
            helper.getView(R.id.iv_more).setVisibility(View.GONE);
            helper.getView(R.id.tv_admin).setVisibility(View.VISIBLE);
            mySlide.setSlide(false);
        } else {
            helper.getView(R.id.iv_more).setVisibility(mIsAdmin ? View.VISIBLE : View.GONE);
            mySlide.setSlide(mIsAdmin);
            helper.getView(R.id.tv_admin).setVisibility(View.GONE);
            helper.setOnClickListener(R.id.iv_more, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mySlide.isShowDelete(mySlide.getCurrentState() == SlideDelete.CLOSE);
                    closeOtherItem(mySlide);
                }
            });
            helper.addOnClickListener(R.id.tv_delete);
            mySlide.setOnSlideDeleteListener(new SlideDelete.OnSlideDeleteListener() {
                @Override
                public void onOpen(SlideDelete slideDelete) {
                    closeOtherItem(slideDelete);
                    mSlideDeleteArrayList.add(slideDelete);
                }

                @Override
                public void onClose(SlideDelete slideDelete) {
                    mSlideDeleteArrayList.remove(slideDelete);
                }

                @Override
                public void onClick() {
                    //如果是点击事件
                    if (mySlide.getCurrentState() == SlideDelete.OPEN) {
                        mySlide.isShowDelete(false);
                    } else {
                        if (mListener != null) {
                            mListener.onItemClick(item);
                        }
                    }
                }
            });
        }
    }

    //其他条目关闭
    private void closeOtherItem(SlideDelete current) {
        // 采用Iterator的原因是for是线程不安全的，迭代器是线程安全的
        for (SlideDelete slideDelete : mSlideDeleteArrayList) {
            if (current != slideDelete) {
                slideDelete.isShowDelete(false);
            }
        }
        mSlideDeleteArrayList.clear();
    }
}
