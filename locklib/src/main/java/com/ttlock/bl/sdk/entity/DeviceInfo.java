//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.entity;

public class DeviceInfo {
    public int specialValue;
    public String modelNum;
    public String hardwareRevision;
    public String firmwareRevision;

    public DeviceInfo() {
    }

    public DeviceInfo(String modelNum, String hardwareVersion, String firmwareVersion) {
        this.modelNum = modelNum;
        this.hardwareRevision = hardwareVersion;
        this.firmwareRevision = firmwareVersion;
    }

    public void setModelNum(String modelNum) {
        this.modelNum = modelNum;
    }

    public void setHardwareRevision(String hardwareRevision) {
        this.hardwareRevision = hardwareRevision;
    }

    public void setFirmwareRevision(String firmwareRevision) {
        this.firmwareRevision = firmwareRevision;
    }
}
