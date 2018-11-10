package cn.jcyh.eaglekinglockdemo.entity;

/**
 * Created by jogger on 2018/11/6.
 */
public class RegistRequestData {
    private String userName;
    private String password;

    public RegistRequestData(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
