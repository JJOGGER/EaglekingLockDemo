package cn.jcyh.doorbelldemo.function.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.doorbelldemo.avchat.AVChatProfile;
import cn.jcyh.doorbelldemo.base.BaseDoorbellActivity;
import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.doorbelldemo.control.ControlCenter;
import cn.jcyh.doorbelldemo.function.contract.DoorbellCallContract;
import cn.jcyh.doorbelldemo.function.presenter.DoorbellCallPresenter;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.entity.CommandJson;
import cn.jcyh.utils.T;

//视频呼叫
public class DoorbellCallActivity extends BaseDoorbellActivity<DoorbellCallPresenter> implements DoorbellCallContract.View {
    @BindView(R.id.tv_call_name)
    TextView tvCallName;
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.chronometer_time)
    Chronometer chronometer_time;
    @BindView(R.id.pb_progress)
    ProgressBar pbProgress;
    private NimUserInfo mDoorbell;


    @Override
    public int getLayoutId() {
        return R.layout.activity_doorbell_call;
    }

    @Override
    protected DoorbellCallPresenter createPresenter() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        return new DoorbellCallPresenter();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void init() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager
                .LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        mDoorbell = ControlCenter.getDoorbellManager().getDoorbellByDeviceID(getIntent().getStringExtra(Constant.FROM_ACCOUNT));
        String fromAccount = getIntent().getStringExtra(Constant.FROM_ACCOUNT);
        String filePath = getIntent().getStringExtra(Constant.FILE_PATH);
        CommandJson commandJson = getIntent().getParcelableExtra(Constant.COMMAND);
        if (commandJson == null) return;
        Glide.with(this)
                .load(filePath)
                .into(ivImage);
        ivImage.setVisibility(View.VISIBLE);
        pbProgress.setVisibility(View.GONE);
        NimUserInfo userByAccount = ControlCenter.getDoorbellManager().getDoorbellByDeviceID(fromAccount);
        if (userByAccount == null) return;
        if (!TextUtils.isEmpty(userByAccount.getName())) {
            tvCallName.setText(userByAccount.getName());
        } else {
            tvCallName.setText(userByAccount.getAccount());
        }
        String commandType = commandJson.getCommandType();
        if (CommandJson.CommandType.DOORBELL_NOTIFICATION.equals(commandType)) {
            if (CommandJson.CommandType.NOTIFICATION_DOORBELL_RING.equals(commandJson.getCommand())) {
                tvMsg.setText(R.string.doorbell_push_msg);
            } else {
                tvMsg.setText(R.string.alarm_push_msg);
            }
        }
        chronometer_time.start();
    }


    @OnClick({R.id.ibtn_accept, R.id.ibtn_reject})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_accept:
                if (AVChatProfile.getInstance().isAVChatting()) {
                    T.show(R.string.videoing_msg);
                    finish();
                    return;
                }
                Bundle bundle=new Bundle();
                bundle.putString(Constant.DEVICE_ID, mDoorbell.getAccount());
                bundle.putString(Constant.DEVICE_NAME,mDoorbell.getName());
                startNewActivity(DoorbellVideoActivity.class, bundle);
                finish();
                break;
            case R.id.ibtn_reject:
                //拒绝接听
                finish();
                break;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void getVideoBitmap(Bitmap bitmap) {
        pbProgress.setVisibility(View.GONE);
        ivImage.setImageBitmap(bitmap);
        ivImage.setVisibility(View.VISIBLE);
    }
}
