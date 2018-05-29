package cn.jcyh.eaglekinglockdemo.ui.dialog;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;

/**
 * Created by it on 2017/3/9.
 * 提示对话框
 */

public class HintDialog extends BaseDialogFragment {
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    private String mHintContent = "";
    private String confirmText = "";
    private String cancelText = "";
    private OnHintDialogListener mListener;

    public interface OnHintDialogListener {
        void onConfirm(boolean isConfirm);
    }

    public void setOnHintDialogListener(OnHintDialogListener listener) {
        mListener = listener;
    }

    public String getHintContent() {
        return mHintContent;
    }

    public void setHintContent(String hintContent) {
        mHintContent = hintContent;
    }

    public void setConfirmText(String text) {
        confirmText = text;
    }

    public void setCancelText(String text) {
        cancelText = text;
    }

    public String getConfirmText() {
        return confirmText;
    }

    public String getCancelText() {
        return cancelText;
    }

    @Override
    int getLayoutId() {
        return R.layout.dialog_hint;
    }

    @Override
    protected void init(View view) {
        tvContent.setText(getHintContent());
        if (!TextUtils.isEmpty(getConfirmText())) {
            tvConfirm.setText(getConfirmText());
        }
        if (!TextUtils.isEmpty(getCancelText())) {
            tvCancel.setText(getCancelText());
        }
    }

    @OnClick({R.id.tv_confirm, R.id.tv_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                if (mListener != null) {
                    mListener.onConfirm(true);
                }
                break;
            case R.id.tv_cancel:
                if (mListener != null) {
                    mListener.onConfirm(false);
                }
                break;
        }
    }
}
