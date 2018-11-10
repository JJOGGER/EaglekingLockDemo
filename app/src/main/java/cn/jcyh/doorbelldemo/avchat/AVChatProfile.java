package cn.jcyh.doorbelldemo.avchat;

/**
 * Created by huangjun on 2015/5/12.
 */
public class AVChatProfile {

    private final String TAG = "AVChatProfile";

    private boolean isAVChatting = false; // 是否正在音视频通话
    private String chattingAccount = "";

    public static AVChatProfile getInstance() {
        return InstanceHolder.instance;
    }

    public boolean isAVChatting() {
        return isAVChatting;
    }

    public void setAVChatting(boolean chating) {
        isAVChatting = chating;
    }

    public String getChattingAccount() {
        return chattingAccount;
    }

    public void setChattingAccount(String chattingAccount) {
        this.chattingAccount = chattingAccount;
    }


    private static class InstanceHolder {
        public final static AVChatProfile instance = new AVChatProfile();
    }
}