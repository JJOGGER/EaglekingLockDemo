package cn.jcyh.eaglekinglockdemo.http;


import java.util.List;

import cn.jcyh.nimlib.entity.AddBindUserRequest;
import cn.jcyh.nimlib.entity.BindDoorbellUser;
import cn.jcyh.nimlib.entity.Doorbell;
import cn.jcyh.nimlib.entity.DoorbellAuthCode;
import cn.jcyh.nimlib.entity.DoorbellParam;
import cn.jcyh.nimlib.entity.DoorbellRecordRequest;
import cn.jcyh.nimlib.entity.DoorbellRecords;
import cn.jcyh.nimlib.entity.DoorbellRequest;
import cn.jcyh.nimlib.entity.GetDoorbellParamRequest;
import cn.jcyh.nimlib.entity.HttpResult;
import cn.jcyh.nimlib.entity.JsonDataRequest;
import cn.jcyh.nimlib.entity.ModifyNickNameRequest;
import cn.jcyh.nimlib.entity.RemoveBindUserRequest;
import cn.jcyh.nimlib.entity.SetAdminRequest;
import cn.jcyh.nimlib.entity.TokenRequest;
import cn.jcyh.nimlib.entity.TokenResponse;
import cn.jcyh.nimlib.entity.UserDoorbellRequest;
import cn.jcyh.nimlib.entity.UserRequest;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by jogger on 2018/1/10.
 */

public interface DoorbellRequestService {
    String BASE_URL = "http://api.9cyh.cn/";//http://api.9cyh.cn/

    @POST("jcvserver/user/token")
    Observable<HttpResult<TokenResponse>> getToken(@Body TokenRequest tokenRequest);

    //获取绑定猫眼的用户列表
    @POST("jcvserver/user/getDeviceUsers")
    Observable<HttpResult<List<BindDoorbellUser>>> getBindDoorbellUsers(@Body DoorbellRequest doorbellRequest);

    @POST("jcvserver/admin/setAdmin")
    Observable<Response<ResponseBody>> setAdminUnbindDoorbell(@Body SetAdminRequest setAdminRequest);

    //获取绑定猫眼的用户列表setAdminUnbindDoorbell
    @POST("/jcvserver/admin/unBindDevice")
    Observable<Response<ResponseBody>> adminUnbindDoorbell(@Body UserDoorbellRequest userDoorbellRequest);

    /**
     * 删除绑定猫眼的用户
     */
    @POST("jcvserver/admin/removeBindUser")
    Observable<Response<ResponseBody>> removeBindUser(@Body RemoveBindUserRequest removeBindUserRequest);

    /**
     * 添加用户绑定猫眼
     */
    @POST("jcvserver/admin/addBindUser")
    Observable<Response<ResponseBody>> addUserDeviceBind(@Body AddBindUserRequest addBindUserRequest);

    /**
     * 获取猫眼授权码
     */
    @FormUrlEncoded
    @POST("jcvserver/admin/getDeviceAuthCode")
    Observable<HttpResult<DoorbellAuthCode>> getDeviceAuthCode(@Body UserDoorbellRequest userDoorbellRequest);

    //绑定猫眼设备
    @POST("jcvserver/user/bindDevice")
    Observable<Response<ResponseBody>> bindDoorbell(@Body UserDoorbellRequest userDoorbellRequest);

    //解绑猫眼设备
    @POST("/jcvserver/user/unBindDevice")
    Observable<Response<ResponseBody>> unDoorbell(@Body UserDoorbellRequest userDoorbellRequest);


    //获取推送信息
    @POST("jcvserver/push/list")
    Observable<HttpResult<DoorbellRecords>> getDoorbellMsgRecords(@Body DoorbellRecordRequest doorbellRecordRequest);

    //删除推送信息
    @POST("jcvserver/push/delete")
    Observable<Response<ResponseBody>> deleteDoorbellMsgRecords(@Body JsonDataRequest jsonDataRequest);

    //获取猫眼设置
    @POST("jcvserver/cateye/get")
    Observable<HttpResult<DoorbellParam>> getDoorbellParams(@Body GetDoorbellParamRequest getDoorbellParamRequest);

    //修改用户昵称 用于设备显示
    @POST("jcvserver/user/modifyNickName")
    Observable<Response<ResponseBody>> modifyNickName(@Body ModifyNickNameRequest modifyNickNameRequest);

    //获取用户下所有的设备列表
    @POST("jcvserver/user/getDevicesByUserId")
    Observable<HttpResult<List<Doorbell>>> getDoorbells(@Body UserRequest userRequest);

}
