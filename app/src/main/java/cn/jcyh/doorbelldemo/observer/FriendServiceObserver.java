package cn.jcyh.doorbelldemo.observer;

import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.friend.model.FriendChangedNotify;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.jcyh.doorbelldemo.bus.nim.NIMFriendAction;
import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.nimlib.observer.event.OnlineStateEventSubscribe;
import cn.jcyh.utils.L;

/**
 * Created by jogger on 2018/8/4.
 */
public class FriendServiceObserver implements Observer<FriendChangedNotify> {
    @Override
    public void onEvent(FriendChangedNotify friendChangedNotify) {
        List<Friend> addedOrUpdatedFriends = friendChangedNotify.getAddedOrUpdatedFriends(); // 新增的好友
        L.e("----------------addedOrUpdatedFriends:" + addedOrUpdatedFriends);
        if (addedOrUpdatedFriends != null && addedOrUpdatedFriends.size() != 0) {
            NIMFriendAction friendAction = new NIMFriendAction();
            friendAction.setType(NIMFriendAction.TYPE_ADD_DOORBELL);
            if (addedOrUpdatedFriends.size() == 1)
                friendAction.putExtra(Constant.FROM_ACCOUNT, addedOrUpdatedFriends.get(0).getAccount());
            EventBus.getDefault().post(friendAction);
            // 订阅在线状态
            OnlineStateEventSubscribe.initSubscribes();
        }
        List<String> deletedFriendAccounts = friendChangedNotify.getDeletedFriends(); // 删除好友或者被解除好友
        L.e("----------------deletedFriendAccounts:" + deletedFriendAccounts);
        if (deletedFriendAccounts != null && deletedFriendAccounts.size() != 0) {
            NIMFriendAction friendAction = new NIMFriendAction();
            friendAction.setType(NIMFriendAction.TYPE_DELETE_DOORBELL);
            EventBus.getDefault().post(friendAction);
        }
    }
}