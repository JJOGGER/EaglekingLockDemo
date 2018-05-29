package cn.jcyh.eaglekinglockdemo.constant;

/**
 * Created by jogger on 2018/5/4.
 */

public class LockConstant {

    private final static String PACKAGE_NAME = "cn.jcyh.eaglekinglockdemo";
    public static final String ACTION_ADD_ADMIN = PACKAGE_NAME + "lock_add_admin";
    public static final String ACTION_UNLOCK = PACKAGE_NAME + "action_unlock";
    public final static String ACTION_BLE_DEVICE = PACKAGE_NAME + ".ACTION_BLE_DEVICE";
    public final static String ACTION_BLE_DISCONNECTED = PACKAGE_NAME + ".ACTION_BLE_DISCONNECTED";
    public static final String ACTION_CUSTOM_PWD = PACKAGE_NAME + "action_custom_pwd";
    public static final String ACTION_RESET_LOCK = PACKAGE_NAME + "action_reset_lock";
    public static final String ACTION_RESET_PWD = PACKAGE_NAME + "action_reset_pwd";//重置密码
    public static final String ACTION_RESET_KEY = PACKAGE_NAME + "action_reset_key";
    public static final String ACTION_SET_ADMIN_PWD = PACKAGE_NAME + "action_set_admin_pwd";//管理员键盘密码
    public static final String ACTION_LOCK_IC_CARD = PACKAGE_NAME + "action_lock_ic_card";
    public static final String TYPE_ADD_IC_CARD = "type_add_ic_card";
    public static final String TYPE_DELETE_IC_CARD = "type_delete_ic_card";
    public static final String TYPE_MODIFY_IC_CARD = "type_modify_ic_card";
    public static final String TYPE_CLEAR_IC_CARD = "type_clear_ic_card";
    public static final String ACTION_LOCK_FINGERPRINT = PACKAGE_NAME + "action_lock_fingerprint";
    public static final String TYPE_ADD_FINGERPRINT="type_add_fingerprint";
    public static final String TYPE_CLEAR_FINGERPRINT="type_clear_fingerprint";
    public static final String TYPE_DELETE_FINGERPRINT="type_delete_fingerprint";
    public static final String TYPE_MODIFY_FINGERPRINT = "type_modify_fingerprint";
    public static final String TYPE_COLLECTION_FINGERPRINT="type_collection_fingerprint";

    public static final String ACTION_LOCK_GET_TIME=PACKAGE_NAME+"action_lock_get_time";
    public static final String ACTION_LOCK_SYNC_TIME=PACKAGE_NAME+"action_lock_sync_time";
    public final static String DEVICE = "device";
    public final static String ERROR_MSG = "error_msg";
}
