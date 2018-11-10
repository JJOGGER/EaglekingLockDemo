package cn.jcyh.doorbelldemo.function.contract;

import com.netease.nimlib.sdk.avchat.model.AVChatData;

import cn.jcyh.doorbelldemo.avchat.AVChatControllerCallback;
import cn.jcyh.doorbelldemo.base.BaseModel;
import cn.jcyh.doorbelldemo.base.BaseView;
import cn.jcyh.doorbelldemo.base.IPresenter;

/**
 * Created by jogger on 2018/7/5.
 */
public interface DoorbellVideoContract {
    interface Model extends BaseModel {

        AVChatData getAvChatData();

        void doCalling(String account, final AVChatControllerCallback<AVChatData> callback);

        void takeSnapshot(String account);

        boolean startAVRecording(String account);

        boolean stopAVRecording(String account);

        boolean isRemoteAudioMuted(String account);

        void muteRemoteAudio(String account, boolean isMute);

        boolean isLocalAudioMuted();

        void muteLocalAudio(boolean isMute);

        void unlock();

        void switchRemoteCamera();

        void hangUp(int type);

        void onHangUp(int type);

        void setIsCallEstablish(boolean isCallEstablish);

    }

    interface View extends BaseView {
        String getDeviceID();

        void initSurfaceView();

        void startLoadAnim();

        void endLoadAnim();

        void toggleLocalMute(boolean isMute);

        void toggleRemoteMute(boolean isMute);

        void toggleAVRecording(boolean isRecording);

        void unLockSuccess();

        void initLockState();

        void finishVideo();

        void functionEnable(boolean enable);

        void showIsDualCamera();

    }

    interface Presenter extends IPresenter<View, Model> {

        void startLockTimer();

        void cancelLockTimer();

        void sendUnlockRequest();


        void changeCamera();

        void finishVideoCall();

        void startCutShort();

        void toggleAVRecording();

        void toggleLocalMute();

        void toggleRemoteMute();

    }
}
