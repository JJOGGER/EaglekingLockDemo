package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.entity.LockKey;
import cn.jcyh.eaglekinglockdemo.ui.fragment.SendPwdFragment;

public class SendPwdActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tl_title)
    TabLayout tLtitle;
    @BindView(R.id.vp_content)
    ViewPager vpContent;
    private LockKey mLockKey;
    private String[] mTitles = new String[]{
            "永久", "限时", "单次", "清空", "自定义", "循环"
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_send_pwd;
    }

    @Override
    protected void init() {
        tvTitle.setText("发送密码");
        mLockKey = getIntent().getParcelableExtra(LockConstant.LOCK_KEY);
        vpContent.setOffscreenPageLimit(5);
        vpContent.setCurrentItem(0);
        MyAdapter myAdapter = new MyAdapter(getSupportFragmentManager());
        vpContent.setAdapter(myAdapter);
        tLtitle.setTabMode(TabLayout.MODE_SCROLLABLE);
        tLtitle.setupWithViewPager(vpContent);
    }

    @OnClick({R.id.ibtn_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
        }
    }

    private class MyAdapter extends FragmentPagerAdapter {

        MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            SendPwdFragment sendPwdFragment = new SendPwdFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(LockConstant.LOCK_KEY, mLockKey);
            bundle.putInt(LockConstant.POSITION, position);
            sendPwdFragment.setArguments(bundle);
            return sendPwdFragment;
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }
}
