package cn.jcyh.doorbelldemo.control;

import android.content.Intent;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;

import cn.jcyh.doorbelldemo.control.manager.IDoorbellManager;
import cn.jcyh.doorbelldemo.control.manager.IUserManager;
import cn.jcyh.doorbelldemo.control.manager.impl.DoorbellManager;
import cn.jcyh.doorbelldemo.control.manager.impl.UserManager;
import cn.jcyh.doorbelldemo.observer.LoginSyncDataStatusObserver;
import cn.jcyh.doorbelldemo.service.MainService;
import cn.jcyh.nimlib.command.ndk.NIMSDK;
import cn.jcyh.utils.Utils;


/**
 * Created by jogger on 2017/3/17.
 * 控制中心
 */

public class ControlCenter {
    public static boolean sIsCalling = false;
    private static IDoorbellManager sDoorbellManager;//猫眼的提供者
    private static IUserManager sUserManager;

    public static IDoorbellManager getDoorbellManager() {
        if (sDoorbellManager == null)
            sDoorbellManager = new DoorbellManager();
        return sDoorbellManager;
    }

    public static IUserManager getUserManager() {
        if (sUserManager == null)
            sUserManager = new UserManager();
        return sUserManager;
    }


    /**
     * 退出
     */
    public static void logout() {
        logout(false);

    }

    public static void logout(boolean isOtherLogin) {
        //退出登录
        getUserManager().logout();
//                PushManager.clearPush();
        Intent intent = new Intent(Utils.getApp(), MainService.class);
        Utils.getApp().stopService(intent);
        intent = new Intent(Utils.getApp(), MainService.class);
        Utils.getApp().stopService(intent);
        NIMSDK.logout();
        LoginSyncDataStatusObserver.getInstance().reset();
        NIMClient.getService(AuthService.class).logout();
        ActivityCollector.finishAllOnlyLogin(isOtherLogin);
    }

    public class DeviceType {
        public static final int TYPE_DOORBELL = 0X001;
        public static final int TYPE_GATEWAY = 0X003;
    }
}
