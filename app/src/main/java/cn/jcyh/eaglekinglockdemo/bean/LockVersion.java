package cn.jcyh.eaglekinglockdemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jogger on 2018/4/27.
 */

public class LockVersion implements Parcelable{
    private int protocolType;
    private int protocolVersion;
    private int scene;
    private int groupId;
    private int orgId;

    protected LockVersion(Parcel in) {
        protocolType = in.readInt();
        protocolVersion = in.readInt();
        scene = in.readInt();
        groupId = in.readInt();
        orgId = in.readInt();
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

    @Override
    public String toString() {
        return "LockVersion{" +
                "protocolType=" + protocolType +
                ", protocolVersion=" + protocolVersion +
                ", scene=" + scene +
                ", groupId=" + groupId +
                ", orgId=" + orgId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(protocolType);
        dest.writeInt(protocolVersion);
        dest.writeInt(scene);
        dest.writeInt(groupId);
        dest.writeInt(orgId);
    }
}
