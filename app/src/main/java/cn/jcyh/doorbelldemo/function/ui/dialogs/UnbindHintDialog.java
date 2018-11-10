package cn.jcyh.doorbelldemo.function.ui.dialogs;

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

public class UnbindHintDialog extends BaseDialogFragment {
    @BindView(R.id.tv_content)
    TextView tv_content;
    private String mHintContent = "";

    public String getHintContent() {
        return mHintContent;
    }

    public void setHintContent(String hintContent) {
        mHintContent = hintContent;
    }

    private OnHintDialogListener mListener;

    public interface OnHintDialogListener {
        void confirm(boolean isConfirm);
    }

    public void setOnHintDialogListener(OnHintDialogListener listener) {
        mListener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_unbind_hint;
    }

    @Override
    protected void init(View view) {
        tv_content.setText(TextUtils.isEmpty(getHintContent()) ? getString(R.string.delete_device_msg) : getHintContent());
    }

    @OnClick({R.id.tv_confirm, R.id.iv_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                if (mListener != null) {
                    mListener.confirm(true);
                }
                break;
            case R.id.iv_cancel:
                if (mListener != null) {
                    mListener.confirm(false);
                }
                break;
        }
    }
}
