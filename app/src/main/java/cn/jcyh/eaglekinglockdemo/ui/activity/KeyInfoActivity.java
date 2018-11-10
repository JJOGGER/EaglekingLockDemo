package cn.jcyh.eaglekinglockdemo.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.base.BaseActivity;
import cn.jcyh.eaglekinglockdemo.constant.KeyStatus;
import cn.jcyh.eaglekinglockdemo.constant.LockConstant;
import cn.jcyh.eaglekinglockdemo.entity.LockKey;
import cn.jcyh.eaglekinglockdemo.http.LockHttpAction;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestCallback;
import cn.jcyh.eaglekinglockdemo.ui.dialog.CommonEditDialog;
import cn.jcyh.eaglekinglockdemo.ui.dialog.HintDialog;
import cn.jcyh.utils.L;
import cn.jcyh.utils.T;

//钥匙信息
public class KeyInfoActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_received_account)
    TextView tvReceivedAccount;
    @BindView(R.id.tv_send_account)
    TextView tvSendAccount;
    @BindView(R.id.tv_send_time)
    TextView tvSendTime;
    @BindView(R.id.tv_auth)
    TextView tvAuth;
    @BindView(R.id.tv_freeze)
    TextView tvFreeze;
    private LockKey mLockKey;

    @Override
    public int getLayoutId() {
        return R.layout.activity_key_info;
    }

    @Override
    protected void init() {
        tvTitle.setText("钥匙信息");
        mLockKey = getIntent().getParcelableExtra(LockConstant.LOCK_KEY);
        L.e("-----lockkey:" + mLockKey);
        Date date = new Date();
        if (!TextUtils.isEmpty(mLockKey.getUsername()))
            tvName.setText(mLockKey.getUsername());
        String keyStatus = mLockKey.getKeyStatus();
        if (mLockKey.getEndDate() == 1) {
            tvDate.setText("单次");
        } else if (mLockKey.getEndDate() == 0) {
            tvDate.setText("永久");
        } else {
            date.setTime(mLockKey.getStartDate());
            String startDate = SimpleDateFormat.getInstance().format(date);
            date.setTime(mLockKey.getEndDate());
            String endDate = SimpleDateFormat.getInstance().format(date);
            tvDate.setText(startDate + "\n" + endDate);
        }
        if ("0".equals(mLockKey.getKeyRight())) {
            tvAuth.setText("授权");
        } else if ("1".equals(mLockKey.getKeyRight())) {
            tvAuth.setText("取消授权");
        }
        if (KeyStatus.KEY_FROZEN.equals(mLockKey.getKeyStatus())) {
            tvFreeze.setText("取消冻结");
        } else {
            tvFreeze.setText("冻结");
        }
        tvReceivedAccount.setText(mLockKey.getUsername());
        tvSendAccount.setText(mLockKey.getSenderUsername());
        date.setTime(mLockKey.getTimestamp());
        String sendDate = SimpleDateFormat.getInstance().format(date);
        tvSendTime.setText(sendDate);
    }


    @OnClick({R.id.ibtn_back, R.id.rl_name, R.id.rl_record, R.id.tv_delete, R.id.tv_freeze, R.id.tv_auth})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.rl_name:
                rename();
                break;
            case R.id.rl_record:
                break;
            case R.id.tv_delete:
                delKey();
                break;
            case R.id.tv_freeze:
                freezeKey();
                break;
            case R.id.tv_auth:
                authKeyUser();
                break;
        }
    }

    /**
     * 重命名
     */
    private void rename() {
        final CommonEditDialog commonEditDialog = new CommonEditDialog();
        commonEditDialog.setHintContent("请输入名称");
        commonEditDialog.setOnDialogListener(new CommonEditDialog.OnCommonEditDialogListener() {
            @Override
            public void onConfirm(boolean isConfirm) {
                commonEditDialog.dismiss();
                if (isConfirm) {
                    String content = commonEditDialog.getEditText();

                }
            }
        });
    }

    private void authKeyUser() {
        final HintDialog hintDialog = new HintDialog();
        if ("0".equals(mLockKey.getKeyRight())) {
            hintDialog.setHintContent("授权用户可以发送该锁的电子钥匙和密码。");
        } else if ("1".equals(mLockKey.getKeyRight())) {
            hintDialog.setHintContent("取消用户发送该锁的电子钥匙和密码的权限");
        }
        hintDialog.setOnHintDialogListener(new HintDialog.OnHintDialogListener() {
            @Override
            public void onConfirm(boolean isConfirm) {
                hintDialog.dismiss();
                if (isConfirm) {
                    if ("0".equals(mLockKey.getKeyRight())) {
                        auth();
                    } else if ("1".equals(mLockKey.getKeyRight())) {
                        unAuth();
                    }
                }
            }
        });
        hintDialog.show(getSupportFragmentManager(), HintDialog.class.getName());
    }

    private void unAuth() {
        LockHttpAction.getHttpAction(this).unAuthKeyUser(mLockKey.getLockId(), mLockKey.getKeyId(), new OnHttpRequestCallback<Boolean>() {

            @Override
            public void onFailure(int errorCode, String desc) {
                T.show( "取消授权失败" + errorCode);
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                T.show( "取消授权成功");
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void auth() {
        LockHttpAction.getHttpAction(this).authKeyUser(mLockKey.getLockId(), mLockKey.getKeyId(), new OnHttpRequestCallback<Boolean>() {

            @Override
            public void onFailure(int errorCode, String desc) {
                T.show( "授权失败" + errorCode);
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                T.show("授权成功");
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void freezeKey() {
        if (KeyStatus.KEY_FROZEN.equals(mLockKey.getKeyStatus())) {
            //取消冻结
            LockHttpAction.getHttpAction(this).unFreezeKey(mLockKey.getKeyId(), new OnHttpRequestCallback<Boolean>() {

                @Override
                public void onFailure(int errorCode, String desc) {
                    T.show( "取消冻结失败" + errorCode);
                }

                @Override
                public void onSuccess(Boolean aBoolean) {
                    T.show( "取消冻结成功");
                    setResult(RESULT_OK);
                    finish();
                }
            });
        } else {
            LockHttpAction.getHttpAction(this).freezeKey(mLockKey.getKeyId(), new OnHttpRequestCallback<Boolean>() {

                @Override
                public void onFailure(int errorCode, String desc) {
                    T.show( "冻结失败" + errorCode);
                }

                @Override
                public void onSuccess(Boolean aBoolean) {
                    T.show( "冻结成功");
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }
    }

    private void delKey() {
        LockHttpAction.getHttpAction(this).delKey(
                mLockKey.getKeyId(),
                new OnHttpRequestCallback<Boolean>() {
                    @Override
                    public void onFailure(int errorCode, String desc) {
                        T.show( "删除失败");
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        T.show("删除成功");
                        setResult(RESULT_OK);
                        finish();
                    }
                }
        );
    }
}
