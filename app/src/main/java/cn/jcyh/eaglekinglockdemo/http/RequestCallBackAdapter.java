package cn.jcyh.eaglekinglockdemo.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.jcyh.eaglekinglockdemo.utils.Timber;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by jogger on 2018/1/18.
 */

public class RequestCallBackAdapter implements Consumer<Response<ResponseBody>> {
    private OnHttpRequestCallback<String> mOnHttpRequestCallback;

    public RequestCallBackAdapter(OnHttpRequestCallback<String> onHttpRequestCallback) {
        mOnHttpRequestCallback = onHttpRequestCallback;
    }


    @Override
    public void accept(Response<ResponseBody> response) throws Exception {
        try {
            String result = response.body().string();
            Timber.e("---------res:" + result);
            if (mOnHttpRequestCallback != null) {
                int code = 0;
                try {
                    JSONObject json = new JSONObject(result);
                    code = json.getInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (code == 200) {
                    mOnHttpRequestCallback.onSuccess(result);
                } else {
                    mOnHttpRequestCallback.onFailure(code);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}