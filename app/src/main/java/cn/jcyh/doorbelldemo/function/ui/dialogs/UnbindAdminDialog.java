package cn.jcyh.doorbelldemo.function.ui.dialogs;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;

/**
 * Created by it on 2017/3/9.
 * 提示对话框
 */

public class UnbindAdminDialog extends BaseDialogFragment {
    @BindView(R.id.tv_content)
    TextView tv_content;
    private String mHintContent = "";

    public String getHintContent() {
        return mHintContent;
    }

    public void setHintContent(String hintContent) {
        mHintContent = hintContent;
    }

    private OnUnbindAdminDialogListener mListener;

    public interface OnUnbindAdminDialogListener {
        void onUnbind();

        void onAppointAdmin();
    }

    public void setOnUnbindAdminDialogListener(OnUnbindAdminDialogListener listener) {
        mListener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_unbind_admin;
    }

    @Override
    protected void init(View view) {
    }

    @OnClick({R.id.tv_appoint_admin, R.id.tv_go_on, R.id.iv_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_appoint_admin:
                if (mListener != null) {
                    mListener.onAppointAdmin();
                }
                break;
            case R.id.tv_go_on:
                if (mListener != null) {
                    mListener.onUnbind();
                }
                break;
            case R.id.iv_cancel:
                dismiss();
                break;
        }
    }
}
