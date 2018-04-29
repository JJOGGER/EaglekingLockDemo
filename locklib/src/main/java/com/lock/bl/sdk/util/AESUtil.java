//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lock.bl.sdk.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    private static boolean DBG = true;

    public AESUtil() {
    }

    public static byte[] aesEncrypt(byte[] source, byte[] aesKeyArray) {
        byte[] encryptResArray = null;

        try {
            LogUtil.e("aesKey:" + DigitUtil.byteArrayToHexString(aesKeyArray), true);
            encryptResArray = Encrypt(source, aesKeyArray, aesKeyArray);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return encryptResArray;
    }

    public static byte[] aesDecrypt(byte[] source, byte[] aesKeyArray) {
        byte[] decryptResArray = null;

        try {
            LogUtil.e("aesKey:" + DigitUtil.byteArrayToHexString(aesKeyArray), true);
            decryptResArray = Decrypt(source, aesKeyArray, aesKeyArray);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return decryptResArray;
    }

    private static byte[] Encrypt(byte[] source, byte[] key, byte[] IV) throws Exception {
        if(key == null) {
            LogUtil.e("Key为空null", DBG);
            return null;
        } else if(key.length != 16) {
            LogUtil.e("Key长度不是16位", DBG);
            return null;
        } else if(IV.length != 16) {
            LogUtil.e("IV向量不是16位", DBG);
            return null;
        } else {
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(IV);
            cipher.init(1, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(source);
            return encrypted;
        }
    }

    private static byte[] Decrypt(byte[] source, byte[] key, byte[] IV) throws Exception {
        try {
            if(key == null) {
                LogUtil.e("Key为空null", DBG);
                return null;
            } else if(key.length != 16) {
                LogUtil.e("Key长度不是16位", DBG);
                return null;
            } else {
                SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                IvParameterSpec iv = new IvParameterSpec(IV);
                cipher.init(2, skeySpec, iv);

                try {
                    byte[] e = cipher.doFinal(source);
                    return e;
                } catch (Exception var8) {
                    System.out.println(var8.toString());
                    LogUtil.w("source=" + DigitUtil.byteArrayToHexString(source), DBG);
                    LogUtil.w("key=" + DigitUtil.byteArrayToHexString(key), DBG);
                    return null;
                }
            }
        } catch (Exception var9) {
            System.out.println(var9.toString());
            return null;
        }
    }
}
