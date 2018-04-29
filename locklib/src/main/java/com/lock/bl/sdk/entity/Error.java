//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lock.bl.sdk.entity;

public enum Error {
    SUCCESS(0, "success", "成功"),
    LOCK_CRC_CHECK_ERROR(1, "CRC error", "CRC校验出错"),
    LOCK_NO_PERMISSION(2, "Not administrator, has no permission.", "非管理员没有操作权限"),
    LOCK_ADMIN_CHECK_ERROR(3, "Wrong administrator password.", "管理员校验出错"),
    LOCK_IS_IN_SETTING_MODE(5, "In setting mode", "当前处于设置状态"),
    LOCK_NOT_EXIST_ADMIN(6, "no administrator", "锁中不存在管理员"),
    LOCK_IS_IN_NO_SETTING_MODE(7, "Non-setting mode", "添加管理处于非设置模式"),
    LOCK_DYNAMIC_PWD_ERROR(8, "invalid dynamic code", "动态码错误"),
    LOCK_NO_POWER(10, "Running out of battery", "电池快没电了"),
    LOCK_INIT_KEYBOARD_FAILED(11, "init keyboard falied", "初始化(重置)键盘密码出错"),
    LOCK_KEY_FLAG_INVALID(13, "ekey invalid", "电子钥匙失效flag过低"),
    LOCK_USER_TIME_EXPIRED(14, "expired", "电子钥匙过期"),
    LOCK_PASSWORD_LENGTH_INVALID(15, "password length invalid", "密码长度无效"),
    LOCK_SUPER_PASSWORD_IS_SAME_WITH_DELETE_PASSWORD(16, "super password is same with delete password", "管理员密码与删除密码相同"),
    LOCK_USER_TIME_INEFFECTIVE(17, "Haven\'t become effective", "电子钥匙尚未生效"),
    LOCK_USER_NOT_LOGIN(18, "user not login", "未登录,无操作权限"),
    LOCK_OPERATE_FAILED(19, "Failed. Undefined error.", "操作失败 未定义的错误"),
    LOCK_PASSWORD_EXIST(20, "password already exists.", "添加的密码已经存在"),
    LOCK_PASSWORD_NOT_EXIST(21, "password not exists.", "删除或者修改的密码不存在"),
    LOCK_NO_FREE_MEMORY(22, "out of memory", "存储空间不足(比如添加密码时，超过存储容量)"),
    IC_CARD_NOT_EXIST(24, "Card number not exist.", "卡号不存在"),
    FR_NOT_EXIST(26, "Finger print not exist.", "指纹不存在"),
    INVALID_COMMAND(27, "Invalid command", "无效指令"),
    INVALID_VENDOR(29, "invalid vendor", "无效特殊字符串,默认SCIENER,有些客户使用专业字符串"),
    AES_PARSE_ERROR(48, "aes parse error", "AES解析错误"),
    KEY_INVALID(49, "key invalid", "钥匙无效(锁可能被重置),解密失败"),
    LOCK_NOT_SUPPORT_CHANGE_PASSCODE(96, "Passcode can\'t be changed for this lock currently.", "此锁当前还不支持修改密码。");

    private String lockname;
    private String lockmac;
    private int errorCode;
    private String errorMsg;
    private String description;
    private byte command;
    private long date;

    private Error(int errorCode, String description, String errorMsg) {
        this.errorCode = errorCode;
        this.description = description;
        this.errorMsg = errorMsg;
    }

    public static Error getInstance(int errorCode) {
        switch(errorCode) {
            case 0:
                return SUCCESS;
            case 1:
                return LOCK_CRC_CHECK_ERROR;
            case 2:
                return LOCK_NO_PERMISSION;
            case 3:
                return LOCK_ADMIN_CHECK_ERROR;
            case 4:
            case 9:
            case 12:
            case 23:
            case 25:
            case 28:
            default:
                return null;
            case 5:
                return LOCK_IS_IN_SETTING_MODE;
            case 6:
                return LOCK_NOT_EXIST_ADMIN;
            case 7:
                return LOCK_IS_IN_NO_SETTING_MODE;
            case 8:
                return LOCK_DYNAMIC_PWD_ERROR;
            case 10:
                return LOCK_NO_POWER;
            case 11:
                return LOCK_INIT_KEYBOARD_FAILED;
            case 13:
                return LOCK_KEY_FLAG_INVALID;
            case 14:
                return LOCK_USER_TIME_EXPIRED;
            case 15:
                return LOCK_PASSWORD_LENGTH_INVALID;
            case 16:
                return LOCK_SUPER_PASSWORD_IS_SAME_WITH_DELETE_PASSWORD;
            case 17:
                return LOCK_USER_TIME_INEFFECTIVE;
            case 18:
                return LOCK_USER_NOT_LOGIN;
            case 19:
                return LOCK_OPERATE_FAILED;
            case 20:
                return LOCK_PASSWORD_EXIST;
            case 21:
                return LOCK_PASSWORD_NOT_EXIST;
            case 22:
                return LOCK_NO_FREE_MEMORY;
            case 24:
                return IC_CARD_NOT_EXIST;
            case 26:
                return FR_NOT_EXIST;
            case 27:
                return INVALID_COMMAND;
            case 29:
                return INVALID_VENDOR;
        }
    }

    public String getLockname() {
        return this.lockname;
    }

    public void setLockname(String lockname) {
        this.lockname = lockname;
    }

    public String getLockmac() {
        return this.lockmac;
    }

    public void setLockmac(String lockmac) {
        this.lockmac = lockmac;
    }

    public String getCommand() {
        return this.command >= 65 && this.command <= 90?String.valueOf((char)this.command):String.format("%#x", new Object[]{Byte.valueOf(this.command)});
    }

    public void setCommand(byte command) {
        this.command = command;
    }

    public long getDate() {
        return this.date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public String getDescription() {
        return this.description;
    }

    public String getErrorCode() {
        return String.format("%#x", new Object[]{Integer.valueOf(this.errorCode)});
    }
}
