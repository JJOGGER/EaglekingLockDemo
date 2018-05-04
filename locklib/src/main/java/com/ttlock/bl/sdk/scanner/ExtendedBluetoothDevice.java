//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;

import com.ttlock.bl.sdk.entity.LockVersion;
import com.ttlock.bl.sdk.service.BluetoothLeService;
import com.ttlock.bl.sdk.util.DigitUtil;

import java.util.Arrays;

public class ExtendedBluetoothDevice implements Parcelable {
    private static final long serialVersionUID = 1L;
    public static final byte GAP_ADTYPE_LOCAL_NAME_COMPLETE = 9;
    public static final byte GAP_ADTYPE_POWER_LEVEL = 10;
    public static final byte GAP_ADTYPE_MANUFACTURER_SPECIFIC = -1;
    public static final byte STATUS_PARK_LOCK = 0;
    public static final byte STATUS_PARK_UNLOCK_NO_CAR = 1;
    public static final byte STATUS_PARK_UNKNOWN = 2;
    public static final byte STATUS_PARK_UNLOCK_HAS_CAR = 3;
    private int parkStatus;
    private BluetoothDevice device;
    private byte[] scanRecord;
    private String name;
    private String mAddress;
    private int rssi;
    private byte protocolType;
    private byte protocolVersion;
    private byte scene;
    public byte groupId;
    public byte orgId;
    private int lockType;
    private boolean isTouch;
    private boolean isSettingMode;
    private boolean isUnlock;
    private byte txPowerLevel;
    private byte batteryCapacity;
    private long date;
    private boolean isWristband;
    private boolean isRoomLock;
    private boolean isSafeLock;
    private boolean isBicycleLock;
    private boolean isLockcar;
    private boolean isGlassLock;
    private int remoteUnlockSwitch;
    public int disconnectStatus;
    public static final int DISCONNECT_STATUS_NONE = 0;
    public static final int CONNECT_TIME_OUT = 1;
    public static final int SERVICE_DISCONNECTED = 2;
    public static final int RESPONSE_TIME_OUT = 3;
    public static final int NORMAL_DISCONNECTED = 4;
    public static final int DEVICE_CANNOT_CONNECT = 5;
    public static final Creator<ExtendedBluetoothDevice> CREATOR = new Creator() {
        public ExtendedBluetoothDevice createFromParcel(Parcel source) {
            return new ExtendedBluetoothDevice(source);
        }

        public ExtendedBluetoothDevice[] newArray(int size) {
            return new ExtendedBluetoothDevice[size];
        }
    };

    public ExtendedBluetoothDevice() {
        this.isTouch = true;
        this.isSettingMode = true;
        this.batteryCapacity = -1;
        this.date = System.currentTimeMillis();
    }

    @RequiresPermission(
            allOf = {"android.permission.BLUETOOTH"}
    )
    public ExtendedBluetoothDevice(BluetoothDevice device) {
        this(device, 0, (byte[])null);
    }

    @RequiresPermission(
            allOf = {"android.permission.BLUETOOTH"}
    )
    public ExtendedBluetoothDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        this.isTouch = true;
        this.isSettingMode = true;
        this.batteryCapacity = -1;
        this.date = System.currentTimeMillis();
        this.device = device;
        this.scanRecord = scanRecord;
        this.rssi = rssi;
        this.name = device.getName();
        this.mAddress = device.getAddress();
        if(scanRecord != null) {
            this.initial();
        }

    }

    @RequiresPermission(
            allOf = {"android.permission.BLUETOOTH"}
    )
    @TargetApi(21)
    public ExtendedBluetoothDevice(ScanResult scanResult) {
        this.isTouch = true;
        this.isSettingMode = true;
        this.batteryCapacity = -1;
        this.date = System.currentTimeMillis();
        this.device = scanResult.getDevice();
        this.scanRecord = scanResult.getScanRecord().getBytes();
        this.rssi = scanResult.getRssi();
        this.name = this.device.getName();
        this.mAddress = this.device.getAddress();
        this.date = System.currentTimeMillis();
        this.initial();
    }

    private void initial() {
        int scanRecordLength = this.scanRecord.length;
        int index = 0;

        while(true) {
            if(index < scanRecordLength) {
                byte len = this.scanRecord[index];
                if(len != 0) {
                    byte adtype = this.scanRecord[index + 1];
                    switch(adtype) {
                        case -1:
                            byte var6 = 2;
                            int var7 = var6 + 1;
                            this.protocolType = this.scanRecord[index + var6];
                            this.protocolVersion = this.scanRecord[index + var7++];
                            if(this.protocolType == 52 && this.protocolVersion == 18) {
                                this.isWristband = true;
                            }

                            if(BluetoothLeService.scanBongOnly) {
                                break;
                            }

                            if(this.protocolType == 5 && this.protocolVersion == 3) {
                                this.scene = this.scanRecord[index + var7++];
                            } else {
                                var6 = 6;
                                var7 = var6 + 1;
                                this.protocolType = this.scanRecord[index + var6];
                                this.protocolVersion = this.scanRecord[index + var7];
                                var6 = 9;
                                var7 = var6 + 1;
                                this.scene = this.scanRecord[index + var6];
                            }

                            if(this.protocolType < 5 || this.getLockType() == 3) {
                                this.isRoomLock = true;
                                return;
                            }

                            if(this.scene <= 3) {
                                this.isRoomLock = true;
                            } else {
                                switch(this.scene) {
                                    case 4:
                                        this.isGlassLock = true;
                                        break;
                                    case 5:
                                        this.isSafeLock = true;
                                        break;
                                    case 6:
                                        this.isBicycleLock = true;
                                        break;
                                    case 7:
                                        this.isLockcar = true;
                                }
                            }

                            this.isUnlock = (this.scanRecord[index + var7] & 1) == 1;
                            this.isSettingMode = (this.scanRecord[index + var7] & 4) != 0;
                            if(this.getLockType() != 5 && this.getLockType() != 8) {
                                if(this.getLockType() == 6) {
                                    this.isTouch = false;
                                    this.isLockcar = true;
                                }
                            } else {
                                this.isTouch = (this.scanRecord[index + var7] & 8) != 0;
                            }

                            if(this.isLockcar) {
                                if(this.isUnlock) {
                                    if((this.scanRecord[index + var7] & 16) == 1) {
                                        this.parkStatus = 3;
                                    } else {
                                        this.parkStatus = 2;
                                    }
                                } else if((this.scanRecord[index + var7] & 16) == 1) {
                                    this.parkStatus = 1;
                                } else {
                                    this.parkStatus = 0;
                                }
                            }

                            ++var7;
                            this.batteryCapacity = this.scanRecord[index + var7];
                            var7 += 3;
                            if(TextUtils.isEmpty(this.mAddress)) {
                                this.setAddress(DigitUtil.getMacString(Arrays.copyOfRange(this.scanRecord, index + var7, index + var7 + 6)));
                            }
                            break;
                        case 9:
                            if(this.name == null || this.name.length() == 0) {
                                byte[] offset = new byte[len - 1];
                                System.arraycopy(this.scanRecord, index + 2, offset, 0, len - 1);
                                this.setName(new String(offset));
                            }

                            if(this.name.toUpperCase().startsWith("LOCK_")) {
                                this.isRoomLock = true;
                            }
                            break;
                        case 10:
                            this.txPowerLevel = this.scanRecord[index + 2];
                    }

                    index += len + 1;
                    continue;
                }
            }

            return;
        }
    }

    public BluetoothDevice getDevice() {
        return this.device;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getAddress() {
        return this.mAddress;
    }

    public boolean isSettingMode() {
        return this.isSettingMode;
    }

    public void setSettingMode(boolean settingMode) {
        this.isSettingMode = settingMode;
    }

    public byte getBatteryCapacity() {
        return this.batteryCapacity;
    }

    public void setBatteryCapacity(byte batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public boolean isTouch() {
        return this.isTouch;
    }

    public int getRssi() {
        return this.rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public void setTouch(boolean touch) {
        this.isTouch = touch;
    }

    public boolean isWristband() {
        return this.isWristband;
    }

    public void setWristband(boolean wristband) {
        this.isWristband = wristband;
    }

    public boolean isBicycleLock() {
        return this.isBicycleLock;
    }

    public void setBicycleLock(boolean bicycleLock) {
        this.isBicycleLock = bicycleLock;
    }

    public boolean isSafeLock() {
        return this.isSafeLock;
    }

    public void setSafeLock(boolean safeLock) {
        this.isSafeLock = safeLock;
    }

    public boolean isRoomLock() {
        return this.isRoomLock;
    }

    public void setRoomLock(boolean roomLock) {
        this.isRoomLock = roomLock;
    }

    public boolean isLockcar() {
        return this.isLockcar;
    }

    public void setLockcar(boolean lockcar) {
        this.isLockcar = lockcar;
    }

    public boolean isGlassLock() {
        return this.isGlassLock;
    }

    public void setGlassLock(boolean glassLock) {
        this.isGlassLock = glassLock;
    }

    public byte getProtocolType() {
        return this.protocolType;
    }

    public void setProtocolType(byte protocolType) {
        this.protocolType = protocolType;
    }

    public byte getProtocolVersion() {
        return this.protocolVersion;
    }

    public void setProtocolVersion(byte protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public byte getScene() {
        return this.scene;
    }

    public void setScene(byte scene) {
        this.scene = scene;
    }

    public int getRemoteUnlockSwitch() {
        return this.remoteUnlockSwitch;
    }

    public void setRemoteUnlockSwitch(int remoteUnlockSwitch) {
        this.remoteUnlockSwitch = remoteUnlockSwitch;
    }

    public boolean equals(Object o) {
        return o instanceof ExtendedBluetoothDevice ?this.mAddress.equals(((ExtendedBluetoothDevice)o).getAddress()):false;
    }

    public long getDate() {
        return this.date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getParkStatus() {
        return this.parkStatus;
    }

    public void setParkStatus(int parkStatus) {
        this.parkStatus = parkStatus;
    }

    public int getLockType() {
        if(this.protocolType == 5 && this.protocolVersion == 3 && this.scene == 7) {
            this.lockType = 8;
        } else if(this.protocolType == 10 && this.protocolVersion == 1) {
            this.lockType = 6;
        } else if(this.protocolType == 11 && this.protocolVersion == 1) {
            this.lockType = 7;
        } else if(this.protocolType == 5 && this.protocolVersion == 4) {
            this.lockType = 4;
        } else if(this.protocolType == 5 && this.protocolVersion == 3) {
            this.lockType = 5;
        } else if(this.protocolType == 5 && this.protocolVersion == 1 || this.name != null && this.name.toUpperCase().startsWith("LOCK_")) {
            this.lockType = 3;
        }

        return this.lockType;
    }

    public String getLockVersionJson() {
        if(this.name.toUpperCase().startsWith("LOCK_")) {
            this.protocolType = 5;
            this.protocolVersion = 1;
        }

        return (new LockVersion(this.protocolType, this.protocolVersion, this.scene, (short)this.groupId, (short)this.orgId)).toGson();
    }

    public String toString() {
        return "ExtendedBluetoothDevice{name=\'" + this.name + '\'' + ", mAddress=\'" + this.mAddress + '\'' + ", rssi=" + this.rssi + ", protocolType=" + this.protocolType + ", protocolVersion=" + this.protocolVersion + ", scene=" + this.scene + ", groupId=" + this.groupId + ", orgId=" + this.orgId + ", lockType=" + this.lockType + ", isTouch=" + this.isTouch + ", isSettingMode=" + this.isSettingMode + ", isWristband=" + this.isWristband() + ", isUnlock=" + this.isUnlock + ", txPowerLevel=" + this.txPowerLevel + ", batteryCapacity=" + this.batteryCapacity + ", date=" + this.date + ", device=" + this.device + ", scanRecord=" + DigitUtil.byteArrayToHexString(this.scanRecord) + '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.parkStatus);
        dest.writeParcelable(this.device, flags);
        dest.writeByteArray(this.scanRecord);
        dest.writeString(this.name);
        dest.writeString(this.mAddress);
        dest.writeInt(this.rssi);
        dest.writeByte(this.protocolType);
        dest.writeByte(this.protocolVersion);
        dest.writeByte(this.scene);
        dest.writeByte(this.groupId);
        dest.writeByte(this.orgId);
        dest.writeInt(this.lockType);
        dest.writeByte((byte)(this.isTouch?1:0));
        dest.writeByte((byte)(this.isSettingMode?1:0));
        dest.writeByte((byte)(this.isUnlock?1:0));
        dest.writeByte(this.txPowerLevel);
        dest.writeByte(this.batteryCapacity);
        dest.writeLong(this.date);
        dest.writeByte((byte)(this.isWristband?1:0));
        dest.writeByte((byte)(this.isRoomLock?1:0));
        dest.writeByte((byte)(this.isSafeLock?1:0));
        dest.writeByte((byte)(this.isBicycleLock?1:0));
        dest.writeByte((byte)(this.isLockcar?1:0));
        dest.writeByte((byte)(this.isGlassLock?1:0));
        dest.writeInt(this.remoteUnlockSwitch);
        dest.writeInt(this.disconnectStatus);
    }

    protected ExtendedBluetoothDevice(Parcel in) {
        this.isTouch = true;
        this.isSettingMode = true;
        this.batteryCapacity = -1;
        this.date = System.currentTimeMillis();
        this.parkStatus = in.readInt();
        this.device = (BluetoothDevice)in.readParcelable(BluetoothDevice.class.getClassLoader());
        this.scanRecord = in.createByteArray();
        this.name = in.readString();
        this.mAddress = in.readString();
        this.rssi = in.readInt();
        this.protocolType = in.readByte();
        this.protocolVersion = in.readByte();
        this.scene = in.readByte();
        this.groupId = in.readByte();
        this.orgId = in.readByte();
        this.lockType = in.readInt();
        this.isTouch = in.readByte() != 0;
        this.isSettingMode = in.readByte() != 0;
        this.isUnlock = in.readByte() != 0;
        this.txPowerLevel = in.readByte();
        this.batteryCapacity = in.readByte();
        this.date = in.readLong();
        this.isWristband = in.readByte() != 0;
        this.isRoomLock = in.readByte() != 0;
        this.isSafeLock = in.readByte() != 0;
        this.isBicycleLock = in.readByte() != 0;
        this.isLockcar = in.readByte() != 0;
        this.isGlassLock = in.readByte() != 0;
        this.remoteUnlockSwitch = in.readInt();
        this.disconnectStatus = in.readInt();
    }
}
