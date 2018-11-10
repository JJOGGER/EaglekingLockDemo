package cn.jcyh.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;

import cn.jcyh.eaglekinglockdemo.BuildConfig;
import cn.jcyh.eaglekinglockdemo.callback.MyLockCallback;
import cn.jcyh.eaglekinglockdemo.config.LockConfig;
import cn.jcyh.eaglekinglockdemo.http.MyLockAPI;


/**
 * Created by jogger on 2018/5/25.
 */

public class Utils {
    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Init utils.
     * <p>Init it in the class of Application.</p>
     *
     * @param context context
     */
    public static void init(@NonNull final Context context) {
        init((Application) context.getApplicationContext());
    }

    /**
     * Init utils.
     * <p>Init it in the class of Application.</p>
     *
     * @param app application
     */
    public static void init(@NonNull final Application app) {
        if (sApplication == null) {
            Utils.sApplication = app;
        }
        if (BuildConfig.DEBUG)
            L.plant(new L.DebugTree());
        //初始化蓝牙锁
//        MyLockAPI.init(app, new MyLockCallback(app));
        MyLockAPI.init(app, new MyLockCallback(app), LockConfig.CLIENT_ID,LockConfig.CLIENT_SECRET);
    }

    /**
     * @return the context of Application object
     */
    public static Application getApp() {
        if (sApplication != null) return sApplication;
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object at = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(at);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            init((Application) app);
            return sApplication;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }
}
