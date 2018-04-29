//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lock.bl.sdk.entity;


import com.lock.bl.sdk.util.GsonUtil;

public class LockVersion {
    public static LockVersion lockVersion_V2S_PLUS = new LockVersion(5, 4, 1, 1, 1);
    public static LockVersion lockVersion_V3 = new LockVersion(5, 3, 1, 1, 1);
    public static LockVersion lockVersion_V2S = new LockVersion(5, 1, 1, 1, 1);
    public static LockVersion lockVersion_Va = new LockVersion(10, 1, 10, 1, 1);
    public static LockVersion lockVersion_Vb = new LockVersion(11, 1, 11, 1, 1);
    public static LockVersion lockVersion_V2 = new LockVersion(3, 0, 0, 0, 0);
    public static LockVersion lockVersion_V3_car = new LockVersion(5, 3, 7, 1, 1);
    private byte protocolType;
    private byte protocolVersion;
    private byte scene;
    private short groupId;
    private short orgId;

    public LockVersion(byte protocolType, byte protocolVersion, byte scene, short groupId, short orgId) {
        this.protocolType = protocolType;
        this.protocolVersion = protocolVersion;
        this.scene = scene;
        this.groupId = groupId;
        this.orgId = orgId;
    }

    public LockVersion(int protocolType, int protocolVersion, int scene, int groupId, int orgId) {

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

    public short getGroupId() {
        return this.groupId;
    }

    public void setGroupId(short groupId) {
        this.groupId = groupId;
    }

    public short getOrgId() {
        return this.orgId;
    }

    public void setOrgId(short orgId) {
        this.orgId = orgId;
    }

    public static LockVersion getLockVersion(int lockType) {
        switch(lockType) {
            case 2:
                return lockVersion_V2;
            case 3:
                return lockVersion_V2S;
            case 4:
                return lockVersion_V2S_PLUS;
            case 5:
                return lockVersion_V3;
            case 6:
                return lockVersion_Va;
            case 7:
                return lockVersion_Vb;
            case 8:
                return lockVersion_V3_car;
            default:
                return null;
        }
    }

    public String toString() {
        return this.protocolType + "," + this.protocolVersion + "," + this.scene + "," + this.groupId + "," + this.orgId;
    }

    public String toGson() {
        return GsonUtil.toJson(this);
    }
}
