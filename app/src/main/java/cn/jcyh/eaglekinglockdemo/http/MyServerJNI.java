package cn.jcyh.eaglekinglockdemo.http;

/**
 * Created by jogger on 2018/4/11.
 */

public class MyServerJNI {
    static {
        System.loadLibrary("my_server");
    }

    public static native String getServerUrl();

    public static native String getServerKey();
}
