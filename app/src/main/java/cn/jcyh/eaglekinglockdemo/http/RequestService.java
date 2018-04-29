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

    /**
     * 同步钥匙数据 APP第一次同步数据，不需要传lastUpdateDate，服务端会返回全量的钥匙数据。
     * @param clientId 注册时分配的app_id
     * @param accessToken 访问令牌
     * @param lastUpdateDate 最近同步时间(最后一次调用该接口，服务端返回的)，不传则返回全量的钥匙数据。
     * @param date 当前时间（毫秒数）
     * @return
     */
    @FormUrlEncoded
    @POST("/v3/key/syncData")
    Observable<Response<ResponseBody>> syncData(@Field("clientId") String clientId,
                                  @Field("accessToken") String accessToken,
                                  @Field("lastUpdateDate") long lastUpdateDate,
                                  @Field("date") long date);
}
