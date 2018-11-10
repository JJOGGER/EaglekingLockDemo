package cn.jcyh.doorbelldemo.base;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import cn.jcyh.nimlib.entity.Doorbell;


/**
 * Created by jogger on 2018/1/19.
 */

public abstract class BaseRecordPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private int[] mTitlesId;
    protected Doorbell mDoorbell;
    private BaseRecordFragment mCurrentFragment;

    public BaseRecordPagerAdapter(FragmentManager fm, Context context, Doorbell doorbell, int[] titlesId) {
        super(fm);
        mContext = context;
        mTitlesId = titlesId;
        mDoorbell = doorbell;
    }

    @Override
    public int getCount() {
        return mTitlesId.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(mTitlesId[position]);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentFragment = (BaseRecordFragment) object;
        super.setPrimaryItem(container, position, object);
    }


    public BaseRecordFragment getCurrentFragment() {
        return mCurrentFragment;
    }
}
