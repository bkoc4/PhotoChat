package com.photo.advanced.photochat.helper.Security;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
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
import java.security.spec.ECGenParameterSpec;
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

    String alias;
    Context context;
    KeyPair keyPair;

    public SecurityHelper(Context context, String alias) {
        this.alias = alias;
        this.context = context;
    }

    public void initialize() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        /*if (keyStore.containsAlias(alias)) {
            Log.d("Burak", "[containsAlias]");
            KeyStore.PrivateKeyEntry privateKeyEntry = null;
            try {
                privateKeyEntry = ((KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null));
                keyPair = new KeyPair(privateKeyEntry.getCertificate().getPublicKey(),privateKeyEntry.getPrivateKey());
                return;
            } catch (UnrecoverableEntryException e) {
                e.printStackTrace();
            }
        }
*/
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
        kpg.initialize(new KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                .setKeySize(512)
                .setCertificateSubject(new X500Principal("CN="))
                .build());
        KeyPair AESkeyPair = kpg.generateKeyPair();

        Log.d("Burak - debug RSA", Arrays.toString(AESkeyPair.getPrivate().getEncoded()));


        KeyPairGenerator generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC);
        generator.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());

        keyPair = generator.generateKeyPair();
        //Log.d("Burak - debug2", Arrays.toString(keyPair.getPrivate().getEncoded()));
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public static SecretKey generateSharedSecret(PrivateKey privateKey,
                                                 PublicKey publicKey) {
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
        return Base64.encodeToString(encVal, Base64.URL_SAFE);
    }

    public byte[] decryptData(byte[] AESKey, String encryptedData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Key key = new SecretKeySpec(AESKey, "AES");
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decValue = c.doFinal(Base64.decode(encryptedData, Base64.URL_SAFE));
        return decValue;
    }


}