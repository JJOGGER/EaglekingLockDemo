//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.net;

import java.util.HashMap;

public class ResponseService {
    private static final String TAG = "ResponseService";
    private static String actionUrl = "https://api.ttlock.com.cn";
    private static String actionUrlV3;

    public ResponseService() {
    }

    public static String getRecoverData(String clientId, String accessToken, int lockId) {
        String url = actionUrlV3 + "/lock/getRecoverData";
        HashMap params = new HashMap();
        params.put("clientId", clientId);
        params.put("accessToken", accessToken);
        params.put("lockId", String.valueOf(lockId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    public static String getUpgradePackage(String clientId, String accessToken, int lockId) {
        String url = actionUrlV3 + "/lock/getUpgradePackage";
        HashMap params = new HashMap();
        params.put("clientId", clientId);
        params.put("accessToken", accessToken);
        params.put("lockId", String.valueOf(lockId));
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    public static String uploadOperateLog(String clientId, String accessToken, int lockId, String records) {
        String url = actionUrlV3 + "/lockRecord/upload";
        HashMap params = new HashMap();
        params.put("clientId", clientId);
        params.put("accessToken", accessToken);
        params.put("lockId", String.valueOf(lockId));
        params.put("records", records);
        params.put("date", String.valueOf(System.currentTimeMillis()));
        return OkHttpRequest.sendPost(url, params);
    }

    static {
        actionUrlV3 = actionUrl + "/v3";
    }
}
