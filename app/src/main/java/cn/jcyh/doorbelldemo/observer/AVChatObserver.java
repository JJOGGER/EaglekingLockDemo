package cn.jcyh.doorbelldemo.observer;


import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.avchat.model.AVChatData;

/**
 * Created by jogger on 2018/7/27.
 */
public class AVChatObserver implements Observer<AVChatData> {
    @Override
    public void onEvent(final AVChatData avChatData) {
//        L.e("----------AVChatObserver:" + avChatData.getExtra());
//        if (PhoneCallStateObserver.getInstance().getPhoneCallState() != PhoneCallStateObserver.PhoneCallStateEnum.IDLE
//                || AVChatProfile.getInstance().isAVChatting()
//                || AVChatManager.getInstance().getCurrentChatId() != 0) {
//            AVChatManager.getInstance().sendControlCommand(avChatData.getChatId(), AVChatControlCommand.BUSY, null);
//            return;
//        }
    }
}
