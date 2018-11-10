package cn.jcyh.doorbelldemo.observer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import cn.jcyh.doorbelldemo.bus.nim.NIMMessageTextAction;
import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.doorbelldemo.function.ui.DoorbellCallActivity;
import cn.jcyh.nimlib.entity.CommandJson;
import cn.jcyh.nimlib.utils.GsonUtil;
import cn.jcyh.utils.L;
import cn.jcyh.utils.NotificationUtil;
import cn.jcyh.utils.Utils;

/**
 * Created by jogger on 2018/7/26.
 */
public class MessageReceiveObserver implements Observer<List<IMMessage>> {
    private static final int NET_PUSH = 0;
    private static final int VIDEO_PUSH = 1;
    private static final int UPLOAD_POWER = 2;

    @Override
    public void onEvent(List<IMMessage> imMessages) {
        L.e("-----------收到消息：" + imMessages.size());
        for (int i = 0; i < imMessages.size(); i++) {
            L.e("---------->内容：" + imMessages.get(i).getContent() + "，来自：" + imMessages.get(i).getFromAccount()
                    + ",消息类型：" + imMessages.get(i).getMsgType() + ":" + imMessages.get(i).getRemoteExtension());
            IMMessage message = imMessages.get(i);
            String content = message.getContent();
            if (message.getMsgType() == MsgTypeEnum.text) {
                NIMMessageTextAction event = new NIMMessageTextAction();
                CommandJson commandJson = null;
                try {
                    commandJson = GsonUtil.fromJson(content, CommandJson.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (commandJson == null) {
                    //获取服务器的推送格式
                    Map<String, Object> remoteExtension = message.getRemoteExtension();
                    String s = GsonUtil.toJson(remoteExtension);
                    L.e("------------:entries" + s);
                    try {
                        commandJson = GsonUtil.fromJson(s, CommandJson.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    L.e("--------command:" + commandJson);
                    if (commandJson == null || commandJson.getCommandType() == null) return;
                    if (commandJson.getFlag2() == VIDEO_PUSH) {
                        //呼叫
                        switch (commandJson.getCommandType()) {
                            case CommandJson.CommandType.DOORBELL_NOTIFICATION:
                                //视频呼叫
                                Intent intent = new Intent(Utils.getApp(), DoorbellCallActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(Constant.COMMAND, commandJson);
                                bundle.putString(Constant.FROM_ACCOUNT, message.getFromAccount());
                                bundle.putString(Constant.FILE_PATH, commandJson.getFlag());
                                intent.putExtras(bundle);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                Utils.getApp().startActivity(intent);
                                break;
                        }
                    } else {
                        //电量
                        NotificationUtil.getNotificationUtil().createNotification("1", "doorbellPush", content);
                    }
                    event.putExtra(Constant.IM_MESSAGE, message);
                    event.putExtra(Constant.COMMAND, commandJson);
                    event.putExtra(Constant.FROM_ACCOUNT, message.getFromAccount());
                    EventBus.getDefault().post(event);
                    return;
                }
                //本地定义的消息
                if (TextUtils.isEmpty(commandJson.getCommandType())) return;
                switch (commandJson.getCommandType()) {
                    case CommandJson.CommandType.DOORBELL_PARAMS_GET_RESPONSE:
                        break;
                }
                event.putExtra(Constant.IM_MESSAGE, message);
                event.putExtra(Constant.COMMAND, commandJson);
                event.putExtra(Constant.FROM_ACCOUNT, message.getFromAccount());
                EventBus.getDefault().post(event);
            }
        }
    }


}
