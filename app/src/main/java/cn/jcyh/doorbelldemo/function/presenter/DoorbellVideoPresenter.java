package cn.jcyh.doorbelldemo.function.presenter;

import android.os.Handler;
import android.os.Message;

import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.avchat.constant.AVChatControlCommand;
import com.netease.nimlib.sdk.avchat.constant.AVChatEventType;
import com.netease.nimlib.sdk.avchat.model.AVChatCalleeAckEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatCommonEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatControlEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatData;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cn.jcyh.doorbelldemo.avchat.AVChatControllerCallback;
import cn.jcyh.doorbelldemo.avchat.AVChatProfile;
import cn.jcyh.doorbelldemo.base.BasePresenter;
import cn.jcyh.doorbelldemo.function.contract.DoorbellVideoContract;
import cn.jcyh.doorbelldemo.function.model.DoorbellVideoModel;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.command.manager.VideoManager;
import cn.jcyh.nimlib.constant.AVChatExitCode;
import cn.jcyh.nimlib.observer.AVChatTimeoutObserver;
import cn.jcyh.nimlib.observer.PhoneCallStateObserver;
import cn.jcyh.nimlib.observer.SimpleAVChatStateObserver;
import cn.jcyh.utils.FileUtil;
import cn.jcyh.utils.L;
import cn.jcyh.utils.T;
import cn.jcyh.utils.Utils;

/**
 * Created by jogger on 2018/7/5.
 */
public class DoorbellVideoPresenter extends BasePresenter<DoorbellVideoContract.View, DoorbellVideoContract.Model> implements DoorbellVideoContract.Presenter {
    private static final byte IS_DUAL_CAMERA = AVChatControlCommand.NOTIFY_CUSTOM_BASE + 1;
    private static final byte SWITCH_CAMERA = AVChatControlCommand.NOTIFY_CUSTOM_BASE + 2;//切换摄像头
    private static final byte UNLOCK = AVChatControlCommand.NOTIFY_CUSTOM_BASE + 3;
    private static final int MSG_CLOSE_LOCK = 1;
    private Timer mLockTimer;//开锁任务
    private LockTask mLockTask;
    private MyHandler mHandler;
    private int mLockTime;
    private boolean mIsRecording = false;//标记是否正在录像

    @Override
    public DoorbellVideoContract.Model attacheModel() {
        return new DoorbellVideoModel();
    }

    @Override
    public void attachView(DoorbellVideoContract.View view) {
        super.attachView(view);
        AVChatProfile.getInstance().setAVChatting(true);
        registerObserves(true);
        mView.startLoadAnim();


        mHandler = new MyHandler(DoorbellVideoPresenter.this);
        mModel.doCalling(mView.getDeviceID(), new AVChatControllerCallback<AVChatData>() {
            @Override
            public void onSuccess(AVChatData avChatData) {
                L.e("---------------onSuccess");
            }

            @Override
            public void onFailed(int code, String errorMsg) {
                if (mView == null) return;
                mModel.hangUp(AVChatExitCode.HANGUP);
                mView.finishVideo();
            }
        });

    }

    /**
     * 来电监听
     */
    private void registerObserves(boolean register) {
        VideoManager.getVideoManager().observeAVChatState(mAVChatStateObserver, register);
        VideoManager.getVideoManager().observeHangUpNotification(mCallHangupObserver, register);
        VideoManager.getVideoManager().observeCalleeAckNotification(mCallAckObserver, register);
        VideoManager.getVideoManager().observeControlNotification(mCallControlObserver, register);
        AVChatTimeoutObserver.getInstance().observeTimeoutNotification(mTimeoutObserver, register, true);
        PhoneCallStateObserver.getInstance().observeAutoHangUpForLocalPhone(mAutohangupforlocalphoneobserver, register);
//        //放到所有UI的基类里面注册，所有的UI实现onKickOut接口
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void detachView() {
        //界面销毁时强制尝试挂断，防止出现红米Note 4X等手机在切后台点击杀死程序时，实际没有杀死的情况
        try {
            mModel.hangUp(AVChatExitCode.HANGUP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AVChatProfile.getInstance().setAVChatting(false);
        registerObserves(false);
        cancelLockTimer();
        super.detachView();
    }


    @Override
    public void startLockTimer() {
        mLockTimer = new Timer();
        mLockTask = new LockTask();
        mLockTimer.schedule(mLockTask, 1, 1000);
    }

    @Override
    public void cancelLockTimer() {
        if (mLockTimer != null) {
            mLockTimer.cancel();
            mLockTimer = null;
        }
        if (mLockTask != null) {
            mLockTask.cancel();
            mLockTask = null;
        }
        mLockTime = 0;
    }

    @Override
    public void sendUnlockRequest() {
        mModel.unlock();
    }

    @Override
    public void changeCamera() {
        mModel.switchRemoteCamera();
    }

    @Override
    public void finishVideoCall() {
        if (mIsRecording)
            mModel.stopAVRecording(mView.getDeviceID());
        mModel.hangUp(AVChatExitCode.HANGUP);
        mView.finishVideo();
    }

    /**
     * 拍照
     */
    @Override
    public void startCutShort() {
        mModel.takeSnapshot(mView.getDeviceID());
    }

    /**
     * 录像
     */
    @Override
    public void toggleAVRecording() {
        if (mIsRecording) {
            boolean result = mModel.stopAVRecording(mView.getDeviceID());
            if (result) {
                mIsRecording = false;
            }
        } else {
            boolean result = mModel.startAVRecording(mView.getDeviceID());
            if (result) {
                mIsRecording = true;
            }
        }
        mView.toggleAVRecording(mIsRecording);
    }

    public int getLockTime() {
        return mLockTime;
    }

    /**
     * 对讲开关
     */
    @Override
    public void toggleLocalMute() {
        boolean isLocalMute = !mModel.isLocalAudioMuted();
        mModel.muteLocalAudio(isLocalMute);
        mView.toggleLocalMute(isLocalMute);
    }

    @Override
    public void toggleRemoteMute() {
        boolean isRemoteMute = !mModel.isRemoteAudioMuted(mView.getDeviceID());
        mModel.muteRemoteAudio(mView.getDeviceID(), isRemoteMute);
        mView.toggleRemoteMute(isRemoteMute);
    }


    /**
     * 开锁任务
     */
    private class LockTask extends TimerTask {
        public void run() {
            Message msg = new Message();
            msg.what = MSG_CLOSE_LOCK;
            mHandler.sendMessage(msg);
        }
    }


    private static class MyHandler extends Handler {
        private WeakReference<DoorbellVideoPresenter> mWeakReference;

        MyHandler(DoorbellVideoPresenter presenter) {
            mWeakReference = new WeakReference<>(presenter);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DoorbellVideoPresenter presenter = mWeakReference.get();
            if (presenter == null || presenter.mView == null) return;
            switch (msg.what) {
                case MSG_CLOSE_LOCK:
                    presenter.mLockTime++;
                    if (presenter.mLockTime == 6) {
                        presenter.mView.initLockState();
                        presenter.cancelLockTimer();
                    }
                    break;
            }
        }
    }


    //通话过程状态监听
    private SimpleAVChatStateObserver mAVChatStateObserver = new SimpleAVChatStateObserver() {
        @Override
        public void onTakeSnapshotResult(String account, boolean success, String filePath) {
            super.onTakeSnapshotResult(account, success, filePath);
            L.e("----------------录制完成：" + filePath);
            File file = new File(filePath);
            if (!file.exists()) return;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",
                    Utils.getApp().getResources().getConfiguration().locale);
            Date date = new Date(System.currentTimeMillis());
            String format = simpleDateFormat.format(date);
            simpleDateFormat.applyPattern("yyyyMMddhhmmss");
            String fileName = "IMG_" + simpleDateFormat.format(date) + ".jpg";
            File newFile = new File(FileUtil.getDoorBellRecordFileSrc(account) +
                    File.separator +
                    format);
            boolean mkdirs = true;
            if (!newFile.exists()) {
                mkdirs = newFile.mkdirs();
            }
            if (mkdirs) {
                newFile = new File(newFile.getAbsoluteFile() + File.separator + fileName);
            }
            boolean b = FileUtil.moveFile(file.getAbsolutePath(), newFile.getAbsolutePath());
            if (b) {
                T.show(R.string.cut_picture_success);
            }
            file.delete();
        }

        @Override
        public void onAVRecordingCompletion(String account, String filePath) {
            if (account != null && filePath != null && filePath.length() > 0) {
                String msg = "音视频录制已结束, " + "账号：" + account + " 录制文件已保存至：" + filePath;
                L.e("-----:" + msg);
                File file = new File(filePath);
//                if (file.exists()) return;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",
                        Utils.getApp().getResources().getConfiguration().locale);
                Date date = new Date(System.currentTimeMillis());
                String format = simpleDateFormat.format(date);
                simpleDateFormat.applyPattern("yyyyMMddhhmmss");
                String fileName = "VID_" + simpleDateFormat.format(date) + ".mp4";
                File newFile = new File(FileUtil.getDoorBellRecordFileSrc(mView.getDeviceID()) +
                        File.separator +
                        format);
                boolean mkdirs = true;
                if (!newFile.exists()) {
                    mkdirs = newFile.mkdirs();
                }
                if (mkdirs) {
                    newFile = new File(newFile.getAbsoluteFile() + File.separator + fileName);
                    boolean b = FileUtil.moveFile(file.getAbsolutePath(), newFile.getAbsolutePath());
                }
            } else {
                L.e("-----录制已结束.");
            }
//            if (AVChatType.VIDEO.getValue()) {
//                avChatVideoUI.resetRecordTip();
//            } else {
//                avChatAudioUI.resetRecordTip();
//            }
        }

        @Override
        public void onLowStorageSpaceWarning(long availableSize) {
            L.e("-------------低内存提醒");
        }

        @Override
        public void onJoinedChannel(int code, String audioFile, String videoFile, int i) {
//            LogUtil.d(TAG, "audioFile -> " + audioFile + " videoFile -> " + videoFile);
            handleWithConnectServerResult(code);
        }

        @Override
        public void onUserJoined(String account) {
            L.e("-------------onUserJoined" + account);
            if (mView == null) return;
            mView.endLoadAnim();
//            getIsDualCamera();
            mView.initSurfaceView();
        }

        @Override
        public void onUserLeave(String account, int event) {
            L.e("-------------onUserLeave:" + account);
            if (mView == null) return;
            mModel.hangUp(AVChatExitCode.HANGUP);
            mView.finishVideo();
        }

        //音视频连接建立，会回调
        @Override
        public void onCallEstablished() {
//            //移除超时监听
            AVChatTimeoutObserver.getInstance().observeTimeoutNotification(mTimeoutObserver, false, true);
            if (mView == null) return;
            mView.functionEnable(true);
        }

    };
    // 通话过程中，收到对方挂断电话
    private Observer<AVChatCommonEvent> mCallHangupObserver = new Observer<AVChatCommonEvent>() {
        @Override
        public void onEvent(AVChatCommonEvent avChatHangUpInfo) {
            if (mView == null) return;
            AVChatData avChatData = mModel.getAvChatData();
            L.e("------------->通话过程中，收到对方挂断电话");
            if (avChatData != null && avChatData.getChatId() == avChatHangUpInfo.getChatId()) {
                mModel.onHangUp(AVChatExitCode.HANGUP);
                finishVideoCall();
//                cancelCallingNotifier();
                // 如果是incoming call主叫方挂断，那么通知栏有通知
//                if (mIsInComingCall && !isCallEstablished) {
//                    activeMissCallNotifier();
//                }
            }

        }
    };

    // 呼叫时，被叫方的响应（接听、拒绝、忙）
    private Observer<AVChatCalleeAckEvent> mCallAckObserver = new Observer<AVChatCalleeAckEvent>() {
        @Override
        public void onEvent(AVChatCalleeAckEvent ackInfo) {
            if (mView == null) return;
            AVChatData info = mModel.getAvChatData();
            if (info != null && info.getChatId() == ackInfo.getChatId()) {
                if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_BUSY ||
                        ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_REJECT) {
                    int type;
                    if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_BUSY) {
                        type = AVChatExitCode.PEER_BUSY;
                    } else {
                        type = AVChatExitCode.REJECT;
                    }
                    mModel.onHangUp(type);
                    finishVideoCall();
                } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_AGREE) {
                    mModel.setIsCallEstablish(true);
                }
            }
        }
    };
    private Observer<Integer> mTimeoutObserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer integer) {
            if (mView == null) return;
            mModel.hangUp(AVChatExitCode.CANCEL);
            // 来电超时，自己未接听
//            if (mIsInComingCall) {
//                activeMissCallNotifier();
//            }
            mView.finishVideo();
        }
    };
    // 监听音视频模式切换通知, 对方音视频开关通知
    private Observer<AVChatControlEvent> mCallControlObserver = new Observer<AVChatControlEvent>() {
        @Override
        public void onEvent(AVChatControlEvent netCallControlNotification) {
            if (mView == null) return;
            handleCallControl(netCallControlNotification);
        }
    };
    private Observer<Integer> mAutohangupforlocalphoneobserver = new Observer<Integer>() {
        @Override
        public void onEvent(Integer integer) {
            if (mView == null) return;
            mModel.onHangUp(AVChatExitCode.PEER_BUSY);
        }
    };

    // 处理音视频切换请求和对方音视频开关通知
    private void handleCallControl(AVChatControlEvent notification) {
        if (VideoManager.getVideoManager().getCurrentChatId() != notification.getChatId()) {
            return;
        }
        byte controlCommand = notification.getControlCommand();
        if (controlCommand == SWITCH_CAMERA) {
            T.show(R.string.change_success);
        } else if (controlCommand == UNLOCK) {
            T.show(R.string.unlock_succ);
            cancelLockTimer();
            mView.unLockSuccess();
            startLockTimer();//重启计时
        } else if (controlCommand == IS_DUAL_CAMERA) {
            mView.showIsDualCamera();
        }
    }

    /**
     * 处理连接服务器的返回值
     */
    private void handleWithConnectServerResult(int auth_result) {
        if (auth_result == 200) {
        } else if (auth_result == 101) { // 连接超时
            L.e("-------------连接超时");
        } else if (auth_result == 401) { // 验证失败
            L.e("-------------验证失败");
        } else if (auth_result == 417) { // 无效的channelId
            L.e("-------------无效的channelId");
        } else { // 连接服务器错误，直接退出
            L.e("-------------连接服务器错误，直接退出");
        }
    }
}
