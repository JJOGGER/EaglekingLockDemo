package cn.jcyh.doorbelldemo.function.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.doorbelldemo.adapter.DoorbellImgRecordAdapter;
import cn.jcyh.doorbelldemo.adapter.DoorbellImgRecordItemAdapter;
import cn.jcyh.doorbelldemo.base.BaseRecordFragment;
import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.doorbelldemo.function.contract.DoorbellRecordImgContract;
import cn.jcyh.doorbelldemo.function.presenter.DoorbellRecordImgPresenter;
import cn.jcyh.doorbelldemo.function.ui.DoorbellRecordActivity;
import cn.jcyh.doorbelldemo.widget.MyLinearLayoutManager;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.entity.Doorbell;
import cn.jcyh.utils.L;
import cn.jcyh.utils.T;

/**
 * Created by jogger on 2018/2/9.
 * 影像记录
 */

public class DoorbellRecordImgFragment extends BaseRecordFragment<DoorbellRecordImgPresenter> implements DoorbellRecordImgContract.View, BaseQuickAdapter.RequestLoadMoreListener {
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    @BindView(R.id.iv_none)
    ImageView ivNone;
    @BindView(R.id.fl_bottom)
    FrameLayout flBottom;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srlRefresh;
    private boolean mIsLoading;
    private DoorbellImgRecordAdapter mRecordAdapter;
    private boolean mIsChooseAll;
    @SuppressLint("StaticFieldLeak")
    private static DoorbellRecordImgFragment sImgFragment;
    private Doorbell mDoorbell;

    public static DoorbellRecordImgFragment getDoorbellRecordImgFragment(Bundle bundle) {
        if (sImgFragment == null) {
            sImgFragment = new DoorbellRecordImgFragment();
            if (bundle != null)
                sImgFragment.setArguments(bundle);
        }
        return sImgFragment;
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_doorbell_record_msg;
    }

    @Override
    public void init() {
        super.init();
        mRecordAdapter = new DoorbellImgRecordAdapter(null);
        rvContent.setLayoutManager(new MyLinearLayoutManager(mActivity));
        rvContent.setAdapter(mRecordAdapter);
        mRecordAdapter.setOnLoadMoreListener(this, rvContent);
        rvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                srlRefresh.setEnabled(topRowVerticalPosition >= 0);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @Override
    protected DoorbellRecordImgPresenter createPresenter() {
        if (getArguments() == null) return null;
        mDoorbell = (Doorbell) getArguments().getSerializable(Constant.DOORBELL);
        return new DoorbellRecordImgPresenter();
    }

    @Override
    public void loadData() {
        mPresenter.getDoorbellRecordImgs();
    }


    @Override
    public Doorbell getDoorbell() {
        return mDoorbell;
    }

    @Override
    public void getDoorbellRecordImgsSuccess(List<DoorbellImgRecordItemAdapter> adapters) {
        ivNone.setVisibility(View.GONE);
        if (getUserVisibleHint())
            ((DoorbellRecordActivity) mActivity).setIsChoose(false, adapters.size() == 0 ? View.GONE : View.VISIBLE);
        flBottom.setVisibility(View.GONE);
        mRecordAdapter.setNewData(adapters);
        mRecordAdapter.isChooseAll(false);
        mRecordAdapter.isShowChoose(false);
        if (srlRefresh.isRefreshing()) {
            srlRefresh.setRefreshing(false);
        }
        setChoose(false);
    }

    @Override
    public boolean existDatas() {
        return mRecordAdapter != null && mRecordAdapter.getData().size() > 0;
    }


    @Override
    public void getDoorbellRecordImgsFail() {
        L.e("----------getDoorbellRecordImgsFail");
        ivNone.setVisibility(View.VISIBLE);
        if (getUserVisibleHint())
            ((DoorbellRecordActivity) mActivity).setIsChoose(false, View.GONE);
        if (srlRefresh != null && srlRefresh.isRefreshing())
            srlRefresh.setRefreshing(false);

    }

    @Override
    public void getMoreImgsSuccess(List<DoorbellImgRecordItemAdapter> adapters) {
        L.e("----------getMoreImgsSuccess");
        if (adapters != null)
            mRecordAdapter.addData(adapters);
        mIsLoading = false;
        mRecordAdapter.loadMoreComplete();
    }

    @Override
    public void getMoreImgsFail() {
        L.e("----------getMoreImgsFail");
        mRecordAdapter.loadMoreEnd();
        mIsLoading = false;
    }

    @Override
    public void deleteChooseDatasSuccess() {
        loadData();
    }

    @Override
    public void onLoadMoreRequested() {
        L.e("----------onLoadMoreRequested");
        if (mIsLoading) return;
        if (rvContent == null) return;
        mIsLoading = true;
        rvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPresenter.isHasNext())
                    mPresenter.getMoreImgs();
                else mRecordAdapter.loadMoreEnd();
            }
        }, 100);
    }

    @Override
    public void onRefresh() {
        loadData();
    }


    @OnClick({R.id.tv_delete, R.id.tv_choose_all})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_delete:
                if (mRecordAdapter.isCanDelete())
                    deleteChoose();
                else T.show(getString(R.string.current_can_not_delete));
                break;
            case R.id.tv_choose_all:
                if (rvContent.getScrollState() == RecyclerView
                        .SCROLL_STATE_IDLE && !rvContent
                        .isComputingLayout()) {
                    mIsChooseAll = !mIsChooseAll;
                    mRecordAdapter.isChooseAll(mIsChooseAll);
                }
                break;
        }
    }

    /**
     * 删除选中
     */
    @Override
    public void deleteChoose() {
        mPresenter.deleteChoose(mRecordAdapter.getData());
    }

    @Override
    public boolean isScrolling(boolean isChoose) {
        boolean isScrolling = super.isScrolling(isChoose);
        if (!isScrolling) mRecordAdapter.isShowChoose(isChoose);
        return isScrolling;
    }
}
