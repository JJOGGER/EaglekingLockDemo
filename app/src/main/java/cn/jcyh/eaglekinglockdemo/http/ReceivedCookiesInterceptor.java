package cn.jcyh.eaglekinglockdemo.http;

import android.content.Context;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by jogger on 2018/3/6.
 */

public class ReceivedCookiesInterceptor implements Interceptor {
    private Context context;

    public ReceivedCookiesInterceptor(Context context) {
        super();
        this.context = context.getApplicationContext();

    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Response originalResponse = chain.proceed(chain.request());
        //这里获取请求返回的cookie
        List<String> headers = originalResponse.headers("Set-Cookie");
        if (headers != null) {
            final StringBuffer cookieBuffer = new StringBuffer();
            //最近在学习RxJava,这里用了RxJava的相关API大家可以忽略,用自己逻辑实现即可.大家可以用别的方法保存cookie数据
            for (int i = 0; i < headers.size(); i++) {
                cookieBuffer.append(headers.get(i));
            }
            String cookie = cookieBuffer.toString().split(";")[0];
//            if (!TextUtils.isEmpty(cookie))
//                UserSharePreUtil.getInstance(context).setString(ConstantUtil.APP_COOKIE, cookie);
        }

        return originalResponse;
    }
}
