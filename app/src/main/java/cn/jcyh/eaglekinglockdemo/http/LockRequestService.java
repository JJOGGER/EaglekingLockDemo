package cn.jcyh.eaglekinglockdemo.http;

import cn.jcyh.eaglekinglockdemo.entity.AddKeyboardPwdData;
import cn.jcyh.eaglekinglockdemo.entity.AuthKeyData;
import cn.jcyh.eaglekinglockdemo.entity.HttpResult;
import cn.jcyh.eaglekinglockdemo.entity.InitRequestData;
import cn.jcyh.eaglekinglockdemo.entity.KeyControlData;
import cn.jcyh.eaglekinglockdemo.entity.KeyboardPwdGetData;
import cn.jcyh.eaglekinglockdemo.entity.LoginData;
import cn.jcyh.eaglekinglockdemo.entity.RegistRequestData;
import cn.jcyh.eaglekinglockdemo.entity.SendKeyData;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by jogger on 2018/1/10.
 */

public interface LockRequestService {
    //    String BASE_URL = "https://api.ttlock.com.cn";api.sciener.cn
//    String BASE_URL = "http://lock.9cyh.cn";
    String BASE_URL = "http://192.168.0.199:8020/api/";
//    String BASE_URL = "https://api.sciener.cn";


    @FormUrlEncoded
    @POST("v3/user/register")
    Observable<Response<ResponseBody>> regist(@Field("clientId") String clientId,
                                              @Field("clientSecret") String clientSecret,
                                              @Field("username") String username,
                                              @Field("password") String password,
                                              @Field("date") long date);

    @POST("User/Register")
    Observable<Response<ResponseBody>> regist(@Body RegistRequestData registRequestData);

    @FormUrlEncoded
    @POST("oauth2/token")
    Observable<Response<ResponseBody>> auth(@Field("client_id") String client_id,
                                            @Field("client_secret") String client_secret,
                                            @Field("grant_type") String grant_type,
                                            @Field("username") String username,
                                            @Field("password") String password,
                                            @Field("redirect_uri") String redirect_uri
    );

    @POST("User/Login")
    Observable<HttpResult<LoginData>> auth(@Body RegistRequestData registRequestData
    );

    /**
     * 同步钥匙数据 APP第一次同步数据，不需要传lastUpdateDate，服务端会返回全量的钥匙数据。
     *
     * @param clientId       注册时分配的app_id
     * @param accessToken    访问令牌
     * @param lastUpdateDate 最近同步时间(最后一次调用该接口，服务端返回的)，不传则返回全量的钥匙数据。
     * @param date           当前时间（毫秒数）
     * @return
     */
    @FormUrlEncoded
    @POST("v3/key/syncData")
    Observable<Response<ResponseBody>> syncData(@Field("clientId") String clientId,
                                                @Field("accessToken") String accessToken,
                                                @Field("lastUpdateDate") long lastUpdateDate,
                                                @Field("date") long date);

    /**
     * 锁初始化接口将锁相关的数据在服务端做初始化，同时会为调用该接口的用户生成一把管理员钥匙。
     * 锁初始化成功后，管理员钥匙拥有者就可以给其他用户发送普通钥匙或是发送密码了。
     */
    @FormUrlEncoded
    @POST("v3/lock/init")
    Observable<Response<ResponseBody>> initLock(@Field("clientId") String clientId,
                                                @Field("accessToken") String accessToken,
                                                @Field("lockName") String lockName,
                                                @Field("lockAlias") String lockAlias,
                                                @Field("lockMac") String lockMac,
                                                @Field("lockKey") String lockKey,
                                                @Field("lockFlagPos") int lockFlagPos,
                                                @Field("aesKeyStr") String aesKeyStr,
                                                @Field("lockVersion") String lockVersion,
                                                @Field("adminPwd") String adminPwd,
                                                @Field("noKeyPwd") String noKeyPwd,
                                                @Field("deletePwd") String deletePwd,
                                                @Field("pwdInfo") String pwdInfo,
                                                @Field("timestamp") long timestamp,
                                                @Field("specialValue") int specialValue,
                                                @Field("timezoneRawOffset") int timezoneRawOffset,
                                                @Field("modelNum") String modelNum,
                                                @Field("hardwareRevision") String hardwareRevision,
                                                @Field("firmwareRevision") String firmwareRevision,
                                                @Field("date") long date);

    @POST("Lock/init")
    Observable<Response<ResponseBody>> initLock(@Body InitRequestData initRequestData);

    @FormUrlEncoded
    @POST("v3/key/send")
    Observable<Response<ResponseBody>> sendKey(@Field("clientId") String clientId,
                                               @Field("accessToken") String accessToken,
                                               @Field("lockId") int lockId,
                                               @Field("receiverUsername") String receiverUsername,
                                               @Field("startDate") long startDate,
                                               @Field("endDate") long endDate,
                                               @Field("remarks") String remarks,
                                               @Field("date") long date);

    @POST("Lock/key/send")
    Observable<Response<ResponseBody>> sendKey(@Body SendKeyData sendKeyData);

    @POST("Lock/keyboardPwd/get")
    Observable<Response<ResponseBody>> getPwd(@Body KeyboardPwdGetData keyboardPwdGetData);

    @POST("Lock/keyboardPwd/add")
    Observable<Response<ResponseBody>> customPwd(@Body AddKeyboardPwdData addKeyboardPwdData);

    @FormUrlEncoded
    @POST("v3/lock/listKey")
    Observable<Response<ResponseBody>> getLockKeys(@Field("clientId") String clientId,
                                                   @Field("accessToken") String accessToken,
                                                   @Field("lockId") int lockId,
                                                   @Field("pageNo") int pageNo,
                                                   @Field("pageSize") int pageSize,
                                                   @Field("date") long date);

    @FormUrlEncoded
    @POST("v3/key/delete")
    Observable<Response<ResponseBody>> delKey(@Field("clientId") String clientId,
                                              @Field("accessToken") String accessToken,
                                              @Field("keyId") int keyId,
                                              @Field("date") long date);

    @POST("Lock/key/delete")
    Observable<Response<ResponseBody>> delKey(@Body KeyControlData keyControlData);

    @POST("v3/lock/resetKey")
    Observable<Response<ResponseBody>> resetKey(@Body KeyControlData keyControlData);

    @FormUrlEncoded
    @POST("v3/lock/deleteAllKey")
    Observable<Response<ResponseBody>> delAllKeys(@Field("clientId") String clientId,
                                                  @Field("accessToken") String accessToken,
                                                  @Field("lockId") int lockId,
                                                  @Field("date") long date);

    @FormUrlEncoded
    @POST("v3/lock/listKeyboardPwd")
    Observable<Response<ResponseBody>> getPwdsByLock(@Field("clientId") String clientId,
                                                     @Field("accessToken") String accessToken,
                                                     @Field("lockId") int lockId,
                                                     @Field("pageNo") int pageNo,
                                                     @Field("pageSize") int pageSize,
                                                     @Field("date") long date);

    @FormUrlEncoded
    @POST("v3/lock/resetKeyboardPwd")
    Observable<Response<ResponseBody>> resetKeyboardPwd(@Field("clientId") String clientId,
                                                        @Field("accessToken") String accessToken,
                                                        @Field("lockId") int lockId,
                                                        @Field("pwdInfo") String pwdInfo,
                                                        @Field("timestamp") long timestamp,
                                                        @Field("date") long date);

    @POST("Lock/key/freeze")
    Observable<Response<ResponseBody>> freezeKey(@Body KeyControlData keyControlData);

    @POST("Lock/key/unfreeze")
    Observable<Response<ResponseBody>> unFreezeKey(@Body KeyControlData keyControlData);

    @POST("Lock/key/authorize")
    Observable<Response<ResponseBody>> authKeyUser(@Body AuthKeyData authKeyData);

    @POST("Lock/key/unauthorize")
    Observable<Response<ResponseBody>> unAuthKeyUser(@Body AuthKeyData authKeyData);

    @FormUrlEncoded
    @POST("v3/lock/rename")
    Observable<Response<ResponseBody>> lockRename(@Field("clientId") String clientId,
                                                  @Field("accessToken") String accessToken,
                                                  @Field("lockId") int lockId,
                                                  @Field("lockAlias") String lockAlias,
                                                  @Field("date") long date);

    @FormUrlEncoded
    @POST("v3/lock/changeAdminKeyboardPwd")
    Observable<Response<ResponseBody>> changeAdminKeyboardPwd(@Field("clientId") String clientId,
                                                              @Field("accessToken") String accessToken,
                                                              @Field("lockId") int lockId,
                                                              @Field("password") String password,
                                                              @Field("date") long date);

    @FormUrlEncoded
    @POST("v3/identityCard/list")
    Observable<Response<ResponseBody>> getICs(@Field("clientId") String clientId,
                                              @Field("accessToken") String accessToken,
                                              @Field("lockId") int lockId,
                                              @Field("pageNo") int pageNo,
                                              @Field("pageSize") int pageSize,
                                              @Field("date") long date);

    @FormUrlEncoded
    @POST("v3/identityCard/delete")
    Observable<Response<ResponseBody>> deleteIC(@Field("clientId") String clientId,
                                                @Field("accessToken") String accessToken,
                                                @Field("lockId") int lockId,
                                                @Field("cardId") int cardId,
                                                @Field("deleteType") int deleteType,
                                                @Field("date") long date);

    @FormUrlEncoded
    @POST("v3/identityCard/add")
    Observable<Response<ResponseBody>> addIC(@Field("clientId") String clientId,
                                             @Field("accessToken") String accessToken,
                                             @Field("lockId") int lockId,
                                             @Field("cardNumber") String cardNumber,
                                             @Field("startDate") long startDate,
                                             @Field("endDate") long endDate,
                                             @Field("addType") int addType,
                                             @Field("date") long date);

    @FormUrlEncoded
    @POST("v3/identityCard/clear ")
    Observable<Response<ResponseBody>> clearICs(@Field("clientId") String clientId,
                                                @Field("accessToken") String accessToken,
                                                @Field("lockId") int lockId,
                                                @Field("date") long date);

    @FormUrlEncoded
    @POST("v3/fingerprint/list")
    Observable<Response<ResponseBody>> getFingerprints(@Field("clientId") String clientId,
                                                       @Field("accessToken") String accessToken,
                                                       @Field("lockId") int lockId,
                                                       @Field("pageNo") int pageNo,
                                                       @Field("pageSize") int pageSize,
                                                       @Field("date") long date);

    @FormUrlEncoded
    @POST("v3/fingerprint/delete")
    Observable<Response<ResponseBody>> deleteFingerprint(@Field("clientId") String clientId,
                                                         @Field("accessToken") String accessToken,
                                                         @Field("lockId") int lockId,
                                                         @Field("fingerprintId") int fingerprintId,
                                                         @Field("deleteType") int deleteType,
                                                         @Field("date") long date);

    @FormUrlEncoded
    @POST("v3/fingerprint/add")
    Observable<Response<ResponseBody>> addFingerprint(@Field("clientId") String clientId,
                                                      @Field("accessToken") String accessToken,
                                                      @Field("lockId") int lockId,
                                                      @Field("fingerprintNumber") String fingerprintNumber,
                                                      @Field("startDate") long startDate,
                                                      @Field("endDate") long endDate,
                                                      @Field("addType") int addType,
                                                      @Field("date") long date);

    @FormUrlEncoded
    @POST("v3/fingerprint/clear")
    Observable<Response<ResponseBody>> clearFingerprints(@Field("clientId") String clientId,
                                                         @Field("accessToken") String accessToken,
                                                         @Field("lockId") int lockId,
                                                         @Field("date") long date);

    @FormUrlEncoded
    @POST("v3/lockRecord/upload")
    Observable<Response<ResponseBody>> uploadLockRecords(@Field("clientId") String clientId,
                                                         @Field("accessToken") String accessToken,
                                                         @Field("lockId") int lockId,
                                                         @Field("records") String records,
                                                         @Field("date") long date);

    @FormUrlEncoded
    @POST("v3/lockRecord/list")
    Observable<Response<ResponseBody>> getLockRecords(@Field("clientId") String clientId,
                                                      @Field("accessToken") String accessToken,
                                                      @Field("lockId") int lockId,
                                                      @Field("startDate") long startDate,
                                                      @Field("endDate") long endDate,
                                                      @Field("pageNo") int pageNo,
                                                      @Field("pageSize") int pageSize,
                                                      @Field("date") long date);
}
