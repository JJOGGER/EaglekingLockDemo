package cn.jcyh.doorbelldemo.control.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import cn.jcyh.doorbelldemo.base.BaseDoorbellActivity;
import cn.jcyh.doorbelldemo.function.ui.dialogs.BaseDialogFragment;


/**
 * Created by jogger on 2017/3/21.
 * 对话框创建
 */

public class DialogHelper {
    private BaseDialogFragment mDialogFragment;
    private FragmentManager mFragmentManager;
    private Object mArgment;
    private Object mArgment2;

    private DialogHelper() {
    }

    public DialogHelper(BaseDoorbellActivity activity, BaseDialogFragment dialogFragment) {
        mFragmentManager = activity.getSupportFragmentManager();
        mDialogFragment = dialogFragment;
    }

    public BaseDialogFragment getDialogFragment() {
        return mDialogFragment;
    }

    public void dismiss() {
        if (mDialogFragment != null && mDialogFragment.getDialog() != null
                && mDialogFragment.getDialog().isShowing()) {
            mDialogFragment.dismiss();
        }
    }

    public void setArgment(Object argment) {
        mArgment = argment;
    }

    public Object getArgment() {
        return mArgment;
    }

    public void setArgment2(Object argment2) {
        mArgment2 = argment2;
    }

    public Object getArgment2() {
        return mArgment2;
    }

    public boolean isShowing() {
        return mDialogFragment != null && mDialogFragment.getDialog() != null &&
                mDialogFragment.getDialog().isShowing();
    }


    public void commit() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (isShowing())
            return;
        Fragment fragmentByTag = mFragmentManager.findFragmentByTag(mDialogFragment.getClass().getSimpleName());
        if (!mDialogFragment.isAdded() && !mDialogFragment.isVisible() && fragmentByTag == null)
            transaction.add(mDialogFragment, mDialogFragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

}
