package cn.jcyh.eaglekinglockdemo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.ttlock.bl.sdk.api.LockAPI;

import cn.jcyh.eaglekinglockdemo.callback.MyLockCallback;
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
        LockAPI.initCallback(new MyLockCallback(this));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
