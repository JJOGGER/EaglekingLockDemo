package cn.jcyh.eaglekinglockdemo.control;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.bean.LockKey;
import com.ttlock.bl.sdk.bean.LockUser;

import java.util.List;

import cn.jcyh.eaglekinglockdemo.constant.Constants;
import cn.jcyh.eaglekinglockdemo.utils.SharePreUtil;

/**
 * Created by jogger on 2018/5/2.
 */

public class ControlCenter {
    private static ControlCenter sControlCenter;
    private Gson mGson;
    private static Context sContext;
    public static LockKey sCurrentKey;//记录当前操作的key

    private ControlCenter() {
        mGson = new Gson();
    }

    public static ControlCenter getControlCenter(Context context) {
        sContext = context.getApplicationContext();
        if (sControlCenter == null) {
            synchronized (ControlCenter.class) {
                if (sControlCenter == null)
                    sControlCenter = new ControlCenter();
            }
        }
        return sControlCenter;
    }

    public String getAccessToken() {
        LockUser userInfo = getUserInfo();
        if (userInfo != null)
            return userInfo.getAccess_token();
        return null;
    }

    public int getOpenId() {
        LockUser userInfo = getUserInfo();
        if (userInfo != null)
            return userInfo.getOpenid();
        return -1;
    }

    public void saveUserInfo(LockUser user) {
        SharePreUtil.getInstance(sContext).setString(Constants.USER_INFO, mGson.toJson(user));
    }

    public LockUser getUserInfo() {
        return mGson.fromJson(SharePreUtil.getInstance(sContext).getString(Constants.USER_INFO, ""), LockUser.class);
    }

    public void saveLockKeys(List<LockKey> keys) {
        SharePreUtil.getInstance(sContext).setString(Constants.KEY_LIST, mGson.toJson(keys));
    }

    public List<LockKey> getLockKeys() {
        return mGson.fromJson(SharePreUtil.getInstance(sContext).getString(Constants.KEY_LIST, ""), new TypeToken<List<LockKey>>() {
        }.getType());
    }
}
