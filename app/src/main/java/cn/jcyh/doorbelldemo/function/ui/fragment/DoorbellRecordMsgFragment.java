package cn.jcyh.doorbelldemo.function.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.doorbelldemo.adapter.DoorbellMsgRecordAdapter;
import cn.jcyh.doorbelldemo.adapter.DoorbellMsgRecordItemAdapter;
import cn.jcyh.doorbelldemo.adapter.DoorbellRecordItemListener;
import cn.jcyh.doorbelldemo.base.BaseRecordFragment;
import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.doorbelldemo.function.contract.DoorbellRecordMsgContract;
import cn.jcyh.doorbelldemo.function.presenter.DoorbellRecordMsgPresenter;
import cn.jcyh.doorbelldemo.function.ui.DoorbellRecordActivity;
import cn.jcyh.doorbelldemo.function.ui.MsgRecordDescActivity;
import cn.jcyh.doorbelldemo.widget.MyLinearLayoutManager;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.entity.Doorbell;
import cn.jcyh.nimlib.entity.DoorbellMsgRecord;
import cn.jcyh.utils.L;
import cn.jcyh.utils.T;

/**
 * Created by jogger on 2018/2/9.
 */

public class DoorbellRecordMsgFragment extends BaseRecordFragment<DoorbellRecordMsgPresenter> implements DoorbellRecordMsgContract.View, BaseQuickAdapter
        .RequestLoadMoreListener {
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srlRefresh;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    @BindView(R.id.iv_none)
    ImageView ivNone;
    @BindView(R.id.fl_bottom)
    FrameLayout flBottom;
    private DoorbellMsgRecordAdapter mRecordAdapter;
    private List<DoorbellMsgRecordItemAdapter> mAdapters;
    private boolean mIsChooseAll;
    private boolean mIsLoading;
    @SuppressLint("StaticFieldLeak")
    private static  DoorbellRecordMsgFragment sMsgFragment;
    private Doorbell mDoorbell;

    public static DoorbellRecordMsgFragment getDoorbellRecordMsgFragment(Bundle bundle) {
        if (sMsgFragment == null) {
            sMsgFragment = new DoorbellRecordMsgFragment();
            if (bundle != null)
                sMsgFragment.setArguments(bundle);
        }
        return sMsgFragment;
    }

    @Override
    public void init() {
        super.init();
        mAdapters = new ArrayList<>();
        mRecordAdapter = new DoorbellMsgRecordAdapter(mAdapters);
        rvContent.setLayoutManager(new MyLinearLayoutManager(mActivity));
        rvContent.setAdapter(mRecordAdapter);
        mRecordAdapter.setOnLoadMoreListener(this, rvContent);
        srlRefresh.setOnRefreshListener(this);
        //请求列表
    }

    @Override
    public boolean existDatas() {
        return mRecordAdapter != null && mRecordAdapter.getData().size() > 0;
    }


    @Override
    public void deleteChoose() {
        mPresenter.deleteChooseDatas(mAdapters);
    }


    @Override
    protected DoorbellRecordMsgPresenter createPresenter() {
        Bundle arguments = getArguments();
        if (arguments == null) return null;
        mDoorbell = (Doorbell) arguments.getSerializable(Constant.DOORBELL);
        return new DoorbellRecordMsgPresenter();
    }

    @Override
    public void loadData() {
        mPresenter.getDoorbellMsgRecords();
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


    @Override
    public boolean isScrolling(boolean isChoose) {
        boolean isScrolling = super.isScrolling(isChoose);
        if (!isScrolling) mRecordAdapter.setShowChoose(isChoose);
        return isScrolling;
    }

    @Override
    public void onLoadMoreRequested() {
        if (mIsLoading) return;
        mIsLoading = true;
        rvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPresenter.isHasNext())
                    mPresenter.getMoreDatas();
                else {
                    mRecordAdapter.loadMoreEnd();
                }
            }
        }, 100);
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public Doorbell getDoorbell() {
        return mDoorbell;
    }

    @Override
    public void getDoorbellMsgRecordsSuccess(final List<List<DoorbellMsgRecord>> doorbellMsgRecords) {
        mAdapters.clear();
        L.e("--------------doorbellMsgRecords:" + doorbellMsgRecords);
        if (doorbellMsgRecords != null && doorbellMsgRecords.size() != 0) {
            for (int i = 0; i < doorbellMsgRecords.size(); i++) {
                final List<DoorbellMsgRecord> records =
                        doorbellMsgRecords.get(i);
                DoorbellMsgRecordItemAdapter adapter = new
                        DoorbellMsgRecordItemAdapter(records);
                adapter.setDoorbellRecordItemListener(new DoorbellRecordItemListener() {
                    @Override
                    public void onItemClick(Object doorbellImgRecord, int pos) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(Constant.DOORBELL_MSG_RECORDS,
                                (ArrayList<? extends Parcelable>)
                                        records);
                        bundle.putInt(Constant.INDEX, pos);
                        startNewActivity(MsgRecordDescActivity.class,
                                bundle);
                    }
                });
                mAdapters.add(adapter);
            }
            mRecordAdapter.setNewData(mAdapters);
            ivNone.setVisibility(View.GONE);
            srlRefresh.setVisibility(View.VISIBLE);
        }
        if (mAdapters.size() > 0) {
            if (getUserVisibleHint()) {
                ((DoorbellRecordActivity) mActivity).setIsChoose(false);
            }
        } else {
            if (getUserVisibleHint()) {
                ((DoorbellRecordActivity) mActivity).setIsChoose(false, View.GONE);
            }
            ivNone.setVisibility(View.VISIBLE);
        }
        if (srlRefresh != null && srlRefresh.isRefreshing()) {
            srlRefresh.setRefreshing(false);
        }
        flBottom.setVisibility(View.GONE);
        mIsLoading = false;
        setChoose(false);
        mRecordAdapter.loadMoreComplete();
    }

    @Override
    public void getMoreDatasSuccess(List<List<DoorbellMsgRecord>> doorbellMsgRecords) {
        if (doorbellMsgRecords != null && doorbellMsgRecords.size() != 0) {
            for (int i = 0; i < doorbellMsgRecords.size(); i++) {
                final List<DoorbellMsgRecord> records =
                        doorbellMsgRecords.get(i);
                DoorbellMsgRecordItemAdapter adapter = new
                        DoorbellMsgRecordItemAdapter(records);
                adapter.setDoorbellRecordItemListener(new DoorbellRecordItemListener() {
                    @Override
                    public void onItemClick(Object doorbellImgRecord, int pos) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(Constant.DOORBELL_MSG_RECORDS,
                                (ArrayList<? extends Parcelable>)
                                        records);
                        bundle.putInt(Constant.INDEX, pos);
                        startNewActivity(MsgRecordDescActivity.class,
                                bundle);
                    }
                });
                mAdapters.add(adapter);
            }
            mRecordAdapter.setNewData(mAdapters);
            mRecordAdapter.loadMoreComplete();
        } else {
            mRecordAdapter.loadMoreEnd();
        }
        if (mRecordAdapter.getData().size() == 0) {
            if (getUserVisibleHint()) {
                ((DoorbellRecordActivity) mActivity).setIsChoose(false, View.GONE);
            }
        } else {
            if (getUserVisibleHint()) {
                ((DoorbellRecordActivity) mActivity).setIsChoose(false);
            }
        }
        if (srlRefresh != null && srlRefresh.isRefreshing()) {
            srlRefresh.setRefreshing(false);
        }
        mIsLoading = false;
    }

    @Override
    public void getMoreDatasFail() {
        mRecordAdapter.loadMoreFail();
        if (srlRefresh != null && srlRefresh.isRefreshing()) {
            srlRefresh.setRefreshing(false);
        }
    }

    @Override
    public void deleteDoorbellMsgRecordsSuccess() {
        loadData();
    }
}
