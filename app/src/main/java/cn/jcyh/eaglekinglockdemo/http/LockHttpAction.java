package cn.jcyh.eaglekinglockdemo.http;

import android.content.Context;

import cn.jcyh.eaglekinglockdemo.config.LockConfig;
import cn.jcyh.eaglekinglockdemo.control.ControlCenter;
import cn.jcyh.eaglekinglockdemo.entity.LockKey;
import cn.jcyh.eaglekinglockdemo.entity.LockKeyboardPwd;
import cn.jcyh.eaglekinglockdemo.entity.LockPwdRecord;
import cn.jcyh.eaglekinglockdemo.entity.LockUser;
import cn.jcyh.eaglekinglockdemo.entity.LoginData;
import cn.jcyh.eaglekinglockdemo.entity.SyncData;
import cn.jcyh.locklib.entity.FR;
import cn.jcyh.locklib.entity.ICCard;
import cn.jcyh.locklib.entity.LockRecord;
import cn.jcyh.locklib.util.DigitUtil;

import static java.lang.System.currentTimeMillis;


/**
 * Created by jogger on 2018/1/10.网络请求
 */
@SuppressWarnings("unchecked")
public class LockHttpAction extends BaseHttpAction {
    private static LockHttpAction sHttpAction;
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static String ACCESS_TOKEN;
    private static Context sContext;

    private LockHttpAction(Context context) {
        super(context);
        sContext = context.getApplicationContext();
        CLIENT_ID = LockConfig.CLIENT_ID;
        CLIENT_SECRET = LockConfig.CLIENT_SECRET;
    }

    @Override
    public ILockHttpRequest getHttpRequest(LockRequestService lockRequestService) {
        return new LockLockHttpRequest(lockRequestService);
    }

    @Override
    public String getBaseUrl() {
        return LockRequestService.BASE_URL;
    }

    public static LockHttpAction getHttpAction(Context context) {
        if (sHttpAction == null) {
            synchronized (LockHttpAction.class) {
                if (sHttpAction == null) {
                    sHttpAction = new LockHttpAction(context);
                }
            }
        }
        ACCESS_TOKEN = ControlCenter.getControlCenter(context).getAccessToken();
        return sHttpAction;
    }

    public void regist(String userName, String pwd, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.regist(userName, DigitUtil.getMD5(pwd),listener);
    }

    public void auth(String userName, String pwd, String directUri, final OnHttpRequestCallback<LockUser> listener) {
        mHttpRequest.auth(CLIENT_ID, CLIENT_SECRET, userName, "password", pwd, directUri, listener);
    }

    public void auth(String userName, String pwd, final OnHttpRequestCallback<LoginData> listener) {
        mHttpRequest.auth(userName, pwd, listener);
    }

    public void login(String accoount, String pwd, final OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.login(accoount, pwd, listener);
    }

    public void syncData(long lastUpdateDate, String accessToken, final OnHttpRequestCallback<SyncData> listener) {
        mHttpRequest.syncData(CLIENT_ID, lastUpdateDate, accessToken, listener);
    }

    public void initLock(LockKey lockKey, final OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.initLock(CLIENT_ID, lockKey, listener);
    }

    /**
     * @param lockId           锁ID
     * @param receiverUsername 接收方用户名
     * @param startDate        有效期开始时间
     * @param endDate          有效期结束时间（1为单次）
     * @param remarks          备注
     */
    public void sendKey(int lockId, String receiverUsername, long startDate, long endDate, String remarks, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.sendKey(CLIENT_ID, ACCESS_TOKEN, lockId, receiverUsername, startDate, endDate, remarks, currentTimeMillis(), listener);
    }

    /**
     * 生成密码
     *
     * @param lockId          锁ID
     *                        keyboardPwdVersion 键盘密码版本, 三代锁的密码版本为4
     * @param keyboardPwdType 键盘密码类型
     * @param startDate       有效期开始时间
     * @param endDate         有效期结束时间
     */
    public void getPwd(int lockId, int keyboardPwdType, long startDate, long endDate, OnHttpRequestCallback<LockKeyboardPwd> listener) {
        mHttpRequest.getPwd(CLIENT_ID, ACCESS_TOKEN, lockId, 4, keyboardPwdType, startDate, endDate, currentTimeMillis(), listener);
    }

    /**
     * 自定义密码
     */
    public void customPwd(int lockId, String keyboardPwd, long startDate, long endDate, OnHttpRequestCallback<LockKeyboardPwd> listener) {
        mHttpRequest.customPwd(CLIENT_ID, ACCESS_TOKEN, lockId, keyboardPwd, startDate, endDate, 1, currentTimeMillis(), listener);
    }

    /**
     * 获取锁钥匙列表
     *
     * @param pageNo   页码，从1开始
     * @param pageSize 每页数量，默认20，最大100
     */
    public void getLockKeys(int lockId, int pageNo, int pageSize, OnHttpRequestCallback<HttpResult<LockKey>> listener) {
        mHttpRequest.getLockKeys(CLIENT_ID, ACCESS_TOKEN, lockId, pageNo, pageSize, currentTimeMillis(), listener);
    }

    /**
     * 删除所有普通钥匙
     */
    public void delAllKeys(int lockId, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.delAllKeys(CLIENT_ID, ACCESS_TOKEN, lockId, currentTimeMillis(), listener);
    }

    /**
     * 删除钥匙
     * 删除普通用户钥匙和删除管理员钥匙都调用该接口。
     * 删除管理员钥匙时，会同时删除该锁的所有普通钥匙和密码。
     */
    public void delKey(int keyId, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.delKey(CLIENT_ID, ACCESS_TOKEN, keyId, currentTimeMillis(), listener);
    }

    /**
     * 重置钥匙
     */
    public void resetKey(int lockId, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.resetKey(CLIENT_ID, ACCESS_TOKEN, lockId, currentTimeMillis(), listener);
    }

    public void getPwdsByLock(int lockId, int pageNo, int pageSize, OnHttpRequestCallback<HttpResult<LockPwdRecord>> listener) {
        mHttpRequest.getPwdsByLock(CLIENT_ID, ACCESS_TOKEN, lockId, pageNo, pageSize, currentTimeMillis(), listener);
    }

    /**
     * 重置密码
     *
     * @param lockId    锁id
     * @param pwdInfo   密码数据，用于生成密码，SDK提供
     * @param timestamp 时间戳，用于初始化密码数据，SDK提供
     */
    public void resetPwd(int lockId, String pwdInfo, long timestamp, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.resetPwd(CLIENT_ID, ACCESS_TOKEN, lockId, pwdInfo, timestamp, currentTimeMillis(), listener);
    }

    /**
     * 冻结钥匙
     */
    public void freezeKey(int keyId, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.freezeKey(CLIENT_ID, ACCESS_TOKEN, keyId, System.currentTimeMillis(), listener);
    }

    /**
     * 取消冻结钥匙
     */
    public void unFreezeKey(int keyId, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.unFreezeKey(CLIENT_ID, ACCESS_TOKEN, keyId, System.currentTimeMillis(), listener);
    }

    /**
     * 授权钥匙
     */
    public void authKeyUser(int lockId, int keyId, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.authKeyUser(CLIENT_ID, ACCESS_TOKEN, lockId, keyId, System.currentTimeMillis(), listener);
    }

    /**
     * 解除授权钥匙
     */
    public void unAuthKeyUser(int lockId, int keyId, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.unAuthKeyUser(CLIENT_ID, ACCESS_TOKEN, lockId, keyId, System.currentTimeMillis(), listener);
    }

    /**
     * 修改锁名称
     */
    public void lockRename(int lockId, String lockAlias, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.lockRename(CLIENT_ID, ACCESS_TOKEN, lockId, lockAlias, System.currentTimeMillis(), listener);
    }

    /**
     * 修改管理员开门密码
     */
    public void changeAdminKeyboardPwd(int lockId, String password, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.changeAdminKeyboardPwd(CLIENT_ID, ACCESS_TOKEN, lockId, password, System.currentTimeMillis(), listener);
    }

    /**
     * 获取ic卡列表
     */
    public void getICs(int lockId, int pageNo, int pageSize, OnHttpRequestCallback<HttpResult<ICCard>> listener) {
        mHttpRequest.getICs(CLIENT_ID, ACCESS_TOKEN, lockId, pageNo, pageSize, System.currentTimeMillis(), listener);
    }

    /**
     * 删除ic
     * 删除方式:1-通过APP走蓝牙删除，2-通过网关走WIFI删除；不传则默认1, 必需先通过APP蓝牙删除后调用该接口，如果锁有连接网关，则可以传2，直接调用该接口删除。
     */
    public void deleteIC(int lockId, int cardId, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.deleteIC(CLIENT_ID, ACCESS_TOKEN, lockId, cardId, 1, System.currentTimeMillis(), listener);
    }

    public void addIC(int lockId, String cardNumber, long startDate, long endDate, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.addIC(CLIENT_ID, ACCESS_TOKEN, lockId, cardNumber, startDate, endDate, 1, System.currentTimeMillis(), listener);
    }

    /**
     * 清空ic数据
     */
    public void clearICs(int lockId, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.clearICs(CLIENT_ID, ACCESS_TOKEN, lockId, System.currentTimeMillis(), listener);
    }


    /**
     * 获取指纹列表
     */
    public void getFingerprints(int lockId, int pageNo, int pageSize, OnHttpRequestCallback<HttpResult<FR>> listener) {
        mHttpRequest.getFingerprints(CLIENT_ID, ACCESS_TOKEN, lockId, pageNo, pageSize, System.currentTimeMillis(), listener);
    }

    /**
     * 删除指纹
     */
    public void deleteFingerprint(int lockId, int fingerprintId, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.deleteFingerprint(CLIENT_ID, ACCESS_TOKEN, lockId, fingerprintId, 1, System.currentTimeMillis(), listener);
    }

    public void addFingerprint(int lockId, String cardNumber, long startDate, long endDate, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.addFingerprint(CLIENT_ID, ACCESS_TOKEN, lockId, cardNumber, startDate, endDate, 1, System.currentTimeMillis(), listener);
    }

    public void clearFingerprints(int lockId, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.clearFingerprints(CLIENT_ID, ACCESS_TOKEN, lockId, System.currentTimeMillis(), listener);
    }

    /**
     * 上传锁记录
     */
    public void uploadLockRecords(int lockId, String records, OnHttpRequestCallback<Boolean> listener) {
        mHttpRequest.uploadLockRecords(CLIENT_ID, ACCESS_TOKEN, lockId, records, System.currentTimeMillis(), listener);
    }

    /**
     * 获取锁记录
     */
    public void getLockRecords(int lockId, long startDate, long endDate, int pageNo, int pageSize, OnHttpRequestCallback<HttpResult<LockRecord>> listener) {
        mHttpRequest.getLockRecords(CLIENT_ID, ACCESS_TOKEN, lockId, startDate, endDate, pageNo, pageSize, System.currentTimeMillis(), listener);
    }
}
