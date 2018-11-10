package cn.jcyh.doorbelldemo.function.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatSurfaceViewRenderer;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.doorbelldemo.base.BaseDoorbellActivity;
import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.doorbelldemo.control.ui.DialogHelper;
import cn.jcyh.doorbelldemo.function.contract.DoorbellVideoContract;
import cn.jcyh.doorbelldemo.function.presenter.DoorbellVideoPresenter;
import cn.jcyh.doorbelldemo.function.ui.dialogs.CommonHintDialog;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.command.manager.VideoManager;

public class DoorbellVideoActivity extends BaseDoorbellActivity<DoorbellVideoPresenter> implements DoorbellVideoContract.View {
    @BindView(R.id.cl_bottom_function)
    ConstraintLayout clBottomFunction;
    @BindView(R.id.tv_video_picture)
    TextView tvVideoPicture;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.tv_video_record)
    TextView tvVideoRecord;
    @BindView(R.id.tv_video_mute)
    TextView tvVideoMute;
    @BindView(R.id.tv_video_talk)
    TextView tvVideoTalk;
    @BindView(R.id.tv_video_lock)
    TextView tvVideoLock;
    @BindView(R.id.c_record_time)
    Chronometer cRecordTime;//录像时间
    @BindView(R.id.rl_record)
    RelativeLayout rlRecord;
    @BindView(R.id.iv_load_bg)
    ImageView ivLoadBg;
    @BindView(R.id.tv_change_camera)
    TextView tvChangeCamera;
    @BindView(R.id.fl_surface)
    FrameLayout flSurface;
    private AnimationDrawable mAudioAnim;
    private DialogHelper mUnLockDialog;
    private DialogHelper mExitVideoDialog;
    private String mDeviceID;
    private String mDeviceName;
    private AVChatSurfaceViewRenderer mSurfaceViewRenderer;

    @Override
    public int getLayoutId() {
        return R.layout.activity_doorbell_video;
    }

    @Override
    protected DoorbellVideoPresenter createPresenter() {
        if (getIntent().getExtras() == null) {
            finish();
            return null;
        }
        Bundle bundle = getIntent().getExtras();
        mDeviceID = bundle.getString(Constant.DEVICE_ID);
        mDeviceName = bundle.getString(Constant.DEVICE_NAME);
        return new DoorbellVideoPresenter();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void init() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager
                .LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        functionEnable(false);
        CommonHintDialog commonHintDialog = new CommonHintDialog();
        commonHintDialog.setHintContent(getString(R.string.unlock_hint));
        commonHintDialog.setOnHintDialogListener(new CommonHintDialog.OnHintDialogListener() {
            @Override
            public void confirm(boolean isConfirm) {
                //开锁
                if (isConfirm) {
                    mPresenter.sendUnlockRequest();
                }
                mUnLockDialog.dismiss();
            }
        });
        mUnLockDialog = new DialogHelper(this, commonHintDialog);
        commonHintDialog = new CommonHintDialog();
        commonHintDialog.setHintContent(getString(R.string.exit_video_hint));
        commonHintDialog.setOnHintDialogListener(new CommonHintDialog.OnHintDialogListener() {
            @Override
            public void confirm(boolean isConfirm) {
                if (isConfirm) {
                    mPresenter.finishVideoCall();
                }
                mExitVideoDialog.dismiss();
            }
        });
        mExitVideoDialog = new DialogHelper(this, commonHintDialog);
        tvDeviceName.setText(mDeviceName);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mSurfaceViewRenderer = new AVChatSurfaceViewRenderer(this);
    }

    @Override
    public void startLoadAnim() {
        ivLoadBg.setVisibility(View.VISIBLE);
        ivLoadBg.setBackgroundResource(R.drawable.loading_anim);
        mAudioAnim = (AnimationDrawable) ivLoadBg.getBackground();
        mAudioAnim.start();
    }

    @Override
    public void endLoadAnim() {
        if (mAudioAnim.isRunning())
            mAudioAnim.stop();
        ivLoadBg.setVisibility(View.GONE);
//        surfaceRemote.setBackgroundDrawable(null);
//        surfaceRemote.setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * 对讲
     */
    @Override
    public void toggleLocalMute(boolean isMute) {
        tvVideoTalk.setSelected(!isMute);
    }

    /**
     * 静音
     */
    @Override
    public void toggleRemoteMute(boolean isMute) {
        tvVideoMute.setSelected(isMute);
    }

    @Override
    public void toggleAVRecording(boolean isRecording) {
        rlRecord.setVisibility(isRecording ? View.VISIBLE : View.GONE);
        tvVideoRecord.setSelected(isRecording);
        if (isRecording) {
            if (cRecordTime != null) {
                cRecordTime.setBase(SystemClock.elapsedRealtime());//复位计时器，停止计时
            }
            cRecordTime.setFormat("0" +
                    (int) ((SystemClock.elapsedRealtime() - cRecordTime.getBase())
                            / 1000 / 60)
                    + ":%s");
            cRecordTime.start();
        } else {
            cRecordTime.setBase(SystemClock.elapsedRealtime());
        }
    }

    /**
     * 解锁
     */
    @Override
    public void unLockSuccess() {
        tvVideoLock.setSelected(true);
    }

    @Override
    public void initLockState() {
        tvVideoLock.setSelected(false);
    }

    @Override
    public void finishVideo() {
        finish();
    }

    @Override
    public void functionEnable(boolean enable) {
        clBottomFunction.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showIsDualCamera() {
        tvChangeCamera.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        exitVideo();
    }

    private void exitVideo() {
        mExitVideoDialog.commit();
    }

    @OnClick({
            R.id.tv_video_record, R.id.tv_video_talk, R.id.tv_video_mute,
            R.id.tv_video_picture, R.id.tv_video_lock,
            R.id.tv_change_camera, R.id.tv_exit_video
    })
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.tv_video_record:
                    //录像
                    mPresenter.toggleAVRecording();
                    break;
                case R.id.tv_video_talk:
                    //对讲
                    mPresenter.toggleLocalMute();
                    break;
                case R.id.tv_video_mute:
                    //静音
                    mPresenter.toggleRemoteMute();
                    break;
                case R.id.tv_video_picture:
                    //截图
                    mPresenter.startCutShort();
                    break;
                case R.id.tv_video_lock:
                    //开锁
                    if (mPresenter.getLockTime() == 0) {
                        mUnLockDialog.commit();
                    }
                    break;
                case R.id.tv_change_camera:
                    mPresenter.changeCamera();
                    break;
                case R.id.tv_exit_video:
                    exitVideo();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDeviceID() {
        return mDeviceID == null ? "" : mDeviceID;
    }
    // 大图像surface view 初始化
    @Override
    public void initSurfaceView() {
        // 设置画布，加入到自己的布局中，用于呈现视频图像
        // account 要显示视频的用户帐号
        VideoManager.getVideoManager().setupRemoteVideoRender(mDeviceID,mSurfaceViewRenderer,AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
        addPreviewLayout(mSurfaceViewRenderer);
    }

    private void addPreviewLayout(SurfaceView surfaceView) {
        if (surfaceView.getParent() != null) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        }
        flSurface.addView(surfaceView);
        surfaceView.setZOrderMediaOverlay(false);
//        largeSizePreviewCoverLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
