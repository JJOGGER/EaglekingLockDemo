//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanSettings.Builder;
import android.os.ParcelUuid;
import android.support.annotation.RequiresPermission;

import com.ttlock.bl.sdk.service.BluetoothLeService;
import com.ttlock.bl.sdk.util.LogUtil;

import java.util.ArrayList;
import java.util.UUID;

@TargetApi(21)
public class ScannerLollipop extends ScannerCompat {
    private static final boolean DBG = true;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ScanCallbackImpl scanCallback = new ScanCallbackImpl();
    private BluetoothLeScanner scanner;

    public ScannerLollipop() {
    }

    @RequiresPermission(
            allOf = {"android.permission.BLUETOOTH_ADMIN", "android.permission.BLUETOOTH"}
    )
    public void startScanInternal(UUID[] serviceUuids) {
        this.scanner = this.mBluetoothAdapter.getBluetoothLeScanner();
        if (this.scanner == null) {
            LogUtil.w("BT le scanner not available", true);
        } else {
            ScanSettings settings = (new Builder()).setScanMode(2).build();
            ArrayList filters = new ArrayList();
            filters.add((new android.bluetooth.le.ScanFilter.Builder()).setServiceUuid(ParcelUuid.fromString(UUID_SERVICE)).build());
            if (BluetoothLeService.scanBongOnly) {
                filters = null;
            }
            this.scanner.startScan(filters, settings, this.scanCallback);
        }

    }

    @RequiresPermission(
            allOf = {"android.permission.BLUETOOTH_ADMIN", "android.permission.BLUETOOTH"}
    )
    public void stopScan() {
        if (this.scanner != null && this.scanCallback != null && this.mBluetoothAdapter.isEnabled()) {
            this.scanner.stopScan(this.scanCallback);
            LogUtil.d("scanCallback:" + this.scanCallback, true);
        }

    }

    private class ScanCallbackImpl extends ScanCallback {
        private ScanCallbackImpl() {
        }

        @RequiresPermission(
                allOf = {"android.permission.BLUETOOTH"}
        )
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            ScannerLollipop.this.mIScanCallback.onScan(new ExtendedBluetoothDevice(result));
        }

        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            LogUtil.w("errorCode=" + errorCode, true);
        }
    }
}
