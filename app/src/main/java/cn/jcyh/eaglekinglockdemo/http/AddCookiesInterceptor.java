package cn.jcyh.eaglekinglockdemo.http;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jogger on 2018/3/6.
 */

 class AddCookiesInterceptor implements Interceptor {
    private Context context;

     AddCookiesInterceptor(Context context) {
        super();
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        final Request.Builder builder = chain.request().newBuilder();
//        String cookie = UserSharePreUtil.getInstance(context).getString(ConstantUtil.APP_COOKIE, "");
//        添加cookie
//        builder.addHeader("cookie", cookie);
        builder.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)");
        return chain.proceed(builder.build());
    }
}
