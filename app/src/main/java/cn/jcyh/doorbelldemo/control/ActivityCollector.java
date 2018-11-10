package cn.jcyh.doorbelldemo.control;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.doorbelldemo.function.ui.LoginActivity;
import cn.jcyh.utils.L;
import cn.jcyh.utils.Utils;


/**
 * Created by it on 2017/3/6.
 * 窗体管理工具
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity :
                activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }


    public static void finishAllOnlyLogin(boolean isOtherLogin) {
        for (Activity activity :
                activities) {
            activity.finish();
            L.i("--->" + activity.getLocalClassName() + "isfinished");
        }
        Intent intent = new Intent(Utils.getApp(), LoginActivity.class);
        intent.putExtra(Constant.IS_OTHER_LOGIN,isOtherLogin);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Utils.getApp().startActivity(intent);
    }

    public static void finishActivity(Class cls) {
        for (Activity activity :
                activities) {
            if (activity.getClass() == cls) {
                activity.finish();
                break;
            }
        }
    }

    public static boolean isRunning(Class cls) {
        for (Activity activity :
                activities) {
            if (activity.getClass() == cls) {
                return true;
            }
        }
        return false;
    }
}
