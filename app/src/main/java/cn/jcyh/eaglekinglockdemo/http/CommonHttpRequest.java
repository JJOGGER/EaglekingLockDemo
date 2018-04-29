package cn.jcyh.eaglekinglockdemo.http;

import com.google.gson.Gson;
import com.lock.bl.sdk.util.Timber;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.jcyh.eaglekinglockdemo.bean.HttpResult;
import cn.jcyh.eaglekinglockdemo.bean.SyncData;
import cn.jcyh.eaglekinglockdemo.bean.User;
import cn.jcyh.eaglekinglockdemo.config.Config;
import cn.jcyh.eaglekinglockdemo.utils.DigitUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by jogger on 2018/4/14.网络请求处理
 */
@SuppressWarnings("unchecked")
class CommonHttpRequest implements IHttpRequest {
    private final RequestService mRequestService;
    private Gson mGson;

    CommonHttpRequest(RequestService requestService) {
        mRequestService = requestService;
        mGson = new Gson();
    }

    @Override
    public void login(String account, String pwd, OnHttpRequestCallback listener) {

    }

    @Override
    public void auth(String userName, String pwd, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> auth = mRequestService.auth(Config.CLIENT_ID, Config.CLIENT_SECRET, "password", userName, DigitUtil.getMD5(pwd), Config.REDIRECT_URI);
        enqueue3(auth, User.class, listener);
    }

    @Override
    public void syncData(long lastUpdateDate, String accessToken, OnHttpRequestCallback listener) {
        Observable<Response<ResponseBody>> syncData = mRequestService.syncData(Config.CLIENT_ID, accessToken, lastUpdateDate, System.currentTimeMillis());
        enqueue3(syncData, SyncData.class, listener);
    }

    /**
     * 不需要解析实体类的回调
     */
    private void enqueue(Observable<Response<ResponseBody>> call, final
    OnHttpRequestCallback<Boolean> listener) {
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<ResponseBody>>() {
                    @Override
                    public void accept(Response<ResponseBody> response) throws Exception {
                        try {
                            String result = response.body().string();
                            Timber.e("---------res:" + result);
                            if (listener != null) {
                                int code = 0;
                                try {
                                    JSONObject json = new JSONObject(result);
                                    code = json.getInt("code");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (code == 200) {
                                    listener.onSuccess(true);
                                } else {
                                    listener.onFailure(code);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (listener != null)
                            listener.onFailure(-1);
                    }
                });
    }


    /**
     * 自动解析
     */

    private <T> void enqueue2(Observable<HttpResult<T>> call, final OnHttpRequestCallback<T>
            listener) {
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HttpResult<T>>() {
                    @Override
                    public void accept(HttpResult<T> tHttpResult) throws Exception {
                        if (listener != null) {
                            Timber.e("--------httpresult:" + tHttpResult);
                            if (tHttpResult.getCode() == 200)
                                listener.onSuccess(tHttpResult.getData());
                            else
                                listener.onFailure(tHttpResult.getCode());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e("----------onError:" + throwable);
                        if (listener != null)
                            listener.onFailure(-1);
                    }
                });
    }

    private <T> void enqueue3(final Observable<Response<ResponseBody>> call, final Class<T> clazz, final OnHttpRequestCallback<T>
            listener) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Consumer<Response<ResponseBody>>() {
                    @Override
                    public void accept(Response<ResponseBody> response) throws Exception {
                String result = response.body().string();
                        Timber.e("----------result:" + result);
                        Class<T> type;
//                        type = (Class<T>) ((ParameterizedType) clazz.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                        if (listener != null) {
                            Timber.e("--------result:" + result);
                            listener.onSuccess(mGson.fromJson(result, clazz));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Timber.e("----------onError3:" + throwable+mRequestService);
                        if (listener != null)
                            listener.onFailure(-1);
                    }
                });
    }
//        }).start();
//
//    }
//    private <T> void enqueue3(final Observable<Response<ResponseBody>> call, final Class<T> clazz, final OnHttpRequestCallback<T>
//            listener) {
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
//                call.subscribeOn(Schedulers.io());
//                call.observeOn(AndroidSchedulers.mainThread());
//                call.subscribe(new Consumer<Response<ResponseBody>>() {
//                    @Override
//                    public void accept(Response<ResponseBody> response) throws Exception {
//                        String result = response.body().string();
//                        Timber.e("----------result:" + result);
//                        Class<T> type;
////                        type = (Class<T>) ((ParameterizedType) clazz.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//                        if (listener != null) {
//                            Timber.e("--------result:" + result);
//                            listener.onSuccess(mGson.fromJson(result, clazz));
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Timber.e("----------onError3:" + throwable);
//                        if (listener != null)
//                            listener.onFailure(-1);
//                    }
//                });
//            }
////        }).start();
////
////    }
}
