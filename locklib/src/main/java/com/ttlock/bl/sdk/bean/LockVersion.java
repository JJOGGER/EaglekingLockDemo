package com.ttlock.bl.sdk.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.ttlock.bl.sdk.util.GsonUtil;

/**
 * Created by jogger on 2018/4/27.
 */

public class LockVersion implements Parcelable {
    public static LockVersion lockVersion_V2S_PLUS = new LockVersion(5, 4, 1, 1, 1);
    public static LockVersion lockVersion_V3 = new LockVersion(5, 3, 1, 1, 1);
    public static LockVersion lockVersion_V2S = new LockVersion(5, 1, 1, 1, 1);
    public static LockVersion lockVersion_Va = new LockVersion(10, 1, 10, 1, 1);
    public static LockVersion lockVersion_Vb = new LockVersion(11, 1, 11, 1, 1);
    public static LockVersion lockVersion_V2 = new LockVersion(3, 0, 0, 0, 0);
    public static LockVersion lockVersion_V3_car = new LockVersion(5, 3, 7, 1, 1);
    private int protocolType;
    private int protocolVersion;
    private int scene;
    private int groupId;
    private int orgId;
    private boolean showAdminKbpwdFlag;
    private String logoUrl = "";

    protected LockVersion(Parcel in) {
        protocolType = in.readInt();
        protocolVersion = in.readInt();
        scene = in.readInt();
        groupId = in.readInt();
        orgId = in.readInt();
        showAdminKbpwdFlag = in.readByte() != 0;
        logoUrl = in.readString();
    }

    public LockVersion(int protocolType, int protocolVersion, int scene, int groupId, int orgId) {
        this.protocolType = protocolType;
        this.protocolVersion = protocolVersion;
        this.scene = scene;
        this.groupId = groupId;
        this.orgId = orgId;
    }

    public static LockVersion getLockVersion(int lockType) {
        switch (lockType) {
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(protocolType);
        dest.writeInt(protocolVersion);
        dest.writeInt(scene);
        dest.writeInt(groupId);
        dest.writeInt(orgId);
        dest.writeByte((byte) (showAdminKbpwdFlag ? 1 : 0));
        dest.writeString(logoUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LockVersion> CREATOR = new Creator<LockVersion>() {
        @Override
        public LockVersion createFromParcel(Parcel in) {
            return new LockVersion(in);
        }

        @Override
        public LockVersion[] newArray(int size) {
            return new LockVersion[size];
        }
    };

    public int getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(int protocolType) {
        this.protocolType = protocolType;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public int getScene() {
        return scene;
    }

    public void setScene(int scene) {
        this.scene = scene;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public boolean isShowAdminKbpwdFlag() {
        return showAdminKbpwdFlag;
    }

    public void setShowAdminKbpwdFlag(boolean showAdminKbpwdFlag) {
        this.showAdminKbpwdFlag = showAdminKbpwdFlag;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String toGson() {
        return GsonUtil.toJson(this);
    }

    @Override
    public String toString() {
        return "LockVersion{" +
                "protocolType=" + protocolType +
                ", protocolVersion=" + protocolVersion +
                ", scene=" + scene +
                ", groupId=" + groupId +
                ", orgId=" + orgId +
                ", showAdminKbpwdFlag=" + showAdminKbpwdFlag +
                ", logoUrl='" + logoUrl + '\'' +
                '}';
    }
}
