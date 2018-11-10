package cn.jcyh.doorbelldemo.control.manager.impl;


import cn.jcyh.doorbelldemo.control.manager.IUserManager;
import cn.jcyh.nimlib.entity.UserRequest;
import cn.jcyh.nimlib.utils.GsonUtil;
import cn.jcyh.utils.ConstantUtil;
import cn.jcyh.utils.SPUtil;

/**
 * Created by jogger on 2018/8/7.
 */
public class UserManager implements IUserManager {
    private static UserRequest sUserRequest;

    @Override
    public UserRequest getUser() {
        if (sUserRequest == null) {
            sUserRequest = GsonUtil.fromJson(SPUtil.getInstance().getString(ConstantUtil.USER_INFO, ""), UserRequest.class);
        }
        return sUserRequest;
    }

    @Override
    public void setUser(UserRequest userRequest) {
        sUserRequest = userRequest;
        SPUtil.getInstance().put(ConstantUtil.USER_INFO, GsonUtil.toJson(userRequest));
    }

    @Override
    public void setIsAutoLogin(boolean isAutoLogin) {
        SPUtil.getInstance().put(ConstantUtil.AUTO_LOGIN, isAutoLogin);
    }

    @Override
    public boolean isAutoLogin() {
        return SPUtil.getInstance().getBoolean(ConstantUtil.AUTO_LOGIN, false);
    }

    @Override
    public void logout() {
        setIsAutoLogin(false);
        getUser().setAccessToken(null);
        setUser(getUser());
    }
}
