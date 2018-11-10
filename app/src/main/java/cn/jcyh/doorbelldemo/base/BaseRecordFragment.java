package cn.jcyh.doorbelldemo.base;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.entity.Doorbell;

/**
 * Created by jogger on 2018/6/5.
 */

public abstract class BaseRecordFragment<T extends BasePresenter> extends BaseFragment<T> implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    @BindView(R.id.iv_none)
    ImageView ivNone;
    @BindView(R.id.tv_choose_all)
    TextView tvChooseAll;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.fl_bottom)
    FrameLayout flBottom;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srlRefresh;
    public Doorbell mDoorbell;
    private boolean mIsChoose;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_doorbell_record_msg;
    }

    @Override
    protected abstract T createPresenter();

    @Override
    public void init() {
        super.init();
        srlRefresh.setOnRefreshListener(this);
    }

    public abstract void deleteChoose();

    public abstract boolean existDatas();

    public boolean isChoose() {
        return mIsChoose;
    }

    public void setChoose(boolean choose) {
        mIsChoose = choose;
    }

    public boolean isScrolling(boolean isChoose) {
        if (rvContent.getScrollState() == RecyclerView
                .SCROLL_STATE_IDLE && !rvContent
                .isComputingLayout()) {
            flBottom.setVisibility(isChoose ? View.VISIBLE : View.GONE);
            mIsChoose = isChoose;
            return false;
        }
        return true;
    }
}
