package cn.jcyh.doorbelldemo.base;

/**
 * Created by jogger on 2018/6/8.
 */
public interface IPresenter<V extends BaseView, M extends BaseModel> {
    void attachView(V view);

    void detachView();

    void onResume();

    void onPause();

    void onRestart();

    M attacheModel();
}
