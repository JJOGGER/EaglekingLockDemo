package cn.jcyh.eaglekinglockdemo.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.utils.ScreenUtil;
import cn.jcyh.eaglekinglockdemo.utils.Timber;

/**
 * Created by jogger on 2017/3/15.
 * 基类fragment
 */

public abstract class BaseFragment extends Fragment {
    public Activity mActivity;
    public boolean mIsVisibleToUser;
    public boolean mIsViewCreated;
    private Unbinder mBind;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        LayoutInflater myInflater = setViewStyle(inflater);
        View view;
        if (myInflater != null) {
            view = myInflater.inflate(getLayoutId(), null);
        } else {
            view = inflater.inflate(getLayoutId(), null);
        }
        mBind = ButterKnife.bind(this, view);
        //沉浸式状态栏
        if (mActivity instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) mActivity;
            if (baseActivity.isImmersive()) {
                View actionbar = view.findViewById(R.id.immersive);
                if (actionbar != null) {
                    int statusHeight = ScreenUtil.getStatusHeight(mActivity);
                    if (statusHeight != -1) {
                        actionbar.setPadding(0, statusHeight, 0, 0);
                    }
                }
            }
        }
        return view;
    }

    protected LayoutInflater setViewStyle(LayoutInflater inflater) {
        return null;
    }

    ;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mIsViewCreated = true;
        init();
        loadData();
    }

    public abstract int getLayoutId();

    public void init() {
    }

    public void loadData() {
    }

    public void existData(boolean existData) {
    }

    public void startNewActivity(Class cls) {
        Intent intent = new Intent(mActivity, cls);
        startActivity(intent);
    }

    public void startNewActivity(Class cls, Bundle bundle) {
        Intent intent = new Intent(mActivity, cls);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void startNewActivityForResult(Class cls, int requestCode) {
        Intent intent = new Intent(mActivity, cls);
        startActivityForResult(intent, requestCode);
    }

    public void startNewActivityForResult(Class cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent(mActivity, cls);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    public void startNewActivity(Class cls, String key, Object value) {
        Intent intent = new Intent(mActivity, cls);
        if (value instanceof Integer) {
            intent.putExtra(key, (Integer) value);
        }
        if (value instanceof Integer[]) {
            intent.putExtra(key, (Integer[]) value);
        }
        if (value instanceof String) {
            intent.putExtra(key, (String) value);
        }
        if (value instanceof String[]) {
            intent.putExtra(key, (String[]) value);
        }
        if (value instanceof Boolean) {
            intent.putExtra(key, (Boolean) value);
        }
        if (value instanceof Byte) {
            intent.putExtra(key, (Byte) value);
        }
        if (value instanceof Byte[]) {
            intent.putExtra(key, (Byte[]) value);
        }
        if (value instanceof Serializable) {
            intent.putExtra(key, (Serializable) value);
        }
        if (value instanceof Serializable[]) {
            intent.putExtra(key, (Serializable[]) value);
        }
        if (value instanceof Parcelable) {
            Timber.e("-------value:" + value);
            intent.putExtra(key, (Parcelable) value);
        }
        if (value instanceof Parcelable[]) {
            intent.putExtra(key, (Parcelable[]) value);
        }
        if (value instanceof Float) {
            intent.putExtra(key, (Float) value);
        }
        if (value instanceof Float[]) {
            intent.putExtra(key, (Float[]) value);
        }
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        MyApp.getRefWatcher().watch(this);
        mIsViewCreated=false;
        mBind.unbind();
    }


}