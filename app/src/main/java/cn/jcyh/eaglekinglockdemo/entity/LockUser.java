package cn.jcyh.eaglekinglockdemo.entity;

/**
 * Created by jogger on 2018/4/26.
 */

public class LockUser {
    private String access_token;
    private int openid;
    private int expires_in;
    private String scope;
    private String refresh_token;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getOpenid() {
        return openid;
    }

    public void setOpenid(int openid) {
        this.openid = openid;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    @Override
    public String toString() {
        return "LockUser{" +
                "access_token='" + access_token + '\'' +
                ", openid=" + openid +
                ", expires_in=" + expires_in +
                ", scope='" + scope + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                '}';
    }
}
