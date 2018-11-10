package cn.jcyh.doorbelldemo.function.ui;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.doorbelldemo.base.BaseDoorbellActivity;
import cn.jcyh.doorbelldemo.function.contract.AddDoorbellContract;
import cn.jcyh.doorbelldemo.function.presenter.AddDoorbellPresenter;
import cn.jcyh.doorbelldemo.widget.ClearableEditText;
import cn.jcyh.eaglekinglockdemo.R;

public class AddDoorbellActivity extends BaseDoorbellActivity<AddDoorbellPresenter> implements AddDoorbellContract.View, TextWatcher, ClearableEditText.OnClearableEditTextListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_add)
    RelativeLayout rlAdd;
    @BindView(R.id.et_device_account)
    EditText etDeviceAccount;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    @BindView(R.id.iv_shadow)
    ImageView ivShadow;
    @BindView(R.id.tv_scanf)
    TextView tvScanf;
    private int REQUEST_SCANF_QR_CODE = 0X001;
    private String mDeviceAccount;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_doorbell;
    }

    @Override
    protected AddDoorbellPresenter createPresenter() {
        return new AddDoorbellPresenter();
    }

    @Override
    protected void init() {
        tvTitle.setText(R.string.add_doorbell);
        etDeviceAccount.addTextChangedListener(this);
    }

    @OnClick({R.id.tv_title, R.id.tv_add, R.id.tv_scanf})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title:
                finish();
                break;
            case R.id.tv_add:
                mPresenter.sendBindRequest();
                break;
            case R.id.tv_scanf:
//                startNewActivityForResult(CaptureActivity.class, REQUEST_SCANF_QR_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null)
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_SCANF_QR_CODE) {
                    //二维码扫描回调
//                    String result = data.getStringExtra(Constant.SCAN_RESULT);
//                    if (!TextUtils.isEmpty(result)) {
//                        etDeviceAccount.setText(result);
//                        tvScanf.setSelected(true);
//                    }
                }
            }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mDeviceAccount = etDeviceAccount.getText().toString().trim();
        if (TextUtils.isEmpty(mDeviceAccount) || mDeviceAccount.length() < 14) {
            ivShadow.setVisibility(View.GONE);
            tvAdd.setEnabled(false);
        } else {
            ivShadow.setVisibility(View.VISIBLE);
            tvAdd.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onTextChanged(CharSequence s) {
        mDeviceAccount = etDeviceAccount.getText().toString().trim();
        if (TextUtils.isEmpty(mDeviceAccount) || mDeviceAccount.length() < 14) {
            ivShadow.setVisibility(View.GONE);
            tvAdd.setEnabled(false);
        } else {
            ivShadow.setVisibility(View.VISIBLE);
            tvAdd.setEnabled(true);
        }
    }

    @Override
    public String getDeviceID() {
        return etDeviceAccount.getText().toString().trim();
    }

    @Override
    public void addDoorbellSuccess() {
        finish();
    }
}
