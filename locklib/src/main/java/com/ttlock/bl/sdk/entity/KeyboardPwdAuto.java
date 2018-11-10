//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.entity;

public class KeyboardPwdAuto {
    private int currentIndex;
    private String fourKeyboardPwdList;
    private String timeControlTb;
    private String position;
    private String checkDigit;

    private KeyboardPwdAuto() {
    }

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public String getFourKeyboardPwdList() {
        return this.fourKeyboardPwdList;
    }

    public void setFourKeyboardPwdList(String fourKeyboardPwdList) {
        this.fourKeyboardPwdList = fourKeyboardPwdList;
    }

    public String getTimeControlTb() {
        return this.timeControlTb;
    }

    public void setTimeControlTb(String timeControlTb) {
        this.timeControlTb = timeControlTb;
    }

    public String getPosition() {
        return this.position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCheckDigit() {
        return this.checkDigit;
    }

    public void setCheckDigit(String checkDigit) {
        this.checkDigit = checkDigit;
    }
}
