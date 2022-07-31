package com.innovaccer.utils;
import java.io.IOException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 
 * @author pramod.singh
 *
 */
public class AesEncrypter {

	@SuppressWarnings("restriction")
	private static String encrypt(Config testConfig,String str) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, AesEncrypter.getKey(testConfig));
		byte[] utf8 = str.getBytes("UTF8");
		byte[] enc = cipher.doFinal(utf8);

		// Encode bytes to base64 to get a string
		return Base64.getEncoder().encodeToString(enc);
	}

	@SuppressWarnings("restriction")
	private static String decrypt(Config testConfig,String str) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, AesEncrypter.getKey(testConfig));
		// Decode base64 to get bytes
		byte[] dec = Base64.getDecoder().decode(str.getBytes());

		byte[] utf8 = cipher.doFinal(dec);

		// Decode using utf-8
		return new String(utf8, "UTF8");
	}

	public static String encryptString(Config testConfig,String str){

		String value = null;
		try {
			value = AesEncrypter.encrypt(testConfig,str);
		} catch (Exception e) {
			testConfig.logComment("Fail to encrypt.....");
			testConfig.logException(e);
		}
		return value;
	}

	public static String decryptString(Config testConfig,String encrypted){
		String str = null;
		try {
			str = AesEncrypter.decrypt(testConfig,encrypted);
		} catch (Exception e) {
			testConfig.logComment("Fail to decrypt.....");
			testConfig.logException(e);
		}
		return str;
	}

	private static SecretKeySpec getKey(Config testConfig){
		String encryptionKey=testConfig.getRunTimeProperty("encryptionKey");
		String privateKey=testConfig.getRunTimeProperty("privateKey");
		byte[] decodedKey = Base64.getDecoder().decode(decode(encryptionKey,privateKey));
		SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 	
		return (SecretKeySpec) secretKey;
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
            out[i] = (byte) (a[i] ^ key[i%key.length]);
        }
        return out;
    }

    private static byte[] base64Decode(String s) {
        try {
            BASE64Decoder d = new BASE64Decoder();
            return d.decodeBuffer(s);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    private static String base64Encode(byte[] bytes) {
        BASE64Encoder enc = new BASE64Encoder();
        return enc.encode(bytes).replaceAll("\\s", "");
    }
}


