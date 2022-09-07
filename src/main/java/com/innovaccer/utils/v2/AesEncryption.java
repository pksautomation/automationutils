package com.innovaccer.utils.v2;

import com.innovaccer.utils.Config;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class AesEncryption {

    private static String encrypt(Config testConfig, String str) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, AesEncryption.getKey(testConfig));
        byte[] utf8 = str.getBytes("UTF8");
        byte[] enc = cipher.doFinal(utf8);
        return Base64.getEncoder().encodeToString(enc);
    }

    private static String decrypt(Config testConfig, String str) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, AesEncryption.getKey(testConfig));
        byte[] dec = Base64.getDecoder().decode(str.getBytes());
        byte[] utf8 = cipher.doFinal(dec);
        return new String(utf8, "UTF8");
    }

    public static String encryptString(Config testConfig, String str) {
        String value = null;
        try {
            value = AesEncryption.encrypt(testConfig, str);
        } catch (Exception e) {
            testConfig.logComment("Fail to encrypt.....");
            testConfig.logException(e);
        }
        return value;
    }

    public static String decryptString(Config testConfig, String encrypted) {
        String str = null;
        try {
            str = AesEncryption.decrypt(testConfig, encrypted);
        } catch (Exception e) {
            testConfig.logComment("Fail to decrypt.....");
            testConfig.logException(e);
        }
        return str;
    }

    private static SecretKeySpec getKey(Config testConfig) {
        String encryptionKey = testConfig.getRunTimeProperty("encryptionKey");
        String privateKey = testConfig.getRunTimeProperty("privateKey");
        byte[] decodedKey = Base64.getDecoder().decode(decode(encryptionKey, privateKey));
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public static String encode(String s, String key) {
        return base64Encode(xorWithKey(s.getBytes(), key.getBytes()));
    }

    public static String decode(String s, String key) {
        return new String(xorWithKey(base64Decode(s), key.getBytes()));
    }

    private static byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i % key.length]);
        }
        return out;
    }

    private static byte[] base64Decode(String string) {
        return Base64.getDecoder().decode(string);
    }

    private static String base64Encode(byte[] bytes) {
        return Arrays.toString(Base64.getEncoder().encode(bytes));
    }
}
