package cn.jcyh.eaglekinglockdemo.http;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by jogger on 2018/1/10.
 */

public interface RequestService {
    String ANYCHAT_URL = "http://ihomecn.rollupcn.com";
    String BASE_URL = "https://api.ttlock.com.cn";

    @FormUrlEncoded
    @POST("/oauth2/token")
    Observable<Response<ResponseBody>> auth(@Field("client_id") String client_id,
                                            @Field("client_secret") String client_secret,
                                            @Field("grant_type") String grant_type,
                                            @Field("username") String username,
                                            @Field("password") String password,
                                            @Field("redirect_uri") String redirect_uri
                                            );
}
