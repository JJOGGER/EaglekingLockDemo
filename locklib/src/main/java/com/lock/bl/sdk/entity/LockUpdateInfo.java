//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lock.bl.sdk.entity;

public class LockUpdateInfo {
    public int errcode;
    int needUpdate;
    String url;
    String releaseNote;
    String title;
    String modelNum;
    String hardwareRevision;
    String firmwareRevision;
    String decryptionKey;
    String version;
    public static final int IS_LATEST_VERSION = 0;
    public static final int NEW_VERSION_AVAILABLE = 1;
    public static final int UNKNOWN_LOCK_VERSION = 2;

    public LockUpdateInfo() {
    }

    public int getNeedUpdate() {
        return this.needUpdate;
    }

    public String getReleaseNote() {
        return this.releaseNote;
    }

    public String getUrl() {
        return this.url;
    }

    public void setNeedUpdate(int needUpdate) {
        this.needUpdate = needUpdate;
    }

    public void setReleaseNote(String releaseNote) {
        this.releaseNote = releaseNote;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDecryptionKey() {
        return this.decryptionKey;
    }

    public String getFirmwareRevision() {
        return this.firmwareRevision;
    }

    public String getHardwareRevision() {
        return this.hardwareRevision;
    }

    public String getModelNum() {
        return this.modelNum;
    }

    public void setDecryptionKey(String decryptionKey) {
        this.decryptionKey = decryptionKey;
    }

    public void setFirmwareRevision(String firmwareRevision) {
        this.firmwareRevision = firmwareRevision;
    }

    public void setHardwareRevision(String hardwareRevision) {
        this.hardwareRevision = hardwareRevision;
    }

    public void setModelNum(String modelNum) {
        this.modelNum = modelNum;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
