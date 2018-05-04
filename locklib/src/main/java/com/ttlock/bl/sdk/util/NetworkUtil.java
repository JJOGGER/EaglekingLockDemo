//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetworkUtil {
    public NetworkUtil() {
    }

    public static String getWifiSSid(Context context) {
        WifiManager wifiManager = (WifiManager)context.getSystemService("wifi");
        String ssid = "";
        if(wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if(wifiInfo != null) {
                ssid = wifiInfo.getSSID();
                if(ssid.length() > 2 && ssid.charAt(0) == 34 && ssid.charAt(ssid.length() - 1) == 34) {
                    ssid = ssid.substring(1, ssid.length() - 1);
                }
            }
        }

        return ssid;
    }

    public static boolean isNetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService("connectivity");
        boolean flag = false;
        if(connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null) {
            flag = connectivityManager.getActiveNetworkInfo().isConnected();
        }

        return flag;
    }
}
