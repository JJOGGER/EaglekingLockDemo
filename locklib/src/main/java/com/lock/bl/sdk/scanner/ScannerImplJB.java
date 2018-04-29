//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lock.bl.sdk.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.RequiresPermission;

import com.lock.bl.sdk.service.BluetoothLeService;
import com.lock.bl.sdk.util.LogUtil;

import java.util.UUID;

public class ScannerImplJB extends ScannerCompat implements LeScanCallback {
    private static boolean DBG = true;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public ScannerImplJB() {
    }

    @RequiresPermission(
            allOf = {"android.permission.BLUETOOTH_ADMIN", "android.permission.BLUETOOTH"}
    )
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        this.mIScanCallback.onScan(new ExtendedBluetoothDevice(device, rssi, scanRecord));
    }

    @RequiresPermission(
            allOf = {"android.permission.BLUETOOTH_ADMIN", "android.permission.BLUETOOTH"}
    )
    public void startScanInternal(UUID[] serviceUuids) {
        LogUtil.d(this.toString(), DBG);
        if(BluetoothLeService.scanBongOnly) {
            this.mBluetoothAdapter.startLeScan(this);
        } else {
            this.mBluetoothAdapter.startLeScan(serviceUuids, this);
        }

    }

    @RequiresPermission(
            allOf = {"android.permission.BLUETOOTH_ADMIN", "android.permission.BLUETOOTH"}
    )
    public void stopScan() {
        try {
            if(this.mBluetoothAdapter.isEnabled()) {
                LogUtil.d(this.toString(), DBG);
                this.mBluetoothAdapter.stopLeScan(this);
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }
}
