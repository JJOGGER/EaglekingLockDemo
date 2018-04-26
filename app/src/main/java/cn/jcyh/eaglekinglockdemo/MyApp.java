package cn.jcyh.eaglekinglockdemo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import cn.jcyh.eaglekinglockdemo.utils.Timber;

/**
 * Created by jogger on 2018/4/26.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
