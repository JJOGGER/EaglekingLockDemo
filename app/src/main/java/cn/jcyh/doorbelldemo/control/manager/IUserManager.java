package cn.jcyh.doorbelldemo.control.manager;

import cn.jcyh.nimlib.entity.UserRequest;

/**
 * Created by jogger on 2018/8/7.
 */
public interface IUserManager {
    UserRequest getUser();

    void setUser(UserRequest userRequest);

//    List<NimUserInfo> getBindDoorbells();
//
//    List<NimUserInfo> getBindDoorbellsSync();

    void setIsAutoLogin(boolean isAutoLogin);

    boolean isAutoLogin();

    void logout();
}
