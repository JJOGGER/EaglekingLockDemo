package cn.jcyh.doorbelldemo.function.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.doorbelldemo.adapter.DoorbellRecordPagerAdapter;
import cn.jcyh.doorbelldemo.base.BaseDoorbellActivity;
import cn.jcyh.doorbelldemo.base.BasePresenter;
import cn.jcyh.doorbelldemo.base.BaseRecordFragment;
import cn.jcyh.doorbelldemo.base.BaseRecordPagerAdapter;
import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.entity.Doorbell;

//门铃消息/图片记录
public class DoorbellRecordActivity extends BaseDoorbellActivity implements ViewPager.OnPageChangeListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_choose)
    TextView tvChoose;
    @BindView(R.id.tl_tab)
    TabLayout tlTab;
    @BindView(R.id.vp_content)
    ViewPager vpContent;
    private Doorbell mDoorbell;
    private boolean mIsChoose;
    private BaseRecordPagerAdapter mRecordAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_doorbell_record;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void init() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mDoorbell = (Doorbell) extras.getSerializable(Constant.DOORBELL);
        }
        mRecordAdapter = new DoorbellRecordPagerAdapter
                (getSupportFragmentManager(), this, mDoorbell);
        vpContent.setAdapter(mRecordAdapter);
        tvTitle.setText(R.string.push_info);
        vpContent.setOffscreenPageLimit(2);
        vpContent.setCurrentItem(0);
        vpContent.addOnPageChangeListener(this);
        tlTab.setupWithViewPager(vpContent);
        setIndicator();
        onPageSelected(0);
    }

    public void setIsChoose(boolean isChoose) {
        setIsChoose(isChoose, View.VISIBLE);
    }

    public void setIsChoose(boolean isChoose, int isVisiable) {
        tvChoose.setVisibility(isVisiable);
        if (isVisiable == View.GONE) {
            tvChoose.setText(R.string.choose);
            mIsChoose = false;
            tvChoose.setSelected(false);
        } else {
            if (isChoose) {
                tvChoose.setText(R.string.cancel);
                mIsChoose = true;
                tvChoose.setSelected(true);
            } else {
                tvChoose.setText(R.string.choose);
                mIsChoose = false;
                tvChoose.setSelected(false);
            }
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        assert (vpContent.getAdapter()) != null;
        BaseRecordFragment currentFragment = (BaseRecordFragment) vpContent.getAdapter().instantiateItem(vpContent, position);
        tvChoose.setVisibility(currentFragment.existDatas() ? View.VISIBLE : View.GONE);
        if (currentFragment.isChoose()) {
            tvChoose.setText(R.string.cancel);
            mIsChoose = true;
            tvChoose.setSelected(true);
        } else {
            tvChoose.setText(R.string.choose);
            mIsChoose = false;
            tvChoose.setSelected(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @OnClick({R.id.tv_title, R.id.tv_choose})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title:
                finish();
                break;
            case R.id.tv_choose:
                boolean isScrolling;
                boolean isChoose;
                if (vpContent.getCurrentItem() == 0) {
                    isChoose = mRecordAdapter.getCurrentFragment().isChoose();
                    isScrolling = mRecordAdapter.getCurrentFragment().isScrolling(!isChoose);
                    isChoose = mRecordAdapter.getCurrentFragment().isChoose();
                } else {
                    isChoose = mRecordAdapter.getCurrentFragment().isChoose();
                    isScrolling = mRecordAdapter.getCurrentFragment().isScrolling(!isChoose);
                    isChoose = mRecordAdapter.getCurrentFragment().isChoose();
                }
                if (!isScrolling) {
                    mIsChoose = !mIsChoose;
                    if (isChoose) {
                        tvChoose.setText(R.string.cancel);
                    } else {
                        tvChoose.setText(R.string.choose);
                    }
                    tvChoose.setSelected(isChoose);
                }
                break;
        }
    }

    public void setIndicator() {
        Class<?> tabLayout = tlTab.getClass();
        Field tabStrip;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
            tabStrip.setAccessible(true);
            LinearLayout llTab;
            try {
                llTab = (LinearLayout) tabStrip.get(tlTab);
                int left = (int) (getDisplayMetrics().density * 55);
                int right = (int) (getDisplayMetrics().density * 55);
                for (int i = 0; i < llTab.getChildCount(); i++) {
                    View child = llTab.getChildAt(i);
                    child.setPadding(0, 0, 0, 0);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.MATCH_PARENT, 1);
                    layoutParams.leftMargin = left;
                    layoutParams.rightMargin = right;
                    child.setLayoutParams(layoutParams);
                    child.invalidate();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public DisplayMetrics getDisplayMetrics() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric;
    }
}
