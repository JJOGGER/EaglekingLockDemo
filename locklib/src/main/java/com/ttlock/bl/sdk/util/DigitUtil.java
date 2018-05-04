//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ttlock.bl.sdk.util;

import android.util.Base64;

import com.scaf.android.client.CodecUtils;
import com.ttlock.bl.sdk.constant.Feature;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DigitUtil {
    private static final boolean DBG = true;

    public DigitUtil() {
    }

    public static byte generateRandomByte() {
        boolean randomByte = false;

        byte randomByte1;
        do {
            randomByte1 = (byte)((int)(Math.random() * 128.0D));
        } while(randomByte1 == 0);

        return randomByte1;
    }

    public static byte[] generateDynamicPassword(int length) {
        byte[] bytes = new byte[length];
        bytes[0] = 48;

        for(int i = 1; i < length; ++i) {
            double r = Math.random() * 10.0D;
            if(r >= 10.0D) {
                r = 9.0D;
            }

            bytes[i] = (byte)((int)(r + 48.0D));
        }

        return bytes;
    }

    public static String getMacString(byte[] macBytes) {
        String value = "";

        for(int i = macBytes.length - 1; i >= 0; --i) {
            String sTemp = Integer.toHexString(255 & macBytes[i]);
            if(sTemp.length() == 1) {
                sTemp = "0" + sTemp;
            }

            value = value + sTemp + ":";
        }

        value = value.substring(0, value.lastIndexOf(":"));
        return value.toUpperCase();
    }

    public static byte[] shortToByteArray(short value) {
        byte[] shortByteArray = new byte[2];

        for(int i = 1; i >= 0; --i) {
            shortByteArray[i] = (byte)value;
            value = (short)(value >> 8);
        }

        return shortByteArray;
    }

    public static byte[] integerToByteArray(int value) {
        byte[] bytes = new byte[4];
        byte[] offset = new byte[]{24, 16, 8, 0};

        for(int i = 0; i < 4; ++i) {
            bytes[i] = (byte)(value >> offset[i]);
        }

        return bytes;
    }

    public static byte[] longToByteArrayWithLen(long value, int len) {
        byte[] bytes = new byte[len];

        for(int i = len - 1; i >= 0; --i) {
            bytes[i] = (byte)((int)value);
            value >>= 8;
        }

        return bytes;
    }

    public static short byteArrayToShort(byte[] array) {
        short value = (short)(array[1] & 255 | array[0] << 8);
        return value;
    }

    public static String byteToHex(byte value) {
        String hex = Integer.toHexString(value & 255);
        if(hex.length() == 1) {
            hex = "0" + hex;
        }

        return hex;
    }

    public static long fourBytesToLong(byte[] data) {
        long res = 0L;
        res |= (long)(data[0] << 24) & 4278190080L;
        res |= (long)(data[1] << 16 & 16711680);
        res |= (long)(data[2] << 8 & '\uff00');
        res |= (long)(data[3] & 255);
        LogUtil.d("res:" + res, true);
        return res;
    }

    public static long bytesToLong(byte[] data) {
        long res = 0L;
        int len = data.length;
        int tmpLen = len;

        for(long[] mask = new long[]{255L, 65280L, 16711680L, 4278190080L, 1095216660480L, 280375465082880L, 71776119061217280L, -72057594037927936L}; tmpLen-- > 0; res |= (long)data[len - tmpLen - 1] << (int)((long)tmpLen * 8L) & mask[tmpLen]) {
            ;
        }

        return res;
    }

    public static long sixBytesToLong(byte[] data) {
        long res = 0L;
        res |= (long)data[0] << 40 & 280375465082880L;
        res |= (long)data[1] << 32 & 1095216660480L;
        res |= (long)data[2] << 24 & 4278190080L;
        res |= (long)data[3] << 16 & 16711680L;
        res |= (long)data[4] << 8 & 65280L;
        res |= (long)data[5] & 255L;
        LogUtil.d("res:" + res, true);
        return res;
    }

    public static String byteArrayToHexString(byte[] array) {
        if(array == null) {
            return null;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('[');
            if(array == null || array.length == 0) {
                stringBuilder.append(']');
            }

            for(int i = 0; i < array.length; ++i) {
                stringBuilder.append(byteToHex(array[i]));
                stringBuilder.append(',');
            }

            stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "]");
            return stringBuilder.toString();
        }
    }

    public static byte[] stringDividerByDotToByteArray(String source) {
        byte[] originalBytes = source.getBytes();
        String originalString = new String(Base64.decode(originalBytes, 0));
        LogUtil.d("originalString:" + originalString, true);
        String[] strings = originalString.split(",");
        int len = strings.length;
        byte[] resBytes = new byte[len];

        try {
            for(int e = 0; e < len; ++e) {
                resBytes[e] = Integer.valueOf(strings[e]).byteValue();
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return resBytes;
    }

    public static String formateDateFromLong(long time, String formate) {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat(formate);
        return formatter.format(date);
    }

    public static byte[] convertTimeToByteArray(String time) {
        int len = time.length();
        len /= 2;
        byte[] values = new byte[len];

        for(int i = 0; i < len; ++i) {
            String string = time.substring(i * 2, i * 2 + 2);
            values[i] = Byte.valueOf(string).byteValue();
        }

        return values;
    }

    public static byte[] encodeDefaultPassword(byte[] sourceBytes) {
        if(sourceBytes != null && sourceBytes.length != 0) {
            byte[] bytes = CodecUtils.encode(sourceBytes);
            return bytes;
        } else {
            LogUtil.d("sourceBytes=" + sourceBytes, true);
            return sourceBytes;
        }
    }

    public static byte[] decodeDefaultPassword(byte[] sourceBytes) {
        if(sourceBytes != null && sourceBytes.length != 0) {
            LogUtil.d("sourceBytes=" + byteArrayToHexString(sourceBytes), true);
            byte[] bytes = CodecUtils.decode(sourceBytes);
            return bytes;
        } else {
            LogUtil.d("sourceBytes=" + sourceBytes, true);
            return sourceBytes;
        }
    }

    public static String getUnlockPassword(long passwordFromLock, long localPassword) {
        long result = (passwordFromLock + localPassword) % 2000000000L;
        return String.valueOf(result);
    }

    public static String getUnlockPwd_new(long passwordFromLock, long localPassword) {
        int result = (int)(passwordFromLock + localPassword);
        return String.valueOf(result);
    }

    public static String getCheckTable() {
        LinkedHashSet set = new LinkedHashSet();

        while(set.size() < 10) {
            set.add(Integer.valueOf(getRandomIntegerByUpperBound(10)));
        }

        StringBuilder stringBuilder = new StringBuilder();
        Iterator iterator = set.iterator();

        while(iterator.hasNext()) {
            stringBuilder.append(iterator.next());
        }

        return stringBuilder.toString();
    }

    public static int getRandomIntegerByUpperBound(int upperBound) {
        Random random = new Random();
        return random.nextInt(upperBound);
    }

    public static byte[] convertCodeAndKeyToByteArray(int code, String secretKey) {
        LogUtil.d("code:" + code, true);
        LogUtil.d("secretKey:" + secretKey, true);
        byte[] res = new byte[]{(byte)(code >> 4), (byte)(code << 4), 0, 0, 0, 0};
        long secretKeyLong = Long.valueOf(secretKey).longValue();
        int offset = 32;
        res[1] = (byte)((int)((long)res[1] | secretKeyLong >> offset));

        for(int i = 2; i < 6; ++i) {
            offset -= 8;
            res[i] = (byte)((int)(secretKeyLong >> offset));
        }

        return res;
    }

    public static String generatePwdByLength(int length) {
        char[] pwd = new char[length];
        Random random = new Random();

        for(int i = 0; i < length; ++i) {
            pwd[i] = (char)(48 + random.nextInt(10));
        }

        return new String(pwd);
    }

    public static int generateRandomIntegerByUpperBound(int upperBound) {
        Random random = new Random();
        return random.nextInt(upperBound);
    }

    public static String generatePwdByType(int pwdType) {
        char[] pwd = new char[6];
        pwd[0] = (char)(48 + pwdType);
        Random random = new Random();

        for(int i = 1; i < 6; ++i) {
            pwd[i] = (char)(48 + random.nextInt(9));
        }

        return new String(pwd);
    }

    public static byte[] convertAesKeyStrToBytes(String aesKeyStr) {
        String[] aesKeyStrings = aesKeyStr.split(",");
        int len = aesKeyStrings.length;
        byte[] aesKey = new byte[len];

        for(int i = 0; i < len; ++i) {
            aesKey[i] = Integer.valueOf(aesKeyStrings[i], 16).byteValue();
        }

        return aesKey;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static String encodeAesKey(byte[] aeskey) {
        if(aeskey != null && aeskey.length > 0) {
            String aeskeyStr = byteArrayToHexString(aeskey);
            aeskeyStr = aeskeyStr.substring(1, aeskeyStr.length() - 1);
            return aeskeyStr;
        } else {
            return null;
        }
    }

    public static String encodeLockData(String originalStr) {
        byte[] encodedBytes = encodeDefaultPassword(originalStr.getBytes());
        String encodedString = byteArrayToStringDividerByDot(encodedBytes);
        return Base64.encodeToString(encodedString.getBytes(), 2);
    }

    public static String decodeLockData(String originalStr) {
        String decodeData = "";

        try {
            decodeData = new String(decodeDefaultPassword(stringDividerByDotToByteArray(originalStr)));
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return decodeData;
    }

    public static String byteArrayToStringDividerByDot(byte[] array) {
        if(array == null) {
            return "null";
        } else if(array.length == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder(array.length * 4);
            sb.append(array[0]);

            for(int i = 1; i < array.length; ++i) {
                sb.append(",");
                sb.append(array[i]);
            }

            return sb.toString();
        }
    }

    public static String getMD5(String source) {
        StringBuffer buf = new StringBuffer("");

        try {
            MessageDigest var6 = MessageDigest.getInstance("MD5");
            var6.update(source.getBytes());
            byte[] e = var6.digest();

            for(int offset = 0; offset < e.length; ++offset) {
                int i = e[offset];
                if(i < 0) {
                    i += 256;
                }

                if(i < 16) {
                    buf.append("0");
                }

                buf.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException var61) {
            var61.printStackTrace();
        }

        return buf.toString();
    }

    public static String generateKeyboardPwd_Json(Queue<String> pwdList) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        StringBuilder one_day_pwd = new StringBuilder((String)pwdList.poll());

        for(int two_days_pwd = 1; two_days_pwd < 300; ++two_days_pwd) {
            one_day_pwd.append(",");
            one_day_pwd.append((String)pwdList.poll());
        }

        LogUtil.d("one_day_pwd:" + one_day_pwd, true);
        jsonObject.put("oneDay", one_day_pwd);
        jsonObject.put("oneDaySequence", 0);
        StringBuilder var11 = new StringBuilder((String)pwdList.poll());

        for(int three_days_pwd = 1; three_days_pwd < 150; ++three_days_pwd) {
            var11.append(",");
            var11.append((String)pwdList.poll());
        }

        jsonObject.put("twoDays", var11);
        jsonObject.put("twoDaysSequence", 0);
        LogUtil.d("twoDays:" + var11, true);
        StringBuilder var12 = new StringBuilder((String)pwdList.poll());

        for(int four_days_pwd = 1; four_days_pwd < 100; ++four_days_pwd) {
            var12.append(",");
            var12.append((String)pwdList.poll());
        }

        jsonObject.put("threeDays", var12);
        jsonObject.put("threeDaysSequence", 0);
        LogUtil.d("three_days_pwd:" + var12, true);
        StringBuilder var13 = new StringBuilder((String)pwdList.poll());

        for(int five_days_pwd = 1; five_days_pwd < 100; ++five_days_pwd) {
            var13.append(",");
            var13.append((String)pwdList.poll());
        }

        jsonObject.put("fourDays", var13);
        jsonObject.put("fourDaysSequence", 0);
        StringBuilder var14 = new StringBuilder((String)pwdList.poll());

        for(int six_days_pwd = 1; six_days_pwd < 50; ++six_days_pwd) {
            var14.append(",");
            var14.append((String)pwdList.poll());
        }

        jsonObject.put("fiveDays", var14);
        jsonObject.put("fiveDaysSequence", 0);
        StringBuilder var15 = new StringBuilder((String)pwdList.poll());

        for(int seven_days_pwd = 1; seven_days_pwd < 50; ++seven_days_pwd) {
            var15.append(",");
            var15.append((String)pwdList.poll());
        }

        jsonObject.put("sixDays", var15);
        jsonObject.put("sixDaysSequence", 0);
        StringBuilder var16 = new StringBuilder((String)pwdList.poll());

        for(int ten_minutes_pwd = 1; ten_minutes_pwd < 50; ++ten_minutes_pwd) {
            var16.append(",");
            var16.append((String)pwdList.poll());
        }

        jsonObject.put("sevenDays", var16);
        jsonObject.put("sevenDaysSequence", 0);
        StringBuilder var17 = new StringBuilder((String)pwdList.poll());

        for(int i = 1; i < 100; ++i) {
            var17.append(",");
            var17.append((String)pwdList.poll());
        }

        jsonObject.put("tenMinutes", var17);
        jsonObject.put("tenMinutesSequence", 0);
        LogUtil.d("ten_minutes_pwd:" + var17, true);
        return jsonObject.toString();
    }

    public static boolean isSupportPasscode(int feature) {
        return (feature & Feature.PASSCODE) != 0;
    }

    public static boolean isSupportIC(int feature) {
        return (feature & Feature.IC) != 0;
    }

    public static boolean isSupportFingerPrint(int feature) {
        return (feature & Feature.FINGER_PRINT) != 0;
    }

    public static boolean isSupportWristband(int feature) {
        return (feature & Feature.WRIST_BAND) != 0;
    }

    public static boolean isSupportAutoLock(int feature) {
        return (feature & Feature.AUTO_LOCK) != 0;
    }

    public static boolean isSupportModifyPasscode(int feature) {
        return (feature & Feature.MODIFY_PASSCODE_FUNCTION) != 0;
    }

    public static boolean isSupportManualLock(int feature) {
        return (feature & Feature.MANUAL_LOCK) != 0;
    }

    public static boolean isSupportShowPasscode(int feature) {
        return (feature & Feature.PASSWORD_DISPLAY_OR_HIDE) != 0;
    }

    public static boolean isSupportCyclicPasscode(int feature) {
        return (feature & Feature.CYCLIC_PASSWORD) != 0;
    }

    public static boolean isSupportRemoteUnlock(int feature) {
        LogUtil.d("feature:" + byteArrayToHexString(integerToByteArray(feature)), true);
        return (feature & Feature.GATEWAY_UNLOCK) != 0;
    }

    public static boolean isSupportMagnetometer(int feature) {
        return (feature & Feature.MAGNETOMETER) != 0;
    }

    public static boolean isSupportRemoteUnlockSwitch(int feature) {
        LogUtil.d("feature:" + byteArrayToHexString(integerToByteArray(feature)), true);
        return (feature & Feature.CONFIG_GATEWAY_UNLOCK) != 0;
    }
}
