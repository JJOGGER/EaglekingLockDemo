//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lock.bl.sdk.scanner;

import android.os.Build;
import android.os.Build.VERSION;

import com.lock.bl.sdk.service.ThreadPool;

import java.util.UUID;

public abstract class ScannerCompat {
    private static boolean DBG = true;
    private static ScannerCompat mInstance;
    protected static String UUID_SERVICE = "00001910-0000-1000-8000-00805f9b34fb";
    protected static UUID[] serviceUuids;
    protected IScanCallback mIScanCallback;
    protected boolean scanBongOnly;

    public ScannerCompat() {
    }

    public static ScannerCompat getScanner() {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mInstance = new ScannerLollipop();
        else mInstance = new ScannerImplJB();
        return mInstance;
    }

    public void startScan(final IScanCallback scanCallback) {
        ThreadPool.getThreadPool().execute(new Runnable() {
            public void run() {
                mIScanCallback = scanCallback;
                startScanInternal(serviceUuids);
            }
        });
    }

    public abstract void startScanInternal(UUID[] var1);

    public abstract void stopScan();

    public void setScanBongOnly(boolean scanBongOnly) {
        this.scanBongOnly = scanBongOnly;
    }

    static {
        serviceUuids = new UUID[]{UUID.fromString(UUID_SERVICE)};
    }
}
