package cn.jcyh.eaglekinglockdemo.control;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import cn.jcyh.eaglekinglockdemo.constant.Constants;
import cn.jcyh.eaglekinglockdemo.entity.LockKey;
import cn.jcyh.eaglekinglockdemo.entity.LockUser;
import cn.jcyh.eaglekinglockdemo.entity.LoginData;
import cn.jcyh.utils.SPUtil;

/**
 * Created by jogger on 2018/5/2.
 */

public class ControlCenter {
    private static ControlCenter sControlCenter;
    private Gson mGson;
    private static Context sContext;
    public static LockKey sCurrentKey;//记录当前操作的key
    public static int sOpenID;

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
        if (sOpenID == -1) {
            LockUser userInfo = getUserInfo();
            if (userInfo != null)
                sOpenID = userInfo.getOpenid();
        }
        LockUser userInfo = getUserInfo();
        if (userInfo != null)
            return userInfo.getOpenid();
        return -1;
    }

    public void saveUserInfo(LockUser user) {
        SPUtil.getInstance().put(Constants.USER_INFO, mGson.toJson(user));
    }

    public LockUser getUserInfo() {
        return mGson.fromJson(SPUtil.getInstance().getString(Constants.USER_INFO, ""), LockUser.class);
    }
    public void saveLoginData(LoginData loginData) {
        SPUtil.getInstance().put(Constants.LOGIN_DATA, mGson.toJson(loginData));
    }

    public LoginData getLoginData() {
        return mGson.fromJson(SPUtil.getInstance().getString(Constants.LOGIN_DATA, ""), LoginData.class);
    }
    public void saveLockKeys(List<LockKey> keys) {
        SPUtil.getInstance().put(Constants.KEY_LIST, mGson.toJson(keys));
    }

    public List<LockKey> getLockKeys() {
        return mGson.fromJson(SPUtil.getInstance().getString(Constants.KEY_LIST, ""), new TypeToken<List<LockKey>>() {
        }.getType());
    }
}
