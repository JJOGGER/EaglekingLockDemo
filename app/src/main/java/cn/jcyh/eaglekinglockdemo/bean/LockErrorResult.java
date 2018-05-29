package cn.jcyh.eaglekinglockdemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.ttlock.bl.sdk.entity.Error;

/**
 * Created by jogger on 2018/5/12.
 * 锁返回码
 */

public class LockErrorResult implements Parcelable {
    private int errorCode;
    private String errorMsg;
    private String errorDesc;

    public LockErrorResult() {
    }
    public LockErrorResult(Error error) {
    }
    protected LockErrorResult(Parcel in) {
        errorCode = in.readInt();
        errorMsg = in.readString();
        errorDesc = in.readString();
    }

    public static final Creator<LockErrorResult> CREATOR = new Creator<LockErrorResult>() {
        @Override
        public LockErrorResult createFromParcel(Parcel in) {
            return new LockErrorResult(in);
        }

        @Override
        public LockErrorResult[] newArray(int size) {
            return new LockErrorResult[size];
        }
    };

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    @Override
    public String toString() {
        return "LockErrorResult{" +
                "errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                ", errorDesc='" + errorDesc + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(errorCode);
        dest.writeString(errorMsg);
        dest.writeString(errorDesc);
    }
}
