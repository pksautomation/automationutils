package com.pksautomation.utils.v2;

import org.apache.commons.io.IOUtils;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

public class EncryptionUtils   {

    private LoggerUtils loggerUtils;
    private boolean isCreatedEncryptedFile = false;

    public EncryptionUtils(Config testConfig) {
        init(testConfig);
    }

	private void init(Config testConfig) {
		loggerUtils = new LoggerUtils(testConfig);
	}

	public EncryptionUtils() {
		init(Config.getConfig());
    }

    private static String encrypt(Config testConfig, String str) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, EncryptionUtils.getKey(testConfig));
        byte[] utf8 = str.getBytes(StandardCharsets.UTF_8);
        byte[] enc = cipher.doFinal(utf8);
        return Base64.getEncoder().encodeToString(enc);
    }

    private static String decrypt(Config testConfig, String str) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, EncryptionUtils.getKey(testConfig));
        byte[] dec = Base64.getDecoder().decode(str.getBytes());
        byte[] utf8 = cipher.doFinal(dec);
        return new String(utf8, StandardCharsets.UTF_8);
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

    public String aesEncryption(Config testConfig, String stringValueToBeEncrypted) {
        String value = null;
        try {
            value = EncryptionUtils.encrypt(testConfig, stringValueToBeEncrypted);
        } catch (Exception e) {
            loggerUtils.logComment("Fail to encrypt.....");
            loggerUtils.logFailureException(e);
        }
        return value;
    }

    public String aesDecryption(Config testConfig, String encryptedString) {
        String str = null;
        try {
            str = EncryptionUtils.decrypt(testConfig, encryptedString);
        } catch (Exception e) {
            loggerUtils.logComment("Fail to decrypt.....");
            loggerUtils.logFailureException(e);
        }
        return str;
    }

    /**
     * Encrypts given Json Body
     *
     * @param Json body String
     * @return Encrypted json String
     */
    public String encryptJsonForInnoCred(String jsonString) {
        PyString result = null;
        try {
            createEncryptedCredentialsFile();
            Properties properties = new Properties();
            String pythonConfigPath = System.getProperty("user.dir") + "/src/test/resources/";
            properties.setProperty("python.path", pythonConfigPath);
            PythonInterpreter.initialize(System.getProperties(), properties, new String[]{""});
            PythonInterpreter pi = new PythonInterpreter();
            pi.exec("from EncryptCreds import to_java_encode_json");
            pi.set("string", new PyString(jsonString));
            pi.exec("result = to_java_encode_json(string)");
            pi.exec("print(result)");
            result = (PyString) pi.get("result");
        } catch (Exception e) {
            loggerUtils.logException("Exception in JsonEncryption : ", e, false);
        }
        return result.toString();

    }

    public void createEncryptedCredentialsFile() {
        if (isCreatedEncryptedFile)
            return;
        InputStream is = APIHelper.class.getClassLoader().getResourceAsStream("PythonFile/EncryptCreds.py");
        String pythonFilePath = System.getProperty("user.dir") + "/src/test/resources/EncryptCreds.py";
        File file = new File(pythonFilePath);
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            IOUtils.copy(is, outputStream);
            isCreatedEncryptedFile = true;
        } catch (Exception e) {
            loggerUtils.logFailureException(e);
        }
    }

}
