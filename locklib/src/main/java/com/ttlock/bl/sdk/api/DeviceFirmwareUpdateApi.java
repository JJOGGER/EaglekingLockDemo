//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.api;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.callback.DeviceFirmwareUpdateCallback;
import com.ttlock.bl.sdk.entity.DeviceInfo;
import com.ttlock.bl.sdk.entity.LockUpdateInfo;
import com.ttlock.bl.sdk.net.ResponseService;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;
import com.ttlock.bl.sdk.service.BluetoothLeService;
import com.ttlock.bl.sdk.service.DfuService;
import com.ttlock.bl.sdk.service.ThreadPool;
import com.ttlock.bl.sdk.util.AESUtil;
import com.ttlock.bl.sdk.util.DigitUtil;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.ttlock.bl.sdk.util.LogUtil;
import com.ttlock.bl.sdk.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DeviceFirmwareUpdateApi {
    private boolean DBG = true;
    private Context mContext;
    private LockAPI mLockAPI;
    private String clientId;
    private String accessToken;
    private int lockid;
    String adminPs;
    String unlockKey;
    int lockFlagPos;
    String lockmac;
    private String lockVersion;
    private String aesKeyStr;
    private long timezoneOffset;
    private String pwdJson;
    private String ICJson;
    private String FRJson;
    private String operateLog;
    private int bleStatus;
    public static final int STATE_CONNECTING = 0;
    public static final int STATE_SUCCESS = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_DISCONNECTED = 3;
    public static final int STATE_ERROR = 4;
    private DeviceFirmwareUpdateCallback deviceFirmwareUpdateCallback;
    private byte currCommand;
    private int upgradeStatus;
    public static final int GetDeviceInfo = -1;
    public static final int UpgradeOprationPreparing = 1;
    public static final int UpgradeOprationUpgrading = 2;
    public static final int UpgradeOprationRecovering = 3;
    public static final int UpgradeOprationSuccess = 4;
    public static final int UploadOperateLog = 5;
    public static final int Download = 6;
    public static final int GetData = 7;
    public static final int EnterDfu = 8;
    public static final int DfuFailed = 1;
    public static final int BLEDisconnected = 2;
    public static final int BLECommandError = 3;
    public static final int RequestError = 4;
    public static final int NetError = 5;
    private Handler handler;
    private int attemptTime = 0;
    private Runnable timeOutRunnable = new Runnable() {
        public void run() {
            LogUtil.w("进入DFU超时", DeviceFirmwareUpdateApi.this.DBG);
            DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onError(1, null, "");
        }
    };
    private DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        public void onDeviceConnecting(String deviceAddress) {
            LogUtil.d("deviceAddress:" + deviceAddress, DeviceFirmwareUpdateApi.this.DBG);
        }

        public void onDfuProcessStarting(String deviceAddress) {
            LogUtil.d("deviceAddress:" + deviceAddress, DeviceFirmwareUpdateApi.this.DBG);
            DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onDfuProcessStarting(deviceAddress);
        }

        public void onEnablingDfuMode(String deviceAddress) {
            DeviceFirmwareUpdateApi.this.handler.removeCallbacks(DeviceFirmwareUpdateApi.this.timeOutRunnable);
            DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onEnablingDfuMode(deviceAddress);
            LogUtil.d("deviceAddress:" + deviceAddress, DeviceFirmwareUpdateApi.this.DBG);
        }

        public void onFirmwareValidating(String deviceAddress) {
            LogUtil.d("deviceAddress:" + deviceAddress, DeviceFirmwareUpdateApi.this.DBG);
        }

        public void onDeviceDisconnecting(String deviceAddress) {
            LogUtil.d("deviceAddress:" + deviceAddress, DeviceFirmwareUpdateApi.this.DBG);
        }

        public void onDfuCompleted(String deviceAddress) {
            LogUtil.d("deviceAddress:" + deviceAddress, DeviceFirmwareUpdateApi.this.DBG);
            DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onDfuCompleted(deviceAddress);
            DfuServiceListenerHelper.unregisterProgressListener(DeviceFirmwareUpdateApi.this.mContext, DeviceFirmwareUpdateApi.this.mDfuProgressListener);
            (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                public void run() {
                    DeviceFirmwareUpdateApi.this.attemptTime = 1;
                    DeviceFirmwareUpdateApi.this.recoveryData();
                }
            }, 4500L);
        }

        public void onDfuAborted(String deviceAddress) {
            DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onDfuAborted(deviceAddress);
            DeviceFirmwareUpdateApi.this.attemptTime = 0;
            LogUtil.d("deviceAddress:" + deviceAddress, DeviceFirmwareUpdateApi.this.DBG);
        }

        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onProgressChanged(deviceAddress, percent, speed, avgSpeed, currentPart, partsTotal);
        }

        public void onError(String deviceAddress, int error, int errorType, String message) {
            LogUtil.d("deviceAddress:" + deviceAddress, DeviceFirmwareUpdateApi.this.DBG);
            DeviceFirmwareUpdateApi.this.handler.removeCallbacks(DeviceFirmwareUpdateApi.this.timeOutRunnable);
            DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onError(1, null, message);
        }
    };
    public static final String UPDATE_FILE_NAME = "realUpdate.zip";
    private String mUpdateFilePath;
    UpgradeFirmwareListener upgradeFirmwareListener = new UpgradeFirmwareListener() {
        public void onUpgradeFirmwareChanged(byte command, Object data, int status) {
            switch(status) {
                case 1:
                    DeviceFirmwareUpdateApi.this.bleStatus = 1;
                    DeviceFirmwareUpdateApi.this.responseCommand(command, data);
                    break;
                case 2:
                    DeviceFirmwareUpdateApi.this.bleStatus = 2;
                    DeviceFirmwareUpdateApi.this.deviceConnectedSendCommand();
                    break;
                case 3:
                    if(DeviceFirmwareUpdateApi.this.bleStatus != 0 && DeviceFirmwareUpdateApi.this.bleStatus != 2) {
                        if(DeviceFirmwareUpdateApi.this.currCommand == 2) {
                            DeviceFirmwareUpdateApi.this.startDfu();
                        }
                    } else if(DeviceFirmwareUpdateApi.this.attemptTime == 1) {
                        LogUtil.w("连接失败 重试一次", DeviceFirmwareUpdateApi.this.DBG);
                        DeviceFirmwareUpdateApi.this.attemptTime++;
                        DeviceFirmwareUpdateApi.this.retry();
                    } else {
                        DeviceFirmwareUpdateApi.this.attemptTime = 0;
                        DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onError(2, null, "");
                    }
                    break;
                case 4:
                    DeviceFirmwareUpdateApi.this.bleStatus = 4;
                    DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onError(3, (Error) data, "");
            }

        }
    };

    public void abortUpgradeProcess() {
        LogUtil.d("退出升级模式", this.DBG);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this.mContext);
        Intent pauseAction = new Intent("no.nordicsemi.android.dfu.broadcast.BROADCAST_ACTION");
        pauseAction.putExtra("no.nordicsemi.android.dfu.extra.EXTRA_ACTION", 2);
        manager.sendBroadcast(pauseAction);
        BluetoothLeService.getBluetoothLeService().setUpgradeFirmwareListener((UpgradeFirmwareListener)null);
    }

    public void upgradeComplete() {
        BluetoothLeService.getBluetoothLeService().setUpgradeFirmwareListener((UpgradeFirmwareListener)null);
        DfuServiceListenerHelper.unregisterProgressListener(this.mContext, this.mDfuProgressListener);
    }

    public DeviceFirmwareUpdateApi() {
    }

    public DeviceFirmwareUpdateApi(Context context, LockAPI lockAPI, DeviceFirmwareUpdateCallback deviceFirmwareUpdateCallback) {
        this.mContext = context;
        this.mLockAPI = lockAPI;
        this.deviceFirmwareUpdateCallback = deviceFirmwareUpdateCallback;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void retry() {
        LogUtil.d("重试:" + this.upgradeStatus, this.DBG);
        ++this.attemptTime;
        switch(this.upgradeStatus) {
            case -1:
                this.getSpecivalValue();
            case 0:
            case 4:
            default:
                break;
            case 1:
                this.getOperateLog(this.lockmac, this.lockVersion, this.aesKeyStr, this.timezoneOffset);
                break;
            case 2:
                this.enableLockDfuMode();
                break;
            case 3:
                this.recoveryData();
                break;
            case 5:
                this.uploadOperateLog(this.operateLog);
                break;
            case 6:
                this.getDownloadUrl();
                break;
            case 7:
                this.getRecoverData();
                break;
            case 8:
                this.enableLockDfuMode();
        }

    }

    private void responseCommand(byte command, Object data) {
        this.attemptTime = 1;
        switch(command) {
            case -112:
                DeviceInfo deviceInfo = (DeviceInfo)data;
                this.deviceFirmwareUpdateCallback.onGetLockFirmware(deviceInfo.specialValue, deviceInfo.modelNum, deviceInfo.hardwareRevision, deviceInfo.firmwareRevision);
                break;
            case 1:
                LogUtil.d("设备特征回调", this.DBG);
                this.mLockAPI.readDeviceInfo((ExtendedBluetoothDevice)null, this.lockVersion, this.aesKeyStr);
                break;
            case 2:
                LogUtil.d("断开连接 准备启动升级", this.DBG);
                this.mLockAPI.disconnect();
                break;
            case 3:
                this.pwdJson = null;
                this.recoveryData();
                break;
            case 5:
                this.ICJson = null;
                this.recoveryData();
                break;
            case 6:
                this.FRJson = null;
                this.recoveryData();
                break;
            case 37:
                this.operateLog = (String)data;
                this.uploadOperateLog((String)data);
                break;
            case 67:
                BluetoothLeService.getBluetoothLeService().setUpgradeFirmwareListener((UpgradeFirmwareListener)null);
                if(this.deviceFirmwareUpdateCallback != null) {
                    this.deviceFirmwareUpdateCallback.onStatusChanged(4);
                }

                this.upgradeStatus = 4;
                LogUtil.d("成功", this.DBG);
        }

    }

    private void startDfu() {
        this.upgradeStatus = 2;
        this.deviceFirmwareUpdateCallback.onStatusChanged(2);
        LogUtil.d("启动升级", this.DBG);
        DfuServiceInitiator starter = (new DfuServiceInitiator(this.lockmac)).setDisableNotification(true).setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true).setForceDfu(true);
        starter.setZip((Uri)null, this.mUpdateFilePath);
        starter.start(this.mContext, DfuService.class);
        this.handler.postDelayed(this.timeOutRunnable, 60000L);
    }

    private void deviceConnectedSendCommand() {
        switch(this.currCommand) {
            case 1:
                this.mLockAPI.searchDeviceFeature((ExtendedBluetoothDevice)null, 0, this.lockVersion, this.adminPs, this.unlockKey, this.lockFlagPos, this.aesKeyStr);
                break;
            case 2:
                this.mLockAPI.enterDFUMode((ExtendedBluetoothDevice)null, 0, this.lockVersion, this.adminPs, this.unlockKey, this.lockFlagPos, this.aesKeyStr);
                break;
            case 3:
                this.recoveryData();
                break;
            case 5:
                this.recoveryData();
                break;
            case 6:
                this.recoveryData();
                break;
            case 37:
                this.mLockAPI.getOperateLog((ExtendedBluetoothDevice)null, this.lockVersion, this.aesKeyStr, this.timezoneOffset);
                break;
            case 67:
                this.mLockAPI.setLockTime((ExtendedBluetoothDevice)null, 0, this.lockVersion, this.unlockKey, System.currentTimeMillis(), this.lockFlagPos, this.aesKeyStr, this.timezoneOffset);
        }

    }

    public void getLockFirmware(String lockmac, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) {
        this.adminPs = adminPs;
        this.unlockKey = unlockKey;
        this.lockFlagPos = lockFlagPos;
        this.aesKeyStr = aesKeyStr;
        this.lockmac = lockmac;
        this.lockVersion = lockVersion;
        this.attemptTime = 1;
        this.getSpecivalValue();
    }

    private void getSpecivalValue() {
        this.upgradeStatus = -1;
        BluetoothLeService.getBluetoothLeService().setUpgradeFirmwareListener(this.upgradeFirmwareListener);
        this.currCommand = 1;
        this.connect(this.lockmac);
    }

    private void connect(final String lockmac) {
        this.bleStatus = 0;
        ThreadPool.getThreadPool().execute(new Runnable() {
            public void run() {
                DeviceFirmwareUpdateApi.this.mLockAPI.connect(lockmac);
            }
        });
    }

    public void upgradeFirmware(String clientId, String accessToken, int lockid, String module, String hardware, String firmware, String lockmac, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr, long timezoneOffset) {
        if(NetworkUtil.isNetConnected(this.mContext)) {
            this.accessToken = accessToken;
            this.lockid = lockid;
            this.clientId = clientId;
            this.lockVersion = lockVersion;
            this.adminPs = adminPs;
            this.unlockKey = unlockKey;
            this.lockFlagPos = lockFlagPos;
            this.aesKeyStr = aesKeyStr;
            this.timezoneOffset = timezoneOffset;
            this.lockmac = lockmac;
            this.attemptTime = 1;
            DfuServiceListenerHelper.registerProgressListener(this.mContext, this.mDfuProgressListener);
            this.getOperateLog(lockmac, lockVersion, aesKeyStr, timezoneOffset);
        } else {
            this.deviceFirmwareUpdateCallback.onError(5, (Error)null, "");
        }

    }

    private void recoveryData() {
        this.upgradeStatus = 3;
        this.deviceFirmwareUpdateCallback.onStatusChanged(3);
        if(!this.dataIsEmpty(this.pwdJson)) {
            this.recoveryDataByBle(1, this.pwdJson);
        } else if(!this.dataIsEmpty(this.ICJson)) {
            this.recoveryDataByBle(2, this.ICJson);
        } else if(!this.dataIsEmpty(this.FRJson)) {
            this.recoveryDataByBle(3, this.FRJson);
        } else {
            this.currCommand = 67;
            LogUtil.d("校准时间", this.DBG);
            if(BluetoothLeService.mConnectionState == 2) {
                this.mLockAPI.setLockTime((ExtendedBluetoothDevice)null, 0, this.lockVersion, this.unlockKey, System.currentTimeMillis(), this.lockFlagPos, this.aesKeyStr, this.timezoneOffset);
            } else {
                this.connect(this.lockmac);
            }
        }

    }

    private void recoveryDataByBle(int op, String json) {
        LogUtil.d("恢复数据", this.DBG);
        this.upgradeStatus = 3;
        byte command = 0;
        switch(op) {
            case 1:
                command = 3;
                break;
            case 2:
                command = 5;
                break;
            case 3:
                command = 6;
        }

        this.currCommand = command;
        if(BluetoothLeService.mConnectionState == 2) {
            this.mLockAPI.recoveryData((ExtendedBluetoothDevice)null, 0, this.lockVersion, this.adminPs, this.unlockKey, this.lockFlagPos, op, json, this.aesKeyStr, this.timezoneOffset);
        } else {
            LogUtil.d("连接蓝牙", this.DBG);
            this.connect(this.lockmac);
        }

    }

    private void getRecoverData() {
        this.upgradeStatus = 7;
        ThreadPool.getThreadPool().execute(new Runnable() {
            public void run() {
                String json = ResponseService.getRecoverData(DeviceFirmwareUpdateApi.this.clientId, DeviceFirmwareUpdateApi.this.accessToken, DeviceFirmwareUpdateApi.this.lockid);
                LogUtil.e("json:" + json, true);
                if(TextUtils.isEmpty(json)) {
                    DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onError(5, (Error)null, "");
                } else {
                    try {
                        JSONObject e = new JSONObject(json);
                        if(e.has("errcode")) {
                            DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onError(4, (Error)null, json);
                        } else {
                            DeviceFirmwareUpdateApi.this.pwdJson = e.getString("keyboardPwdList");
                            DeviceFirmwareUpdateApi.this.ICJson = e.getString("identityCardList");
                            DeviceFirmwareUpdateApi.this.FRJson = e.getString("fingerprintList");
                            DeviceFirmwareUpdateApi.this.getDownloadUrl();
                        }
                    } catch (JSONException var3) {
                        var3.printStackTrace();
                        DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onError(4, (Error)null, json);
                    }
                }

            }
        });
    }

    private void getDownloadUrl() {
        this.upgradeStatus = 6;
        ThreadPool.getThreadPool().execute(new Runnable() {
            public void run() {
                String json = ResponseService.getUpgradePackage(DeviceFirmwareUpdateApi.this.clientId, DeviceFirmwareUpdateApi.this.accessToken, DeviceFirmwareUpdateApi.this.lockid);
                if(TextUtils.isEmpty(json)) {
                    DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onError(5, (Error)null, "");
                } else {
                    LockUpdateInfo lockUpdateInfo = (LockUpdateInfo) GsonUtil.toObject(json, new TypeToken() {
                    });
                    if(lockUpdateInfo.errcode == 0) {
                        DeviceFirmwareUpdateApi.this.downloadUpdatePackage(lockUpdateInfo, lockUpdateInfo.getUrl());
                    } else {
                        DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onError(4, (Error)null, json);
                    }
                }

            }
        });
    }

    private void uploadOperateLog(final String records) {
        this.upgradeStatus = 5;
        ThreadPool.getThreadPool().execute(new Runnable() {
            public void run() {
                String json = ResponseService.uploadOperateLog(DeviceFirmwareUpdateApi.this.clientId, DeviceFirmwareUpdateApi.this.accessToken, DeviceFirmwareUpdateApi.this.lockid, records);
                if(TextUtils.isEmpty(json)) {
                    DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onError(5, (Error)null, "");
                } else {
                    try {
                        JSONObject e = new JSONObject(json);
                        LogUtil.e("json:" + json, true);
                        int errcode = e.getInt("errcode");
                        if(errcode == 0) {
                            DeviceFirmwareUpdateApi.this.getRecoverData();
                        } else {
                            DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onError(4, (Error)null, json);
                        }
                    } catch (JSONException var4) {
                        var4.printStackTrace();
                        DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onError(4, (Error)null, json);
                    }
                }

            }
        });
    }

    private void getOperateLog(String lockmac, String lockVersion, String aesKeyStr, long timezoneOffset) {
        BluetoothLeService.getBluetoothLeService().setUpgradeFirmwareListener(this.upgradeFirmwareListener);
        this.currCommand = 37;
        this.upgradeStatus = 1;
        this.deviceFirmwareUpdateCallback.onStatusChanged(1);
        this.connect(lockmac);
    }

    private void downloadUpdatePackage(final LockUpdateInfo lockUpdateInfo, final String fileUrl) {
        ThreadPool.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    URL e = new URL(fileUrl);
                    URLConnection conn = e.openConnection();
                    InputStream is = conn.getInputStream();
                    int fileLen = conn.getContentLength();
                    ByteArrayOutputStream os = new ByteArrayOutputStream(fileLen);
                    byte[] bs = new byte[1024];

                    int len;
                    while((len = is.read(bs)) != -1) {
                        os.write(bs, 0, len);
                    }

                    byte[] source = ((ByteArrayOutputStream)os).toByteArray();
                    byte[] decryptedBytes = AESUtil.aesDecrypt(source, DigitUtil.decodeLockData(lockUpdateInfo.getDecryptionKey()).getBytes());
                    DeviceFirmwareUpdateApi.this.mUpdateFilePath = DeviceFirmwareUpdateApi.this.mContext.getCacheDir().getAbsolutePath() + File.separator + "realUpdate.zip";
                    FileOutputStream os1 = new FileOutputStream(DeviceFirmwareUpdateApi.this.mUpdateFilePath);
                    if(decryptedBytes != null) {
                        os1.write(decryptedBytes);
                    }

                    os1.close();
                    is.close();
                    DeviceFirmwareUpdateApi.this.enableLockDfuMode();
                } catch (IOException var10) {
                    var10.printStackTrace();
                    DeviceFirmwareUpdateApi.this.deviceFirmwareUpdateCallback.onError(5, (Error)null, var10.getMessage());
                }

            }
        });
    }

    private void enableLockDfuMode() {
        this.upgradeStatus = 8;
        LogUtil.e("进入升级模式", this.DBG);
        this.currCommand = 2;
        if(BluetoothLeService.mConnectionState == 2) {
            LogUtil.d("直连", this.DBG);
            this.mLockAPI.enterDFUMode((ExtendedBluetoothDevice)null, 0, this.lockVersion, this.adminPs, this.unlockKey, this.lockFlagPos, this.aesKeyStr);
        } else {
            this.connect(this.lockmac);
        }

    }

    private boolean dataIsEmpty(String data) {
        LogUtil.d("data:" + data, this.DBG);
        return TextUtils.isEmpty(data) || data.equals("[]");
    }

    public interface UpgradeFirmwareListener {
        void onUpgradeFirmwareChanged(byte var1, Object var2, int var3);
    }
}
