package com.photo.advanced.photochat.helper.Security;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import com.photo.advanced.photochat.helper.PreferencesHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

public class SecurityHelper {

    private KeyPair keyPair;
    private PrivateKey privateKey;

    public SecurityHelper() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidAlgorithmParameterException {
        String key = PreferencesHelper.getECKey();
        if (!key.equals(PreferencesHelper.defaultString)) {
            return;
        }

        KeyPairGenerator generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC);
        generator.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());

        keyPair = generator.generateKeyPair();
        privateKey = keyPair.getPrivate();

        PreferencesHelper.setECKey(Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.DEFAULT));

        KeyFactory keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_EC);
        PrivateKey qwe = null;
        try {
            qwe = keyFactory.generatePrivate(new ECPrivateKeySpec(((ECPrivateKey) keyPair.getPrivate()).getS(), ((ECPrivateKey) keyPair.getPrivate()).getParams()));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        //todo: send public key to server
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public SecretKey generateSharedSecret(PublicKey publicKey) {
        try {
            KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH", "BC");
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(publicKey, true);

            SecretKey key = keyAgreement.generateSecret("AES");
            return key;
        } catch (InvalidKeyException | NoSuchAlgorithmException
                | NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /*
        public String[] encryptAESKey(byte[] AESkey, PublicKey toUserRSAPublicKey) throws CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException, KeyStoreException, NoSuchPaddingException, InvalidKeyException {


            Cipher inputCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

            inputCipher.init(Cipher.ENCRYPT_MODE, toUserRSAPublicKey);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inputCipher);
            cipherOutputStream.write(AESkey);
            cipherOutputStream.close();
            String toUserEncryptedKey = Base64.encodeToString(outputStream.toByteArray(), Base64.URL_SAFE);

            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyStore.PrivateKeyEntry privateKeyEntry =
                    (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
            inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());
            ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream2 = new CipherOutputStream(outputStream2, inputCipher);
            cipherOutputStream2.write(AESkey);
            cipherOutputStream2.close();
            String fromUserEncryptedKey = Base64.encodeToString(outputStream2.toByteArray(), Base64.URL_SAFE);

            return new String[]{fromUserEncryptedKey, toUserEncryptedKey};
        }

        public byte[] decryptAESKey(String encryptedAESKey) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException, NoSuchPaddingException, InvalidKeyException {
            byte[] encryptedBytes = Base64.decode(encryptedAESKey, Base64.URL_SAFE);
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyStore.PrivateKeyEntry privateKeyEntry =
                    (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(encryptedBytes), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte) nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i);
            }

            return bytes;
        }

        public static byte[] generateNewAESKey() throws NoSuchAlgorithmException {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            SecureRandom random = new SecureRandom();
            keyGen.init(256,random);
            SecretKey secretKey = keyGen.generateKey();
            return secretKey.getEncoded();
        }
    */
    public String encryptData(byte[] AESKey, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Key key = new SecretKeySpec(AESKey, "AES");
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data);
        return Base64.encodeToString(encVal, Base64.URL_SAFE | Base64.NO_WRAP);
    }

    public byte[] decryptData(byte[] AESKey, String encryptedData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Key key = new SecretKeySpec(AESKey, "AES");
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decValue = c.doFinal(Base64.decode(encryptedData, Base64.URL_SAFE | Base64.NO_WRAP));
        return decValue;
    }


}