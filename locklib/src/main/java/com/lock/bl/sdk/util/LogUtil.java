//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lock.bl.sdk.util;

import android.util.Log;

public class LogUtil {
    private static boolean DBG = true;
    private static String callerClazzName;
    private static String callerMethodName;
    private static int callerLineNumber;
    private static final String msg = "%s(L:%d) - %s";

    public LogUtil() {
    }

    public static void setDBG(boolean DBG) {
        LogUtil.DBG = DBG;
    }

    public static boolean isDBG() {
        return DBG;
    }

    private static void generateCallerInfo() {
        StackTraceElement caller = (new Throwable()).getStackTrace()[2];
        callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        callerMethodName = caller.getMethodName();
        callerLineNumber = caller.getLineNumber();
    }

    public static void d(String content, boolean DBG) {
        if(LogUtil.DBG && DBG) {
            generateCallerInfo();
            Log.d(callerClazzName, String.format("%s(L:%d) - %s", new Object[]{callerMethodName, Integer.valueOf(callerLineNumber), content}));
        }

    }

    public static void i(String content, boolean DBG) {
        if(LogUtil.DBG && DBG) {
            generateCallerInfo();
            Log.i(callerClazzName, String.format("%s(L:%d) - %s", new Object[]{callerMethodName, Integer.valueOf(callerLineNumber), content}));
        }

    }

    public static void w(String content, boolean DBG) {
        if(LogUtil.DBG) {
            generateCallerInfo();
            Log.w(callerClazzName, String.format("%s(L:%d) - %s", new Object[]{callerMethodName, Integer.valueOf(callerLineNumber), content}));
        }

    }

    public static void e(String content, boolean DBG) {
        if(LogUtil.DBG) {
            generateCallerInfo();
            Log.e(callerClazzName, String.format("%s(L:%d) - %s", new Object[]{callerMethodName, Integer.valueOf(callerLineNumber), content}));
        }

    }
}
