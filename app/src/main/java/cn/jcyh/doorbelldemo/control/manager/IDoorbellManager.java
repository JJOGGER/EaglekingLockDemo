package cn.jcyh.doorbelldemo.control.manager;

import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;

/**
 * Created by jogger on 2018/8/7.
 */
public interface IDoorbellManager {
    List<NimUserInfo> getDoorbells();

    List<String> getDoorbellIDs();

    boolean isBinded(String account);

    NimUserInfo getDoorbellByDeviceID(String account);

    boolean isOnline(String account);

    void setLastDoorbellID(String lastDoorbellID);

    String getLastDoorbellID();

}
