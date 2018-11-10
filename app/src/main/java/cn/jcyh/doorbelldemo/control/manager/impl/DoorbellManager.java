package cn.jcyh.doorbelldemo.control.manager.impl;

import android.text.TextUtils;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

import cn.jcyh.doorbelldemo.control.manager.IDoorbellManager;
import cn.jcyh.nimlib.observer.event.OnlineStateEventCache;
import cn.jcyh.nimlib.observer.event.OnlineStateEventManager;
import cn.jcyh.utils.ConstantUtil;
import cn.jcyh.utils.SPUtil;

/**
 * Created by jogger on 2018/8/7.
 */
public class DoorbellManager implements IDoorbellManager {

    /**
     * 获取猫眼列表
     */
    @Override
    public List<NimUserInfo> getDoorbells() {
        List<NimUserInfo> userInfoList = NIMClient.getService(UserService.class).getUserInfoList(getDoorbellIDs());
        return userInfoList == null ? new ArrayList<NimUserInfo>() : userInfoList;
    }

    /**
     * 获取猫眼id列表
     */
    @Override
    public List<String> getDoorbellIDs() {
        List<String> friendAccounts = NIMClient.getService(FriendService.class).getFriendAccounts();
        return friendAccounts == null ? new ArrayList<String>() : friendAccounts;
    }

    /**
     * 判断是否已绑定
     */
    @Override
    public boolean isBinded(String account) {
        return NIMClient.getService(FriendService.class).isMyFriend(account);
    }

    /**
     * 通过id获取猫眼
     */
    @Override
    public NimUserInfo getDoorbellByDeviceID(String deviceID) {
        if (TextUtils.isEmpty(deviceID)) return null;
        List<NimUserInfo> doorbells = getDoorbells();
        for (int i = 0; i < doorbells.size(); i++) {
            if (deviceID.equals(doorbells.get(i).getAccount()))
                return doorbells.get(i);
        }
        return null;
    }

    /**
     * 判断猫眼是否在线
     */
    @Override
    public boolean isOnline(String account) {
        OnlineStateEventManager.checkSubscribe(account);
        return OnlineStateEventCache.isOnline(account);
    }

    @Override
    public void setLastDoorbellID(String lastDoorbellID) {
        SPUtil.getInstance().put(ConstantUtil.LAST_DOORBELL_ID, lastDoorbellID);
    }

    /**
     * 获取最后保存的猫眼id
     */
    @Override
    public String getLastDoorbellID() {
        return SPUtil.getInstance().getString(ConstantUtil.LAST_DOORBELL_ID, "");
    }
}
