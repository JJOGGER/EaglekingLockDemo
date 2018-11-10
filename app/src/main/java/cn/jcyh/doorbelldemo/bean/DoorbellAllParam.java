package cn.jcyh.doorbelldemo.bean;

import cn.jcyh.nimlib.entity.DoorbellModelParam;
import cn.jcyh.nimlib.entity.DoorbellSensorParam;

/**
 * Created by jogger on 2018/8/21.
 */
public class DoorbellAllParam {
    private DoorbellModelParam doorbellModelParam;
    private DoorbellSensorParam doorbellSensorParam;

    public void setDoorbellModelParam(DoorbellModelParam doorbellModelParam) {
        this.doorbellModelParam = doorbellModelParam;
    }

    public void setDoorbellSensorParam(DoorbellSensorParam doorbellSensorParam) {
        this.doorbellSensorParam = doorbellSensorParam;
    }

    @Override
    public String toString() {
        return "DoorbellAllParam{" +
                "doorbellModelParam=" + doorbellModelParam +
                ", doorbellSensorParam=" + doorbellSensorParam +
                '}';
    }

    public DoorbellModelParam getDoorbellModelParam() {
        if (doorbellModelParam == null) {
            doorbellModelParam = new DoorbellModelParam();
        }
        return doorbellModelParam;
    }

    public DoorbellSensorParam getDoorbellSensorParam() {
        if (doorbellSensorParam == null) {
            doorbellSensorParam = new DoorbellSensorParam();
        }
        return doorbellSensorParam;
    }

}
