//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.callback;

public interface DeviceFirmwareUpdateCallback {
    void onGetLockFirmware(int var1, String var2, String var3, String var4);

    void onStatusChanged(int var1);

    void onDfuProcessStarting(String var1);

    void onEnablingDfuMode(String var1);

    void onDfuCompleted(String var1);

    void onDfuAborted(String var1);

    void onProgressChanged(String var1, int var2, float var3, float var4, int var5, int var6);

    void onError(int var1, Error var2, String var3);
}
