package cn.jcyh.eaglekinglockdemo.http;

import java.io.IOException;

import cn.jcyh.eaglekinglockdemo.config.Config;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jogger on 2018/3/6.
 */

 class AddCookiesInterceptor implements Interceptor {

     AddCookiesInterceptor() {
        super();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        final Request.Builder builder = chain.request().newBuilder();
//        String cookie = UserSharePreUtil.getInstance(context).getString(ConstantUtil.APP_COOKIE, "");
//        添加cookie
//        builder.addHeader("cookie", cookie);
        builder.addHeader("LockUser-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)");
        builder.addHeader("UserRequest-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)");
        Config.HeaderConfig headerConfig = Config.getHeaderConfig();
        builder.addHeader("AppKey", headerConfig.getAppkey());
        builder.addHeader("Nonce", headerConfig.getNonce());
        builder.addHeader("Timestamp", headerConfig.getTimestamp());
        builder.addHeader("Sign", headerConfig.getSign());
        return chain.proceed(builder.build());
    }
}
