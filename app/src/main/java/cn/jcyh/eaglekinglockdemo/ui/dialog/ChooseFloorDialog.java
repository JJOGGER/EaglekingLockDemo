package cn.jcyh.eaglekinglockdemo.ui.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.entity.LockKey;

/**
 * Created by jogger on 2018/11/10.
 */
public class ChooseFloorDialog extends BaseDialogFragment {
    @BindView(R.id.s_floor)
    Spinner sFloor;
    @BindView(R.id.s_room)
    Spinner sRoom;
    private LockKey mLockKey;
    private OnChooseFloorDialogListener mListener;

    public void setOnChooseFloorDialogListener(OnChooseFloorDialogListener listener) {
        mListener = listener;
    }

    public interface OnChooseFloorDialogListener {
        void onConfirm(LockKey lockKey);
    }

    @Override
    int getLayoutId() {
        return R.layout.dialog_choose_floor;
    }

    @Override
    protected void init(View view) {
        super.init(view);
        Bundle bundle = getArguments();
        if (bundle == null) return;
        mLockKey = bundle.getParcelable(LockConstant.LOCK_KEY);
    }

    @OnClick({R.id.btn_confirm})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                if (mListener != null)
                    mListener.onConfirm(mLockKey);
                break;
        }
    }
}
