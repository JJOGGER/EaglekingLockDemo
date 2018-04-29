//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lock.bl.sdk.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.lock.bl.sdk.api.DeviceFirmwareUpdateApi;
import com.lock.bl.sdk.api.LockAPI;
import com.lock.bl.sdk.callback.LockCallback;
import com.lock.bl.sdk.constant.RecoveryData;
import com.lock.bl.sdk.entity.DeviceInfo;
import com.lock.bl.sdk.entity.Error;
import com.lock.bl.sdk.entity.FR;
import com.lock.bl.sdk.entity.ICCard;
import com.lock.bl.sdk.entity.Passcode;
import com.lock.bl.sdk.entity.TransferData;
import com.lock.bl.sdk.scanner.ExtendedBluetoothDevice;
import com.lock.bl.sdk.scanner.IScanCallback;
import com.lock.bl.sdk.scanner.ScannerCompat;
import com.lock.bl.sdk.util.DigitUtil;
import com.lock.bl.sdk.util.LogUtil;
import com.lock.bl.sdk.util.Timber;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class BluetoothLeService extends Service {
    private static final String TAG = BluetoothLeService.class.getSimpleName();
    private static final boolean DBG = true;
    private boolean isWaitCommand;
    private static final int MAX_CONNECT_COUNT = 3;
    private static final int MAX_CONNECT_INTEVAL = 2000;
    private long connectTime;
    private boolean isNeedReCon = true;
    private int connectCnt = 0;
    private static final int PWD_TYPE_MAX_DAY_180 = 1;
    private static final int PWD_TYPE_CONTAIN_MONTH = 2;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothDevice mBluetoothDevice;
    private ExtendedBluetoothDevice mExtendedBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static int mConnectionState = 0;
    public static final UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static String UUID_SERVICE = "00001910-0000-1000-8000-00805f9b34fb";
    public static String UUID_WRITE = "0000fff2-0000-1000-8000-00805f9b34fb";
    public static String UUID_READ = "0000fff4-0000-1000-8000-00805f9b34fb";
    public static final String TTL_SERVICE = "00001910-0000-1000-8000-00805f9b34fb";
    public static final String TTL_WRITE = "0000fff2-0000-1000-8000-00805f9b34fb";
    public static final String TTL_READ = "0000fff4-0000-1000-8000-00805f9b34fb";
    private static final String BONG_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca1e";
    private static final String BONG_WRITE = "6e400002-b5a3-f393-e0a9-e50e24dcca1e";
    private static final String BONG_READ = "6e400003-b5a3-f393-e0a9-e50e24dcca1e";
    private static final String DEVICE_INFORMATION_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";
    private static final String READ_MODEL_NUMBER_UUID = "00002a24-0000-1000-8000-00805f9b34fb";
    private static final String READ_FIRMWARE_REVISION_UUID = "00002a26-0000-1000-8000-00805f9b34fb";
    private static final String READ_HARDWARE_REVISION_UUID = "00002a27-0000-1000-8000-00805f9b34fb";
    private static final String READ_MANUFACTURER_NAME_UUID = "00002a29-0000-1000-8000-00805f9b34fb";
    private BluetoothGattCharacteristic modelNumberCharacteristic;
    private BluetoothGattCharacteristic hardwareRevisionCharacteristic;
    private BluetoothGattCharacteristic firmwareRevisionCharacteristic;
    private static LockCallback sLockCallback;
    private Context mContext;
    private Handler mHandler;
    public static boolean scanBongOnly;
    private ScannerCompat mScanner;
    private boolean mScanning;
    private BluetoothLeService.ScanCallback scanCallback;
    private static BluetoothLeService bluetoothLeService;
    private LinkedList<byte[]> dataQueue;
    private byte[] mReceivedDataBuffer;
    private int mReceivedBufferCount;
    private int leftRecDataCount;
    private int maxBufferCount = 256;
    public static byte[] aesKeyArray;
    private int currentAPICommand;
    String adminPs;
    String unlockKey;
    private long calibationTime;
    private long timezoneOffSet;
    int mUid;
    String pwdInfo;
    long timestamp;
    byte validPwdNum = 0;
    byte[] pwdData;
    int dataPos;
    int packetLen = 28;
    String keyboardPwd;
    String deletePwd;
    int lockFlagPos;
    String lockname;
    String originalPwd;
    String newPwd;
    long startDate;
    long endDate;
    byte keyboardPwdType;
    private Queue<String> pwdList;
    //    private List<LogOperate> logOperates;
    private JSONArray moveDateArray;
    private static final long SCAN_PERIOD = 5000L;
    private long unlockDate;
    private long No;
    Error error;
    Error tmpError;
    Timer timer;
    public boolean scan;
    private int feature = 1;
    private String wristbandKey;
    private String lockVersionString;
    private String modelNumber;
    private String hardwareRevision;
    private String firmwareRevision;
    private String manufactureDate;
    private String lockClock;
    private static final int CONNECT_TIME_OUT = 10000;
    private List<String> pwds;
    private List<RecoveryData> recoveryDatas;
    private int tempOptype;
    private TransferData transferData;
    private boolean isSetLockName;
    private DeviceFirmwareUpdateApi.UpgradeFirmwareListener upgradeFirmwareListener;
    private DeviceInfo deviceInfo;
    private ArrayList<ICCard> icCards;
    private ArrayList<FR> frs;
    private ArrayList<Passcode> passcodes;
    private final ReentrantLock conLock = new ReentrantLock();

    BroadcastReceiver bluttoothState = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String stateExtra = "android.bluetooth.adapter.extra.STATE";
            int state = intent.getIntExtra(stateExtra, 10);
            switch (state) {
                case 10:
                    LogUtil.d("BluetoothAdapter.STATE_OFF", true);
                    BluetoothLeService.mConnectionState = 0;
                    break;
                case 11:
                    LogUtil.d("BluetoothAdapter.STATE_TURNING_ON", true);
                    break;
                case 12:
                    LogUtil.d("BluetoothAdapter.STATE_ON", true);
                    if (BluetoothLeService.this.scan) {
                        BluetoothLeService.this.startScan();
                    } else {
                        LogUtil.d("do not start scan", true);
                    }
                    break;
                case 13:
                    LogUtil.d("BluetoothAdapter.STATE_TURNING_OFF", true);
            }
        }
    };

    //    TimerTask disTimerTask;
//    Runnable disConRunable = new Runnable() {
//        public void run() {
//            if (BluetoothLeService.mConnectionState == 2) {
//                LogUtil.d("disconnecting……", true);
//                BluetoothLeService.this.disconnect();
//            } else if (BluetoothLeService.mConnectionState == 1) {
//                LogUtil.d("disconnecting……", true);
//                BluetoothLeService.this.disconnect();
//                BluetoothLeService.this.close();
//                BluetoothLeService.this.startScan();
//                if (BluetoothLeService.this.upgradeFirmwareListener != null) {
//                    BluetoothLeService.this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 0, (Object) null, 3);
//                } else if (BluetoothLeService.sLockCallback != null) {
//                    BluetoothLeService.this.mExtendedBluetoothDevice.disconnectStatus = 1;
//                    BluetoothLeService.sLockCallback.onDeviceDisconnected(BluetoothLeService.this.mExtendedBluetoothDevice);
//                } else {
//                    LogUtil.w("sLockCallback is null", true);
//                }
//            }
//
//        }
//    };
//    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
//        @RequiresPermission("android.permission.BLUETOOTH")
//        @TargetApi(21)
//        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            if (BluetoothLeService.this.mBluetoothGatt != gatt) {
//                Log.e(TAG, "-------onConnectionStateChange--gatt=" + gatt + " status=" + status + " newState=" + newState);
//                gatt.close();
//            } else {
//                LogUtil.d(Thread.currentThread().toString().toString(), true);
//                Log.e(TAG, "--------onConnectionStateChange-gatt=" + gatt + " status=" + status + " newState=" + newState);
//                if (newState == BluetoothProfile.STATE_CONNECTED) {
//                    BluetoothLeService.mConnectionState = 2;
//                    Log.e(TAG, "---------Connected to GATT server.");
//                    LogUtil.e("移除disConRunable", true);
//                    BluetoothLeService.this.mHandler.removeCallbacks(BluetoothLeService.this.disConRunable);
//
//                    try {
//                        Thread.sleep(600L);
//                    } catch (InterruptedException var9) {
//                        var9.printStackTrace();
//                    }
//
//                    BluetoothLeService.this.connectTime = System.currentTimeMillis();
//                    if (BluetoothLeService.this.mBluetoothGatt != null) {
//                        Log.e(TAG, "---------Attempting to start service discovery:" + BluetoothLeService.this.mBluetoothGatt.discoverServices());
//                    } else {
//                        BluetoothLeService.mConnectionState = 0;
//                        TTLockAPI.getTTLockCallback().onDeviceDisconnected(BluetoothLeService.this.mExtendedBluetoothDevice);
//                    }
//                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                    BluetoothLeService.this.mHandler.removeCallbacks(BluetoothLeService.this.disConRunable);
//                    if (BluetoothLeService.this.isNeedReCon && BluetoothLeService.this.connectCnt < 3 && System.currentTimeMillis() - BluetoothLeService.this.connectTime < 2000L) {
//                        Log.e(TAG, "---------connect again:" + BluetoothLeService.this.connectCnt);
//
//                        try {
//                            BluetoothLeService.this.conLock.lock();
//                            BluetoothLeService.this.connect(BluetoothLeService.this.mExtendedBluetoothDevice);
//                        } finally {
//                            BluetoothLeService.this.conLock.unlock();
//                        }
//                    } else {
//                        BluetoothLeService.mConnectionState = 0;
//                        Log.e(TAG, "------- Disconnected from GATT server.");
//                        BluetoothLeService.this.close();
//                        BluetoothLeService.this.startScan();
//                        if (BluetoothLeService.this.upgradeFirmwareListener != null) {
//                            BluetoothLeService.this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 0, "", 3);
//                        } else {
//                            TTLockAPI.getTTLockCallback().onDeviceDisconnected(BluetoothLeService.this.mExtendedBluetoothDevice);
//                        }
//                    }
//                }
//
//            }
//        }
//
//        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//            if (BluetoothLeService.this.mBluetoothGatt == gatt) {
//                Log.e(TAG, "--------onServicesDiscovered-gatt=" + gatt + " status=" + status);
//                LogUtil.d(Thread.currentThread().toString().toString(), true);
//                if (status == 0) {
//                    if (BluetoothLeService.this.mBluetoothGatt == null) {
//                        LogUtil.w("mBluetoothGatt null", true);
//                        return;
//                    }
//
//                    if (BluetoothLeService.scanBongOnly) {
//                        BluetoothLeService.UUID_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca1e";
//                        BluetoothLeService.UUID_READ = "6e400003-b5a3-f393-e0a9-e50e24dcca1e";
//                        BluetoothLeService.UUID_WRITE = "6e400002-b5a3-f393-e0a9-e50e24dcca1e";
//                    } else {
//                        BluetoothLeService.UUID_SERVICE = "00001910-0000-1000-8000-00805f9b34fb";
//                        BluetoothLeService.UUID_READ = "0000fff4-0000-1000-8000-00805f9b34fb";
//                        BluetoothLeService.UUID_WRITE = "0000fff2-0000-1000-8000-00805f9b34fb";
//                    }
//
//                    BluetoothGattService service = BluetoothLeService.this.mBluetoothGatt.getService(UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb"));
//                    List gattCharacteristics;
//                    Iterator var5;
//                    BluetoothGattCharacteristic gattCharacteristic;
//                    if (service != null) {
//                        gattCharacteristics = service.getCharacteristics();
//                        if (gattCharacteristics != null && gattCharacteristics.size() > 0) {
//                            var5 = gattCharacteristics.iterator();
//
//                            while (var5.hasNext()) {
//                                gattCharacteristic = (BluetoothGattCharacteristic) var5.next();
//                                LogUtil.d(gattCharacteristic.getUuid().toString(), true);
//                                LogUtil.d("read characteristic:" + Thread.currentThread(), true);
//                                if (gattCharacteristic.getUuid().toString().equals("00002a24-0000-1000-8000-00805f9b34fb")) {
//                                    BluetoothLeService.this.modelNumberCharacteristic = gattCharacteristic;
//                                } else if (gattCharacteristic.getUuid().toString().equals("00002a26-0000-1000-8000-00805f9b34fb")) {
//                                    BluetoothLeService.this.firmwareRevisionCharacteristic = gattCharacteristic;
//                                } else if (gattCharacteristic.getUuid().toString().equals("00002a27-0000-1000-8000-00805f9b34fb")) {
//                                    BluetoothLeService.this.hardwareRevisionCharacteristic = gattCharacteristic;
//                                }
//                            }
//                        }
//                    }
//
//                    service = BluetoothLeService.this.mBluetoothGatt.getService(UUID.fromString(BluetoothLeService.UUID_SERVICE));
//                    if (service != null) {
//                        gattCharacteristics = service.getCharacteristics();
//                        if (gattCharacteristics != null && gattCharacteristics.size() > 0) {
//                            var5 = gattCharacteristics.iterator();
//
//                            while (var5.hasNext()) {
//                                gattCharacteristic = (BluetoothGattCharacteristic) var5.next();
//                                LogUtil.d(gattCharacteristic.getUuid().toString(), true);
//                                if (gattCharacteristic.getUuid().toString().equals(BluetoothLeService.UUID_WRITE)) {
//                                    BluetoothLeService.this.mNotifyCharacteristic = gattCharacteristic;
//                                    LogUtil.d("mNotifyCharacteristic:" + BluetoothLeService.this.mNotifyCharacteristic, true);
//                                } else if (gattCharacteristic.getUuid().toString().equals(BluetoothLeService.UUID_READ)) {
//                                    gatt.setCharacteristicNotification(gattCharacteristic, true);
//                                    BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(BluetoothLeService.UUID_HEART_RATE_MEASUREMENT);
//                                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                                    if (gatt.writeDescriptor(descriptor)) {
//                                        LogUtil.d("writeDescriptor successed", true);
//                                    } else {
//                                        LogUtil.d("writeDescriptor failed", true);
//                                    }
//                                }
//                            }
//                        }
//                    } else {
//                        LogUtil.w("service is null", true);
//                        BluetoothLeService.mConnectionState = 0;
//                        BluetoothLeService.this.close();
//                        BluetoothLeService.this.mExtendedBluetoothDevice.disconnectStatus = 2;
//                        TTLockAPI.getTTLockCallback().onDeviceDisconnected(BluetoothLeService.this.mExtendedBluetoothDevice);
//                    }
//                } else {
//                    LogUtil.w("onServicesDiscovered received: " + status, true);
//                }
//
//            }
//        }
//
//        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//            if (BluetoothLeService.this.mBluetoothGatt == gatt) {
//                LogUtil.d(Thread.currentThread().toString().toString(), true);
//                super.onDescriptorWrite(gatt, descriptor, status);
//                LogUtil.d("gatt=" + gatt + " descriptor=" + descriptor + " status=" + status, true);
//                LogUtil.d(descriptor.getCharacteristic().getUuid().toString(), true);
//                BluetoothLeService.this.isNeedReCon = false;
//                if (BluetoothLeService.this.upgradeFirmwareListener != null) {
//                    BluetoothLeService.this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 0, (Object) null, 2);
//                } else {
//                    TTLockAPI.getTTLockCallback().onDeviceConnected(BluetoothLeService.this.mExtendedBluetoothDevice);
//                }
//
//            }
//        }
//
//        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            if (BluetoothLeService.this.mBluetoothGatt != gatt) {
//                LogUtil.w("gatt=" + gatt + " characteristic=" + characteristic + " status=" + status, true);
//            } else {
//                LogUtil.d(Thread.currentThread().toString().toString(), true);
//                LogUtil.d("gatt=" + gatt + " characteristic=" + characteristic + " status=" + status, true);
//                if (status == 0) {
//                    if (BluetoothLeService.this.dataQueue.size() > 0) {
//                        characteristic.setValue((byte[]) BluetoothLeService.this.dataQueue.poll());
//                        gatt.writeCharacteristic(characteristic);
//                    } else {
//                        BluetoothLeService.this.mHandler.removeCallbacks(BluetoothLeService.this.disConRunable);
//                        BluetoothLeService.this.disTimerTask = new TimerTask() {
//                            public void run() {
//                                BluetoothLeService.this.disconnect();
//                            }
//                        };
//                        long delay = 2500L;
//                        if (BluetoothLeService.this.currentAPICommand == 19) {
//                            delay = 5500L;
//                        }
//
//                        LogUtil.e("------------------timer----------------" + BluetoothLeService.this.timer, true);
//                        BluetoothLeService.this.timer.schedule(BluetoothLeService.this.disTimerTask, delay);
//                    }
//                } else {
//                    LogUtil.w("onCharacteristicWrite failed", true);
//                }
//
//                super.onCharacteristicWrite(gatt, characteristic, status);
//            }
//        }
//
//        @RequiresPermission("android.permission.BLUETOOTH")
//        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//            if (BluetoothLeService.this.mBluetoothGatt == gatt) {
//                super.onCharacteristicChanged(gatt, characteristic);
//
//                try {
//                    LogUtil.d("gatt=" + gatt + " characteristic=" + characteristic, true);
//                    byte[] e = characteristic.getValue();
//                    int dataLen = e.length;
//                    if (BluetoothLeService.scanBongOnly) {
//                        if ("success".equals(new String(e))) {
//                            BluetoothLeService.this.error = Error.SUCCESS;
//                        } else {
//                            BluetoothLeService.this.error = Error.LOCK_OPERATE_FAILED;
//                        }
//
//                        switch (BluetoothLeService.this.tempOptype) {
//                            case 1:
//                                BluetoothLeService.sLockCallback.onSetWristbandKeyToDev(BluetoothLeService.this.error);
//                                break;
//                            case 2:
//                                BluetoothLeService.sLockCallback.onSetWristbandKeyRssi(BluetoothLeService.this.error);
//                        }
//
//                        return;
//                    }
//
//                    LogUtil.d("receiver data=" + DigitUtil.byteArrayToHexString(e), true);
//                    if (BluetoothLeService.this.mReceivedBufferCount + dataLen <= BluetoothLeService.this.maxBufferCount) {
//                        System.arraycopy(e, 0, BluetoothLeService.this.mReceivedDataBuffer, BluetoothLeService.this.mReceivedBufferCount, dataLen);
//                        BluetoothLeService.this.mReceivedBufferCount = BluetoothLeService.this.mReceivedBufferCount + dataLen;
//                    }
//
//                    LogUtil.d("mReceivedBufferCount:" + BluetoothLeService.this.mReceivedBufferCount, true);
//                    LogUtil.d("dataLen:" + dataLen, true);
//                    if (BluetoothLeService.this.mReceivedBufferCount == dataLen && e[0] == 127 && e[1] == 90) {
//                        boolean last = false;
//                        int last1;
//                        if (e[2] >= 5) {
//                            last1 = e[11] + 1;
//                            BluetoothLeService.this.leftRecDataCount = last1 - (dataLen - 12);
//                        } else {
//                            last1 = e[5] + 1;
//                            BluetoothLeService.this.leftRecDataCount = last1 - (dataLen - 6);
//                        }
//                    } else {
//                        BluetoothLeService.this.leftRecDataCount = BluetoothLeService.this.leftRecDataCount - dataLen;
//                    }
//
//                    LogUtil.d("leftRecDataCount:" + BluetoothLeService.this.leftRecDataCount, true);
//                    byte last2 = BluetoothLeService.this.mReceivedDataBuffer[BluetoothLeService.this.mReceivedBufferCount - 1];
//                    byte lastSec = BluetoothLeService.this.mReceivedDataBuffer[BluetoothLeService.this.mReceivedBufferCount - 2];
//                    if (lastSec == 13 && last2 == 10) {
//                        BluetoothLeService.this.mReceivedBufferCount = BluetoothLeService.this.mReceivedBufferCount - 2;
//                        LogUtil.d("mReceivedDataBuffer=" + DigitUtil.byteArrayToHexString(Arrays.copyOf(BluetoothLeService.this.mReceivedDataBuffer, BluetoothLeService.this.mReceivedBufferCount)), true);
//                        BluetoothLeService.this.processCommandResponse(Arrays.copyOf(BluetoothLeService.this.mReceivedDataBuffer, BluetoothLeService.this.mReceivedBufferCount));
//                        BluetoothLeService.this.mReceivedBufferCount = 0;
//                        LogUtil.e("------------------timer----------------" + BluetoothLeService.this.timer, true);
//                        BluetoothLeService.this.disTimerTask.cancel();
//                        LogUtil.d("num:" + BluetoothLeService.this.timer.purge(), true);
//                    } else if (BluetoothLeService.this.leftRecDataCount == 0) {
//                        LogUtil.d("mReceivedDataBuffer=" + DigitUtil.byteArrayToHexString(Arrays.copyOf(BluetoothLeService.this.mReceivedDataBuffer, BluetoothLeService.this.mReceivedBufferCount)), true);
//                        BluetoothLeService.this.processCommandResponse(Arrays.copyOf(BluetoothLeService.this.mReceivedDataBuffer, BluetoothLeService.this.mReceivedBufferCount));
//                        BluetoothLeService.this.mReceivedBufferCount = 0;
//                        BluetoothLeService.this.disTimerTask.cancel();
//                        LogUtil.d("num:" + BluetoothLeService.this.timer.purge(), true);
//                    }
//                } catch (Exception var7) {
//                    var7.printStackTrace();
//                    BluetoothLeService.this.mReceivedBufferCount = 0;
//                }
//
//            }
//        }
//
//        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            super.onCharacteristicRead(gatt, characteristic, status);
//            LogUtil.e(Thread.currentThread() + " " + new String(characteristic.getValue()), true);
//            LogUtil.e(Thread.currentThread() + " " + characteristic.getUuid(), true);
//        }
//    };
//
//    public BluetoothLeService() {
//    }
//
    public void onCreate() {
        super.onCreate();
        this.registerReceiver(this.bluttoothState, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
        bluetoothLeService = this;
        this.mHandler = new Handler();
        this.mContext = this.getApplicationContext();
        sLockCallback = LockAPI.getLockCallback();
        this.mReceivedDataBuffer = new byte[this.maxBufferCount];
        this.initialize();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.e("-------Service is Started", true);
        return START_STICKY;
    }

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //
    public static BluetoothLeService getBluetoothLeService() {
        return bluetoothLeService;
    }

    //

    public boolean initialize() {
        if (this.mBluetoothManager == null) {
            this.mBluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
            if (this.mBluetoothManager == null) {
                Timber.e("-------Unable to initialize BluetoothManager.");
                return false;
            }
        }

        this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        if (this.mBluetoothAdapter == null) {
            Timber.e("------Unable to obtain a BluetoothAdapter.");
            return false;
        } else {
            this.scanCallback = new BluetoothLeService.ScanCallback();
            this.mScanner = ScannerCompat.getScanner();
            if (LockAPI.scan) {
                this.scan = true;
                this.startScan();
            } else {
                this.scan = false;
            }

            this.timer = new Timer();
            return true;
        }
    }

    public void startScan() {
        if (!this.scan) {
            Timber.e("------Already stop scan");
        } else {
            Timber.e("-------启动扫描");
            if (!this.mBluetoothAdapter.isEnabled()) {
                Timber.e("---------BluetoothAdapter is disabled", true);
            } else {
                if (this.mScanner == null) {
                    this.mScanner = ScannerCompat.getScanner();
                }
                if (this.scanCallback == null) {
                    this.scanCallback = new BluetoothLeService.ScanCallback();
                }
                if (this.mScanning) {
                    this.mScanner.stopScan();
                }

                this.mScanner.startScan(this.scanCallback);
                this.mScanning = true;
                if (mConnectionState != 0) {
                    Timber.e("---------Ble not disconnected", true);
                }
                Timber.e("------scanCallback：" + scanCallback);
            }
        }
    }

    public void stopScan() {
        Timber.e("---------进入停止扫描", true);
        Timber.e("---------mScanner:" + this.mScanner);
        Timber.e("---------mScanning:" + this.mScanning);
        if (this.mScanner != null && this.mScanning) {
            this.mScanning = false;
            Timber.e("---------发送停止扫描");
            this.mScanner.stopScan();
        }
    }

    //
//    @RequiresPermission("android.permission.BLUETOOTH")
//    public boolean connect(String address) {
//        boolean device;
//        try {
//            this.conLock.lock();
//            this.stopScan();
//            ++this.connectCnt;
//            this.connectTime = System.currentTimeMillis();
//            if (this.mBluetoothAdapter != null && address != null) {
//                Log.e(TAG, "---------mBluetoothGatt ……"+mBluetoothGatt);
//                if (this.mBluetoothGatt != null) {
//                    LogUtil.d("mBluetoothGatt not null", true);
//                    this.disconnect();
//                    this.close();
//                }
//
//                BluetoothDevice device1 = this.mBluetoothAdapter.getRemoteDevice(address);
//                Log.e(TAG, "---------connect ……");
//                Log.e(TAG, "---------移除disConRunable");
//                this.mHandler.removeCallbacks(this.disConRunable);
//                this.mHandler.postDelayed(this.disConRunable, 10000L);
//                this.mBluetoothGatt = device1.connectGatt(this, false, this.mGattCallback);
//                Log.e(TAG, "--------Trying to create a new connection.");
//                LogUtil.i("connected mBluetoothGatt:" + this.mBluetoothGatt, true);
//                this.mBluetoothDeviceAddress = address;
//                this.mBluetoothDevice = device1;
//                Log.e(TAG, "------device.getBondState==" + device1.getBondState());
//                this.mExtendedBluetoothDevice = new ExtendedBluetoothDevice(device1);
//                Log.e(TAG, "------device:" + device1);
//                Log.e(TAG, "------mExtendedBluetoothDevice:" + this.mExtendedBluetoothDevice);
//                Log.e(TAG, "------mExtendedBluetoothDevice.device:" + this.mExtendedBluetoothDevice.getDevice());
//                mConnectionState = 1;
//                return true;
//            }
//
//            Log.e(TAG, "-------BluetoothAdapter not initialized or unspecified address.");
//            Log.e(TAG, "------mBluetoothAdapter:" + this.mBluetoothAdapter);
//            Log.e(TAG, "-----------address:" + address);
//            device = false;
//        } finally {
//            this.conLock.unlock();
//            Log.e(TAG, "-----------unlock:");
//        }
//
//        return device;
//    }
//
//    @RequiresPermission("android.permission.BLUETOOTH")
//    public boolean connect(ExtendedBluetoothDevice extendedBluetoothDevice) {
//        this.stopScan();
//        ++this.connectCnt;
//        this.connectTime = System.currentTimeMillis();
//        LogUtil.i("extendedBluetoothDevice:" + extendedBluetoothDevice, true);
//        String address = extendedBluetoothDevice.getDevice().getAddress();
//        if (this.mBluetoothAdapter != null && address != null) {
//            if (this.mBluetoothGatt != null) {
//                LogUtil.d("mBluetoothGatt not null", true);
//                this.disconnect();
//                this.close();
//            }
//
//            BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(address);
//            LogUtil.d("connect ……", true);
//            LogUtil.e("移除disConRunable", true);
//            this.mHandler.removeCallbacks(this.disConRunable);
//            this.mHandler.postDelayed(this.disConRunable, 10000L);
//            extendedBluetoothDevice.disconnectStatus = 0;
//            this.mBluetoothGatt = device.connectGatt(this, false, this.mGattCallback);
//            Log.e(TAG, "-----------Trying to create a new connection.");
//            Log.e(TAG, "-------connected mBluetoothGatt:" + this.mBluetoothGatt);
//            this.mBluetoothDeviceAddress = address;
//            this.mBluetoothDevice = device;
//            this.mExtendedBluetoothDevice = extendedBluetoothDevice;
//            Log.e(TAG, "-------device.getBondState==" + device.getBondState());
//            Log.e(TAG, "-------mExtendedBluetoothDevice:" + this.mExtendedBluetoothDevice);
//            mConnectionState = 1;
//            return true;
//        } else {
//            Log.e(TAG, "-------BluetoothAdapter not initialized or unspecified address.");
//            return false;
//        }
//    }
//
//    public void sendCommand(TransferData transferData) {
//        this.transferData = transferData;
//        this.mUid = transferData.getmUid();
//        this.adminPs = transferData.getAdminPs();
//        this.unlockKey = transferData.getUnlockKey();
//        this.startDate = transferData.getStartDate();
//        this.endDate = transferData.getEndDate();
//        aesKeyArray = TransferData.getAesKeyArray();
//        this.unlockDate = transferData.getUnlockDate();
//        this.lockFlagPos = transferData.getLockFlagPos();
//        this.originalPwd = transferData.getOriginalPwd();
//        this.newPwd = transferData.getNewPwd();
//        this.No = transferData.getNo();
//        this.pwds = transferData.getPwds();
//        this.wristbandKey = transferData.getWristbandKey();
//        this.calibationTime = transferData.getCalibationTime();
//        this.timezoneOffSet = transferData.getTimezoneOffSet();
//        this.sendCommand(transferData.getTransferData(), transferData.getAPICommand());
//    }
//
//    public void sendCommand(byte[] commandSrc, int uid, String unlockKey, byte[] aesKey, long date, int apiCommand) {
//        this.mUid = uid;
//        this.unlockKey = unlockKey;
//        aesKeyArray = aesKey;
//        this.currentAPICommand = apiCommand;
//        this.calibationTime = date;
//        this.sendCommand(commandSrc, apiCommand);
//    }
//
//    public void sendCommand(byte[] commandSrc, String adminPs, String unlockKey, byte[] aesKey, int apiCommand) {
//        this.adminPs = adminPs;
//        this.unlockKey = unlockKey;
//        aesKeyArray = aesKey;
//        this.currentAPICommand = apiCommand;
//        this.sendCommand(commandSrc, apiCommand);
//    }
//
//    public void sendCommand(byte[] commandSrc, String adminPs, String unlockKey, int lockFlagPos, byte[] aesKey, byte validPwdNum, byte keyboardPwdType, String originalPwd, String string, long startDate, long endDate, int apiCommand) {
//        this.originalPwd = originalPwd;
//        this.startDate = startDate;
//        this.endDate = endDate;
//        this.keyboardPwdType = keyboardPwdType;
//        this.sendCommand(commandSrc, adminPs, unlockKey, lockFlagPos, aesKey, validPwdNum, string, apiCommand);
//    }
//
//    public void sendCommand(byte[] commandSrc, String adminPs, String unlockKey, int lockFlagPos, byte[] aesKey, byte validPwdNum, String string, int apiCommand) {
//        this.adminPs = adminPs;
//        this.unlockKey = unlockKey;
//        this.validPwdNum = validPwdNum;
//        this.lockFlagPos = lockFlagPos;
//        aesKeyArray = aesKey;
//        this.currentAPICommand = apiCommand;
//        switch (apiCommand) {
//            case 5:
//                this.keyboardPwd = string;
//            case 6:
//            case 7:
//            case 8:
//            case 9:
//            case 10:
//            case 11:
//            case 13:
//            case 14:
//            case 15:
//            case 16:
//            case 18:
//            case 19:
//            default:
//                break;
//            case 12:
//                this.deletePwd = string;
//                break;
//            case 17:
//                this.lockname = string;
//                break;
//            case 20:
//            case 21:
//            case 22:
//            case 23:
//            case 24:
//                this.newPwd = string;
//        }
//
//        this.sendCommand(commandSrc, apiCommand);
//    }
//
//    public void sendCommand(byte[] commandSrc, int apiCommand, byte[] aesKeyArray) {
//        BluetoothLeService.aesKeyArray = aesKeyArray;
//        this.sendCommand(commandSrc, apiCommand);
//    }
//
//    public void sendCommand(byte[] commandSrc, int apiCommand) {
//        this.currentAPICommand = apiCommand;
//        if (this.currentAPICommand == 26) {
//            this.logOperates = new ArrayList();
//            this.moveDateArray = new JSONArray();
//        }
//
//        this.sendCommand(commandSrc);
//    }
//
//    public void sendCommand(byte[] commandSrc, String pwdInfo, long timestamp, int apiCommand) {
//        this.currentAPICommand = apiCommand;
//        this.pwdInfo = pwdInfo;
//        this.timestamp = timestamp;
//        this.sendCommand(commandSrc);
//    }
//
//    public void sendCommand(byte[] commandSrc) {
//        int length = commandSrc.length;
//        byte[] command = new byte[commandSrc.length + 2];
//        System.arraycopy(commandSrc, 0, command, 0, length);
//        command[length] = 13;
//        command[length + 1] = 10;
//        LogUtil.d("send datas:" + DigitUtil.byteArrayToHexString(commandSrc), true);
//        int len = length + 2;
//        this.dataQueue = new LinkedList();
//
//        for (int startPos = 0; len > 0; startPos += 20) {
//            int e = Math.min(len, 20);
//            byte[] data = new byte[e];
//            System.arraycopy(command, startPos, data, 0, e);
//            this.dataQueue.add(data);
//            len -= 20;
//        }
//
//        if (this.mNotifyCharacteristic != null && this.mBluetoothGatt != null) {
//            try {
//                this.mNotifyCharacteristic.setValue((byte[]) this.dataQueue.poll());
//                this.mBluetoothGatt.writeCharacteristic(this.mNotifyCharacteristic);
//            } catch (Exception var8) {
//                mConnectionState = 0;
//                if (sLockCallback != null) {
//                    sLockCallback.onDeviceDisconnected(this.mExtendedBluetoothDevice);
//                }
//            }
//        } else {
//            LogUtil.d("mNotifyCharacteristic:" + this.mNotifyCharacteristic, true);
//            LogUtil.d("mBluetoothGatt:" + this.mBluetoothGatt, true);
//            LogUtil.d("mNotifyCharacteristic or mBluetoothGatt is null", true);
//            mConnectionState = 0;
//            TTLockAPI.getTTLockCallback().onDeviceDisconnected(this.mExtendedBluetoothDevice);
//        }
//
//    }
//
//    public void sendBongCommand(String wristbandKey) {
//        byte[] values = new byte[12];
//        byte[] constant = new byte[]{49, 0, 0, 0};
//        byte cmd = 1;
//        byte enable = 1;
//        System.arraycopy(constant, 0, values, 0, 4);
//        values[4] = cmd;
//        values[5] = enable;
//        System.arraycopy(wristbandKey.getBytes(), 0, values, 6, 6);
//        this.tempOptype = cmd;
//        if (this.mNotifyCharacteristic != null && this.mBluetoothGatt != null) {
//            this.mNotifyCharacteristic.setValue(values);
//            this.mBluetoothGatt.writeCharacteristic(this.mNotifyCharacteristic);
//        } else {
//            LogUtil.d("mNotifyCharacteristic or mBluetoothGatt is null", true);
//        }
//
//    }
//
//    public void setBongRssi(byte rssi) {
//        byte[] values = new byte[6];
//        byte[] constant = new byte[]{49, 0, 0, 0};
//        byte cmd = 2;
//        System.arraycopy(constant, 0, values, 0, 4);
//        values[4] = cmd;
//        values[5] = rssi;
//        this.tempOptype = cmd;
//        if (this.mNotifyCharacteristic != null && this.mBluetoothGatt != null) {
//            this.mNotifyCharacteristic.setValue(values);
//            this.mBluetoothGatt.writeCharacteristic(this.mNotifyCharacteristic);
//        } else {
//            LogUtil.d("mNotifyCharacteristic or mBluetoothGatt is null", true);
//        }
//
//    }
//
//    @RequiresPermission("android.permission.BLUETOOTH")
//    private void processCommandResponse(byte[] values) {
//        this.mExtendedBluetoothDevice.disconnectStatus = 4;
//        Command command = new Command(values);
//        if (!command.isChecksumValid()) {
//            this.error = Error.LOCK_CRC_CHECK_ERROR;
//            this.error.setCommand(command.getCommand());
//            this.errorCallback(this.error);
//        } else {
//            byte groupId;
//            byte orgId;
//            short var44;
//            if (this.currentAPICommand == 1) {
//                if (values[0] == 127 && values[1] == 90) {
//                    this.error = Error.SUCCESS;
//                    groupId = values[2];
//                    orgId = values[3];
//                    byte var22 = values[4];
//                    short var29 = DigitUtil.byteArrayToShort(new byte[]{values[5], values[6]});
//                    var44 = DigitUtil.byteArrayToShort(new byte[]{values[7], values[8]});
//                    TTLockAPI.getTTLockCallback().onGetLockVersion(this.mExtendedBluetoothDevice, groupId, orgId, var22, var29, var44, this.error);
//                } else {
//                    this.error = Error.LOCK_OPERATE_FAILED;
//                    TTLockAPI.getTTLockCallback().onGetLockVersion(this.mExtendedBluetoothDevice, 0, 0, 0, 0, 0, this.error);
//                }
//
//            } else {
//                groupId = command.organization[0];
//                orgId = command.sub_organization[0];
//                this.mExtendedBluetoothDevice.groupId = groupId;
//                this.mExtendedBluetoothDevice.orgId = orgId;
//                byte[] data = null;
//                LogUtil.d("currentAPICommand : " + this.currentAPICommand, true);
//                LogUtil.d("command.getCommand():" + (char) command.getCommand() + "-" + String.format("%#x", new Object[]{Byte.valueOf(command.getCommand())}), true);
//                switch (command.getLockType()) {
//                    case 2:
//                    case 3:
//                    case 6:
//                    case 7:
//                        data = command.getData();
//                        break;
//                    case 4:
//                        data = command.getData(aesKeyArray);
//                        break;
//                    case 5:
//                    case 8:
//                        this.mHandler.removeCallbacks(this.disConRunable);
//                        data = command.getData(aesKeyArray);
//                }
//
//                if (data != null && data.length != 0) {
//                    LogUtil.d("feedback comman:" + (char) data[0] + "-" + String.format("%#x", new Object[]{Byte.valueOf(data[0])}), true);
//                    LogUtil.d("values:" + DigitUtil.byteArrayToHexString(values), true);
//                    LogUtil.d("response data:" + DigitUtil.byteArrayToHexString(data), true);
//                    if (this.currentAPICommand == 17 && this.isSetLockName && data[0] != -1) {
//                        this.isSetLockName = false;
//                        LogUtil.d("data:" + DigitUtil.byteArrayToHexString(data), true);
//                        String var27 = (new String(data)).trim();
//                        LogUtil.e(var27, true);
//                        if ("OK".equals(var27)) {
//                            TTLockAPI.getTTLockCallback().onSetLockName(this.mExtendedBluetoothDevice, this.lockname, this.error);
//                        } else {
//                            this.error = Error.LOCK_OPERATE_FAILED;
//                            this.errorCallback(this.error);
//                        }
//
//                    } else {
//                        if (command.getCommand() == 25) {
//                            if (data[1] == 1) {
//                                aesKeyArray = Arrays.copyOfRange(data, 2, data.length);
//                                this.adminPs = new String(DigitUtil.generateDynamicPassword(10));
//                                this.unlockKey = new String(DigitUtil.generateDynamicPassword(10));
//                                CommandUtil.V_addAdmin(command.getLockType(), this.adminPs, this.unlockKey, aesKeyArray);
//                            } else {
//                                this.error = Error.getInstance(data[2]);
//                                this.error.setCommand(command.getCommand());
//                                this.errorCallback(this.error);
//                            }
//                        } else if (command.getCommand() == 84) {
//                            if (data[1] == 1) {
//                                LogUtil.d("success", true);
//                                this.error = Error.SUCCESS;
//                                this.error.setLockname(this.mExtendedBluetoothDevice.getName());
//                                this.error.setLockmac(this.mExtendedBluetoothDevice.getAddress());
//                                this.error.setCommand(data[0]);
//                                this.error.setDate(System.currentTimeMillis());
//                                int delay;
//                                int battery;
//                                Calendar op;
//                                TimeZone code;
//                                long secretKey;
//                                byte var25;
//                                byte[] var26;
//                                String var31;
//                                int var35;
//                                RecoveryData var38;
//                                long var39;
//                                RecoveryData var41;
//                                short var50;
//                                label584:
//                                switch (data[0]) {
//                                    case -112:
//                                        LogUtil.d("COMM_READ_DEVICE_INFO:" + DigitUtil.byteArrayToHexString(data), true);
//                                        LogUtil.d("COMM_READ_DEVICE_INFO:" + new String(Arrays.copyOfRange(data, 2, data.length - 1)), true);
//                                        switch (this.tempOptype) {
//                                            case 1:
//                                                LogUtil.d("modelNumber before:" + this.modelNumber, true);
//                                                this.modelNumber = new String(Arrays.copyOfRange(data, 2, data.length - 1));
//                                                LogUtil.d("modelNumber after:" + this.modelNumber, true);
//                                                this.tempOptype = 2;
//                                                CommandUtil.readDeviceInfo(command.getLockType(), (byte) 2, aesKeyArray);
//                                                break label584;
//                                            case 2:
//                                                this.hardwareRevision = new String(Arrays.copyOfRange(data, (byte) 2, data.length - 1));
//                                                this.tempOptype = 3;
//                                                CommandUtil.readDeviceInfo(command.getLockType(), (byte) 3, aesKeyArray);
//                                                break label584;
//                                            case 3:
//                                                LogUtil.d("firmwareRevision before:" + this.firmwareRevision, true);
//                                                this.firmwareRevision = new String(Arrays.copyOfRange(data, 2, data.length - 1));
//                                                LogUtil.d("firmwareRevision after:" + this.firmwareRevision, true);
//                                                this.tempOptype = 4;
//                                                if (this.upgradeFirmwareListener != null) {
//                                                    if (this.deviceInfo != null) {
//                                                        this.deviceInfo.modelNum = this.modelNumber;
//                                                        this.deviceInfo.hardwareRevision = this.hardwareRevision;
//                                                        this.deviceInfo.firmwareRevision = this.firmwareRevision;
//                                                    }
//
//                                                    this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) -112, this.deviceInfo, 1);
//                                                } else if (this.currentAPICommand == 2) {
//                                                    if (this.transferData != null) {
//                                                        this.mExtendedBluetoothDevice.setRemoteUnlockSwitch(this.transferData.getOpValue());
//                                                    }
//
//                                                    TTLockAPI.getTTLockCallback().onAddAdministrator(this.mExtendedBluetoothDevice, command.getLockVersionString(), DigitUtil.encodeLockData(this.adminPs), DigitUtil.encodeLockData(this.unlockKey), this.keyboardPwd, this.deletePwd, this.pwdInfo, this.timestamp, DigitUtil.encodeAesKey(aesKeyArray), this.feature, this.modelNumber, this.hardwareRevision, this.firmwareRevision, this.error);
//                                                } else {
//                                                    CommandUtil.readDeviceInfo(command.getLockType(), (byte) 4, aesKeyArray);
//                                                }
//                                                break label584;
//                                            case 4:
//                                                this.manufactureDate = new String(Arrays.copyOfRange(data, 2, data.length - 1));
//                                                this.tempOptype = 6;
//                                                CommandUtil.readDeviceInfo(command.getLockType(), (byte) 6, aesKeyArray);
//                                                break label584;
//                                            case 5:
//                                            default:
//                                                this.modelNumber = new String(Arrays.copyOfRange(data, (byte) 2, data.length - 1));
//                                                this.tempOptype = 2;
//                                                CommandUtil.readDeviceInfo(command.getLockType(), (byte) 2, aesKeyArray);
//                                                break label584;
//                                            case 6:
//                                                this.lockClock = "";
//
//                                                for (var35 = 2; var35 < data.length - 1; ++var35) {
//                                                    this.lockClock = this.lockClock + String.format("%02d", new Object[]{Byte.valueOf(data[var35])});
//                                                }
//
//                                                this.tempOptype = -1;
//                                                TTLockAPI.getTTLockCallback().onReadDeviceInfo(this.mExtendedBluetoothDevice, this.modelNumber, this.hardwareRevision, this.firmwareRevision, this.manufactureDate, this.lockClock);
//                                                break label584;
//                                        }
//                                    case -1:
//                                        if (this.currentAPICommand == 17) {
//                                            this.isSetLockName = true;
//                                        }
//                                        break;
//                                    case 1:
//                                        this.feature = (int) DigitUtil.fourBytesToLong(Arrays.copyOfRange(data, 3, 7));
//                                        if (this.upgradeFirmwareListener != null) {
//                                            this.deviceInfo = new DeviceInfo();
//                                            this.deviceInfo.specialValue = this.feature;
//                                            this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 1, this.deviceInfo, 1);
//                                        } else {
//                                            switch (this.currentAPICommand) {
//                                                case 2:
//                                                    if (DigitUtil.isSupportRemoteUnlockSwitch(this.feature)) {
//                                                        CommandUtil_V3.controlRemoteUnlock(command, (byte) 1, (byte) 0, aesKeyArray);
//                                                    } else {
//                                                        CommandUtil.operateFinished(command.getLockType());
//                                                    }
//                                                    break label584;
//                                                case 23:
//                                                    this.error = this.tmpError;
//                                                    if (!DigitUtil.isSupportModifyPasscode(this.feature)) {
//                                                        this.error = Error.LOCK_NOT_SUPPORT_CHANGE_PASSCODE;
//                                                        this.error.setCommand((byte) 3);
//                                                    }
//
//                                                    TTLockAPI.getTTLockCallback().onModifyKeyboardPassword(this.mExtendedBluetoothDevice, this.keyboardPwdType, this.originalPwd, this.newPwd, this.error);
//                                                    break label584;
//                                                case 48:
//                                                    TTLockAPI.getTTLockCallback().onOperateRemoteUnlockSwitch(this.mExtendedBluetoothDevice, data[2], this.transferData.getOp(), this.transferData.getOpValue(), this.feature, this.error);
//                                                    break label584;
//                                                default:
//                                                    TTLockAPI.getTTLockCallback().onSearchDeviceFeature(this.mExtendedBluetoothDevice, data[2], this.feature, this.error);
//                                            }
//                                        }
//                                        break;
//                                    case 2:
//                                        if (this.upgradeFirmwareListener != null) {
//                                            this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 2, "", 1);
//                                        } else {
//                                            TTLockAPI.getTTLockCallback().onEnterDFUMode(this.mExtendedBluetoothDevice, this.error);
//                                        }
//                                        break;
//                                    case 3:
//                                        switch (data[3]) {
//                                            case 1:
//                                                TTLockAPI.getTTLockCallback().onDeleteAllKeyboardPassword(this.mExtendedBluetoothDevice, this.error);
//                                                break label584;
//                                            case 2:
//                                                switch (this.currentAPICommand) {
//                                                    case 20:
//                                                        this.keyboardPwdType = 3;
//                                                        break;
//                                                    case 21:
//                                                        this.keyboardPwdType = 1;
//                                                        break;
//                                                    case 22:
//                                                        this.keyboardPwdType = 2;
//                                                }
//
//                                                TTLockAPI.getTTLockCallback().onAddKeyboardPassword(this.mExtendedBluetoothDevice, this.keyboardPwdType, this.originalPwd, this.startDate, this.endDate, this.error);
//                                                break label584;
//                                            case 3:
//                                                if (this.currentAPICommand == 41) {
//                                                    ++this.dataPos;
//                                                    if (this.dataPos < this.pwds.size()) {
//                                                        CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 3, this.keyboardPwdType, (String) this.pwds.get(this.dataPos), this.newPwd, 0L, 0L, aesKeyArray);
//                                                    }
//                                                } else {
//                                                    TTLockAPI.getTTLockCallback().onDeleteOneKeyboardPassword(this.mExtendedBluetoothDevice, this.keyboardPwdType, this.newPwd, this.error);
//                                                }
//                                            case 4:
//                                            default:
//                                                break label584;
//                                            case 5:
//                                                TTLockAPI.getTTLockCallback().onModifyKeyboardPassword(this.mExtendedBluetoothDevice, this.keyboardPwdType, this.originalPwd, this.newPwd, this.error);
//                                                break label584;
//                                            case 6:
//                                                ++this.dataPos;
//                                                LogUtil.e("dataPos:" + this.dataPos, true);
//                                                if (this.dataPos < this.recoveryDatas.size()) {
//                                                    var41 = (RecoveryData) this.recoveryDatas.get(this.dataPos);
//                                                    CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 6, (byte) (var41.keyboardPwdType == 2 ? 1 : var41.keyboardPwdType), var41.cycleType, var41.keyboardPwd, var41.keyboardPwd, var41.startDate, var41.endDate, aesKeyArray, this.timezoneOffSet);
//                                                } else if (this.upgradeFirmwareListener != null) {
//                                                    this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 3, "", 1);
//                                                } else {
//                                                    sLockCallback.onRecoveryData(this.mExtendedBluetoothDevice, this.transferData.getOp(), this.error);
//                                                }
//                                                break label584;
//                                        }
//                                    case 4:
//                                        var44 = CommandUtil_V3.parseKeyboardPwd(Arrays.copyOfRange(data, 2, data.length));
//                                        if (var44 != 0 && var44 != -1) {
//                                            CommandUtil.getValidKeyboardPassword(command.getLockType(), var44, aesKeyArray);
//                                        }
//                                        break;
//                                    case 5:
//                                        var25 = data[2];
//                                        switch (data[3]) {
//                                            case 1:
//                                                var50 = this.parseIC(Arrays.copyOfRange(data, 4, data.length));
//                                                LogUtil.d("search:" + var50, true);
//                                                if (var50 == -1) {
//                                                    sLockCallback.onSearchICCard(this.mExtendedBluetoothDevice, data[2], GsonUtil.toJson(this.icCards), this.error);
//                                                    LogUtil.d("完成", true);
//                                                } else {
//                                                    CommandUtil.searchICCardNo(command.getLockType(), var50, aesKeyArray);
//                                                }
//                                                break label584;
//                                            case 2:
//                                                if (data[4] == 1) {
//                                                    if (this.currentAPICommand == 44) {
//                                                        ++this.dataPos;
//                                                        LogUtil.e("dataPos:" + this.dataPos, true);
//                                                        if (this.dataPos < this.recoveryDatas.size()) {
//                                                            var38 = (RecoveryData) this.recoveryDatas.get(this.dataPos);
//                                                            CommandUtil.recoveryICCardPeriod(command.getLockType(), Long.valueOf(var38.cardNumber).longValue(), var38.startDate, var38.endDate, aesKeyArray, this.timezoneOffSet);
//                                                        } else if (this.upgradeFirmwareListener != null) {
//                                                            this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 5, "", 1);
//                                                        } else {
//                                                            sLockCallback.onRecoveryData(this.mExtendedBluetoothDevice, this.transferData.getOp(), this.error);
//                                                        }
//                                                    } else {
//                                                        var39 = DigitUtil.fourBytesToLong(Arrays.copyOfRange(data, 5, data.length));
//                                                        this.isWaitCommand = false;
//                                                        TTLockAPI.getTTLockCallback().onAddICCard(this.mExtendedBluetoothDevice, 2, var25, var39, this.error);
//                                                    }
//                                                } else if (data[4] == 2) {
//                                                    LogUtil.d("进入添加IC卡模式", true);
//                                                    if (this.currentAPICommand == 44) {
//                                                        ++this.dataPos;
//                                                        LogUtil.e("dataPos:" + this.dataPos, true);
//                                                        if (this.dataPos < this.recoveryDatas.size()) {
//                                                            var38 = (RecoveryData) this.recoveryDatas.get(this.dataPos);
//                                                            CommandUtil.recoveryICCardPeriod(command.getLockType(), Long.valueOf(var38.cardNumber).longValue(), var38.startDate, var38.endDate, aesKeyArray, this.timezoneOffSet);
//                                                        } else {
//                                                            sLockCallback.onRecoveryData(this.mExtendedBluetoothDevice, this.transferData.getOp(), this.error);
//                                                        }
//                                                    } else {
//                                                        this.isWaitCommand = true;
//                                                        TTLockAPI.getTTLockCallback().onAddICCard(this.mExtendedBluetoothDevice, 1, var25, 0L, this.error);
//                                                    }
//                                                }
//                                                break label584;
//                                            case 3:
//                                                TTLockAPI.getTTLockCallback().onDeleteICCard(this.mExtendedBluetoothDevice, var25, this.No, this.error);
//                                                break label584;
//                                            case 4:
//                                                TTLockAPI.getTTLockCallback().onClearICCard(this.mExtendedBluetoothDevice, var25, this.error);
//                                                break label584;
//                                            case 5:
//                                                TTLockAPI.getTTLockCallback().onModifyICCardPeriod(this.mExtendedBluetoothDevice, var25, this.No, this.startDate, this.endDate, this.error);
//                                            default:
//                                                break label584;
//                                        }
//                                    case 6:
//                                        var25 = data[2];
//                                        switch (data[3]) {
//                                            case 2:
//                                                if (data[4] == 1) {
//                                                    if (this.currentAPICommand == 44) {
//                                                        ++this.dataPos;
//                                                        if (this.dataPos < this.recoveryDatas.size()) {
//                                                            var38 = (RecoveryData) this.recoveryDatas.get(this.dataPos);
//                                                            CommandUtil.recoveryFRPeriod(command.getLockType(), Long.valueOf(var38.fingerprintNumber).longValue(), var38.startDate, var38.endDate, aesKeyArray, this.timezoneOffSet);
//                                                        } else if (this.upgradeFirmwareListener != null) {
//                                                            this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 6, "", 1);
//                                                        } else {
//                                                            sLockCallback.onRecoveryData(this.mExtendedBluetoothDevice, this.transferData.getOp(), this.error);
//                                                        }
//                                                    } else {
//                                                        var39 = DigitUtil.sixBytesToLong(Arrays.copyOfRange(data, 5, data.length));
//                                                        this.isWaitCommand = false;
//                                                        TTLockAPI.getTTLockCallback().onAddFingerPrint(this.mExtendedBluetoothDevice, 2, var25, var39, this.error);
//                                                        TTLockAPI.getTTLockCallback().onAddFingerPrint(this.mExtendedBluetoothDevice, 2, var25, var39, -1, this.error);
//                                                    }
//                                                } else {
//                                                    byte var42;
//                                                    if (data[4] == 2) {
//                                                        this.isWaitCommand = true;
//                                                        TTLockAPI.getTTLockCallback().onAddFingerPrint(this.mExtendedBluetoothDevice, 1, var25, 0L, this.error);
//                                                        var42 = -1;
//                                                        LogUtil.d("data.length:" + data.length, true);
//                                                        if (data.length == 6) {
//                                                            var42 = data[5];
//                                                        }
//
//                                                        TTLockAPI.getTTLockCallback().onAddFingerPrint(this.mExtendedBluetoothDevice, 1, var25, 0L, var42, this.error);
//                                                        LogUtil.d("进入添加FR模式", true);
//                                                    } else if (data[4] == 3) {
//                                                        this.isWaitCommand = true;
//                                                        LogUtil.d("第一次采集成功，进行第二次采集", true);
//                                                        TTLockAPI.getTTLockCallback().onFingerPrintCollection(this.mExtendedBluetoothDevice, var25, this.error);
//                                                        var42 = -1;
//                                                        byte var43 = -1;
//                                                        if (data.length == 7) {
//                                                            var42 = data[5];
//                                                            var43 = data[6];
//                                                        }
//
//                                                        if (var42 == 0) {
//                                                            TTLockAPI.getTTLockCallback().onAddFingerPrint(this.mExtendedBluetoothDevice, 1, var25, 0L, data[6], this.error);
//                                                            LogUtil.d("进入添加FR模式", true);
//                                                        } else {
//                                                            TTLockAPI.getTTLockCallback().onFingerPrintCollection(this.mExtendedBluetoothDevice, var25, var42, var43, this.error);
//                                                        }
//                                                    } else if (data[4] == 4) {
//                                                        if (data[5] == 0) {
//                                                            this.isWaitCommand = true;
//                                                            TTLockAPI.getTTLockCallback().onAddFingerPrint(this.mExtendedBluetoothDevice, 1, var25, 0L, this.error);
//                                                            TTLockAPI.getTTLockCallback().onAddFingerPrint(this.mExtendedBluetoothDevice, 1, var25, 0L, data[6], this.error);
//                                                            LogUtil.d("进入添加FR模式", true);
//                                                        } else {
//                                                            this.isWaitCommand = true;
//                                                            TTLockAPI.getTTLockCallback().onFingerPrintCollection(this.mExtendedBluetoothDevice, var25, this.error);
//                                                            TTLockAPI.getTTLockCallback().onFingerPrintCollection(this.mExtendedBluetoothDevice, var25, data[5], data[6], this.error);
//                                                        }
//                                                    }
//                                                }
//                                                break label584;
//                                            case 3:
//                                                TTLockAPI.getTTLockCallback().onDeleteFingerPrint(this.mExtendedBluetoothDevice, var25, this.No, this.error);
//                                                break label584;
//                                            case 4:
//                                                TTLockAPI.getTTLockCallback().onClearFingerPrint(this.mExtendedBluetoothDevice, var25, this.error);
//                                                break label584;
//                                            case 5:
//                                                LogUtil.d("No:" + this.No, true);
//                                                TTLockAPI.getTTLockCallback().onModifyFingerPrintPeriod(this.mExtendedBluetoothDevice, var25, this.No, this.startDate, this.endDate, this.error);
//                                                break label584;
//                                            case 6:
//                                                var50 = this.parseFR(Arrays.copyOfRange(data, 4, data.length));
//                                                LogUtil.d("search:" + var50, true);
//                                                if (var50 == -1) {
//                                                    sLockCallback.onSearchFingerPrint(this.mExtendedBluetoothDevice, data[2], GsonUtil.toJson(this.frs), this.error);
//                                                    LogUtil.d("完成", true);
//                                                } else {
//                                                    CommandUtil.searchFRNo(command.getLockType(), var50, aesKeyArray);
//                                                }
//                                            default:
//                                                break label584;
//                                        }
//                                    case 7:
//                                        var44 = (short) (data[2] << 8 | data[3] & 255);
//                                        if (var44 == 0) {
//                                            sLockCallback.onSearchPasscode(this.mExtendedBluetoothDevice, GsonUtil.toJson(this.passcodes), this.error);
//                                        } else {
//                                            var50 = this.parsePasscode(Arrays.copyOfRange(data, 4, data.length));
//                                            if (var50 == -1) {
//                                                sLockCallback.onSearchPasscode(this.mExtendedBluetoothDevice, GsonUtil.toJson(this.passcodes), this.error);
//                                            } else {
//                                                CommandUtil.searchPasscode(command.getLockType(), var50, aesKeyArray);
//                                            }
//                                        }
//                                        break;
//                                    case 20:
//                                        this.isWaitCommand = true;
//                                        this.mExtendedBluetoothDevice.setParkStatus(data[3]);
//                                        TTLockAPI.getTTLockCallback().onGetLockSwitchState(this.mExtendedBluetoothDevice, data[2], data[3], this.error);
//                                        break;
//                                    case 37:
//                                        var44 = CommandUtil_V3.parseOperateLog(this.logOperates, Arrays.copyOfRange(data, 2, data.length), this.timezoneOffSet);
//                                        if (var44 != 0 && var44 != -16) {
//                                            CommandUtil.getOperateLog(command.getLockType(), var44, aesKeyArray);
//                                        } else if (this.upgradeFirmwareListener != null) {
//                                            this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 37, GsonUtil.toJson(this.logOperates), 1);
//                                        } else {
//                                            TTLockAPI.getTTLockCallback().onGetOperateLog(this.mExtendedBluetoothDevice, GsonUtil.toJson(this.logOperates), this.error);
//                                        }
//                                        break;
//                                    case 48:
//                                        switch (this.currentAPICommand) {
//                                            case 5:
//                                                CommandUtil.S_setAdminKeyboardPwd(command.getLockType(), this.keyboardPwd, aesKeyArray);
//                                                break label584;
//                                            case 6:
//                                                CommandUtil.C_calibationTime(command.getLockType(), this.calibationTime, this.timezoneOffSet, aesKeyArray);
//                                            case 7:
//                                            case 8:
//                                            case 9:
//                                            case 10:
//                                            case 11:
//                                            case 13:
//                                            case 14:
//                                            case 15:
//                                            case 18:
//                                            case 26:
//                                            case 42:
//                                            default:
//                                                break label584;
//                                            case 12:
//                                                CommandUtil.D_setDeletePassword(command.getLockType(), this.deletePwd, aesKeyArray);
//                                                break label584;
//                                            case 16:
//                                                CommandUtil_V3.initPasswords(command.getLockType(), aesKeyArray, 11);
//                                                break label584;
//                                            case 17:
//                                                CommandUtil.AT_setLockname(command.getLockType(), this.lockname, aesKeyArray);
//                                                break label584;
//                                            case 19:
//                                                CommandUtil.R_resetLock(command.getLockType());
//                                                break label584;
//                                            case 20:
//                                                CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 2, (byte) 2, this.originalPwd, "", this.startDate, this.endDate, aesKeyArray, this.timezoneOffSet);
//                                                break label584;
//                                            case 21:
//                                                CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 2, (byte) 1, this.originalPwd, "", this.startDate, this.endDate, aesKeyArray, this.timezoneOffSet);
//                                                break label584;
//                                            case 22:
//                                                CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 2, (byte) 3, this.originalPwd, "", this.startDate, this.endDate, aesKeyArray, this.timezoneOffSet);
//                                                break label584;
//                                            case 23:
//                                                CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 5, this.keyboardPwdType, this.originalPwd, this.newPwd, this.startDate, this.endDate, aesKeyArray, this.timezoneOffSet);
//                                                break label584;
//                                            case 24:
//                                                CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 3, this.keyboardPwdType, this.originalPwd, this.newPwd, 0L, 0L, aesKeyArray);
//                                                break label584;
//                                            case 25:
//                                                CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 1, (byte) 0, "", "", 0L, 0L, aesKeyArray);
//                                                break label584;
//                                            case 27:
//                                                CommandUtil.searchDeviceFeature(command.getLockType());
//                                                break label584;
//                                            case 28:
//                                                this.icCards = new ArrayList();
//                                                CommandUtil.searchICCardNo(command.getLockType(), (byte) 0, aesKeyArray);
//                                                break label584;
//                                            case 29:
//                                                CommandUtil.addICCard(command.getLockType(), aesKeyArray);
//                                                break label584;
//                                            case 30:
//                                                CommandUtil.modifyICCardPeriod(command.getLockType(), this.No, this.startDate, this.endDate, aesKeyArray, this.timezoneOffSet);
//                                                break label584;
//                                            case 31:
//                                                CommandUtil.deleteICCard(command.getLockType(), this.No, aesKeyArray);
//                                                break label584;
//                                            case 32:
//                                                CommandUtil.clearICCard(command.getLockType(), aesKeyArray);
//                                                break label584;
//                                            case 33:
//                                                CommandUtil.setWristbandKey(command.getLockType(), this.wristbandKey, aesKeyArray);
//                                                break label584;
//                                            case 34:
//                                                CommandUtil.addFR(command.getLockType(), aesKeyArray);
//                                                break label584;
//                                            case 35:
//                                                LogUtil.d("No:" + this.No, true);
//                                                CommandUtil.modifyFRPeriod(command.getLockType(), this.No, this.startDate, this.endDate, aesKeyArray, this.timezoneOffSet);
//                                                break label584;
//                                            case 36:
//                                                CommandUtil.deleteFR(command.getLockType(), this.No, aesKeyArray);
//                                                break label584;
//                                            case 37:
//                                                CommandUtil.clearFR(command.getLockType(), aesKeyArray);
//                                                break label584;
//                                            case 38:
//                                                CommandUtil.searchAutoLockTime(command.getLockType(), aesKeyArray);
//                                                break label584;
//                                            case 39:
//                                                CommandUtil.modifyAutoLockTime(command.getLockType(), (short) ((int) this.calibationTime), aesKeyArray);
//                                                break label584;
//                                            case 40:
//                                                CommandUtil.enterDFUMode(command.getLockType(), aesKeyArray);
//                                                break label584;
//                                            case 41:
//                                                this.dataPos = 0;
//                                                CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 3, this.keyboardPwdType, (String) this.pwds.get(this.dataPos), this.newPwd, 0L, 0L, aesKeyArray);
//                                                break label584;
//                                            case 43:
//                                                CommandUtil.screenPasscodeManage(command.getLockType(), this.transferData.getOp(), aesKeyArray);
//                                                break label584;
//                                            case 44:
//                                                this.recoveryDatas = (List) GsonUtil.toObject(this.transferData.getJson(), new TypeToken() {
//                                                });
//                                                LogUtil.e("transferData.getJson():" + this.transferData.getJson(), true);
//                                                LogUtil.e("transferData.getOp():" + this.transferData.getOp(), true);
//                                                if (this.recoveryDatas != null && this.recoveryDatas.size() != 0) {
//                                                    this.dataPos = 0;
//                                                    var41 = (RecoveryData) this.recoveryDatas.get(0);
//                                                    switch (this.transferData.getOp()) {
//                                                        case 1:
//                                                            CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 6, (byte) (var41.keyboardPwdType == 2 ? 1 : var41.keyboardPwdType), var41.cycleType, var41.keyboardPwd, var41.keyboardPwd, var41.startDate, var41.endDate, aesKeyArray, this.timezoneOffSet);
//                                                            break label584;
//                                                        case 2:
//                                                            CommandUtil.recoveryICCardPeriod(command.getLockType(), Long.valueOf(var41.cardNumber).longValue(), var41.startDate, var41.endDate, aesKeyArray, this.timezoneOffSet);
//                                                            break label584;
//                                                        case 3:
//                                                            CommandUtil.recoveryFRPeriod(command.getLockType(), Long.valueOf(var41.fingerprintNumber).longValue(), var41.startDate, var41.endDate, aesKeyArray, this.timezoneOffSet);
//                                                    }
//                                                } else {
//                                                    LogUtil.d("recoveryDatas为空", true);
//                                                    sLockCallback.onRecoveryData(this.mExtendedBluetoothDevice, this.transferData.getOp(), this.error);
//                                                }
//                                                break label584;
//                                            case 45:
//                                                CommandUtil.readPwdPara(command.getLockType(), aesKeyArray);
//                                                break label584;
//                                            case 46:
//                                                this.frs = new ArrayList();
//                                                CommandUtil.searchFRNo(command.getLockType(), (byte) 0, aesKeyArray);
//                                                break label584;
//                                            case 47:
//                                                this.passcodes = new ArrayList();
//                                                CommandUtil.searchPasscode(command.getLockType(), (byte) 0, aesKeyArray);
//                                                break label584;
//                                            case 48:
//                                                CommandUtil_V3.controlRemoteUnlock(command, (byte) this.transferData.getOp(), (byte) this.transferData.getOpValue(), aesKeyArray);
//                                                break label584;
//                                        }
//                                    case 49:
//                                        LogUtil.d("currentAPICommand:" + this.currentAPICommand + " COMM_INIT_PASSWORDS", true);
//                                        if (this.currentAPICommand == 2) {
//                                            CommandUtil.searchDeviceFeature(command.getLockType());
//                                        } else {
//                                            TTLockAPI.getTTLockCallback().onResetKeyboardPassword(this.mExtendedBluetoothDevice, this.pwdInfo, this.timestamp, this.error);
//                                        }
//                                        break;
//                                    case 50:
//                                        var25 = data[2];
//                                        var35 = (data[3] << 4 | data[4] >> 4 & 15) & 4095;
//                                        LogUtil.d("bytes:" + DigitUtil.byteArrayToHexString(data), true);
//                                        secretKey = ((long) data[4] * 1L << 32 & 64424509440L | (long) (data[5] << 24) & 4278190080L | (long) (data[6] << 16 & 16711680) | (long) (data[7] << 8 & '\uff00') | (long) (data[8] & 255)) & 68719476735L;
//                                        LogUtil.d("code:" + var35, true);
//                                        LogUtil.d("secretKey:" + secretKey, true);
//                                        LogUtil.d("sec:" + DigitUtil.byteArrayToHexString(data), true);
//                                        Calendar var45 = Calendar.getInstance();
//                                        var45.set(2000 + data[9], data[10] - 1, data[11], data[12], data[13]);
//                                        LogUtil.d("data[9]:" + data[9], true);
//                                        TimeZone var46 = TimeZone.getDefault();
//                                        LogUtil.d("timezoneOffSet:" + this.timezoneOffSet, true);
//                                        if (var46.inDaylightTime(new Date(System.currentTimeMillis()))) {
//                                            this.timezoneOffSet -= (long) var46.getDSTSavings();
//                                        }
//
//                                        var46.setRawOffset((int) this.timezoneOffSet);
//                                        var45.setTimeZone(var46);
//                                        long var49 = var45.getTimeInMillis();
//                                        if (data[9] == 0) {
//                                            var49 = 0L;
//                                        }
//
//                                        LogUtil.d("code:" + var35, true);
//                                        PwdInfoV3 pwdInfoV3 = PwdInfoV3.getInstance(var45.get(Calendar.YEAR), var35, String.valueOf(secretKey), var49);
//                                        String pwdInfoSource = GsonUtil.toJson(pwdInfoV3);
//                                        long timestamp = var45.getTimeInMillis();
//                                        String pwdInfo = CommandUtil.encry(pwdInfoSource, timestamp);
//                                        sLockCallback.onSearchPasscodeParam(this.mExtendedBluetoothDevice, var25, pwdInfo, timestamp, this.error);
//                                        break;
//                                    case 52:
//                                        Calendar var36 = Calendar.getInstance();
//                                        var36.set(2000 + data[2], data[3] - 1, data[4], data[5], data[6], data[7]);
//                                        LogUtil.d(data[2] + ":" + data[3] + ":" + data[4] + ":" + data[5] + ":" + data[6], true);
//                                        TimeZone var48 = TimeZone.getDefault();
//                                        LogUtil.d("timezoneOffSet:" + this.timezoneOffSet, true);
//                                        if (var48.inDaylightTime(new Date(System.currentTimeMillis()))) {
//                                            this.timezoneOffSet -= (long) var48.getDSTSavings();
//                                        }
//
//                                        var48.setRawOffset((int) this.timezoneOffSet);
//                                        var36.setTimeZone(var48);
//                                        LogUtil.d("calendar.getTimeInMillis():" + var36.getTimeInMillis(), true);
//                                        TTLockAPI.getTTLockCallback().onGetLockTime(this.mExtendedBluetoothDevice, var36.getTimeInMillis(), this.error);
//                                        break;
//                                    case 53:
//                                        var25 = data[2];
//                                        TTLockAPI.getTTLockCallback().onSetWristbandKeyToLock(this.mExtendedBluetoothDevice, var25, this.error);
//                                        break;
//                                    case 54:
//                                        var25 = data[2];
//                                        byte var47 = data[3];
//                                        switch (var47) {
//                                            case 1:
//                                                LogUtil.e(DigitUtil.byteArrayToHexString(data), true);
//                                                short var34 = (short) (data[4] << 8 | data[5] & 255);
//                                                short var37 = (short) (data[6] << 8 | data[7] & 255);
//                                                short maxTime = (short) (data[8] << 8 | data[9] & 255);
//                                                LogUtil.d("currentTime:" + var34 + " minTime:" + var37 + " maxTime:" + maxTime, true);
//                                                TTLockAPI.getTTLockCallback().onSearchAutoLockTime(this.mExtendedBluetoothDevice, var25, var34, var37, maxTime, this.error);
//                                                break label584;
//                                            case 2:
//                                                TTLockAPI.getTTLockCallback().onModifyAutoLockTime(this.mExtendedBluetoothDevice, var25, (int) this.calibationTime, this.error);
//                                            default:
//                                                break label584;
//                                        }
//                                    case 55:
//                                        if (data[3] == 1) {
//                                            if (this.transferData == null) {
//                                                this.transferData = new TransferData();
//                                            }
//
//                                            this.transferData.setOpValue(data[4]);
//                                        }
//
//                                        switch (this.currentAPICommand) {
//                                            case 2:
//                                                CommandUtil.operateFinished(command.getLockType());
//                                                break label584;
//                                            case 48:
//                                                this.transferData.setOp(data[3]);
//                                                CommandUtil.searchDeviceFeature(command.getLockType());
//                                            default:
//                                                break label584;
//                                        }
//                                    case 65:
//                                        battery = data.length - 2;
//                                        var26 = new byte[battery];
//                                        System.arraycopy(data, 2, var26, 0, battery);
//                                        switch (this.currentAPICommand) {
//                                            case 3:
//                                            case 4:
//                                                CommandUtil.G_unlock(command.getLockType(), this.unlockKey, var26, aesKeyArray, this.unlockDate, this.timezoneOffSet);
//                                                break label584;
//                                            case 5:
//                                                if (command.getLockType() == 5) {
//                                                    CommandUtil.checkRandom(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                } else {
//                                                    CommandUtil.S_setAdminKeyboardPwd(command.getLockType(), this.keyboardPwd, aesKeyArray);
//                                                }
//                                                break label584;
//                                            case 6:
//                                            case 7:
//                                            case 8:
//                                            case 9:
//                                            case 10:
//                                            case 11:
//                                            case 18:
//                                            case 26:
//                                            case 45:
//                                            default:
//                                                LogUtil.d("无效指令", true);
//                                                break label584;
//                                            case 12:
//                                                if (command.getLockType() == 5) {
//                                                    CommandUtil.checkRandom(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                } else {
//                                                    CommandUtil.D_setDeletePassword(command.getLockType(), this.deletePwd, aesKeyArray);
//                                                }
//                                                break label584;
//                                            case 13:
//                                            case 14:
//                                                if (command.getLockType() == 8) {
//                                                    CommandUtil.lock(command.getLockType(), this.unlockKey, var26, aesKeyArray, this.unlockDate);
//                                                } else {
//                                                    CommandUtil.L_lock(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                }
//                                                break label584;
//                                            case 15:
//                                                TTLockAPI.getTTLockCallback().onResetEKey(this.mExtendedBluetoothDevice, this.lockFlagPos, this.error);
//                                                break label584;
//                                            case 16:
//                                                switch (command.getLockType()) {
//                                                    case 3:
//                                                        this.pwdList = new LinkedList();
//                                                        var31 = this.generatePwd(KeyboardPwd.ONE_DAY_PWD);
//                                                        CommandUtil_V2S.synPwd(command.getLockType(), var31, 0);
//                                                        break label584;
//                                                    case 4:
//                                                        this.pwdData = new byte[1624];
//                                                        this.generateTransmissionData(command.getScene(), this.pwdData, this.validPwdNum);
//                                                        this.dataPos = 0;
//                                                        CommandUtil_V2S_PLUS.synPwd(command.getLockType(), Arrays.copyOfRange(this.pwdData, this.dataPos, this.dataPos + this.packetLen), this.dataPos, aesKeyArray);
//                                                        break label584;
//                                                    case 5:
//                                                        CommandUtil.checkRandom(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                    default:
//                                                        break label584;
//                                                }
//                                            case 17:
//                                                CommandUtil.checkRandom(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                break label584;
//                                            case 19:
//                                                if (command.getLockType() == 5) {
//                                                    CommandUtil.checkRandom(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                } else {
//                                                    CommandUtil.R_resetLock(command.getLockType());
//                                                }
//                                                break label584;
//                                            case 20:
//                                            case 21:
//                                            case 22:
//                                            case 23:
//                                            case 24:
//                                            case 25:
//                                            case 27:
//                                            case 28:
//                                            case 29:
//                                            case 30:
//                                            case 31:
//                                            case 32:
//                                            case 33:
//                                            case 34:
//                                            case 35:
//                                            case 36:
//                                            case 37:
//                                            case 38:
//                                            case 39:
//                                            case 40:
//                                            case 41:
//                                            case 42:
//                                            case 43:
//                                            case 44:
//                                            case 46:
//                                            case 47:
//                                            case 48:
//                                                CommandUtil.checkRandom(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                break label584;
//                                        }
//                                    case 67:
//                                        if (this.upgradeFirmwareListener != null) {
//                                            this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 67, "", 1);
//                                        } else {
//                                            TTLockAPI.getTTLockCallback().onSetLockTime(this.mExtendedBluetoothDevice, this.error);
//                                        }
//                                        break;
//                                    case 68:
//                                        LogUtil.d("set delete pwd success", true);
//                                        if (this.currentAPICommand == 2) {
//                                            switch (command.getLockType()) {
//                                                case 3:
//                                                    this.pwdList = new LinkedList();
//                                                    String var33 = this.generatePwd(KeyboardPwd.ONE_DAY_PWD);
//                                                    CommandUtil_V2S.synPwd(command.getLockType(), var33, 0);
//                                                    break label584;
//                                                case 4:
//                                                    this.pwdData = new byte[1624];
//                                                    this.generateTransmissionData(command.getScene(), this.pwdData, this.validPwdNum);
//                                                    this.dataPos = 0;
//                                                    CommandUtil_V2S_PLUS.synPwd(command.getLockType(), Arrays.copyOfRange(this.pwdData, this.dataPos, this.dataPos + this.packetLen), this.dataPos, aesKeyArray);
//                                            }
//                                        } else {
//                                            TTLockAPI.getTTLockCallback().onSetDeletePassword(this.mExtendedBluetoothDevice, this.deletePwd, this.error);
//                                        }
//                                        break;
//                                    case 71:
//                                        var25 = -1;
//                                        int var40 = 0;
//                                        Calendar var32 = Calendar.getInstance();
//                                        secretKey = var32.getTimeInMillis();
//                                        int calendar = (int) (secretKey / 1000L);
//                                        int timeZone = data.length;
//                                        if (timeZone > 2) {
//                                            var25 = data[2];
//                                            if (timeZone >= 17) {
//                                                var40 = (int) DigitUtil.fourBytesToLong(Arrays.copyOfRange(data, 3, 7));
//                                                calendar = (int) DigitUtil.fourBytesToLong(Arrays.copyOfRange(data, 7, 11));
//                                                var32.set(2000 + data[11], data[12] - 1, data[13], data[14], data[15], data[16]);
//                                                TimeZone deleteTime = TimeZone.getDefault();
//                                                LogUtil.d("timezoneOffSet:" + this.timezoneOffSet, true);
//                                                if (deleteTime.inDaylightTime(new Date(System.currentTimeMillis()))) {
//                                                    this.timezoneOffSet -= (long) deleteTime.getDSTSavings();
//                                                }
//
//                                                deleteTime.setRawOffset((int) this.timezoneOffSet);
//                                                var32.setTimeZone(deleteTime);
//                                                secretKey = var32.getTimeInMillis();
//                                            }
//                                        }
//
//                                        this.mExtendedBluetoothDevice.setBatteryCapacity(var25);
//                                        if (command.getLockType() == 6) {
//                                            TTLockAPI.getTTLockCallback().onLock(this.mExtendedBluetoothDevice, var25, var40, (int) (this.unlockDate / 1000L), secretKey, this.error);
//                                        } else {
//                                            TTLockAPI.getTTLockCallback().onUnlock(this.mExtendedBluetoothDevice, var40, calendar, secretKey, this.error);
//                                        }
//                                        break;
//                                    case 73:
//                                        switch (command.getLockType()) {
//                                            case 3:
//                                                battery = this.pwdList.size();
//                                                TTLockAPI.getTTLockCallback().onResetKeyboardPasswordProgress(this.mExtendedBluetoothDevice, battery / 9, this.error);
//                                                boolean var28 = false;
//                                                byte var30;
//                                                if (battery < 300) {
//                                                    var30 = 1;
//                                                } else if (battery < 450) {
//                                                    var30 = 2;
//                                                } else if (battery < 550) {
//                                                    var30 = 3;
//                                                } else if (battery < 650) {
//                                                    var30 = 4;
//                                                } else if (battery < 700) {
//                                                    var30 = 5;
//                                                } else if (battery < 750) {
//                                                    var30 = 6;
//                                                } else if (battery < 800) {
//                                                    var30 = 7;
//                                                } else {
//                                                    if (battery >= 900) {
//                                                        try {
//                                                            var31 = DigitUtil.generateKeyboardPwd_Json(this.pwdList);
//                                                            this.timestamp = System.currentTimeMillis();
//                                                            this.pwdInfo = CommandUtil.encry(var31, this.timestamp);
//                                                            LogUtil.d("pwdInfoOrigin:" + var31, true);
//                                                            if (this.currentAPICommand == 2) {
//                                                                TTLockAPI.getTTLockCallback().onAddAdministrator(this.mExtendedBluetoothDevice, command.getLockVersionString(), DigitUtil.encodeLockData(this.adminPs), DigitUtil.encodeLockData(this.unlockKey), this.keyboardPwd, this.deletePwd, this.pwdInfo, this.timestamp, DigitUtil.encodeAesKey(aesKeyArray), 1, this.modelNumber, this.hardwareRevision, this.firmwareRevision, this.error);
//                                                            } else {
//                                                                TTLockAPI.getTTLockCallback().onResetKeyboardPassword(this.mExtendedBluetoothDevice, this.pwdInfo, this.timestamp, this.error);
//                                                            }
//                                                        } catch (JSONException var21) {
//                                                            var21.printStackTrace();
//                                                        }
//                                                        break label584;
//                                                    }
//
//                                                    var30 = 8;
//                                                }
//
//                                                var31 = this.generatePwd(var30);
//                                                CommandUtil_V2S.synPwd(command.getLockType(), var31, battery);
//                                                break label584;
//                                            case 4:
//                                                this.dataPos += this.packetLen;
//                                                if (this.dataPos + 1 < this.pwdData.length) {
//                                                    TTLockAPI.getTTLockCallback().onResetKeyboardPasswordProgress(this.mExtendedBluetoothDevice, this.dataPos * 100 / this.pwdData.length, this.error);
//                                                    CommandUtil_V2S_PLUS.synPwd(command.getLockType(), Arrays.copyOfRange(this.pwdData, this.dataPos, this.dataPos + this.packetLen), this.dataPos, aesKeyArray);
//                                                } else {
//                                                    LogUtil.e("LOCK_TYPE_V2S_PLUS", true);
//                                                    if (this.currentAPICommand == 2) {
//                                                        TTLockAPI.getTTLockCallback().onAddAdministrator(this.mExtendedBluetoothDevice, command.getLockVersionString(), DigitUtil.encodeLockData(this.adminPs), DigitUtil.encodeLockData(this.unlockKey), this.keyboardPwd, this.deletePwd, this.pwdInfo, this.timestamp, DigitUtil.encodeAesKey(aesKeyArray), 1, this.modelNumber, this.hardwareRevision, this.firmwareRevision, this.error);
//                                                        this.disconnect();
//                                                    } else {
//                                                        TTLockAPI.getTTLockCallback().onResetKeyboardPassword(this.mExtendedBluetoothDevice, this.pwdInfo, this.timestamp, this.error);
//                                                    }
//                                                }
//                                            default:
//                                                break label584;
//                                        }
//                                    case 76:
//                                        var25 = -1;
//                                        if (data.length > 2) {
//                                            var25 = data[2];
//                                        }
//
//                                        this.mExtendedBluetoothDevice.setBatteryCapacity(var25);
//                                        TTLockAPI.getTTLockCallback().onUnlock(this.mExtendedBluetoothDevice, 0, (int) (System.currentTimeMillis() / 1000L), System.currentTimeMillis(), this.error);
//                                        break;
//                                    case 78:
//                                        TTLockAPI.getTTLockCallback().onSetLockName(this.mExtendedBluetoothDevice, this.lockname, this.error);
//                                        break;
//                                    case 82:
//                                        TTLockAPI.getTTLockCallback().onResetLock(this.mExtendedBluetoothDevice, this.error);
//                                        break;
//                                    case 83:
//                                        if (this.currentAPICommand == 2) {
//                                            if (command.getLockType() == 5) {
//                                                CommandUtil_V3.initPasswords(command.getLockType(), aesKeyArray, this.currentAPICommand);
//                                            } else {
//                                                this.deletePwd = DigitUtil.generatePwdByLength(7);
//                                                CommandUtil.D_setDeletePassword(command.getLockType(), this.deletePwd, aesKeyArray);
//                                            }
//                                        } else {
//                                            TTLockAPI.getTTLockCallback().onSetAdminKeyboardPassword(this.mExtendedBluetoothDevice, this.keyboardPwd, this.error);
//                                        }
//                                        break;
//                                    case 85:
//                                        battery = data.length - 2;
//                                        var26 = new byte[battery];
//                                        System.arraycopy(data, 2, var26, 0, battery);
//                                        switch (this.currentAPICommand) {
//                                            case 3:
//                                            case 4:
//                                                CommandUtil.G_unlock(command.getLockType(), this.unlockKey, var26, aesKeyArray, this.unlockDate, this.timezoneOffSet);
//                                                break label584;
//                                            case 6:
//                                                if (command.getLockType() != 5 && command.getLockType() != 8) {
//                                                    CommandUtil.C_calibationTime(command.getLockType(), this.calibationTime, this.timezoneOffSet, aesKeyArray);
//                                                } else {
//                                                    CommandUtil.checkRandom(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                }
//                                                break label584;
//                                            case 13:
//                                            case 14:
//                                                if (command.getLockType() == 8) {
//                                                    CommandUtil.lock(command.getLockType(), this.unlockKey, var26, aesKeyArray, this.unlockDate);
//                                                } else {
//                                                    CommandUtil.L_lock(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                }
//                                                break label584;
//                                            case 42:
//                                                CommandUtil.lock(command.getLockType(), this.unlockKey, var26, aesKeyArray, this.unlockDate);
//                                                break label584;
//                                            case 45:
//                                                CommandUtil.checkRandom(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                            default:
//                                                break label584;
//                                        }
//                                    case 86:
//                                        if (command.getLockType() == 5 && !Constant.SCIENER.equals(new String(data, 2, 7))) {
//                                            this.error = Error.AES_PARSE_ERROR;
//                                            this.error.setCommand(command.getCommand());
//                                            this.errorCallback(this.error);
//                                            return;
//                                        }
//
//                                        if (command.getLockType() == 8) {
//                                            CommandUtil.searchDeviceFeature(command.getLockType());
//                                        } else if (command.getLockType() == 6) {
//                                            TTLockAPI.getTTLockCallback().onAddAdministrator(this.mExtendedBluetoothDevice, command.getLockVersionString(), DigitUtil.encodeLockData(this.adminPs), DigitUtil.encodeLockData(this.unlockKey), "", "", "", 0L, "", 0, this.modelNumber, this.hardwareRevision, this.firmwareRevision, this.error);
//                                        } else {
//                                            this.keyboardPwd = DigitUtil.generatePwdByLength(7);
//                                            CommandUtil.S_setAdminKeyboardPwd(command.getLockType(), this.keyboardPwd, aesKeyArray);
//                                        }
//                                        break;
//                                    case 87:
//                                        if (command.getLockType() != 5 && command.getLockType() != 8) {
//                                            battery = data.length;
//                                            if (battery == 8) {
//                                                if (data[7] == 1) {
//                                                    op = Calendar.getInstance();
//                                                    op.set(2000 + data[2], data[3] - 1, data[4], data[5], data[6]);
//                                                    code = TimeZone.getDefault();
//                                                    LogUtil.d("timezoneOffSet:" + this.timezoneOffSet, true);
//                                                    if (code.inDaylightTime(new Date(System.currentTimeMillis()))) {
//                                                        this.timezoneOffSet -= (long) code.getDSTSavings();
//                                                    }
//
//                                                    code.setRawOffset((int) this.timezoneOffSet);
//                                                    op.setTimeZone(code);
//                                                    this.moveDateArray.put(op.getTimeInMillis());
//                                                    CommandUtil_Va.Va_Get_Lockcar_Alarm(command.getLockType());
//                                                } else {
//                                                    LogUtil.w("读取车位锁数据出错", true);
//                                                }
//                                            } else if (battery == 3) {
//                                                TTLockAPI.getTTLockCallback().onGetOperateLog(this.mExtendedBluetoothDevice, this.moveDateArray.toString(), this.error);
//                                            }
//                                        } else {
//                                            this.tempOptype = 1;
//                                            CommandUtil.readDeviceInfo(command.getLockType(), (byte) 1, aesKeyArray);
//                                            this.mExtendedBluetoothDevice.disconnectStatus = 3;
//                                            this.mHandler.postDelayed(this.disConRunable, 1500L);
//                                        }
//                                        break;
//                                    case 88:
//                                        delay = (int) DigitUtil.fourBytesToLong(Arrays.copyOfRange(data, 3, 7));
//                                        battery = (int) DigitUtil.fourBytesToLong(Arrays.copyOfRange(data, 7, 11));
//                                        op = Calendar.getInstance();
//                                        op.set(2000 + data[11], data[12] - 1, data[13], data[14], data[15], data[16]);
//                                        code = TimeZone.getDefault();
//                                        LogUtil.d("timezoneOffSet:" + this.timezoneOffSet, true);
//                                        if (code.inDaylightTime(new Date(System.currentTimeMillis()))) {
//                                            this.timezoneOffSet -= (long) code.getDSTSavings();
//                                        }
//
//                                        code.setRawOffset((int) this.timezoneOffSet);
//                                        op.setTimeZone(code);
//                                        secretKey = op.getTimeInMillis();
//                                        this.mExtendedBluetoothDevice.setBatteryCapacity(data[2]);
//                                        TTLockAPI.getTTLockCallback().onLock(this.mExtendedBluetoothDevice, data[2], delay, battery, secretKey, this.error);
//                                        break;
//                                    case 89:
//                                        delay = this.transferData.getOp();
//                                        if (data[3] == 1) {
//                                            delay = data[4];
//                                        } else {
//                                            delay -= 2;
//                                        }
//
//                                        TTLockAPI.getTTLockCallback().onScreenPasscodeOperate(this.mExtendedBluetoothDevice, data[2], delay, this.error);
//                                        break;
//                                    default:
//                                        LogUtil.w("异常指令", true);
//                                }
//                            } else if (this.currentAPICommand == 6 && data[0] == 85) {
//                                CommandUtil.C_calibationTime(command.getLockType(), this.calibationTime, this.timezoneOffSet, aesKeyArray);
//                            } else if (this.currentAPICommand == 44 && data[2] != 22) {
//                                ++this.dataPos;
//                                if (this.dataPos < this.recoveryDatas.size()) {
//                                    RecoveryData var23 = (RecoveryData) this.recoveryDatas.get(this.dataPos);
//                                    switch (this.transferData.getOp()) {
//                                        case 1:
//                                            CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 6, (byte) (var23.keyboardPwdType == 2 ? 1 : var23.keyboardPwdType), var23.cycleType, var23.keyboardPwd, var23.keyboardPwd, var23.startDate, var23.endDate, aesKeyArray, this.timezoneOffSet);
//                                            break;
//                                        case 2:
//                                            CommandUtil.recoveryICCardPeriod(command.getLockType(), Long.valueOf(var23.cardNumber).longValue(), var23.startDate, var23.endDate, aesKeyArray, this.timezoneOffSet);
//                                            break;
//                                        case 3:
//                                            CommandUtil.recoveryFRPeriod(command.getLockType(), Long.valueOf(var23.fingerprintNumber).longValue(), var23.startDate, var23.endDate, aesKeyArray, this.timezoneOffSet);
//                                    }
//                                } else {
//                                    sLockCallback.onRecoveryData(this.mExtendedBluetoothDevice, this.transferData.getOp(), this.error);
//                                }
//                            } else {
//                                this.error = Error.getInstance(data[2]);
//                                this.error.setCommand(data[0]);
//                                if (data[0] == 3 && data[3] == 5) {
//                                    this.tmpError = this.error;
//                                    CommandUtil.searchDeviceFeature(command.getLockType());
//                                } else {
//                                    this.errorCallback(this.error);
//                                }
//                            }
//                        }
//
//                        if (command.getLockType() == 5 && !this.isWaitCommand) {
//                            LogUtil.e("开始计时", true);
//                            long var24 = 2000L;
//                            if (this.currentAPICommand == 19 || this.currentAPICommand == 34) {
//                                var24 = 7000L;
//                            }
//
//                            this.mExtendedBluetoothDevice.disconnectStatus = 3;
//                            this.mHandler.postDelayed(this.disConRunable, var24);
//                        }
//
//                    }
//                } else {
//                    if (this.currentAPICommand == 19 && command.length == 0) {
//                        TTLockAPI.getTTLockCallback().onResetLock(this.mExtendedBluetoothDevice, Error.SUCCESS);
//                    } else {
//                        this.error = Error.KEY_INVALID;
//                        this.error.setCommand(command.getCommand());
//                        this.errorCallback(this.error);
//                    }
//
//                }
//            }
//        }
//    }
//
//    public void disconnect() {
//        mConnectionState = 0;
//        LogUtil.d("dis ble connect", true);
//        if (this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
//            this.mBluetoothGatt.disconnect();
//        } else {
//            Log.w(TAG, "BluetoothAdapter not initialized");
//        }
//    }
//
//    public synchronized void close() {
//        if (this.mBluetoothGatt != null) {
//            this.mBluetoothGatt.close();
//            this.mBluetoothGatt = null;
//        }
//    }
//
//    @RequiresPermission("android.permission.BLUETOOTH")
//    private void errorCallback(Error error) {
//        error.setLockname(this.mExtendedBluetoothDevice.getName());
//        error.setLockmac(this.mExtendedBluetoothDevice.getAddress());
//        error.setDate(System.currentTimeMillis());
//        if (this.upgradeFirmwareListener != null) {
//            this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 0, error, 4);
//        } else {
//            LogUtil.d("error" + error, true);
//            switch (this.currentAPICommand) {
//                case 2:
//                    TTLockAPI.getTTLockCallback().onAddAdministrator(this.mExtendedBluetoothDevice, (String) null, (String) null, (String) null, (String) null, (String) null, (String) null, 0L, (String) null, this.feature, this.modelNumber, this.hardwareRevision, this.firmwareRevision, error);
//                    break;
//                case 3:
//                case 4:
//                    TTLockAPI.getTTLockCallback().onUnlock(this.mExtendedBluetoothDevice, 0, (int) (this.unlockDate / 1000L), this.unlockDate, error);
//                    break;
//                case 5:
//                    TTLockAPI.getTTLockCallback().onSetAdminKeyboardPassword(this.mExtendedBluetoothDevice, (String) null, error);
//                case 6:
//                case 7:
//                case 8:
//                case 9:
//                case 10:
//                case 11:
//                case 24:
//                case 25:
//                case 41:
//                default:
//                    break;
//                case 12:
//                    TTLockAPI.getTTLockCallback().onSetDeletePassword(this.mExtendedBluetoothDevice, (String) null, error);
//                    break;
//                case 13:
//                case 14:
//                    TTLockAPI.getTTLockCallback().onLock(this.mExtendedBluetoothDevice, -1, 0, (int) (this.unlockDate / 1000L), this.unlockDate, error);
//                    break;
//                case 15:
//                    TTLockAPI.getTTLockCallback().onResetEKey(this.mExtendedBluetoothDevice, this.lockFlagPos, error);
//                    break;
//                case 16:
//                    TTLockAPI.getTTLockCallback().onResetKeyboardPassword(this.mExtendedBluetoothDevice, (String) null, 0L, error);
//                    break;
//                case 17:
//                    TTLockAPI.getTTLockCallback().onSetLockName(this.mExtendedBluetoothDevice, this.lockname, error);
//                    break;
//                case 18:
//                    TTLockAPI.getTTLockCallback().onGetLockTime(this.mExtendedBluetoothDevice, 0L, error);
//                    break;
//                case 19:
//                    TTLockAPI.getTTLockCallback().onResetLock(this.mExtendedBluetoothDevice, error);
//                    break;
//                case 20:
//                case 21:
//                case 22:
//                    TTLockAPI.getTTLockCallback().onAddKeyboardPassword(this.mExtendedBluetoothDevice, this.keyboardPwdType, this.originalPwd, this.startDate, this.endDate, error);
//                    break;
//                case 23:
//                    TTLockAPI.getTTLockCallback().onModifyKeyboardPassword(this.mExtendedBluetoothDevice, this.keyboardPwdType, this.originalPwd, this.newPwd, error);
//                    break;
//                case 26:
//                    TTLockAPI.getTTLockCallback().onGetOperateLog(this.mExtendedBluetoothDevice, (String) null, error);
//                    break;
//                case 27:
//                    TTLockAPI.getTTLockCallback().onSearchDeviceFeature(this.mExtendedBluetoothDevice, -1, -1, error);
//                    break;
//                case 28:
//                    sLockCallback.onSearchICCard(this.mExtendedBluetoothDevice, -1, (String) null, error);
//                    break;
//                case 29:
//                    TTLockAPI.getTTLockCallback().onAddICCard(this.mExtendedBluetoothDevice, 0, -1, this.No, error);
//                    break;
//                case 30:
//                    TTLockAPI.getTTLockCallback().onModifyICCardPeriod(this.mExtendedBluetoothDevice, -1, this.No, this.startDate, this.endDate, error);
//                    break;
//                case 31:
//                    TTLockAPI.getTTLockCallback().onDeleteICCard(this.mExtendedBluetoothDevice, -1, this.No, error);
//                    break;
//                case 32:
//                    TTLockAPI.getTTLockCallback().onClearICCard(this.mExtendedBluetoothDevice, -1, error);
//                    break;
//                case 33:
//                    TTLockAPI.getTTLockCallback().onSetWristbandKeyToLock(this.mExtendedBluetoothDevice, -1, error);
//                    break;
//                case 34:
//                    TTLockAPI.getTTLockCallback().onAddFingerPrint(this.mExtendedBluetoothDevice, 0, -1, this.No, error);
//                    TTLockAPI.getTTLockCallback().onAddFingerPrint(this.mExtendedBluetoothDevice, 0, -1, this.No, -1, error);
//                    break;
//                case 35:
//                    TTLockAPI.getTTLockCallback().onModifyFingerPrintPeriod(this.mExtendedBluetoothDevice, -1, this.No, this.startDate, this.endDate, error);
//                    break;
//                case 36:
//                    TTLockAPI.getTTLockCallback().onDeleteFingerPrint(this.mExtendedBluetoothDevice, -1, this.No, error);
//                    break;
//                case 37:
//                    TTLockAPI.getTTLockCallback().onClearFingerPrint(this.mExtendedBluetoothDevice, -1, error);
//                    break;
//                case 38:
//                    TTLockAPI.getTTLockCallback().onSearchAutoLockTime(this.mExtendedBluetoothDevice, -1, 0, 0, 0, error);
//                    break;
//                case 39:
//                    TTLockAPI.getTTLockCallback().onModifyAutoLockTime(this.mExtendedBluetoothDevice, -1, 0, error);
//                    break;
//                case 40:
//                    TTLockAPI.getTTLockCallback().onEnterDFUMode(this.mExtendedBluetoothDevice, error);
//                    break;
//                case 42:
//                    TTLockAPI.getTTLockCallback().onLock(this.mExtendedBluetoothDevice, -1, -1, -1, -1L, error);
//                    break;
//                case 43:
//                    TTLockAPI.getTTLockCallback().onScreenPasscodeOperate(this.mExtendedBluetoothDevice, -1, -1, error);
//                    break;
//                case 44:
//                    sLockCallback.onRecoveryData(this.mExtendedBluetoothDevice, this.transferData.getOp(), error);
//                    break;
//                case 45:
//                    sLockCallback.onSearchPasscodeParam(this.mExtendedBluetoothDevice, -1, (String) null, 0L, error);
//                    break;
//                case 46:
//                    sLockCallback.onSearchFingerPrint(this.mExtendedBluetoothDevice, -1, (String) null, error);
//                    break;
//                case 47:
//                    sLockCallback.onSearchPasscode(this.mExtendedBluetoothDevice, (String) null, error);
//            }
//
//        }
//    }
//
//    public String generateTransmissionData(int scene, byte[] values, byte validPwdNum) {
//        LinkedHashSet pwdSet = new LinkedHashSet();
//        StringBuilder fourKeyboardPwdList = new StringBuilder();
//        fourKeyboardPwdList.append('[');
//
//        while (pwdSet.size() < 300) {
//            String iterator = DigitUtil.generatePwdByLength(4);
//            if (pwdSet.add(iterator)) {
//                fourKeyboardPwdList.append(iterator);
//                fourKeyboardPwdList.append(",");
//            }
//        }
//
//        fourKeyboardPwdList.replace(fourKeyboardPwdList.length() - 1, fourKeyboardPwdList.length(), "]");
//        Iterator var23 = pwdSet.iterator();
//
//        int pointer;
//        int timeControlTbBuilder;
//        for (pointer = 0; var23.hasNext(); values[pointer++] = (byte) (timeControlTbBuilder >> 8)) {
//            String timeTable = (String) var23.next();
//            timeControlTbBuilder = Integer.valueOf(timeTable).intValue();
//            values[pointer++] = (byte) timeControlTbBuilder;
//        }
//
//        byte[] var24 = new byte[1000];
//
//        for (timeControlTbBuilder = 0; timeControlTbBuilder < 1000; ++timeControlTbBuilder) {
//            var24[timeControlTbBuilder] = -1;
//        }
//
//        StringBuilder var25 = new StringBuilder();
//        var25.append('{');
//        byte pwdType = 1;
//        switch (scene) {
//            case 1:
//                pwdType = 1;
//                break;
//            case 2:
//            case 3:
//                pwdType = 2;
//        }
//
//        int timePos;
//        int posSet;
//        label154:
//        switch (pwdType) {
//            case 1:
//                timePos = 0;
//
//                while (true) {
//                    if (timePos >= 218) {
//                        break label154;
//                    }
//
//                    do {
//                        posSet = DigitUtil.generateRandomIntegerByUpperBound(1000);
//                    } while (var24[posSet] != -1);
//
//                    var24[posSet] = (byte) (timePos < 10 ? 0 : timePos - 9);
//                    if (timePos == 0) {
//                        var25.append(0);
//                        var25.append(':');
//                        var25.append('[');
//                        var25.append(String.format("%03d", new Object[]{Integer.valueOf(posSet)}));
//                    } else if (timePos > 0 && timePos < 9) {
//                        var25.append(',');
//                        var25.append(String.format("%03d", new Object[]{Integer.valueOf(posSet)}));
//                    } else if (timePos == 9) {
//                        var25.append(',');
//                        var25.append(String.format("%03d", new Object[]{Integer.valueOf(posSet)}));
//                        var25.append(']');
//                    } else {
//                        var25.append(',');
//                        var25.append(timePos - 9);
//                        var25.append(':');
//                        var25.append(String.format("%03d", new Object[]{Integer.valueOf(posSet)}));
//                    }
//
//                    ++timePos;
//                }
//            case 2:
//                for (timePos = 0; timePos < 255; ++timePos) {
//                    do {
//                        posSet = DigitUtil.generateRandomIntegerByUpperBound(1000);
//                    } while (var24[posSet] != -1);
//
//                    var24[posSet] = (byte) (timePos < 10 ? 0 : timePos - 9);
//                    if (timePos == 0) {
//                        var25.append(0);
//                        var25.append(':');
//                        var25.append('[');
//                        var25.append(String.format("%03d", new Object[]{Integer.valueOf(posSet)}));
//                    } else if (timePos > 0 && timePos < 9) {
//                        var25.append(',');
//                        var25.append(String.format("%03d", new Object[]{Integer.valueOf(posSet)}));
//                    } else if (timePos == 9) {
//                        var25.append(',');
//                        var25.append(String.format("%03d", new Object[]{Integer.valueOf(posSet)}));
//                        var25.append(']');
//                    } else if (timePos < 138) {
//                        var25.append(',');
//                        var25.append(timePos - 9);
//                        var25.append(':');
//                        var25.append(String.format("%03d", new Object[]{Integer.valueOf(posSet)}));
//                    } else if (timePos < 233) {
//                        timePos = timePos == 138 ? 209 : timePos;
//                        var25.append(',');
//                        var25.append(timePos);
//                        var25.append(':');
//                        var25.append(String.format("%03d", new Object[]{Integer.valueOf(posSet)}));
//                    } else if (timePos == 233) {
//                        timePos = 254;
//                        var25.append(',');
//                        var25.append(timePos);
//                        var25.append(':');
//                        var25.append(String.format("%03d", new Object[]{Integer.valueOf(posSet)}));
//                    }
//                }
//        }
//
//        var25.append('}');
//        System.arraycopy(var24, 0, values, pointer, 1000);
//        pointer += 1000;
//        byte[] var27 = new byte[3];
//        TreeSet var26 = new TreeSet();
//
//        while (var26.size() < 3) {
//            var26.add(Byte.valueOf((byte) (DigitUtil.generateRandomIntegerByUpperBound(7) + 1)));
//        }
//
//        StringBuilder positionBuilder = new StringBuilder();
//        positionBuilder.append('[');
//        Iterator posIterator = var26.iterator();
//
//        for (int checkingTable = 0; checkingTable < 3; ++checkingTable) {
//            var27[checkingTable] = ((Byte) posIterator.next()).byteValue();
//            positionBuilder.append(var27[checkingTable]);
//            positionBuilder.append(',');
//        }
//
//        positionBuilder.replace(positionBuilder.length() - 1, positionBuilder.length(), "]");
//        System.arraycopy(var27, 0, values, pointer, 3);
//        pointer += 3;
//        byte[] var28 = new byte[10];
//        LinkedHashSet convertSet = new LinkedHashSet();
//
//        while (convertSet.size() < 10) {
//            convertSet.add(Byte.valueOf((byte) DigitUtil.generateRandomIntegerByUpperBound(10)));
//        }
//
//        Iterator convertIterator = convertSet.iterator();
//        StringBuilder checkDigitBuilder = new StringBuilder();
//
//        for (int res = 0; res < 10; ++res) {
//            var28[res] = ((Byte) convertIterator.next()).byteValue();
//            checkDigitBuilder.append(var28[res]);
//        }
//
//        System.arraycopy(var28, 0, values, pointer, 10);
//        values[1613] = validPwdNum;
//        new StringBuilder();
//        JSONObject jsonObject = new JSONObject();
//
//        try {
//            jsonObject.put("position", positionBuilder);
//            jsonObject.put("currentIndex", -1);
//            jsonObject.put("timeControlTb", var25);
//            jsonObject.put("fourKeyboardPwdList", fourKeyboardPwdList);
//            jsonObject.put("checkDigit", checkDigitBuilder);
//        } catch (JSONException var22) {
//            var22.printStackTrace();
//        }
//
//        this.pwdInfo = CommandUtil.encry(jsonObject.toString(), this.timestamp);
//        LogUtil.d("values:" + DigitUtil.byteArrayToHexString(Arrays.copyOfRange(values, 0, 1000)), true);
//        LogUtil.d("values:" + DigitUtil.byteArrayToHexString(Arrays.copyOfRange(values, 1000, 1613)), true);
//        return jsonObject.toString();
//    }
//
//    private String generatePwd(int pwdType) {
//        StringBuilder stringBuilder = new StringBuilder();
//
//        for (int i = 0; i < 5; ++i) {
//            String pwd = DigitUtil.generatePwdByType(pwdType);
//            this.pwdList.add(pwd);
//            stringBuilder.append(pwd);
//        }
//
//        return stringBuilder.toString();
//    }
//
//    public boolean isConnected(String address) {
//        return address == null ? false : address.equals(this.mBluetoothDeviceAddress) && mConnectionState == 2;
//    }
//
//    public void clearTask() {
//        if (this.mHandler != null) {
//            this.mHandler.removeCallbacks(this.disConRunable);
//        }
//
//        if (this.disTimerTask != null) {
//            this.disTimerTask.cancel();
//        }
//
//        if (this.timer != null) {
//            this.timer.purge();
//        }
//
//    }
//
//    public void onDestroy() {
//        super.onDestroy();
//        LogUtil.e("-----------------onDestroy-------------------------", true);
//        this.unregisterReceiver(this.bluttoothState);
//        if (this.disTimerTask != null) {
//            this.disTimerTask.cancel();
//        }
//
//        this.disTimerTask = null;
//        this.upgradeFirmwareListener = null;
//        this.timer.cancel();
//        this.timer.purge();
//        this.timer = null;
//        TTLockAPI.scan = false;
//        LogUtil.d("TTLockAPI.scan:" + TTLockAPI.scan, true);
//        this.stopScan();
//        this.disconnect();
//        this.close();
//    }
//
//    public int getConnectCnt() {
//        return this.connectCnt;
//    }
//
//    public void setConnectCnt(int connectCnt) {
//        this.connectCnt = connectCnt;
//    }
//
//    public boolean isNeedReCon() {
//        return this.isNeedReCon;
//    }
//
//    public void setNeedReCon(boolean needReCon) {
//        this.isNeedReCon = needReCon;
//    }
//
    public boolean isScan() {
        return this.scan;
    }

    public synchronized void setScan(boolean scan) {
        this.scan = scan;
    }

    //
//    public void setScanBongOnly(boolean scanBongOnly) {
//        BluetoothLeService.scanBongOnly = scanBongOnly;
//    }
//
//    public boolean refreshDeviceCache(BluetoothGatt gatt) {
//        if (null != gatt) {
//            try {
//                Method localMethod = gatt.getClass().getMethod("refresh", new Class[0]);
//                if (localMethod != null) {
//                    boolean bool = ((Boolean) localMethod.invoke(gatt, new Object[0])).booleanValue();
//                    return bool;
//                }
//            } catch (Exception var5) {
//                var5.printStackTrace();
//            }
//        }
//
//        return false;
//    }
//
//    public void setUpgradeFirmwareListener(UpgradeFirmwareListener upgradeFirmwareListener) {
//        this.upgradeFirmwareListener = upgradeFirmwareListener;
//    }
//
//    public short parseIC(byte[] datas) {
//        short nextReq = (short) (datas[0] << 8 | datas[1] & 255);
//        int dataIndex = 2;
//
//        while (dataIndex < datas.length) {
//            ICCard icCard = new ICCard();
//            long cardNo = DigitUtil.fourBytesToLong(Arrays.copyOfRange(datas, dataIndex, dataIndex + 4));
//            icCard.cardNumber = String.valueOf(cardNo);
//            dataIndex += 4;
//            int year = datas[dataIndex++] + 2000;
//            byte month = datas[dataIndex++];
//            byte day = datas[dataIndex++];
//            byte hour = datas[dataIndex++];
//            byte minute = datas[dataIndex++];
//            Calendar calendar = Calendar.getInstance();
//            TimeZone timeZone = TimeZone.getDefault();
//            if (timeZone.inDaylightTime(new Date(System.currentTimeMillis()))) {
//                this.timezoneOffSet -= (long) timeZone.getDSTSavings();
//            }
//
//            timeZone.setRawOffset((int) this.timezoneOffSet);
//            calendar.setTimeZone(timeZone);
//            calendar.set(year, month - 1, day, hour, minute);
//            icCard.startDate = calendar.getTimeInMillis();
//            year = datas[dataIndex++] + 2000;
//            month = datas[dataIndex++];
//            day = datas[dataIndex++];
//            hour = datas[dataIndex++];
//            minute = datas[dataIndex++];
//            calendar.setTimeZone(timeZone);
//            calendar.set(year, month - 1, day, hour, minute);
//            icCard.endDate = calendar.getTimeInMillis();
//            this.icCards.add(icCard);
//        }
//
//        return nextReq;
//    }
//
//    public short parseFR(byte[] datas) {
//        short nextReq = (short) (datas[0] << 8 | datas[1] & 255);
//        int dataIndex = 2;
//
//        while (dataIndex < datas.length) {
//            FR fr = new FR();
//            long cardNo = DigitUtil.sixBytesToLong(Arrays.copyOfRange(datas, dataIndex, dataIndex + 6));
//            fr.fingerprintNumber = String.valueOf(cardNo);
//            dataIndex += 6;
//            int year = datas[dataIndex++] + 2000;
//            byte month = datas[dataIndex++];
//            byte day = datas[dataIndex++];
//            byte hour = datas[dataIndex++];
//            byte minute = datas[dataIndex++];
//            Calendar calendar = Calendar.getInstance();
//            TimeZone timeZone = TimeZone.getDefault();
//            if (timeZone.inDaylightTime(new Date(System.currentTimeMillis()))) {
//                this.timezoneOffSet -= (long) timeZone.getDSTSavings();
//            }
//
//            timeZone.setRawOffset((int) this.timezoneOffSet);
//            calendar.setTimeZone(timeZone);
//            calendar.set(year, month - 1, day, hour, minute);
//            fr.startDate = calendar.getTimeInMillis();
//            year = datas[dataIndex++] + 2000;
//            month = datas[dataIndex++];
//            day = datas[dataIndex++];
//            hour = datas[dataIndex++];
//            minute = datas[dataIndex++];
//            calendar.setTimeZone(timeZone);
//            calendar.set(year, month - 1, day, hour, minute);
//            fr.endDate = calendar.getTimeInMillis();
//            this.frs.add(fr);
//        }
//
//        return nextReq;
//    }
//
//    private short parsePasscode(byte[] datas) {
//        short nextReq = (short) (datas[0] << 8 | datas[1] & 255);
//
//        Passcode passcode;
//        for (int dataIndex = 2; dataIndex < datas.length; this.passcodes.add(passcode)) {
//            byte recordLen = datas[dataIndex++];
//            passcode = new Passcode();
//            passcode.keyboardPwdType = datas[dataIndex++];
//            byte passcodeLen = datas[dataIndex++];
//            passcode.newKeyboardPwd = new String(Arrays.copyOfRange(datas, dataIndex, dataIndex + passcodeLen));
//            dataIndex += passcodeLen;
//            passcodeLen = datas[dataIndex++];
//            passcode.keyboardPwd = new String(Arrays.copyOfRange(datas, dataIndex, dataIndex + passcodeLen));
//            dataIndex += passcodeLen;
//            int year = datas[dataIndex++] + 2000;
//            byte month = datas[dataIndex++];
//            byte day = datas[dataIndex++];
//            byte hour = datas[dataIndex++];
//            byte minute = datas[dataIndex++];
//            Calendar calendar = Calendar.getInstance();
//            TimeZone timeZone = TimeZone.getDefault();
//            if (timeZone.inDaylightTime(new Date(System.currentTimeMillis()))) {
//                this.timezoneOffSet -= (long) timeZone.getDSTSavings();
//            }
//
//            timeZone.setRawOffset((int) this.timezoneOffSet);
//            calendar.setTimeZone(timeZone);
//            calendar.set(year, month - 1, day, hour, minute, 0);
//            passcode.startDate = calendar.getTimeInMillis();
//            switch (passcode.keyboardPwdType) {
//                case 1:
//                default:
//                    break;
//                case 2:
//                case 3:
//                    year = datas[dataIndex++] + 2000;
//                    month = datas[dataIndex++];
//                    day = datas[dataIndex++];
//                    hour = datas[dataIndex++];
//                    minute = datas[dataIndex++];
//                    calendar.setTimeZone(timeZone);
//                    calendar.set(year, month - 1, day, hour, minute);
//                    passcode.endDate = calendar.getTimeInMillis();
//                    break;
//                case 4:
//                    passcode.cycleType = (short) (datas[dataIndex++] << 8 | datas[dataIndex++] & 255);
//            }
//
//            if (passcode.keyboardPwdType == 1) {
//                passcode.keyboardPwdType = 2;
//            } else if (passcode.keyboardPwdType == 2) {
//                passcode.keyboardPwdType = 1;
//            }
//        }
//
//        return nextReq;
//    }
//
    class ScanCallback implements IScanCallback {
        ScanCallback() {
        }

        public void onScan(final ExtendedBluetoothDevice extendedBluetoothDevice) {
            ThreadPool.getThreadPool().execute(new Runnable() {
                public void run() {
                    Timber.e("---------onScan>" + BluetoothLeService.this.mScanning + "--->" + BluetoothLeService.scanBongOnly +
                            "--->" + extendedBluetoothDevice.isWristband());
                    if (BluetoothLeService.this.mScanning) {
                        if (BluetoothLeService.scanBongOnly) {
                            if (extendedBluetoothDevice.isWristband()) {
                                LockAPI.getLockCallback().onFoundDevice(extendedBluetoothDevice);
                            }
                        } else {
                            LockAPI.getLockCallback().onFoundDevice(extendedBluetoothDevice);
                        }
                    } else {
                        LogUtil.w(Thread.currentThread().toString() + " mScanning:" + BluetoothLeService.this.mScanning, true);
                    }

                }
            });
        }
    }

    public boolean isConnected(String address) {
        return address != null && (address.equals(this.mBluetoothDeviceAddress) && mConnectionState == 2);
    }

    public void sendCommand(TransferData transferData) {
        this.transferData = transferData;
        this.mUid = transferData.getmUid();
        this.adminPs = transferData.getAdminPs();
        this.unlockKey = transferData.getUnlockKey();
        this.startDate = transferData.getStartDate();
        this.endDate = transferData.getEndDate();
        aesKeyArray = TransferData.getAesKeyArray();
        this.unlockDate = transferData.getUnlockDate();
        this.lockFlagPos = transferData.getLockFlagPos();
        this.originalPwd = transferData.getOriginalPwd();
        this.newPwd = transferData.getNewPwd();
        this.No = transferData.getNo();
        this.pwds = transferData.getPwds();
        this.wristbandKey = transferData.getWristbandKey();
        this.calibationTime = transferData.getCalibationTime();
        this.timezoneOffSet = transferData.getTimezoneOffSet();
        this.sendCommand(transferData.getTransferData(), transferData.getAPICommand());
    }

    public void sendCommand(byte[] commandSrc, int apiCommand, byte[] aesKeyArray) {
        BluetoothLeService.aesKeyArray = aesKeyArray;
        this.sendCommand(commandSrc, apiCommand);
    }

    public void sendCommand(byte[] commandSrc, String adminPs, String unlockKey, byte[] aesKey, int apiCommand) {
        this.adminPs = adminPs;
        this.unlockKey = unlockKey;
        aesKeyArray = aesKey;
        this.currentAPICommand = apiCommand;
        this.sendCommand(commandSrc, apiCommand);
    }

    public void sendCommand(byte[] commandSrc, int apiCommand) {
        this.currentAPICommand = apiCommand;
        if (this.currentAPICommand == 26) {
//            this.logOperates = new ArrayList();
            this.moveDateArray = new JSONArray();
        }

        this.sendCommand(commandSrc);
    }

    public void sendCommand(byte[] commandSrc, String adminPs, String unlockKey, int lockFlagPos, byte[] aesKey, byte validPwdNum, byte keyboardPwdType, String originalPwd, String string, long startDate, long endDate, int apiCommand) {
        this.originalPwd = originalPwd;
        this.startDate = startDate;
        this.endDate = endDate;
        this.keyboardPwdType = keyboardPwdType;
        this.sendCommand(commandSrc, adminPs, unlockKey, lockFlagPos, aesKey, validPwdNum, string, apiCommand);
    }

    public void sendCommand(byte[] commandSrc, String adminPs, String unlockKey, int lockFlagPos, byte[] aesKey, byte validPwdNum, String string, int apiCommand) {
        this.adminPs = adminPs;
        this.unlockKey = unlockKey;
        this.validPwdNum = validPwdNum;
        this.lockFlagPos = lockFlagPos;
        aesKeyArray = aesKey;
        this.currentAPICommand = apiCommand;
        switch (apiCommand) {
            case 5:
                this.keyboardPwd = string;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 13:
            case 14:
            case 15:
            case 16:
            case 18:
            case 19:
            default:
                break;
            case 12:
                this.deletePwd = string;
                break;
            case 17:
                this.lockname = string;
                break;
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
                this.newPwd = string;
        }

        this.sendCommand(commandSrc, apiCommand);
    }

    public void sendCommand(byte[] commandSrc) {
        int length = commandSrc.length;
        byte[] command = new byte[commandSrc.length + 2];
        System.arraycopy(commandSrc, 0, command, 0, length);
        command[length] = 13;
        command[length + 1] = 10;
        Timber.e("----send datas:" + DigitUtil.byteArrayToHexString(commandSrc), true);
        int len = length + 2;
        this.dataQueue = new LinkedList();

        for (int startPos = 0; len > 0; startPos += 20) {
            int e = Math.min(len, 20);
            byte[] data = new byte[e];
            System.arraycopy(command, startPos, data, 0, e);
            this.dataQueue.add(data);
            len -= 20;
        }

        if (this.mNotifyCharacteristic != null && this.mBluetoothGatt != null) {
            try {
                this.mNotifyCharacteristic.setValue((byte[]) this.dataQueue.poll());
                this.mBluetoothGatt.writeCharacteristic(this.mNotifyCharacteristic);
            } catch (Exception var8) {
                mConnectionState = 0;
                if (sLockCallback != null) {
                    sLockCallback.onDeviceDisconnected(this.mExtendedBluetoothDevice);
                }
            }
        } else {
            Timber.e("----mNotifyCharacteristic:" + this.mNotifyCharacteristic);
            Timber.e("----mBluetoothGatt:" + this.mBluetoothGatt);
            Timber.e("----mNotifyCharacteristic or mBluetoothGatt is null");
            mConnectionState = 0;
            LockAPI.getLockCallback().onDeviceDisconnected(this.mExtendedBluetoothDevice);
        }

    }

    public void setNeedReCon(boolean needReCon) {
        this.isNeedReCon = needReCon;
    }

    public void setConnectCnt(int connectCnt) {
        this.connectCnt = connectCnt;
    }

    public boolean connect(String address) {
        boolean device;
        try {
            this.conLock.lock();
            this.stopScan();
            ++this.connectCnt;
            this.connectTime = System.currentTimeMillis();
            if (this.mBluetoothAdapter != null && address != null) {
                Timber.e("---------mBluetoothGatt ……" + mBluetoothGatt);
                if (this.mBluetoothGatt != null) {
                    Timber.e("-----mBluetoothGatt not null", true);
                    this.disconnect();
                    this.close();
                }

                BluetoothDevice device1 = this.mBluetoothAdapter.getRemoteDevice(address);
                Timber.e("--------------connect ……");
                Timber.e("--------------移除disConRunable");
                this.mHandler.removeCallbacks(this.disConRunable);
                this.mHandler.postDelayed(this.disConRunable, 10000L);
                this.mBluetoothGatt = device1.connectGatt(this, false, this.mGattCallback);
                Timber.e("----------Trying to create a new connection.");
                LogUtil.i("connected mBluetoothGatt:" + this.mBluetoothGatt, true);
                this.mBluetoothDeviceAddress = address;
                this.mBluetoothDevice = device1;
                Timber.e("---------device.getBondState==" + device1.getBondState());
                this.mExtendedBluetoothDevice = new ExtendedBluetoothDevice(device1);
                Timber.e("--------device:" + device1);
                Timber.e("--------mExtendedBluetoothDevice:" + this.mExtendedBluetoothDevice);
                Timber.e("---------mExtendedBluetoothDevice.device:" + this.mExtendedBluetoothDevice.getDevice());
                mConnectionState = 1;
                return true;
            }

            Timber.e("-----------BluetoothAdapter not initialized or unspecified address.");
            Timber.e("--------mBluetoothAdapter:" + this.mBluetoothAdapter);
            Timber.e("----------------address:" + address);
            device = false;
        } finally {
            this.conLock.unlock();
            Timber.e("-------------unlock:");
        }

        return device;
    }

    public void disconnect() {
        mConnectionState = 0;
        Timber.e("------dis ble connect");
        if (this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
            this.mBluetoothGatt.disconnect();
        } else {
            Timber.e("------BluetoothAdapter not initialized");
        }
    }

    public synchronized void close() {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
    }

    Runnable disConRunable = new Runnable() {
        public void run() {
            if (BluetoothLeService.mConnectionState == 2) {
                LogUtil.d("disconnecting……", true);
                BluetoothLeService.this.disconnect();
            } else if (BluetoothLeService.mConnectionState == 1) {
                LogUtil.d("disconnecting……", true);
                BluetoothLeService.this.disconnect();
                BluetoothLeService.this.close();
                BluetoothLeService.this.startScan();
                if (BluetoothLeService.this.upgradeFirmwareListener != null) {
                    BluetoothLeService.this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 0, (Object) null, 3);
                } else if (BluetoothLeService.sLockCallback != null) {
                    BluetoothLeService.this.mExtendedBluetoothDevice.disconnectStatus = 1;
                    BluetoothLeService.sLockCallback.onDeviceDisconnected(BluetoothLeService.this.mExtendedBluetoothDevice);
                } else {
                    LogUtil.w("mTTLockCallback is null", true);
                }
            }

        }
    };
    //    @Override
//    public String toString() {
//        return "BluetoothLeService{" +
//                "isWaitCommand=" + isWaitCommand +
//                ", connectTime=" + connectTime +
//                ", isNeedReCon=" + isNeedReCon +
//                ", connectCnt=" + connectCnt +
//                ", mBluetoothManager=" + mBluetoothManager +
//                ", mBluetoothAdapter=" + mBluetoothAdapter +
//                ", mBluetoothDeviceAddress='" + mBluetoothDeviceAddress + '\'' +
//                ", mBluetoothDevice=" + mBluetoothDevice +
//                ", mExtendedBluetoothDevice=" + mExtendedBluetoothDevice +
//                ", mBluetoothGatt=" + mBluetoothGatt +
//                ", mNotifyCharacteristic=" + mNotifyCharacteristic +
//                ", modelNumberCharacteristic=" + modelNumberCharacteristic +
//                ", hardwareRevisionCharacteristic=" + hardwareRevisionCharacteristic +
//                ", firmwareRevisionCharacteristic=" + firmwareRevisionCharacteristic +
//                ", mContext=" + mContext +
//                ", mHandler=" + mHandler +
//                ", mScanner=" + mScanner +
//                ", mScanning=" + mScanning +
//                ", scanCallback=" + scanCallback +
//                ", dataQueue=" + dataQueue +
//                ", mReceivedDataBuffer=" + Arrays.toString(mReceivedDataBuffer) +
//                ", mReceivedBufferCount=" + mReceivedBufferCount +
//                ", leftRecDataCount=" + leftRecDataCount +
//                ", maxBufferCount=" + maxBufferCount +
//                ", currentAPICommand=" + currentAPICommand +
//                ", adminPs='" + adminPs + '\'' +
//                ", unlockKey='" + unlockKey + '\'' +
//                ", calibationTime=" + calibationTime +
//                ", timezoneOffSet=" + timezoneOffSet +
//                ", mUid=" + mUid +
//                ", pwdInfo='" + pwdInfo + '\'' +
//                ", timestamp=" + timestamp +
//                ", validPwdNum=" + validPwdNum +
//                ", pwdData=" + Arrays.toString(pwdData) +
//                ", dataPos=" + dataPos +
//                ", packetLen=" + packetLen +
//                ", keyboardPwd='" + keyboardPwd + '\'' +
//                ", deletePwd='" + deletePwd + '\'' +
//                ", lockFlagPos=" + lockFlagPos +
//                ", lockname='" + lockname + '\'' +
//                ", originalPwd='" + originalPwd + '\'' +
//                ", newPwd='" + newPwd + '\'' +
//                ", startDate=" + startDate +
//                ", endDate=" + endDate +
//                ", keyboardPwdType=" + keyboardPwdType +
//                ", pwdList=" + pwdList +
//                ", logOperates=" + logOperates +
//                ", moveDateArray=" + moveDateArray +
//                ", unlockDate=" + unlockDate +
//                ", No=" + No +
//                ", error=" + error +
//                ", tmpError=" + tmpError +
//                ", timer=" + timer +
//                ", scan=" + scan +
//                ", feature=" + feature +
//                ", wristbandKey='" + wristbandKey + '\'' +
//                ", lockVersionString='" + lockVersionString + '\'' +
//                ", modelNumber='" + modelNumber + '\'' +
//                ", hardwareRevision='" + hardwareRevision + '\'' +
//                ", firmwareRevision='" + firmwareRevision + '\'' +
//                ", manufactureDate='" + manufactureDate + '\'' +
//                ", lockClock='" + lockClock + '\'' +
//                ", pwds=" + pwds +
//                ", recoveryDatas=" + recoveryDatas +
//                ", tempOptype=" + tempOptype +
//                ", transferData=" + transferData +
//                ", isSetLockName=" + isSetLockName +
//                ", upgradeFirmwareListener=" + upgradeFirmwareListener +
//                ", deviceInfo=" + deviceInfo +
//                ", icCards=" + icCards +
//                ", frs=" + frs +
//                ", passcodes=" + passcodes +
//                ", conLock=" + conLock +
//                ", bluttoothState=" + bluttoothState +
//                ", disTimerTask=" + disTimerTask +
//                ", disConRunable=" + disConRunable +
//                ", mGattCallback=" + mGattCallback +
//                '}';
//    }
    TimerTask disTimerTask;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @RequiresPermission("android.permission.BLUETOOTH")
        @TargetApi(21)
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (BluetoothLeService.this.mBluetoothGatt != gatt) {
                Log.e(TAG, "-------onConnectionStateChange--gatt=" + gatt + " status=" + status + " newState=" + newState);
                gatt.close();
            } else {
                LogUtil.d(Thread.currentThread().toString().toString(), true);
                Log.e(TAG, "--------onConnectionStateChange-gatt=" + gatt + " status=" + status + " newState=" + newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    BluetoothLeService.mConnectionState = 2;
                    Log.e(TAG, "---------Connected to GATT server.");
                    LogUtil.e("移除disConRunable", true);
                    BluetoothLeService.this.mHandler.removeCallbacks(BluetoothLeService.this.disConRunable);

                    try {
                        Thread.sleep(600L);
                    } catch (InterruptedException var9) {
                        var9.printStackTrace();
                    }

                    BluetoothLeService.this.connectTime = System.currentTimeMillis();
                    if (BluetoothLeService.this.mBluetoothGatt != null) {
                        Log.e(TAG, "---------Attempting to start service discovery:" + BluetoothLeService.this.mBluetoothGatt.discoverServices());
                    } else {
                        BluetoothLeService.mConnectionState = 0;
                        LockAPI.getLockCallback().onDeviceDisconnected(BluetoothLeService.this.mExtendedBluetoothDevice);
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    BluetoothLeService.this.mHandler.removeCallbacks(BluetoothLeService.this.disConRunable);
                    if (BluetoothLeService.this.isNeedReCon && BluetoothLeService.this.connectCnt < 3 && System.currentTimeMillis() - BluetoothLeService.this.connectTime < 2000L) {
                        Log.e(TAG, "---------connect again:" + BluetoothLeService.this.connectCnt);

                        try {
                            BluetoothLeService.this.conLock.lock();
                            BluetoothLeService.this.connect(BluetoothLeService.this.mExtendedBluetoothDevice);
                        } finally {
                            BluetoothLeService.this.conLock.unlock();
                        }
                    } else {
                        BluetoothLeService.mConnectionState = 0;
                        Log.e(TAG, "------- Disconnected from GATT server.");
                        BluetoothLeService.this.close();
                        BluetoothLeService.this.startScan();
                        if (BluetoothLeService.this.upgradeFirmwareListener != null) {
                            BluetoothLeService.this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 0, "", 3);
                        } else {
                            LockAPI.getLockCallback().onDeviceDisconnected(BluetoothLeService.this.mExtendedBluetoothDevice);
                        }
                    }
                }

            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (BluetoothLeService.this.mBluetoothGatt == gatt) {
                Log.e(TAG, "--------onServicesDiscovered-gatt=" + gatt + " status=" + status);
                LogUtil.d(Thread.currentThread().toString().toString(), true);
                if (status == 0) {
                    if (BluetoothLeService.this.mBluetoothGatt == null) {
                        LogUtil.w("mBluetoothGatt null", true);
                        return;
                    }

                    if (BluetoothLeService.scanBongOnly) {
                        BluetoothLeService.UUID_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca1e";
                        BluetoothLeService.UUID_READ = "6e400003-b5a3-f393-e0a9-e50e24dcca1e";
                        BluetoothLeService.UUID_WRITE = "6e400002-b5a3-f393-e0a9-e50e24dcca1e";
                    } else {
                        BluetoothLeService.UUID_SERVICE = "00001910-0000-1000-8000-00805f9b34fb";
                        BluetoothLeService.UUID_READ = "0000fff4-0000-1000-8000-00805f9b34fb";
                        BluetoothLeService.UUID_WRITE = "0000fff2-0000-1000-8000-00805f9b34fb";
                    }

                    BluetoothGattService service = BluetoothLeService.this.mBluetoothGatt.getService(UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb"));
                    List gattCharacteristics;
                    Iterator var5;
                    BluetoothGattCharacteristic gattCharacteristic;
                    if (service != null) {
                        gattCharacteristics = service.getCharacteristics();
                        if (gattCharacteristics != null && gattCharacteristics.size() > 0) {
                            var5 = gattCharacteristics.iterator();

                            while (var5.hasNext()) {
                                gattCharacteristic = (BluetoothGattCharacteristic) var5.next();
                                LogUtil.d(gattCharacteristic.getUuid().toString(), true);
                                LogUtil.d("read characteristic:" + Thread.currentThread(), true);
                                if (gattCharacteristic.getUuid().toString().equals("00002a24-0000-1000-8000-00805f9b34fb")) {
                                    BluetoothLeService.this.modelNumberCharacteristic = gattCharacteristic;
                                } else if (gattCharacteristic.getUuid().toString().equals("00002a26-0000-1000-8000-00805f9b34fb")) {
                                    BluetoothLeService.this.firmwareRevisionCharacteristic = gattCharacteristic;
                                } else if (gattCharacteristic.getUuid().toString().equals("00002a27-0000-1000-8000-00805f9b34fb")) {
                                    BluetoothLeService.this.hardwareRevisionCharacteristic = gattCharacteristic;
                                }
                            }
                        }
                    }

                    service = BluetoothLeService.this.mBluetoothGatt.getService(UUID.fromString(BluetoothLeService.UUID_SERVICE));
                    if (service != null) {
                        gattCharacteristics = service.getCharacteristics();
                        if (gattCharacteristics != null && gattCharacteristics.size() > 0) {
                            var5 = gattCharacteristics.iterator();

                            while (var5.hasNext()) {
                                gattCharacteristic = (BluetoothGattCharacteristic) var5.next();
                                LogUtil.d(gattCharacteristic.getUuid().toString(), true);
                                if (gattCharacteristic.getUuid().toString().equals(BluetoothLeService.UUID_WRITE)) {
                                    BluetoothLeService.this.mNotifyCharacteristic = gattCharacteristic;
                                    LogUtil.d("mNotifyCharacteristic:" + BluetoothLeService.this.mNotifyCharacteristic, true);
                                } else if (gattCharacteristic.getUuid().toString().equals(BluetoothLeService.UUID_READ)) {
                                    gatt.setCharacteristicNotification(gattCharacteristic, true);
                                    BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(BluetoothLeService.UUID_HEART_RATE_MEASUREMENT);
                                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    if (gatt.writeDescriptor(descriptor)) {
                                        LogUtil.d("writeDescriptor successed", true);
                                    } else {
                                        LogUtil.d("writeDescriptor failed", true);
                                    }
                                }
                            }
                        }
                    } else {
                        LogUtil.w("service is null", true);
                        BluetoothLeService.mConnectionState = 0;
                        BluetoothLeService.this.close();
                        BluetoothLeService.this.mExtendedBluetoothDevice.disconnectStatus = 2;
                        LockAPI.getLockCallback().onDeviceDisconnected(BluetoothLeService.this.mExtendedBluetoothDevice);
                    }
                } else {
                    LogUtil.w("onServicesDiscovered received: " + status, true);
                }

            }
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (BluetoothLeService.this.mBluetoothGatt == gatt) {
                LogUtil.d(Thread.currentThread().toString().toString(), true);
                super.onDescriptorWrite(gatt, descriptor, status);
                LogUtil.d("gatt=" + gatt + " descriptor=" + descriptor + " status=" + status, true);
                LogUtil.d(descriptor.getCharacteristic().getUuid().toString(), true);
                BluetoothLeService.this.isNeedReCon = false;
                if (BluetoothLeService.this.upgradeFirmwareListener != null) {
                    BluetoothLeService.this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 0, (Object) null, 2);
                } else {
                    LockAPI.getLockCallback().onDeviceConnected(BluetoothLeService.this.mExtendedBluetoothDevice);
                }

            }
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (BluetoothLeService.this.mBluetoothGatt != gatt) {
                LogUtil.w("gatt=" + gatt + " characteristic=" + characteristic + " status=" + status, true);
            } else {
                LogUtil.d(Thread.currentThread().toString().toString(), true);
                LogUtil.d("gatt=" + gatt + " characteristic=" + characteristic + " status=" + status, true);
                if (status == 0) {
                    if (BluetoothLeService.this.dataQueue.size() > 0) {
                        characteristic.setValue((byte[]) BluetoothLeService.this.dataQueue.poll());
                        gatt.writeCharacteristic(characteristic);
                    } else {
                        BluetoothLeService.this.mHandler.removeCallbacks(BluetoothLeService.this.disConRunable);
                        BluetoothLeService.this.disTimerTask = new TimerTask() {
                            public void run() {
                                BluetoothLeService.this.disconnect();
                            }
                        };
                        long delay = 2500L;
                        if (BluetoothLeService.this.currentAPICommand == 19) {
                            delay = 5500L;
                        }

                        LogUtil.e("------------------timer----------------" + BluetoothLeService.this.timer, true);
                        BluetoothLeService.this.timer.schedule(BluetoothLeService.this.disTimerTask, delay);
                    }
                } else {
                    LogUtil.w("onCharacteristicWrite failed", true);
                }

                super.onCharacteristicWrite(gatt, characteristic, status);
            }
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (BluetoothLeService.this.mBluetoothGatt == gatt) {
                super.onCharacteristicChanged(gatt, characteristic);

                try {
                    LogUtil.d("gatt=" + gatt + " characteristic=" + characteristic, true);
                    byte[] e = characteristic.getValue();
                    int dataLen = e.length;
                    if (BluetoothLeService.scanBongOnly) {
                        if ("success".equals(new String(e))) {
                            BluetoothLeService.this.error = Error.SUCCESS;
                        } else {
                            BluetoothLeService.this.error = Error.LOCK_OPERATE_FAILED;
                        }

                        switch (BluetoothLeService.this.tempOptype) {
                            case 1:
                                BluetoothLeService.sLockCallback.onSetWristbandKeyToDev(BluetoothLeService.this.error);
                                break;
                            case 2:
                                BluetoothLeService.sLockCallback.onSetWristbandKeyRssi(BluetoothLeService.this.error);
                        }

                        return;
                    }

                    LogUtil.d("receiver data=" + DigitUtil.byteArrayToHexString(e), true);
                    if (BluetoothLeService.this.mReceivedBufferCount + dataLen <= BluetoothLeService.this.maxBufferCount) {
                        System.arraycopy(e, 0, BluetoothLeService.this.mReceivedDataBuffer, BluetoothLeService.this.mReceivedBufferCount, dataLen);
                        BluetoothLeService.this.mReceivedBufferCount = BluetoothLeService.this.mReceivedBufferCount + dataLen;
                    }

                    LogUtil.d("mReceivedBufferCount:" + BluetoothLeService.this.mReceivedBufferCount, true);
                    LogUtil.d("dataLen:" + dataLen, true);
                    if (BluetoothLeService.this.mReceivedBufferCount == dataLen && e[0] == 127 && e[1] == 90) {
                        boolean last = false;
                        int last1;
                        if (e[2] >= 5) {
                            last1 = e[11] + 1;
                            BluetoothLeService.this.leftRecDataCount = last1 - (dataLen - 12);
                        } else {
                            last1 = e[5] + 1;
                            BluetoothLeService.this.leftRecDataCount = last1 - (dataLen - 6);
                        }
                    } else {
                        BluetoothLeService.this.leftRecDataCount = BluetoothLeService.this.leftRecDataCount - dataLen;
                    }

                    LogUtil.d("leftRecDataCount:" + BluetoothLeService.this.leftRecDataCount, true);
                    byte last2 = BluetoothLeService.this.mReceivedDataBuffer[BluetoothLeService.this.mReceivedBufferCount - 1];
                    byte lastSec = BluetoothLeService.this.mReceivedDataBuffer[BluetoothLeService.this.mReceivedBufferCount - 2];
                    if (lastSec == 13 && last2 == 10) {
                        BluetoothLeService.this.mReceivedBufferCount = BluetoothLeService.this.mReceivedBufferCount - 2;
                        LogUtil.d("mReceivedDataBuffer=" + DigitUtil.byteArrayToHexString(Arrays.copyOf(BluetoothLeService.this.mReceivedDataBuffer, BluetoothLeService.this.mReceivedBufferCount)), true);
                        BluetoothLeService.this.processCommandResponse(Arrays.copyOf(BluetoothLeService.this.mReceivedDataBuffer, BluetoothLeService.this.mReceivedBufferCount));
                        BluetoothLeService.this.mReceivedBufferCount = 0;
                        LogUtil.e("------------------timer----------------" + BluetoothLeService.this.timer, true);
                        BluetoothLeService.this.disTimerTask.cancel();
                        LogUtil.d("num:" + BluetoothLeService.this.timer.purge(), true);
                    } else if (BluetoothLeService.this.leftRecDataCount == 0) {
                        LogUtil.d("mReceivedDataBuffer=" + DigitUtil.byteArrayToHexString(Arrays.copyOf(BluetoothLeService.this.mReceivedDataBuffer, BluetoothLeService.this.mReceivedBufferCount)), true);
                        BluetoothLeService.this.processCommandResponse(Arrays.copyOf(BluetoothLeService.this.mReceivedDataBuffer, BluetoothLeService.this.mReceivedBufferCount));
                        BluetoothLeService.this.mReceivedBufferCount = 0;
                        BluetoothLeService.this.disTimerTask.cancel();
                        LogUtil.d("num:" + BluetoothLeService.this.timer.purge(), true);
                    }
                } catch (Exception var7) {
                    var7.printStackTrace();
                    BluetoothLeService.this.mReceivedBufferCount = 0;
                }

            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            LogUtil.e(Thread.currentThread() + " " + new String(characteristic.getValue()), true);
            LogUtil.e(Thread.currentThread() + " " + characteristic.getUuid(), true);
        }
    };

    public void clearTask() {
        if (this.mHandler != null) {
            this.mHandler.removeCallbacks(this.disConRunable);
        }

        if (this.disTimerTask != null) {
            this.disTimerTask.cancel();
        }

        if (this.timer != null) {
            this.timer.purge();
        }

    }

    public boolean connect(ExtendedBluetoothDevice extendedBluetoothDevice) {
        this.stopScan();
        ++this.connectCnt;
        this.connectTime = System.currentTimeMillis();
        Log.e(TAG, "--------extendedBluetoothDevice:" + extendedBluetoothDevice);
        String address = extendedBluetoothDevice.getDevice().getAddress();
        if (this.mBluetoothAdapter != null && address != null) {
            if (this.mBluetoothGatt != null) {
                Log.e(TAG, "--------mBluetoothGatt not null");
                this.disconnect();
                this.close();
            }

            BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(address);
            Timber.e("--------connect ……");
            Timber.e("---------移除disConRunable");
            this.mHandler.removeCallbacks(this.disConRunable);
            this.mHandler.postDelayed(this.disConRunable, 10000L);
            extendedBluetoothDevice.disconnectStatus = 0;
            this.mBluetoothGatt = device.connectGatt(this, false, this.mGattCallback);
            Timber.e("------------Trying to create a new connection.");
            Timber.e("--------connected mBluetoothGatt:" + this.mBluetoothGatt);
            this.mBluetoothDeviceAddress = address;
            this.mBluetoothDevice = device;
            this.mExtendedBluetoothDevice = extendedBluetoothDevice;
            Timber.e("------device.getBondState==" + device.getBondState());
            Timber.e("-------mExtendedBluetoothDevice:" + this.mExtendedBluetoothDevice);
            mConnectionState = 1;
            return true;
        } else {
            Timber.e("-------BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
    }

    private void processCommandResponse(byte[] values) {
//        this.mExtendedBluetoothDevice.disconnectStatus = 4;
//        Command command = new Command(values);
//        if (!command.isChecksumValid()) {
//            this.error = Error.LOCK_CRC_CHECK_ERROR;
//            this.error.setCommand(command.getCommand());
//            this.errorCallback(this.error);
//        } else {
//            byte groupId;
//            byte orgId;
//            short var44;
//            if (this.currentAPICommand == 1) {
//                if (values[0] == 127 && values[1] == 90) {
//                    this.error = Error.SUCCESS;
//                    groupId = values[2];
//                    orgId = values[3];
//                    byte var22 = values[4];
//                    short var29 = DigitUtil.byteArrayToShort(new byte[]{values[5], values[6]});
//                    var44 = DigitUtil.byteArrayToShort(new byte[]{values[7], values[8]});
//                    TTLockAPI.getTTLockCallback().onGetLockVersion(this.mExtendedBluetoothDevice, groupId, orgId, var22, var29, var44, this.error);
//                } else {
//                    this.error = Error.LOCK_OPERATE_FAILED;
//                    TTLockAPI.getTTLockCallback().onGetLockVersion(this.mExtendedBluetoothDevice, 0, 0, 0, 0, 0, this.error);
//                }
//
//            } else {
//                groupId = command.organization[0];
//                orgId = command.sub_organization[0];
//                this.mExtendedBluetoothDevice.groupId = groupId;
//                this.mExtendedBluetoothDevice.orgId = orgId;
//                byte[] data = null;
//                LogUtil.d("currentAPICommand : " + this.currentAPICommand, true);
//                LogUtil.d("command.getCommand():" + (char) command.getCommand() + "-" + String.format("%#x", new Object[]{Byte.valueOf(command.getCommand())}), true);
//                switch (command.getLockType()) {
//                    case 2:
//                    case 3:
//                    case 6:
//                    case 7:
//                        data = command.getData();
//                        break;
//                    case 4:
//                        data = command.getData(aesKeyArray);
//                        break;
//                    case 5:
//                    case 8:
//                        this.mHandler.removeCallbacks(this.disConRunable);
//                        data = command.getData(aesKeyArray);
//                }
//
//                if (data != null && data.length != 0) {
//                    LogUtil.d("feedback comman:" + (char) data[0] + "-" + String.format("%#x", new Object[]{Byte.valueOf(data[0])}), true);
//                    LogUtil.d("values:" + DigitUtil.byteArrayToHexString(values), true);
//                    LogUtil.d("response data:" + DigitUtil.byteArrayToHexString(data), true);
//                    if (this.currentAPICommand == 17 && this.isSetLockName && data[0] != -1) {
//                        this.isSetLockName = false;
//                        LogUtil.d("data:" + DigitUtil.byteArrayToHexString(data), true);
//                        String var27 = (new String(data)).trim();
//                        LogUtil.e(var27, true);
//                        if ("OK".equals(var27)) {
//                            TTLockAPI.getTTLockCallback().onSetLockName(this.mExtendedBluetoothDevice, this.lockname, this.error);
//                        } else {
//                            this.error = Error.LOCK_OPERATE_FAILED;
//                            this.errorCallback(this.error);
//                        }
//
//                    } else {
//                        if (command.getCommand() == 25) {
//                            if (data[1] == 1) {
//                                aesKeyArray = Arrays.copyOfRange(data, 2, data.length);
//                                this.adminPs = new String(DigitUtil.generateDynamicPassword(10));
//                                this.unlockKey = new String(DigitUtil.generateDynamicPassword(10));
//                                CommandUtil.V_addAdmin(command.getLockType(), this.adminPs, this.unlockKey, aesKeyArray);
//                            } else {
//                                this.error = Error.getInstance(data[2]);
//                                this.error.setCommand(command.getCommand());
//                                this.errorCallback(this.error);
//                            }
//                        } else if (command.getCommand() == 84) {
//                            if (data[1] == 1) {
//                                LogUtil.d("success", true);
//                                this.error = Error.SUCCESS;
//                                this.error.setLockname(this.mExtendedBluetoothDevice.getName());
//                                this.error.setLockmac(this.mExtendedBluetoothDevice.getAddress());
//                                this.error.setCommand(data[0]);
//                                this.error.setDate(System.currentTimeMillis());
//                                int delay;
//                                int battery;
//                                Calendar op;
//                                TimeZone code;
//                                long secretKey;
//                                byte var25;
//                                byte[] var26;
//                                String var31;
//                                int var35;
//                                RecoveryData var38;
//                                long var39;
//                                RecoveryData var41;
//                                short var50;
//                                label584:
//                                switch (data[0]) {
//                                    case -112:
//                                        LogUtil.d("COMM_READ_DEVICE_INFO:" + DigitUtil.byteArrayToHexString(data), true);
//                                        LogUtil.d("COMM_READ_DEVICE_INFO:" + new String(Arrays.copyOfRange(data, 2, data.length - 1)), true);
//                                        switch (this.tempOptype) {
//                                            case 1:
//                                                LogUtil.d("modelNumber before:" + this.modelNumber, true);
//                                                this.modelNumber = new String(Arrays.copyOfRange(data, 2, data.length - 1));
//                                                LogUtil.d("modelNumber after:" + this.modelNumber, true);
//                                                this.tempOptype = 2;
//                                                CommandUtil.readDeviceInfo(command.getLockType(), (byte) 2, aesKeyArray);
//                                                break label584;
//                                            case 2:
//                                                this.hardwareRevision = new String(Arrays.copyOfRange(data, (byte) 2, data.length - 1));
//                                                this.tempOptype = 3;
//                                                CommandUtil.readDeviceInfo(command.getLockType(), (byte) 3, aesKeyArray);
//                                                break label584;
//                                            case 3:
//                                                LogUtil.d("firmwareRevision before:" + this.firmwareRevision, true);
//                                                this.firmwareRevision = new String(Arrays.copyOfRange(data, 2, data.length - 1));
//                                                LogUtil.d("firmwareRevision after:" + this.firmwareRevision, true);
//                                                this.tempOptype = 4;
//                                                if (this.upgradeFirmwareListener != null) {
//                                                    if (this.deviceInfo != null) {
//                                                        this.deviceInfo.modelNum = this.modelNumber;
//                                                        this.deviceInfo.hardwareRevision = this.hardwareRevision;
//                                                        this.deviceInfo.firmwareRevision = this.firmwareRevision;
//                                                    }
//
//                                                    this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) -112, this.deviceInfo, 1);
//                                                } else if (this.currentAPICommand == 2) {
//                                                    if (this.transferData != null) {
//                                                        this.mExtendedBluetoothDevice.setRemoteUnlockSwitch(this.transferData.getOpValue());
//                                                    }
//
//                                                    TTLockAPI.getTTLockCallback().onAddAdministrator(this.mExtendedBluetoothDevice, command.getLockVersionString(), DigitUtil.encodeLockData(this.adminPs), DigitUtil.encodeLockData(this.unlockKey), this.keyboardPwd, this.deletePwd, this.pwdInfo, this.timestamp, DigitUtil.encodeAesKey(aesKeyArray), this.feature, this.modelNumber, this.hardwareRevision, this.firmwareRevision, this.error);
//                                                } else {
//                                                    CommandUtil.readDeviceInfo(command.getLockType(), (byte) 4, aesKeyArray);
//                                                }
//                                                break label584;
//                                            case 4:
//                                                this.manufactureDate = new String(Arrays.copyOfRange(data, 2, data.length - 1));
//                                                this.tempOptype = 6;
//                                                CommandUtil.readDeviceInfo(command.getLockType(), (byte) 6, aesKeyArray);
//                                                break label584;
//                                            case 5:
//                                            default:
//                                                this.modelNumber = new String(Arrays.copyOfRange(data, (byte) 2, data.length - 1));
//                                                this.tempOptype = 2;
//                                                CommandUtil.readDeviceInfo(command.getLockType(), (byte) 2, aesKeyArray);
//                                                break label584;
//                                            case 6:
//                                                this.lockClock = "";
//
//                                                for (var35 = 2; var35 < data.length - 1; ++var35) {
//                                                    this.lockClock = this.lockClock + String.format("%02d", new Object[]{Byte.valueOf(data[var35])});
//                                                }
//
//                                                this.tempOptype = -1;
//                                                TTLockAPI.getTTLockCallback().onReadDeviceInfo(this.mExtendedBluetoothDevice, this.modelNumber, this.hardwareRevision, this.firmwareRevision, this.manufactureDate, this.lockClock);
//                                                break label584;
//                                        }
//                                    case -1:
//                                        if (this.currentAPICommand == 17) {
//                                            this.isSetLockName = true;
//                                        }
//                                        break;
//                                    case 1:
//                                        this.feature = (int) DigitUtil.fourBytesToLong(Arrays.copyOfRange(data, 3, 7));
//                                        if (this.upgradeFirmwareListener != null) {
//                                            this.deviceInfo = new DeviceInfo();
//                                            this.deviceInfo.specialValue = this.feature;
//                                            this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 1, this.deviceInfo, 1);
//                                        } else {
//                                            switch (this.currentAPICommand) {
//                                                case 2:
//                                                    if (DigitUtil.isSupportRemoteUnlockSwitch(this.feature)) {
//                                                        CommandUtil_V3.controlRemoteUnlock(command, (byte) 1, (byte) 0, aesKeyArray);
//                                                    } else {
//                                                        CommandUtil.operateFinished(command.getLockType());
//                                                    }
//                                                    break label584;
//                                                case 23:
//                                                    this.error = this.tmpError;
//                                                    if (!DigitUtil.isSupportModifyPasscode(this.feature)) {
//                                                        this.error = Error.LOCK_NOT_SUPPORT_CHANGE_PASSCODE;
//                                                        this.error.setCommand((byte) 3);
//                                                    }
//
//                                                    TTLockAPI.getTTLockCallback().onModifyKeyboardPassword(this.mExtendedBluetoothDevice, this.keyboardPwdType, this.originalPwd, this.newPwd, this.error);
//                                                    break label584;
//                                                case 48:
//                                                    TTLockAPI.getTTLockCallback().onOperateRemoteUnlockSwitch(this.mExtendedBluetoothDevice, data[2], this.transferData.getOp(), this.transferData.getOpValue(), this.feature, this.error);
//                                                    break label584;
//                                                default:
//                                                    TTLockAPI.getTTLockCallback().onSearchDeviceFeature(this.mExtendedBluetoothDevice, data[2], this.feature, this.error);
//                                            }
//                                        }
//                                        break;
//                                    case 2:
//                                        if (this.upgradeFirmwareListener != null) {
//                                            this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 2, "", 1);
//                                        } else {
//                                            TTLockAPI.getTTLockCallback().onEnterDFUMode(this.mExtendedBluetoothDevice, this.error);
//                                        }
//                                        break;
//                                    case 3:
//                                        switch (data[3]) {
//                                            case 1:
//                                                TTLockAPI.getTTLockCallback().onDeleteAllKeyboardPassword(this.mExtendedBluetoothDevice, this.error);
//                                                break label584;
//                                            case 2:
//                                                switch (this.currentAPICommand) {
//                                                    case 20:
//                                                        this.keyboardPwdType = 3;
//                                                        break;
//                                                    case 21:
//                                                        this.keyboardPwdType = 1;
//                                                        break;
//                                                    case 22:
//                                                        this.keyboardPwdType = 2;
//                                                }
//
//                                                TTLockAPI.getTTLockCallback().onAddKeyboardPassword(this.mExtendedBluetoothDevice, this.keyboardPwdType, this.originalPwd, this.startDate, this.endDate, this.error);
//                                                break label584;
//                                            case 3:
//                                                if (this.currentAPICommand == 41) {
//                                                    ++this.dataPos;
//                                                    if (this.dataPos < this.pwds.size()) {
//                                                        CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 3, this.keyboardPwdType, (String) this.pwds.get(this.dataPos), this.newPwd, 0L, 0L, aesKeyArray);
//                                                    }
//                                                } else {
//                                                    TTLockAPI.getTTLockCallback().onDeleteOneKeyboardPassword(this.mExtendedBluetoothDevice, this.keyboardPwdType, this.newPwd, this.error);
//                                                }
//                                            case 4:
//                                            default:
//                                                break label584;
//                                            case 5:
//                                                TTLockAPI.getTTLockCallback().onModifyKeyboardPassword(this.mExtendedBluetoothDevice, this.keyboardPwdType, this.originalPwd, this.newPwd, this.error);
//                                                break label584;
//                                            case 6:
//                                                ++this.dataPos;
//                                                LogUtil.e("dataPos:" + this.dataPos, true);
//                                                if (this.dataPos < this.recoveryDatas.size()) {
//                                                    var41 = (RecoveryData) this.recoveryDatas.get(this.dataPos);
//                                                    CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 6, (byte) (var41.keyboardPwdType == 2 ? 1 : var41.keyboardPwdType), var41.cycleType, var41.keyboardPwd, var41.keyboardPwd, var41.startDate, var41.endDate, aesKeyArray, this.timezoneOffSet);
//                                                } else if (this.upgradeFirmwareListener != null) {
//                                                    this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 3, "", 1);
//                                                } else {
//                                                    mTTLockCallback.onRecoveryData(this.mExtendedBluetoothDevice, this.transferData.getOp(), this.error);
//                                                }
//                                                break label584;
//                                        }
//                                    case 4:
//                                        var44 = CommandUtil_V3.parseKeyboardPwd(Arrays.copyOfRange(data, 2, data.length));
//                                        if (var44 != 0 && var44 != -1) {
//                                            CommandUtil.getValidKeyboardPassword(command.getLockType(), var44, aesKeyArray);
//                                        }
//                                        break;
//                                    case 5:
//                                        var25 = data[2];
//                                        switch (data[3]) {
//                                            case 1:
//                                                var50 = this.parseIC(Arrays.copyOfRange(data, 4, data.length));
//                                                LogUtil.d("search:" + var50, true);
//                                                if (var50 == -1) {
//                                                    mTTLockCallback.onSearchICCard(this.mExtendedBluetoothDevice, data[2], GsonUtil.toJson(this.icCards), this.error);
//                                                    LogUtil.d("完成", true);
//                                                } else {
//                                                    CommandUtil.searchICCardNo(command.getLockType(), var50, aesKeyArray);
//                                                }
//                                                break label584;
//                                            case 2:
//                                                if (data[4] == 1) {
//                                                    if (this.currentAPICommand == 44) {
//                                                        ++this.dataPos;
//                                                        LogUtil.e("dataPos:" + this.dataPos, true);
//                                                        if (this.dataPos < this.recoveryDatas.size()) {
//                                                            var38 = (RecoveryData) this.recoveryDatas.get(this.dataPos);
//                                                            CommandUtil.recoveryICCardPeriod(command.getLockType(), Long.valueOf(var38.cardNumber).longValue(), var38.startDate, var38.endDate, aesKeyArray, this.timezoneOffSet);
//                                                        } else if (this.upgradeFirmwareListener != null) {
//                                                            this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 5, "", 1);
//                                                        } else {
//                                                            mTTLockCallback.onRecoveryData(this.mExtendedBluetoothDevice, this.transferData.getOp(), this.error);
//                                                        }
//                                                    } else {
//                                                        var39 = DigitUtil.fourBytesToLong(Arrays.copyOfRange(data, 5, data.length));
//                                                        this.isWaitCommand = false;
//                                                        TTLockAPI.getTTLockCallback().onAddICCard(this.mExtendedBluetoothDevice, 2, var25, var39, this.error);
//                                                    }
//                                                } else if (data[4] == 2) {
//                                                    LogUtil.d("进入添加IC卡模式", true);
//                                                    if (this.currentAPICommand == 44) {
//                                                        ++this.dataPos;
//                                                        LogUtil.e("dataPos:" + this.dataPos, true);
//                                                        if (this.dataPos < this.recoveryDatas.size()) {
//                                                            var38 = (RecoveryData) this.recoveryDatas.get(this.dataPos);
//                                                            CommandUtil.recoveryICCardPeriod(command.getLockType(), Long.valueOf(var38.cardNumber).longValue(), var38.startDate, var38.endDate, aesKeyArray, this.timezoneOffSet);
//                                                        } else {
//                                                            mTTLockCallback.onRecoveryData(this.mExtendedBluetoothDevice, this.transferData.getOp(), this.error);
//                                                        }
//                                                    } else {
//                                                        this.isWaitCommand = true;
//                                                        TTLockAPI.getTTLockCallback().onAddICCard(this.mExtendedBluetoothDevice, 1, var25, 0L, this.error);
//                                                    }
//                                                }
//                                                break label584;
//                                            case 3:
//                                                TTLockAPI.getTTLockCallback().onDeleteICCard(this.mExtendedBluetoothDevice, var25, this.No, this.error);
//                                                break label584;
//                                            case 4:
//                                                TTLockAPI.getTTLockCallback().onClearICCard(this.mExtendedBluetoothDevice, var25, this.error);
//                                                break label584;
//                                            case 5:
//                                                TTLockAPI.getTTLockCallback().onModifyICCardPeriod(this.mExtendedBluetoothDevice, var25, this.No, this.startDate, this.endDate, this.error);
//                                            default:
//                                                break label584;
//                                        }
//                                    case 6:
//                                        var25 = data[2];
//                                        switch (data[3]) {
//                                            case 2:
//                                                if (data[4] == 1) {
//                                                    if (this.currentAPICommand == 44) {
//                                                        ++this.dataPos;
//                                                        if (this.dataPos < this.recoveryDatas.size()) {
//                                                            var38 = (RecoveryData) this.recoveryDatas.get(this.dataPos);
//                                                            CommandUtil.recoveryFRPeriod(command.getLockType(), Long.valueOf(var38.fingerprintNumber).longValue(), var38.startDate, var38.endDate, aesKeyArray, this.timezoneOffSet);
//                                                        } else if (this.upgradeFirmwareListener != null) {
//                                                            this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 6, "", 1);
//                                                        } else {
//                                                            mTTLockCallback.onRecoveryData(this.mExtendedBluetoothDevice, this.transferData.getOp(), this.error);
//                                                        }
//                                                    } else {
//                                                        var39 = DigitUtil.sixBytesToLong(Arrays.copyOfRange(data, 5, data.length));
//                                                        this.isWaitCommand = false;
//                                                        TTLockAPI.getTTLockCallback().onAddFingerPrint(this.mExtendedBluetoothDevice, 2, var25, var39, this.error);
//                                                        TTLockAPI.getTTLockCallback().onAddFingerPrint(this.mExtendedBluetoothDevice, 2, var25, var39, -1, this.error);
//                                                    }
//                                                } else {
//                                                    byte var42;
//                                                    if (data[4] == 2) {
//                                                        this.isWaitCommand = true;
//                                                        TTLockAPI.getTTLockCallback().onAddFingerPrint(this.mExtendedBluetoothDevice, 1, var25, 0L, this.error);
//                                                        var42 = -1;
//                                                        LogUtil.d("data.length:" + data.length, true);
//                                                        if (data.length == 6) {
//                                                            var42 = data[5];
//                                                        }
//
//                                                        TTLockAPI.getTTLockCallback().onAddFingerPrint(this.mExtendedBluetoothDevice, 1, var25, 0L, var42, this.error);
//                                                        LogUtil.d("进入添加FR模式", true);
//                                                    } else if (data[4] == 3) {
//                                                        this.isWaitCommand = true;
//                                                        LogUtil.d("第一次采集成功，进行第二次采集", true);
//                                                        TTLockAPI.getTTLockCallback().onFingerPrintCollection(this.mExtendedBluetoothDevice, var25, this.error);
//                                                        var42 = -1;
//                                                        byte var43 = -1;
//                                                        if (data.length == 7) {
//                                                            var42 = data[5];
//                                                            var43 = data[6];
//                                                        }
//
//                                                        if (var42 == 0) {
//                                                            TTLockAPI.getTTLockCallback().onAddFingerPrint(this.mExtendedBluetoothDevice, 1, var25, 0L, data[6], this.error);
//                                                            LogUtil.d("进入添加FR模式", true);
//                                                        } else {
//                                                            TTLockAPI.getTTLockCallback().onFingerPrintCollection(this.mExtendedBluetoothDevice, var25, var42, var43, this.error);
//                                                        }
//                                                    } else if (data[4] == 4) {
//                                                        if (data[5] == 0) {
//                                                            this.isWaitCommand = true;
//                                                            TTLockAPI.getTTLockCallback().onAddFingerPrint(this.mExtendedBluetoothDevice, 1, var25, 0L, this.error);
//                                                            TTLockAPI.getTTLockCallback().onAddFingerPrint(this.mExtendedBluetoothDevice, 1, var25, 0L, data[6], this.error);
//                                                            LogUtil.d("进入添加FR模式", true);
//                                                        } else {
//                                                            this.isWaitCommand = true;
//                                                            TTLockAPI.getTTLockCallback().onFingerPrintCollection(this.mExtendedBluetoothDevice, var25, this.error);
//                                                            TTLockAPI.getTTLockCallback().onFingerPrintCollection(this.mExtendedBluetoothDevice, var25, data[5], data[6], this.error);
//                                                        }
//                                                    }
//                                                }
//                                                break label584;
//                                            case 3:
//                                                TTLockAPI.getTTLockCallback().onDeleteFingerPrint(this.mExtendedBluetoothDevice, var25, this.No, this.error);
//                                                break label584;
//                                            case 4:
//                                                TTLockAPI.getTTLockCallback().onClearFingerPrint(this.mExtendedBluetoothDevice, var25, this.error);
//                                                break label584;
//                                            case 5:
//                                                LogUtil.d("No:" + this.No, true);
//                                                TTLockAPI.getTTLockCallback().onModifyFingerPrintPeriod(this.mExtendedBluetoothDevice, var25, this.No, this.startDate, this.endDate, this.error);
//                                                break label584;
//                                            case 6:
//                                                var50 = this.parseFR(Arrays.copyOfRange(data, 4, data.length));
//                                                LogUtil.d("search:" + var50, true);
//                                                if (var50 == -1) {
//                                                    mTTLockCallback.onSearchFingerPrint(this.mExtendedBluetoothDevice, data[2], GsonUtil.toJson(this.frs), this.error);
//                                                    LogUtil.d("完成", true);
//                                                } else {
//                                                    CommandUtil.searchFRNo(command.getLockType(), var50, aesKeyArray);
//                                                }
//                                            default:
//                                                break label584;
//                                        }
//                                    case 7:
//                                        var44 = (short) (data[2] << 8 | data[3] & 255);
//                                        if (var44 == 0) {
//                                            mTTLockCallback.onSearchPasscode(this.mExtendedBluetoothDevice, GsonUtil.toJson(this.passcodes), this.error);
//                                        } else {
//                                            var50 = this.parsePasscode(Arrays.copyOfRange(data, 4, data.length));
//                                            if (var50 == -1) {
//                                                mTTLockCallback.onSearchPasscode(this.mExtendedBluetoothDevice, GsonUtil.toJson(this.passcodes), this.error);
//                                            } else {
//                                                CommandUtil.searchPasscode(command.getLockType(), var50, aesKeyArray);
//                                            }
//                                        }
//                                        break;
//                                    case 20:
//                                        this.isWaitCommand = true;
//                                        this.mExtendedBluetoothDevice.setParkStatus(data[3]);
//                                        TTLockAPI.getTTLockCallback().onGetLockSwitchState(this.mExtendedBluetoothDevice, data[2], data[3], this.error);
//                                        break;
//                                    case 37:
//                                        var44 = CommandUtil_V3.parseOperateLog(this.logOperates, Arrays.copyOfRange(data, 2, data.length), this.timezoneOffSet);
//                                        if (var44 != 0 && var44 != -16) {
//                                            CommandUtil.getOperateLog(command.getLockType(), var44, aesKeyArray);
//                                        } else if (this.upgradeFirmwareListener != null) {
//                                            this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 37, GsonUtil.toJson(this.logOperates), 1);
//                                        } else {
//                                            TTLockAPI.getTTLockCallback().onGetOperateLog(this.mExtendedBluetoothDevice, GsonUtil.toJson(this.logOperates), this.error);
//                                        }
//                                        break;
//                                    case 48:
//                                        switch (this.currentAPICommand) {
//                                            case 5:
//                                                CommandUtil.S_setAdminKeyboardPwd(command.getLockType(), this.keyboardPwd, aesKeyArray);
//                                                break label584;
//                                            case 6:
//                                                CommandUtil.C_calibationTime(command.getLockType(), this.calibationTime, this.timezoneOffSet, aesKeyArray);
//                                            case 7:
//                                            case 8:
//                                            case 9:
//                                            case 10:
//                                            case 11:
//                                            case 13:
//                                            case 14:
//                                            case 15:
//                                            case 18:
//                                            case 26:
//                                            case 42:
//                                            default:
//                                                break label584;
//                                            case 12:
//                                                CommandUtil.D_setDeletePassword(command.getLockType(), this.deletePwd, aesKeyArray);
//                                                break label584;
//                                            case 16:
//                                                CommandUtil_V3.initPasswords(command.getLockType(), aesKeyArray, 11);
//                                                break label584;
//                                            case 17:
//                                                CommandUtil.AT_setLockname(command.getLockType(), this.lockname, aesKeyArray);
//                                                break label584;
//                                            case 19:
//                                                CommandUtil.R_resetLock(command.getLockType());
//                                                break label584;
//                                            case 20:
//                                                CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 2, (byte) 2, this.originalPwd, "", this.startDate, this.endDate, aesKeyArray, this.timezoneOffSet);
//                                                break label584;
//                                            case 21:
//                                                CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 2, (byte) 1, this.originalPwd, "", this.startDate, this.endDate, aesKeyArray, this.timezoneOffSet);
//                                                break label584;
//                                            case 22:
//                                                CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 2, (byte) 3, this.originalPwd, "", this.startDate, this.endDate, aesKeyArray, this.timezoneOffSet);
//                                                break label584;
//                                            case 23:
//                                                CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 5, this.keyboardPwdType, this.originalPwd, this.newPwd, this.startDate, this.endDate, aesKeyArray, this.timezoneOffSet);
//                                                break label584;
//                                            case 24:
//                                                CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 3, this.keyboardPwdType, this.originalPwd, this.newPwd, 0L, 0L, aesKeyArray);
//                                                break label584;
//                                            case 25:
//                                                CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 1, (byte) 0, "", "", 0L, 0L, aesKeyArray);
//                                                break label584;
//                                            case 27:
//                                                CommandUtil.searchDeviceFeature(command.getLockType());
//                                                break label584;
//                                            case 28:
//                                                this.icCards = new ArrayList();
//                                                CommandUtil.searchICCardNo(command.getLockType(), (byte) 0, aesKeyArray);
//                                                break label584;
//                                            case 29:
//                                                CommandUtil.addICCard(command.getLockType(), aesKeyArray);
//                                                break label584;
//                                            case 30:
//                                                CommandUtil.modifyICCardPeriod(command.getLockType(), this.No, this.startDate, this.endDate, aesKeyArray, this.timezoneOffSet);
//                                                break label584;
//                                            case 31:
//                                                CommandUtil.deleteICCard(command.getLockType(), this.No, aesKeyArray);
//                                                break label584;
//                                            case 32:
//                                                CommandUtil.clearICCard(command.getLockType(), aesKeyArray);
//                                                break label584;
//                                            case 33:
//                                                CommandUtil.setWristbandKey(command.getLockType(), this.wristbandKey, aesKeyArray);
//                                                break label584;
//                                            case 34:
//                                                CommandUtil.addFR(command.getLockType(), aesKeyArray);
//                                                break label584;
//                                            case 35:
//                                                LogUtil.d("No:" + this.No, true);
//                                                CommandUtil.modifyFRPeriod(command.getLockType(), this.No, this.startDate, this.endDate, aesKeyArray, this.timezoneOffSet);
//                                                break label584;
//                                            case 36:
//                                                CommandUtil.deleteFR(command.getLockType(), this.No, aesKeyArray);
//                                                break label584;
//                                            case 37:
//                                                CommandUtil.clearFR(command.getLockType(), aesKeyArray);
//                                                break label584;
//                                            case 38:
//                                                CommandUtil.searchAutoLockTime(command.getLockType(), aesKeyArray);
//                                                break label584;
//                                            case 39:
//                                                CommandUtil.modifyAutoLockTime(command.getLockType(), (short) ((int) this.calibationTime), aesKeyArray);
//                                                break label584;
//                                            case 40:
//                                                CommandUtil.enterDFUMode(command.getLockType(), aesKeyArray);
//                                                break label584;
//                                            case 41:
//                                                this.dataPos = 0;
//                                                CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 3, this.keyboardPwdType, (String) this.pwds.get(this.dataPos), this.newPwd, 0L, 0L, aesKeyArray);
//                                                break label584;
//                                            case 43:
//                                                CommandUtil.screenPasscodeManage(command.getLockType(), this.transferData.getOp(), aesKeyArray);
//                                                break label584;
//                                            case 44:
//                                                this.recoveryDatas = (List) GsonUtil.toObject(this.transferData.getJson(), new TypeToken() {
//                                                });
//                                                LogUtil.e("transferData.getJson():" + this.transferData.getJson(), true);
//                                                LogUtil.e("transferData.getOp():" + this.transferData.getOp(), true);
//                                                if (this.recoveryDatas != null && this.recoveryDatas.size() != 0) {
//                                                    this.dataPos = 0;
//                                                    var41 = (RecoveryData) this.recoveryDatas.get(0);
//                                                    switch (this.transferData.getOp()) {
//                                                        case 1:
//                                                            CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 6, (byte) (var41.keyboardPwdType == 2 ? 1 : var41.keyboardPwdType), var41.cycleType, var41.keyboardPwd, var41.keyboardPwd, var41.startDate, var41.endDate, aesKeyArray, this.timezoneOffSet);
//                                                            break label584;
//                                                        case 2:
//                                                            CommandUtil.recoveryICCardPeriod(command.getLockType(), Long.valueOf(var41.cardNumber).longValue(), var41.startDate, var41.endDate, aesKeyArray, this.timezoneOffSet);
//                                                            break label584;
//                                                        case 3:
//                                                            CommandUtil.recoveryFRPeriod(command.getLockType(), Long.valueOf(var41.fingerprintNumber).longValue(), var41.startDate, var41.endDate, aesKeyArray, this.timezoneOffSet);
//                                                    }
//                                                } else {
//                                                    LogUtil.d("recoveryDatas为空", true);
//                                                    mTTLockCallback.onRecoveryData(this.mExtendedBluetoothDevice, this.transferData.getOp(), this.error);
//                                                }
//                                                break label584;
//                                            case 45:
//                                                CommandUtil.readPwdPara(command.getLockType(), aesKeyArray);
//                                                break label584;
//                                            case 46:
//                                                this.frs = new ArrayList();
//                                                CommandUtil.searchFRNo(command.getLockType(), (byte) 0, aesKeyArray);
//                                                break label584;
//                                            case 47:
//                                                this.passcodes = new ArrayList();
//                                                CommandUtil.searchPasscode(command.getLockType(), (byte) 0, aesKeyArray);
//                                                break label584;
//                                            case 48:
//                                                CommandUtil_V3.controlRemoteUnlock(command, (byte) this.transferData.getOp(), (byte) this.transferData.getOpValue(), aesKeyArray);
//                                                break label584;
//                                        }
//                                    case 49:
//                                        LogUtil.d("currentAPICommand:" + this.currentAPICommand + " COMM_INIT_PASSWORDS", true);
//                                        if (this.currentAPICommand == 2) {
//                                            CommandUtil.searchDeviceFeature(command.getLockType());
//                                        } else {
//                                            TTLockAPI.getTTLockCallback().onResetKeyboardPassword(this.mExtendedBluetoothDevice, this.pwdInfo, this.timestamp, this.error);
//                                        }
//                                        break;
//                                    case 50:
//                                        var25 = data[2];
//                                        var35 = (data[3] << 4 | data[4] >> 4 & 15) & 4095;
//                                        LogUtil.d("bytes:" + DigitUtil.byteArrayToHexString(data), true);
//                                        secretKey = ((long) data[4] * 1L << 32 & 64424509440L | (long) (data[5] << 24) & 4278190080L | (long) (data[6] << 16 & 16711680) | (long) (data[7] << 8 & '\uff00') | (long) (data[8] & 255)) & 68719476735L;
//                                        LogUtil.d("code:" + var35, true);
//                                        LogUtil.d("secretKey:" + secretKey, true);
//                                        LogUtil.d("sec:" + DigitUtil.byteArrayToHexString(data), true);
//                                        Calendar var45 = Calendar.getInstance();
//                                        var45.set(2000 + data[9], data[10] - 1, data[11], data[12], data[13]);
//                                        LogUtil.d("data[9]:" + data[9], true);
//                                        TimeZone var46 = TimeZone.getDefault();
//                                        LogUtil.d("timezoneOffSet:" + this.timezoneOffSet, true);
//                                        if (var46.inDaylightTime(new Date(System.currentTimeMillis()))) {
//                                            this.timezoneOffSet -= (long) var46.getDSTSavings();
//                                        }
//
//                                        var46.setRawOffset((int) this.timezoneOffSet);
//                                        var45.setTimeZone(var46);
//                                        long var49 = var45.getTimeInMillis();
//                                        if (data[9] == 0) {
//                                            var49 = 0L;
//                                        }
//
//                                        LogUtil.d("code:" + var35, true);
//                                        PwdInfoV3 pwdInfoV3 = PwdInfoV3.getInstance(var45.get(Calendar.YEAR), var35, String.valueOf(secretKey), var49);
//                                        String pwdInfoSource = GsonUtil.toJson(pwdInfoV3);
//                                        long timestamp = var45.getTimeInMillis();
//                                        String pwdInfo = CommandUtil.encry(pwdInfoSource, timestamp);
//                                        mTTLockCallback.onSearchPasscodeParam(this.mExtendedBluetoothDevice, var25, pwdInfo, timestamp, this.error);
//                                        break;
//                                    case 52:
//                                        Calendar var36 = Calendar.getInstance();
//                                        var36.set(2000 + data[2], data[3] - 1, data[4], data[5], data[6], data[7]);
//                                        LogUtil.d(data[2] + ":" + data[3] + ":" + data[4] + ":" + data[5] + ":" + data[6], true);
//                                        TimeZone var48 = TimeZone.getDefault();
//                                        LogUtil.d("timezoneOffSet:" + this.timezoneOffSet, true);
//                                        if (var48.inDaylightTime(new Date(System.currentTimeMillis()))) {
//                                            this.timezoneOffSet -= (long) var48.getDSTSavings();
//                                        }
//
//                                        var48.setRawOffset((int) this.timezoneOffSet);
//                                        var36.setTimeZone(var48);
//                                        LogUtil.d("calendar.getTimeInMillis():" + var36.getTimeInMillis(), true);
//                                        TTLockAPI.getTTLockCallback().onGetLockTime(this.mExtendedBluetoothDevice, var36.getTimeInMillis(), this.error);
//                                        break;
//                                    case 53:
//                                        var25 = data[2];
//                                        TTLockAPI.getTTLockCallback().onSetWristbandKeyToLock(this.mExtendedBluetoothDevice, var25, this.error);
//                                        break;
//                                    case 54:
//                                        var25 = data[2];
//                                        byte var47 = data[3];
//                                        switch (var47) {
//                                            case 1:
//                                                LogUtil.e(DigitUtil.byteArrayToHexString(data), true);
//                                                short var34 = (short) (data[4] << 8 | data[5] & 255);
//                                                short var37 = (short) (data[6] << 8 | data[7] & 255);
//                                                short maxTime = (short) (data[8] << 8 | data[9] & 255);
//                                                LogUtil.d("currentTime:" + var34 + " minTime:" + var37 + " maxTime:" + maxTime, true);
//                                                TTLockAPI.getTTLockCallback().onSearchAutoLockTime(this.mExtendedBluetoothDevice, var25, var34, var37, maxTime, this.error);
//                                                break label584;
//                                            case 2:
//                                                TTLockAPI.getTTLockCallback().onModifyAutoLockTime(this.mExtendedBluetoothDevice, var25, (int) this.calibationTime, this.error);
//                                            default:
//                                                break label584;
//                                        }
//                                    case 55:
//                                        if (data[3] == 1) {
//                                            if (this.transferData == null) {
//                                                this.transferData = new TransferData();
//                                            }
//
//                                            this.transferData.setOpValue(data[4]);
//                                        }
//
//                                        switch (this.currentAPICommand) {
//                                            case 2:
//                                                CommandUtil.operateFinished(command.getLockType());
//                                                break label584;
//                                            case 48:
//                                                this.transferData.setOp(data[3]);
//                                                CommandUtil.searchDeviceFeature(command.getLockType());
//                                            default:
//                                                break label584;
//                                        }
//                                    case 65:
//                                        battery = data.length - 2;
//                                        var26 = new byte[battery];
//                                        System.arraycopy(data, 2, var26, 0, battery);
//                                        switch (this.currentAPICommand) {
//                                            case 3:
//                                            case 4:
//                                                CommandUtil.G_unlock(command.getLockType(), this.unlockKey, var26, aesKeyArray, this.unlockDate, this.timezoneOffSet);
//                                                break label584;
//                                            case 5:
//                                                if (command.getLockType() == 5) {
//                                                    CommandUtil.checkRandom(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                } else {
//                                                    CommandUtil.S_setAdminKeyboardPwd(command.getLockType(), this.keyboardPwd, aesKeyArray);
//                                                }
//                                                break label584;
//                                            case 6:
//                                            case 7:
//                                            case 8:
//                                            case 9:
//                                            case 10:
//                                            case 11:
//                                            case 18:
//                                            case 26:
//                                            case 45:
//                                            default:
//                                                LogUtil.d("无效指令", true);
//                                                break label584;
//                                            case 12:
//                                                if (command.getLockType() == 5) {
//                                                    CommandUtil.checkRandom(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                } else {
//                                                    CommandUtil.D_setDeletePassword(command.getLockType(), this.deletePwd, aesKeyArray);
//                                                }
//                                                break label584;
//                                            case 13:
//                                            case 14:
//                                                if (command.getLockType() == 8) {
//                                                    CommandUtil.lock(command.getLockType(), this.unlockKey, var26, aesKeyArray, this.unlockDate);
//                                                } else {
//                                                    CommandUtil.L_lock(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                }
//                                                break label584;
//                                            case 15:
//                                                TTLockAPI.getTTLockCallback().onResetEKey(this.mExtendedBluetoothDevice, this.lockFlagPos, this.error);
//                                                break label584;
//                                            case 16:
//                                                switch (command.getLockType()) {
//                                                    case 3:
//                                                        this.pwdList = new LinkedList();
//                                                        var31 = this.generatePwd(KeyboardPwd.ONE_DAY_PWD);
//                                                        CommandUtil_V2S.synPwd(command.getLockType(), var31, 0);
//                                                        break label584;
//                                                    case 4:
//                                                        this.pwdData = new byte[1624];
//                                                        this.generateTransmissionData(command.getScene(), this.pwdData, this.validPwdNum);
//                                                        this.dataPos = 0;
//                                                        CommandUtil_V2S_PLUS.synPwd(command.getLockType(), Arrays.copyOfRange(this.pwdData, this.dataPos, this.dataPos + this.packetLen), this.dataPos, aesKeyArray);
//                                                        break label584;
//                                                    case 5:
//                                                        CommandUtil.checkRandom(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                    default:
//                                                        break label584;
//                                                }
//                                            case 17:
//                                                CommandUtil.checkRandom(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                break label584;
//                                            case 19:
//                                                if (command.getLockType() == 5) {
//                                                    CommandUtil.checkRandom(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                } else {
//                                                    CommandUtil.R_resetLock(command.getLockType());
//                                                }
//                                                break label584;
//                                            case 20:
//                                            case 21:
//                                            case 22:
//                                            case 23:
//                                            case 24:
//                                            case 25:
//                                            case 27:
//                                            case 28:
//                                            case 29:
//                                            case 30:
//                                            case 31:
//                                            case 32:
//                                            case 33:
//                                            case 34:
//                                            case 35:
//                                            case 36:
//                                            case 37:
//                                            case 38:
//                                            case 39:
//                                            case 40:
//                                            case 41:
//                                            case 42:
//                                            case 43:
//                                            case 44:
//                                            case 46:
//                                            case 47:
//                                            case 48:
//                                                CommandUtil.checkRandom(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                break label584;
//                                        }
//                                    case 67:
//                                        if (this.upgradeFirmwareListener != null) {
//                                            this.upgradeFirmwareListener.onUpgradeFirmwareChanged((byte) 67, "", 1);
//                                        } else {
//                                            TTLockAPI.getTTLockCallback().onSetLockTime(this.mExtendedBluetoothDevice, this.error);
//                                        }
//                                        break;
//                                    case 68:
//                                        LogUtil.d("set delete pwd success", true);
//                                        if (this.currentAPICommand == 2) {
//                                            switch (command.getLockType()) {
//                                                case 3:
//                                                    this.pwdList = new LinkedList();
//                                                    String var33 = this.generatePwd(KeyboardPwd.ONE_DAY_PWD);
//                                                    CommandUtil_V2S.synPwd(command.getLockType(), var33, 0);
//                                                    break label584;
//                                                case 4:
//                                                    this.pwdData = new byte[1624];
//                                                    this.generateTransmissionData(command.getScene(), this.pwdData, this.validPwdNum);
//                                                    this.dataPos = 0;
//                                                    CommandUtil_V2S_PLUS.synPwd(command.getLockType(), Arrays.copyOfRange(this.pwdData, this.dataPos, this.dataPos + this.packetLen), this.dataPos, aesKeyArray);
//                                            }
//                                        } else {
//                                            TTLockAPI.getTTLockCallback().onSetDeletePassword(this.mExtendedBluetoothDevice, this.deletePwd, this.error);
//                                        }
//                                        break;
//                                    case 71:
//                                        var25 = -1;
//                                        int var40 = 0;
//                                        Calendar var32 = Calendar.getInstance();
//                                        secretKey = var32.getTimeInMillis();
//                                        int calendar = (int) (secretKey / 1000L);
//                                        int timeZone = data.length;
//                                        if (timeZone > 2) {
//                                            var25 = data[2];
//                                            if (timeZone >= 17) {
//                                                var40 = (int) DigitUtil.fourBytesToLong(Arrays.copyOfRange(data, 3, 7));
//                                                calendar = (int) DigitUtil.fourBytesToLong(Arrays.copyOfRange(data, 7, 11));
//                                                var32.set(2000 + data[11], data[12] - 1, data[13], data[14], data[15], data[16]);
//                                                TimeZone deleteTime = TimeZone.getDefault();
//                                                LogUtil.d("timezoneOffSet:" + this.timezoneOffSet, true);
//                                                if (deleteTime.inDaylightTime(new Date(System.currentTimeMillis()))) {
//                                                    this.timezoneOffSet -= (long) deleteTime.getDSTSavings();
//                                                }
//
//                                                deleteTime.setRawOffset((int) this.timezoneOffSet);
//                                                var32.setTimeZone(deleteTime);
//                                                secretKey = var32.getTimeInMillis();
//                                            }
//                                        }
//
//                                        this.mExtendedBluetoothDevice.setBatteryCapacity(var25);
//                                        if (command.getLockType() == 6) {
//                                            TTLockAPI.getTTLockCallback().onLock(this.mExtendedBluetoothDevice, var25, var40, (int) (this.unlockDate / 1000L), secretKey, this.error);
//                                        } else {
//                                            TTLockAPI.getTTLockCallback().onUnlock(this.mExtendedBluetoothDevice, var40, calendar, secretKey, this.error);
//                                        }
//                                        break;
//                                    case 73:
//                                        switch (command.getLockType()) {
//                                            case 3:
//                                                battery = this.pwdList.size();
//                                                TTLockAPI.getTTLockCallback().onResetKeyboardPasswordProgress(this.mExtendedBluetoothDevice, battery / 9, this.error);
//                                                boolean var28 = false;
//                                                byte var30;
//                                                if (battery < 300) {
//                                                    var30 = 1;
//                                                } else if (battery < 450) {
//                                                    var30 = 2;
//                                                } else if (battery < 550) {
//                                                    var30 = 3;
//                                                } else if (battery < 650) {
//                                                    var30 = 4;
//                                                } else if (battery < 700) {
//                                                    var30 = 5;
//                                                } else if (battery < 750) {
//                                                    var30 = 6;
//                                                } else if (battery < 800) {
//                                                    var30 = 7;
//                                                } else {
//                                                    if (battery >= 900) {
//                                                        try {
//                                                            var31 = DigitUtil.generateKeyboardPwd_Json(this.pwdList);
//                                                            this.timestamp = System.currentTimeMillis();
//                                                            this.pwdInfo = CommandUtil.encry(var31, this.timestamp);
//                                                            LogUtil.d("pwdInfoOrigin:" + var31, true);
//                                                            if (this.currentAPICommand == 2) {
//                                                                TTLockAPI.getTTLockCallback().onAddAdministrator(this.mExtendedBluetoothDevice, command.getLockVersionString(), DigitUtil.encodeLockData(this.adminPs), DigitUtil.encodeLockData(this.unlockKey), this.keyboardPwd, this.deletePwd, this.pwdInfo, this.timestamp, DigitUtil.encodeAesKey(aesKeyArray), 1, this.modelNumber, this.hardwareRevision, this.firmwareRevision, this.error);
//                                                            } else {
//                                                                TTLockAPI.getTTLockCallback().onResetKeyboardPassword(this.mExtendedBluetoothDevice, this.pwdInfo, this.timestamp, this.error);
//                                                            }
//                                                        } catch (JSONException var21) {
//                                                            var21.printStackTrace();
//                                                        }
//                                                        break label584;
//                                                    }
//
//                                                    var30 = 8;
//                                                }
//
//                                                var31 = this.generatePwd(var30);
//                                                CommandUtil_V2S.synPwd(command.getLockType(), var31, battery);
//                                                break label584;
//                                            case 4:
//                                                this.dataPos += this.packetLen;
//                                                if (this.dataPos + 1 < this.pwdData.length) {
//                                                    TTLockAPI.getTTLockCallback().onResetKeyboardPasswordProgress(this.mExtendedBluetoothDevice, this.dataPos * 100 / this.pwdData.length, this.error);
//                                                    CommandUtil_V2S_PLUS.synPwd(command.getLockType(), Arrays.copyOfRange(this.pwdData, this.dataPos, this.dataPos + this.packetLen), this.dataPos, aesKeyArray);
//                                                } else {
//                                                    LogUtil.e("LOCK_TYPE_V2S_PLUS", true);
//                                                    if (this.currentAPICommand == 2) {
//                                                        TTLockAPI.getTTLockCallback().onAddAdministrator(this.mExtendedBluetoothDevice, command.getLockVersionString(), DigitUtil.encodeLockData(this.adminPs), DigitUtil.encodeLockData(this.unlockKey), this.keyboardPwd, this.deletePwd, this.pwdInfo, this.timestamp, DigitUtil.encodeAesKey(aesKeyArray), 1, this.modelNumber, this.hardwareRevision, this.firmwareRevision, this.error);
//                                                        this.disconnect();
//                                                    } else {
//                                                        TTLockAPI.getTTLockCallback().onResetKeyboardPassword(this.mExtendedBluetoothDevice, this.pwdInfo, this.timestamp, this.error);
//                                                    }
//                                                }
//                                            default:
//                                                break label584;
//                                        }
//                                    case 76:
//                                        var25 = -1;
//                                        if (data.length > 2) {
//                                            var25 = data[2];
//                                        }
//
//                                        this.mExtendedBluetoothDevice.setBatteryCapacity(var25);
//                                        TTLockAPI.getTTLockCallback().onUnlock(this.mExtendedBluetoothDevice, 0, (int) (System.currentTimeMillis() / 1000L), System.currentTimeMillis(), this.error);
//                                        break;
//                                    case 78:
//                                        TTLockAPI.getTTLockCallback().onSetLockName(this.mExtendedBluetoothDevice, this.lockname, this.error);
//                                        break;
//                                    case 82:
//                                        TTLockAPI.getTTLockCallback().onResetLock(this.mExtendedBluetoothDevice, this.error);
//                                        break;
//                                    case 83:
//                                        if (this.currentAPICommand == 2) {
//                                            if (command.getLockType() == 5) {
//                                                CommandUtil_V3.initPasswords(command.getLockType(), aesKeyArray, this.currentAPICommand);
//                                            } else {
//                                                this.deletePwd = DigitUtil.generatePwdByLength(7);
//                                                CommandUtil.D_setDeletePassword(command.getLockType(), this.deletePwd, aesKeyArray);
//                                            }
//                                        } else {
//                                            TTLockAPI.getTTLockCallback().onSetAdminKeyboardPassword(this.mExtendedBluetoothDevice, this.keyboardPwd, this.error);
//                                        }
//                                        break;
//                                    case 85:
//                                        battery = data.length - 2;
//                                        var26 = new byte[battery];
//                                        System.arraycopy(data, 2, var26, 0, battery);
//                                        switch (this.currentAPICommand) {
//                                            case 3:
//                                            case 4:
//                                                CommandUtil.G_unlock(command.getLockType(), this.unlockKey, var26, aesKeyArray, this.unlockDate, this.timezoneOffSet);
//                                                break label584;
//                                            case 6:
//                                                if (command.getLockType() != 5 && command.getLockType() != 8) {
//                                                    CommandUtil.C_calibationTime(command.getLockType(), this.calibationTime, this.timezoneOffSet, aesKeyArray);
//                                                } else {
//                                                    CommandUtil.checkRandom(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                }
//                                                break label584;
//                                            case 13:
//                                            case 14:
//                                                if (command.getLockType() == 8) {
//                                                    CommandUtil.lock(command.getLockType(), this.unlockKey, var26, aesKeyArray, this.unlockDate);
//                                                } else {
//                                                    CommandUtil.L_lock(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                                }
//                                                break label584;
//                                            case 42:
//                                                CommandUtil.lock(command.getLockType(), this.unlockKey, var26, aesKeyArray, this.unlockDate);
//                                                break label584;
//                                            case 45:
//                                                CommandUtil.checkRandom(command.getLockType(), this.unlockKey, var26, aesKeyArray);
//                                            default:
//                                                break label584;
//                                        }
//                                    case 86:
//                                        if (command.getLockType() == 5 && !Constant.SCIENER.equals(new String(data, 2, 7))) {
//                                            this.error = Error.AES_PARSE_ERROR;
//                                            this.error.setCommand(command.getCommand());
//                                            this.errorCallback(this.error);
//                                            return;
//                                        }
//
//                                        if (command.getLockType() == 8) {
//                                            CommandUtil.searchDeviceFeature(command.getLockType());
//                                        } else if (command.getLockType() == 6) {
//                                            TTLockAPI.getTTLockCallback().onAddAdministrator(this.mExtendedBluetoothDevice, command.getLockVersionString(), DigitUtil.encodeLockData(this.adminPs), DigitUtil.encodeLockData(this.unlockKey), "", "", "", 0L, "", 0, this.modelNumber, this.hardwareRevision, this.firmwareRevision, this.error);
//                                        } else {
//                                            this.keyboardPwd = DigitUtil.generatePwdByLength(7);
//                                            CommandUtil.S_setAdminKeyboardPwd(command.getLockType(), this.keyboardPwd, aesKeyArray);
//                                        }
//                                        break;
//                                    case 87:
//                                        if (command.getLockType() != 5 && command.getLockType() != 8) {
//                                            battery = data.length;
//                                            if (battery == 8) {
//                                                if (data[7] == 1) {
//                                                    op = Calendar.getInstance();
//                                                    op.set(2000 + data[2], data[3] - 1, data[4], data[5], data[6]);
//                                                    code = TimeZone.getDefault();
//                                                    LogUtil.d("timezoneOffSet:" + this.timezoneOffSet, true);
//                                                    if (code.inDaylightTime(new Date(System.currentTimeMillis()))) {
//                                                        this.timezoneOffSet -= (long) code.getDSTSavings();
//                                                    }
//
//                                                    code.setRawOffset((int) this.timezoneOffSet);
//                                                    op.setTimeZone(code);
//                                                    this.moveDateArray.put(op.getTimeInMillis());
//                                                    CommandUtil_Va.Va_Get_Lockcar_Alarm(command.getLockType());
//                                                } else {
//                                                    LogUtil.w("读取车位锁数据出错", true);
//                                                }
//                                            } else if (battery == 3) {
//                                                TTLockAPI.getTTLockCallback().onGetOperateLog(this.mExtendedBluetoothDevice, this.moveDateArray.toString(), this.error);
//                                            }
//                                        } else {
//                                            this.tempOptype = 1;
//                                            CommandUtil.readDeviceInfo(command.getLockType(), (byte) 1, aesKeyArray);
//                                            this.mExtendedBluetoothDevice.disconnectStatus = 3;
//                                            this.mHandler.postDelayed(this.disConRunable, 1500L);
//                                        }
//                                        break;
//                                    case 88:
//                                        delay = (int) DigitUtil.fourBytesToLong(Arrays.copyOfRange(data, 3, 7));
//                                        battery = (int) DigitUtil.fourBytesToLong(Arrays.copyOfRange(data, 7, 11));
//                                        op = Calendar.getInstance();
//                                        op.set(2000 + data[11], data[12] - 1, data[13], data[14], data[15], data[16]);
//                                        code = TimeZone.getDefault();
//                                        LogUtil.d("timezoneOffSet:" + this.timezoneOffSet, true);
//                                        if (code.inDaylightTime(new Date(System.currentTimeMillis()))) {
//                                            this.timezoneOffSet -= (long) code.getDSTSavings();
//                                        }
//
//                                        code.setRawOffset((int) this.timezoneOffSet);
//                                        op.setTimeZone(code);
//                                        secretKey = op.getTimeInMillis();
//                                        this.mExtendedBluetoothDevice.setBatteryCapacity(data[2]);
//                                        TTLockAPI.getTTLockCallback().onLock(this.mExtendedBluetoothDevice, data[2], delay, battery, secretKey, this.error);
//                                        break;
//                                    case 89:
//                                        delay = this.transferData.getOp();
//                                        if (data[3] == 1) {
//                                            delay = data[4];
//                                        } else {
//                                            delay -= 2;
//                                        }
//
//                                        TTLockAPI.getTTLockCallback().onScreenPasscodeOperate(this.mExtendedBluetoothDevice, data[2], delay, this.error);
//                                        break;
//                                    default:
//                                        LogUtil.w("异常指令", true);
//                                }
//                            } else if (this.currentAPICommand == 6 && data[0] == 85) {
//                                CommandUtil.C_calibationTime(command.getLockType(), this.calibationTime, this.timezoneOffSet, aesKeyArray);
//                            } else if (this.currentAPICommand == 44 && data[2] != 22) {
//                                ++this.dataPos;
//                                if (this.dataPos < this.recoveryDatas.size()) {
//                                    RecoveryData var23 = (RecoveryData) this.recoveryDatas.get(this.dataPos);
//                                    switch (this.transferData.getOp()) {
//                                        case 1:
//                                            CommandUtil.manageKeyboardPassword(command.getLockType(), (byte) 6, (byte) (var23.keyboardPwdType == 2 ? 1 : var23.keyboardPwdType), var23.cycleType, var23.keyboardPwd, var23.keyboardPwd, var23.startDate, var23.endDate, aesKeyArray, this.timezoneOffSet);
//                                            break;
//                                        case 2:
//                                            CommandUtil.recoveryICCardPeriod(command.getLockType(), Long.valueOf(var23.cardNumber).longValue(), var23.startDate, var23.endDate, aesKeyArray, this.timezoneOffSet);
//                                            break;
//                                        case 3:
//                                            CommandUtil.recoveryFRPeriod(command.getLockType(), Long.valueOf(var23.fingerprintNumber).longValue(), var23.startDate, var23.endDate, aesKeyArray, this.timezoneOffSet);
//                                    }
//                                } else {
//                                    mTTLockCallback.onRecoveryData(this.mExtendedBluetoothDevice, this.transferData.getOp(), this.error);
//                                }
//                            } else {
//                                this.error = Error.getInstance(data[2]);
//                                this.error.setCommand(data[0]);
//                                if (data[0] == 3 && data[3] == 5) {
//                                    this.tmpError = this.error;
//                                    CommandUtil.searchDeviceFeature(command.getLockType());
//                                } else {
//                                    this.errorCallback(this.error);
//                                }
//                            }
//                        }
//
//                        if (command.getLockType() == 5 && !this.isWaitCommand) {
//                            LogUtil.e("开始计时", true);
//                            long var24 = 2000L;
//                            if (this.currentAPICommand == 19 || this.currentAPICommand == 34) {
//                                var24 = 7000L;
//                            }
//
//                            this.mExtendedBluetoothDevice.disconnectStatus = 3;
//                            this.mHandler.postDelayed(this.disConRunable, var24);
//                        }
//
//                    }
//                } else {
//                    if (this.currentAPICommand == 19 && command.length == 0) {
//                        TTLockAPI.getTTLockCallback().onResetLock(this.mExtendedBluetoothDevice, Error.SUCCESS);
//                    } else {
//                        this.error = Error.KEY_INVALID;
//                        this.error.setCommand(command.getCommand());
//                        this.errorCallback(this.error);
//                    }
//
//                }
//            }
//        }
    }
}
