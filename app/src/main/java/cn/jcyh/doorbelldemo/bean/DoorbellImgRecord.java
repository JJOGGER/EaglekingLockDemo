package cn.jcyh.doorbelldemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jogger on 2018/2/12.
 */

public class DoorbellImgRecord implements Parcelable {
    private String filePath;
    private String date;
    private boolean isSelect;

    public DoorbellImgRecord() {
    }

    protected DoorbellImgRecord(Parcel in) {
        filePath = in.readString();
        date = in.readString();
        isSelect = in.readByte() != 0;
    }

    public static final Creator<DoorbellImgRecord> CREATOR = new Creator<DoorbellImgRecord>() {
        @Override
        public DoorbellImgRecord createFromParcel(Parcel in) {
            return new DoorbellImgRecord(in);
        }

        @Override
        public DoorbellImgRecord[] newArray(int size) {
            return new DoorbellImgRecord[size];
        }
    };

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }


    @Override
    public String toString() {
        return "DoorbellImgRecord{" +
                "filePath='" + filePath + '\'' +
                ", date='" + date + '\'' +
                ", isSelect=" + isSelect +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filePath);
        dest.writeString(date);
        dest.writeByte((byte) (isSelect ? 1 : 0));
    }
}
