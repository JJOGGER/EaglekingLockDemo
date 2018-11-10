package cn.jcyh.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jogger on 2017/4/12.
 */

public class UserSharePreUtil {
    private static UserSharePreUtil mSharePreUtil;
    private static Context sContext;
    private static SharedPreferences sPreferencesUser;

    private UserSharePreUtil() {

    }

    public static UserSharePreUtil getInstance(Context context) {
        sContext = context.getApplicationContext();
        if (mSharePreUtil == null) {
            synchronized (UserSharePreUtil.class) {
                if (mSharePreUtil == null) {
                    mSharePreUtil = new UserSharePreUtil();
                }
            }
        }
        sPreferencesUser = sContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return mSharePreUtil;
    }


    /**
     * 保存在手机里的文件名
     */
    private static final String FILE_NAME = "jcyh_data";

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = sPreferencesUser.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = sPreferencesUser.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = sPreferencesUser.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void setBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sPreferencesUser.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public String getString(String key, String value) {
        return sPreferencesUser.getString(key, value);
    }

    public int getInt(String key, int value) {
        return sPreferencesUser.getInt(key, value);
    }

    public long getLong(String key, long value) {
        return sPreferencesUser.getLong(key, value);
    }

    public boolean getBoolean(String key, boolean value) {
        return sPreferencesUser.getBoolean(key, value);
    }

}