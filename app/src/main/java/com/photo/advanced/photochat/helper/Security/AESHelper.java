package com.photo.advanced.photochat.helper.Security;

import android.util.Base64;

import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESHelper {

    private byte[] keyValue;

    public AESHelper() {
    }

    public AESHelper(byte[] keyValue) {
        this.keyValue = keyValue;
    }

    public void setKeyValue(byte[] keyValue) {
        this.keyValue = keyValue;
    }

    public String encrypt(byte[] data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data);
        //String encryptedValue = new BASE64Encoder().encode(encVal);
        return Base64.encodeToString(encVal,Base64.URL_SAFE);
    }

    public byte[] decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);

        byte[] decValue = c.doFinal(Base64.decode(encryptedData,Base64.URL_SAFE));
        return decValue;
    }

    private Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, "AES");
        return key;
    }
}
