package cn.jcyh.eaglekinglockdemo.ui.dialog;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;

/**
 * Created by it on 2017/3/9.
 * 编辑对话框
 */

public class CommonEditDialog extends BaseDialogFragment {
    @BindView(R.id.et_content)
    EditText etContent;
    private String mHintContent = "";
    private String mEditText = "";
    private int mInputType = -1;

    public String getHintContent() {
        return mHintContent;
    }

    public void setHintContent(String hintContent) {
        mHintContent = hintContent;
    }

    public interface OnCommonEditDialogListener {
        void onConfirm(boolean isConfirm);
    }

    private OnCommonEditDialogListener mListener;


    public void setOnDialogListener(OnCommonEditDialogListener listener) {
        mListener = listener;
    }

    @Override
    int getLayoutId() {
        return R.layout.dialog_common_edit;
    }

    public void setEditText(String text) {
        mEditText = text;
    }

    public void setInputType(int type) {
        mInputType = type;
    }

    @Override
    protected void init(View view) {
        if (!TextUtils.isEmpty(mHintContent))
            etContent.setHint(mHintContent);
        if (!TextUtils.isEmpty(mEditText)) {
            etContent.setText(mEditText);
            etContent.setSelectAllOnFocus(true);
//            etContent.setSelection();
        }
        if (mInputType != -1)
            etContent.setInputType(mInputType);
    }

    public String getEditText() {
        return etContent.getText().toString();
    }

    @OnClick({R.id.tv_confirm, R.id.tv_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                if (mListener != null) {
                    if (TextUtils.isEmpty(etContent.getText())) {
                        etContent.setError("输入不能为空");
                        return;
                    }
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
