package cn.jcyh.doorbelldemo.function.contract;


import cn.jcyh.doorbelldemo.base.BaseModel;
import cn.jcyh.doorbelldemo.base.BaseView;
import cn.jcyh.doorbelldemo.base.IPresenter;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;

/**
 * Created by jogger on 2018/8/6.
 */
public interface UserInfoContract {
    interface Model extends BaseModel {

        void modifyPwd(String userID, String oldPwd, String newPwd, OnHttpRequestListener<Boolean> listener);
    }

    interface View extends BaseView {
        String getNickname();

        void modifyNicknameSuccess();

        void modifyAvatorSuccess(String newPath);

        String getOldPwd();

        String getNewPwd();

        void modifyPwdSuccess();
    }

    interface Presenter extends IPresenter<View, Model> {
        void modifyNickname();

        void modifyAvator(String filePath);

        void modifyPwd();
    }
}
