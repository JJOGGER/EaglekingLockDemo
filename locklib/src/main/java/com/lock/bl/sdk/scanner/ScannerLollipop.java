//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lock.bl.sdk.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanSettings.Builder;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;

import com.lock.bl.sdk.service.BluetoothLeService;
import com.lock.bl.sdk.util.Timber;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScannerLollipop extends ScannerCompat {
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ScanCallbackImpl scanCallback = new ScanCallbackImpl();
    private BluetoothLeScanner scanner;

    public ScannerLollipop() {
    }

    public void startScanInternal(UUID[] serviceUuids) {
        this.scanner = this.mBluetoothAdapter.getBluetoothLeScanner();
        if (this.scanner == null) {
            Timber.e("----------BT le scanner not available", true);
        } else {
            ScanSettings settings = (new Builder()).setScanMode(2).build();
            List<ScanFilter> filters = new ArrayList<>();
            ScanFilter build = (new ScanFilter.Builder()).setServiceUuid(ParcelUuid.fromString(UUID_SERVICE)).build();
            filters.add(build);
            Timber.e("----------startScanInternal---"+BluetoothLeService.scanBongOnly);
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
            Timber.e("----------scanCallback:" + this.scanCallback, true);
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
            Timber.e("----------errorCode=" + errorCode);
        }
    }
}
