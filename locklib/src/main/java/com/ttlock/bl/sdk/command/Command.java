//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.command;

import com.google.gson.Gson;
import com.scaf.android.client.CodecUtils;
import com.ttlock.bl.sdk.entity.LockVersion;
import com.ttlock.bl.sdk.util.AESUtil;
import com.ttlock.bl.sdk.util.DigitUtil;
import com.ttlock.bl.sdk.util.LogUtil;

public class Command {
    private static Gson gson = new Gson();
    private static boolean DBG = false;
    public static final byte COMM_INITIALIZATION = 69;
    public static final byte COMM_GET_AES_KEY = 25;
    public static final byte COMM_RESPONSE = 84;
    public static final byte COMM_ADD_ADMIN = 86;
    public static final byte COMM_CHECK_ADMIN = 65;
    public static final byte COMM_SET_ADMIN_KEYBOARD_PWD = 83;
    public static final byte COMM_SET_DELETE_PWD = 68;
    public static final byte COMM_SET_LOCK_NAME = 78;
    public static final byte COMM_SYN_KEYBOARD_PWD = 73;
    public static final byte COMM_CHECK_USER_TIME = 85;
    public static final byte COMM_GET_ALARM_ERRCORD_OR_OPERATION_FINISHED = 87;
    public static final byte COMM_UNLOCK = 71;
    public static final byte COMM_LOCK = 76;
    public static final byte COMM_TIME_CALIBRATE = 67;
    public static final byte COMM_MANAGE_KEYBOARD_PASSWORD = 3;
    public static final byte COMM_GET_VALID_KEYBOARD_PASSWORD = 4;
    public static final byte COMM_GET_OPERATE_LOG = 37;
    public static final byte COMM_CHECK_RANDOM = 48;
    public static final byte COMM_INIT_PASSWORDS = 49;
    public static final byte COMM_READ_PWD_PARA = 50;
    public static final byte COMM_RESET_KEYBOARD_PWD_COUNT = 51;
    public static final byte COMM_GET_LOCK_TIME = 52;
    public static final byte COMM_RESET_LOCK = 82;
    public static final byte COMM_SEARCHE_DEVICE_FEATURE = 1;
    public static final byte COMM_IC_MANAGE = 5;
    public static final byte COMM_FR_MANAGE = 6;
    public static final byte COMM_PWD_LIST = 7;
    public static final byte COMM_SET_WRIST_BAND_KEY = 53;
    public static final byte COMM_AUTO_LOCK_MANAGE = 54;
    public static final byte COMM_READ_DEVICE_INFO = -112;
    public static final byte COMM_ENTER_DFU_MODE = 2;
    public static final byte COMM_SEARCH_BICYCLE_STATUS = 20;
    public static final byte COMM_FUNCTION_LOCK = 88;
    public static final byte COMM_SHOW_PASSWORD = 89;
    public static final byte COMM_CONTROL_REMOTE_UNLOCK = 55;
    public static final byte VERSION_LOCK_V1 = 1;
    public byte[] header;
    public byte protocol_type;
    public byte command;
    public byte encrypt;
    public byte length;
    public byte[] data;
    public byte checksum;
    private static final byte APP_COMMAND = -86;
    static final int ENCRY_OLD = 1;
    static final int ENCRY_AES_CBC = 2;
    private byte sub_version;
    private byte scene;
    public byte[] organization;
    public byte[] sub_organization;
    private boolean mIsChecksumValid;
    private int lockType;

    /**
     * @deprecated
     */
    @Deprecated
    public Command(byte commandType) {
        this.header = new byte[2];
        this.header[0] = 127;
        this.header[1] = 90;
        this.encrypt = DigitUtil.generateRandomByte();
        this.protocol_type = commandType;
        this.data = new byte[0];
        this.length = 0;
        this.generateLockType();
    }

    public Command(LockVersion lockVersion) {
        this.header = new byte[2];
        this.header[0] = 127;
        this.header[1] = 90;
        this.protocol_type = lockVersion.getProtocolType();
        this.sub_version = lockVersion.getProtocolVersion();
        this.scene = lockVersion.getScene();
        this.organization = DigitUtil.shortToByteArray(lockVersion.getGroupId());
        this.sub_organization = DigitUtil.shortToByteArray(lockVersion.getOrgId());
        this.encrypt = -86;
        this.generateLockType();
    }

    public Command(String lockVersionString) {
        LockVersion lockVersion = (LockVersion) gson.fromJson(lockVersionString, LockVersion.class);
        this.header = new byte[2];
        this.header[0] = 127;
        this.header[1] = 90;
        this.protocol_type = lockVersion.getProtocolType();
        this.sub_version = lockVersion.getProtocolVersion();
        this.scene = lockVersion.getScene();
        this.organization = DigitUtil.shortToByteArray(lockVersion.getGroupId());
        this.sub_organization = DigitUtil.shortToByteArray(lockVersion.getOrgId());
        this.encrypt = -86;
        this.generateLockType();
    }

    public Command(int lockType) {
        LockVersion lockVersion = LockVersion.getLockVersion(lockType);
        this.header = new byte[2];
        this.header[0] = 127;
        this.header[1] = 90;
        this.protocol_type = lockVersion.getProtocolType();
        this.sub_version = lockVersion.getProtocolVersion();
        this.scene = lockVersion.getScene();
        this.organization = DigitUtil.shortToByteArray(lockVersion.getGroupId());
        this.sub_organization = DigitUtil.shortToByteArray(lockVersion.getOrgId());
        this.encrypt = -86;
        if (lockType == 2) {
            this.encrypt = DigitUtil.generateRandomByte();
            this.data = new byte[0];
        }

        this.generateLockType();
    }

    public Command(byte[] command) {
        this.header = new byte[2];
        this.header[0] = command[0];
        this.header[1] = command[1];
        this.protocol_type = command[2];

        try {
            if (this.protocol_type >= 5) {
                this.organization = new byte[2];
                this.sub_organization = new byte[2];
                this.sub_version = command[3];
                this.scene = command[4];
                this.organization[0] = command[5];
                this.organization[1] = command[6];
                this.sub_organization[0] = command[7];
                this.sub_organization[1] = command[8];
                this.command = command[9];
                this.encrypt = command[10];
                this.length = command[11];
                this.data = new byte[this.length];
                System.arraycopy(command, 12, this.data, 0, this.length);
            } else {
                this.command = command[3];
                this.encrypt = command[4];
                this.length = command[5];
                this.data = new byte[this.length];
                System.arraycopy(command, 6, this.data, 0, this.length);
            }

            this.checksum = command[command.length - 1];
            byte[] e = new byte[command.length - 1];
            System.arraycopy(command, 0, e, 0, e.length);
            byte checksum = CodecUtils.crccompute(e);
            this.mIsChecksumValid = checksum == this.checksum;
            LogUtil.d("checksum=" + checksum + " this.checksum=" + this.checksum, DBG);
            LogUtil.d("mIsChecksumValid : " + this.mIsChecksumValid, DBG);
            this.generateLockType();
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public void setCommand(byte command) {
        this.command = command;
    }

    public void setCommand(int command) {
        this.command = (byte) command;
    }

    public byte getCommand() {
        return this.command;
    }

    public byte getScene() {
        return this.scene;
    }

    public void setScene(byte scene) {
        this.scene = scene;
    }

    public void setData(byte[] data) {
        this.data = CodecUtils.encodeWithEncrypt(data, this.encrypt);
        this.length = (byte) this.data.length;
    }

    public byte[] getData() {
        byte[] values = CodecUtils.decodeWithEncrypt(this.data, this.encrypt);
        return values;
    }

    public byte[] getData(byte[] aesKeyArray) {
        byte[] values = AESUtil.aesDecrypt(this.data, aesKeyArray);
        return values;
    }

    public void setData(byte[] data, byte[] aesKeyArray) {
        LogUtil.d("data=" + DigitUtil.byteArrayToHexString(data), DBG);
        LogUtil.d("aesKeyArray=" + DigitUtil.byteArrayToHexString(aesKeyArray), DBG);
        this.data = AESUtil.aesEncrypt(data, aesKeyArray);
        this.length = (byte) this.data.length;
    }

    public boolean isChecksumValid() {
        return this.mIsChecksumValid;
    }

    public int getLockType() {
        return this.lockType;
    }

    public void setLockType(int lockType) {
        this.lockType = lockType;
    }

    public String getLockVersionString() {
        short org = DigitUtil.byteArrayToShort(this.organization);
        short sub_org = DigitUtil.byteArrayToShort(this.sub_organization);
        LockVersion lockVersion = new LockVersion(this.protocol_type, this.sub_version, this.scene, org, sub_org);
        return gson.toJson(lockVersion);
    }

    private void generateLockType() {
        if (this.protocol_type == 5 && this.sub_version == 3 && this.scene == 7) {
            this.setLockType(8);
        } else if (this.protocol_type == 10 && this.sub_version == 1) {
            this.setLockType(6);
        } else if (this.protocol_type == 5 && this.sub_version == 3) {
            this.setLockType(5);
        } else if (this.protocol_type == 5 && this.sub_version == 4) {
            this.setLockType(4);
        } else if (this.protocol_type == 5 && this.sub_version == 1) {
            this.setLockType(3);
        } else if (this.protocol_type == 11 && this.sub_version == 1) {
            this.setLockType(7);
        } else if (this.protocol_type == 3) {
            this.setLockType(2);
        }

    }

    public byte[] buildCommand() {
        byte[] commandWithoutChecksum;
        byte[] commandWithChecksum;
        byte checksumJava;
        if (this.protocol_type >= 5) {
            commandWithoutChecksum = new byte[12 + this.length];
            commandWithoutChecksum[0] = this.header[0];
            commandWithoutChecksum[1] = this.header[1];
            commandWithoutChecksum[2] = this.protocol_type;
            commandWithoutChecksum[3] = this.sub_version;
            commandWithoutChecksum[4] = this.scene;
            commandWithoutChecksum[5] = this.organization[0];
            commandWithoutChecksum[6] = this.organization[1];
            commandWithoutChecksum[7] = this.sub_organization[0];
            commandWithoutChecksum[8] = this.sub_organization[1];
            commandWithoutChecksum[9] = this.command;
            commandWithoutChecksum[10] = this.encrypt;
            commandWithoutChecksum[11] = this.length;
            if (this.data != null && this.data.length > 0) {
                System.arraycopy(this.data, 0, commandWithoutChecksum, 12, this.data.length);
            }

            commandWithChecksum = new byte[commandWithoutChecksum.length + 1];
            checksumJava = CodecUtils.crccompute(commandWithoutChecksum);
            System.arraycopy(commandWithoutChecksum, 0, commandWithChecksum, 0, commandWithoutChecksum.length);
            commandWithChecksum[commandWithChecksum.length - 1] = checksumJava;
            LogUtil.d("buildCommand : " + (char) this.command + "-" + String.format("%#x", new Object[]{Byte.valueOf(this.command)}), DBG);
            return commandWithChecksum;
        } else {
            commandWithoutChecksum = new byte[6 + this.length];
            commandWithoutChecksum[0] = this.header[0];
            commandWithoutChecksum[1] = this.header[1];
            commandWithoutChecksum[2] = this.protocol_type;
            commandWithoutChecksum[3] = this.command;
            commandWithoutChecksum[4] = this.encrypt;
            commandWithoutChecksum[5] = this.length;
            if (this.data.length > 0) {
                System.arraycopy(this.data, 0, commandWithoutChecksum, 6, this.data.length);
            }

            commandWithChecksum = new byte[commandWithoutChecksum.length + 1];
            checksumJava = CodecUtils.crccompute(commandWithoutChecksum);
            System.arraycopy(commandWithoutChecksum, 0, commandWithChecksum, 0, commandWithoutChecksum.length);
            commandWithChecksum[commandWithChecksum.length - 1] = checksumJava;
            LogUtil.d("buildCommand : " + (char) this.command, DBG);
            return commandWithChecksum;
        }
    }
}
