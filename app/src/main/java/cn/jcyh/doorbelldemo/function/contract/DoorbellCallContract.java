package cn.jcyh.doorbelldemo.function.contract;

import android.graphics.Bitmap;

import cn.jcyh.doorbelldemo.base.BaseModel;
import cn.jcyh.doorbelldemo.base.BaseView;
import cn.jcyh.doorbelldemo.base.IPresenter;


/**
 * Created by jogger on 2018/7/3.
 */
public interface DoorbellCallContract {
    interface Model extends BaseModel {

    }

    interface View extends BaseView {
        void getVideoBitmap(Bitmap bitmap);
    }

    interface Presenter extends IPresenter<View, Model> {
    }
}
