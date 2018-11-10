package cn.jcyh.doorbelldemo.function.model;

import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoQuality;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatParameters;

import java.util.concurrent.atomic.AtomicBoolean;

import cn.jcyh.doorbelldemo.avchat.AVChatControllerCallback;
import cn.jcyh.doorbelldemo.function.contract.DoorbellVideoContract;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.AVChatConfigs;
import cn.jcyh.nimlib.command.manager.VideoManager;
import cn.jcyh.nimlib.constant.AVChatExitCode;
import cn.jcyh.utils.L;
import cn.jcyh.utils.T;
import cn.jcyh.utils.Utils;

/**
 * Created by jogger on 2018/7/5.
 */
public class DoorbellVideoModel implements DoorbellVideoContract.Model {
    private AVChatConfigs mAVChatConfigs;
    private boolean mDestroyRTC = false;
    private AVChatData mAVChatData;

    @Override
    public void setIsCallEstablish(boolean isCallEstablish) {
        mIsCallEstablish.set(isCallEstablish);
    }

    private AtomicBoolean mIsCallEstablish = new AtomicBoolean(false);

    public DoorbellVideoModel() {
        mAVChatConfigs = new AVChatConfigs(Utils.getApp());
    }

    @Override
    public AVChatData getAvChatData() {
        return mAVChatData;
    }

    /**
     * *************************** 拨打和接听 ****************************
     */
    @Override
    public void doCalling(String account, final AVChatControllerCallback<AVChatData> callback) {
        VideoManager.getVideoManager().initVideo();
        VideoManager.getVideoManager().setParameters(mAVChatConfigs.getAvChatParameters());
        VideoManager.getVideoManager().setParameter(AVChatParameters.KEY_VIDEO_QUALITY, AVChatVideoQuality.QUALITY_480P);
        VideoManager.getVideoManager().setParameter(AVChatParameters.KEY_VIDEO_FRAME_FILTER, true);
        VideoManager.getVideoManager().call(account, new AVChatCallback<AVChatData>() {
            @Override
            public void onSuccess(AVChatData data) {
                mAVChatData = data;
//                VideoManager.getVideoManager().muteLocalVideo(true);
                VideoManager.getVideoManager().muteLocalAudio(true);
                callback.onSuccess(data);
            }

            @Override
            public void onFailed(int code) {
                L.e("----------avChat call failed code->" + code);

                if (code == ResponseCode.RES_FORBIDDEN) {
                    T.show(R.string.avchat_no_permission);
                } else {
                    T.show(R.string.avchat_call_failed);
                }
                closeRtc();
                callback.onFailed(code, "");
            }

            @Override
            public void onException(Throwable exception) {
                L.e("------------avChat call onException->" + exception);
                closeRtc();
                callback.onFailed(-1, exception.toString());
            }
        });
    }

    /**
     * 截图
     */
    @Override
    public void takeSnapshot(String account) {
        VideoManager.getVideoManager().takeSnapshot(account);
    }

    /**
     * 开启录像
     */
    @Override
    public boolean startAVRecording(String account) {
        return VideoManager.getVideoManager().startAVRecording(account);
    }

    /**
     * 结束录像
     */
    @Override
    public boolean stopAVRecording(String account) {
        return VideoManager.getVideoManager().stopAVRecording(account);
    }

    @Override
    public boolean isRemoteAudioMuted(String account) {
        return VideoManager.getVideoManager().isRemoteAudioMuted(account);
    }

    /**
     * 静音设置
     */
    @Override
    public void muteRemoteAudio(String account, boolean isMute) {
        VideoManager.getVideoManager().muteRemoteAudio(account, isMute);
    }

    @Override
    public boolean isLocalAudioMuted() {
        return VideoManager.getVideoManager().isLocalAudioMuted();
    }

    /**
     * 对讲设置
     */
    @Override
    public void muteLocalAudio(boolean isMute) {
        VideoManager.getVideoManager().muteLocalAudio(isMute);
    }

    @Override
    public void unlock() {
        VideoManager.getVideoManager().unlock();
    }

    /**
     * 摄像头切换
     */
    @Override
    public void switchRemoteCamera() {
        VideoManager.getVideoManager().switchRemoteCamera();
    }

    /**
     * ********************** 挂断相关操作 **********************
     */
    @Override
    public void hangUp(int type) {
        if (mDestroyRTC) {
            return;
        }
        if ((type == AVChatExitCode.HANGUP || type == AVChatExitCode.PEER_NO_RESPONSE
                || type == AVChatExitCode.CANCEL || type == AVChatExitCode.REJECT || type == AVChatExitCode.FREQUENCY_LIMIT) && mAVChatData != null) {
            VideoManager.getVideoManager().hangUp(mAVChatData.getChatId(), new AVChatCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }

                @Override
                public void onFailed(int code) {
                    L.e("----hangup onFailed->" + code);
                }

                @Override
                public void onException(Throwable exception) {
                    L.e("----hangup onException->" + exception);
                }
            });
        }
        closeRtc();
        showQuitToast(type);
    }

    @Override
    public void onHangUp(int type) {
        closeRtc();
        showQuitToast(type);
    }

    private void closeRtc() {
        if (mDestroyRTC) {
            return;
        }
        VideoManager.getVideoManager().endVideo();
        mDestroyRTC = true;
//        AVChatSoundPlayer.instance().stop();
    }

    // 显示退出toast
    private void showQuitToast(int code) {
        switch (code) {
            case AVChatExitCode.NET_CHANGE: // 网络切换
            case AVChatExitCode.NET_ERROR: // 网络异常
            case AVChatExitCode.CONFIG_ERROR: // 服务器返回数据错误
                T.show(R.string.avchat_net_error_then_quit);
                break;
            case AVChatExitCode.REJECT:
                T.show(R.string.avchat_call_reject);
                break;
            case AVChatExitCode.PEER_HANGUP:
            case AVChatExitCode.HANGUP:
                if (mIsCallEstablish.get()) {
                    T.show(R.string.avchat_call_finish);
                }
                break;
            case AVChatExitCode.PEER_BUSY:
                T.show(R.string.avchat_peer_busy);
                break;
            case AVChatExitCode.PROTOCOL_INCOMPATIBLE_PEER_LOWER:
                T.show(R.string.avchat_peer_protocol_low_version);
                break;
            case AVChatExitCode.PROTOCOL_INCOMPATIBLE_SELF_LOWER:
                T.show(R.string.avchat_local_protocol_low_version);
                break;
            case AVChatExitCode.INVALIDE_CHANNELID:
                T.show(R.string.avchat_invalid_channel_id);
                break;
            case AVChatExitCode.LOCAL_CALL_BUSY:
                T.show(R.string.avchat_local_call_busy);
                break;
            default:
                break;
        }
    }
}
