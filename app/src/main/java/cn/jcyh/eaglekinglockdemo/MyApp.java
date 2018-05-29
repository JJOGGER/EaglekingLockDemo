package cn.jcyh.eaglekinglockdemo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import cn.jcyh.eaglekinglockdemo.callback.MyLockCallback;
import cn.jcyh.eaglekinglockdemo.config.LockConfig;
import cn.jcyh.eaglekinglockdemo.http.MyLockAPI;
import cn.jcyh.eaglekinglockdemo.utils.Timber;

/**
 * Created by jogger on 2018/4/26.
 */

public class MyApp extends Application {
    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        MyLockAPI.init(this, new MyLockCallback(this), LockConfig.CLIENT_ID, LockConfig.CLIENT_SECRET);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
