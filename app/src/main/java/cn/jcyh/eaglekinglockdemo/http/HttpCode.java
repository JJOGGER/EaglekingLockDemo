package cn.jcyh.eaglekinglockdemo.http;

/**
 * Created by jogger on 2018/1/18.
 */

public class HttpCode {
    public static final int REQUEST_ERROR = 1001;//数据库操作失败
    public static final int USER_REGISTED = 1002;//已注册
    public static final int VALI_CODE_ERROR = 1003;//验证码错误
    public static final int VALI_CODE_TIME_OUT = 1004;//验证码超时
    public static final int VALI_CODE_SEND_FAILED = 1005;//验证码发送失败
    public static final int DATA_NO_EXIST = 1006;//未注册
    public static final int USER_PWD_ERROR=1007;//密码错误
    public static final int USER_NO_EXISTS=1008;//用户不存在
}
