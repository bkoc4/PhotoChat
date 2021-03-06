package com.photo.advanced.photochat.helper.Security;

import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.photo.advanced.photochat.helper.PreferencesHelper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLOutput;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SecurityHelper {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public SecurityHelper() {
        String ecKeyS = PreferencesHelper.getECKeyS();

        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC);
            generator.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());

            KeyPair keyPair = generator.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

            if (!ecKeyS.equals(PreferencesHelper.defaultString)) {

                BigInteger s = new BigInteger(new String(Base64.decode(PreferencesHelper.getECKeyS(),Base64.URL_SAFE),"UTF-8"));
                KeyFactory keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_EC);
                try {
                    privateKey = keyFactory.generatePrivate(new ECPrivateKeySpec(s, ((ECPrivateKey) keyPair.getPrivate()).getParams()));
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }

                return;
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String encodeS = Base64.encodeToString(((ECPrivateKey)privateKey).getS().toString().getBytes(), Base64.URL_SAFE);
        PreferencesHelper.setECKeyS(encodeS);
    }

   public PublicKey generatePublicKey() {
       return publicKey;
    }

    public String[] generateEncodedPublicKey() {
        try {
            ECPublicKey publicKey = (ECPublicKey) generatePublicKey();
            String encodeX = Base64.encodeToString(publicKey.getW().getAffineX().toString().getBytes(), Base64.URL_SAFE);
            String encodeY = Base64.encodeToString(publicKey.getW().getAffineY().toString().getBytes(), Base64.URL_SAFE);

            return new String[] {encodeX,encodeY};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PublicKey fromStringToPublicKey (String keyX, String keyY) {
        try {
        BigInteger x = new BigInteger(new String(Base64.decode(keyX,Base64.URL_SAFE),"UTF-8"));
        BigInteger y = new BigInteger(new String(Base64.decode(keyY,Base64.URL_SAFE),"UTF-8"));

        ECPoint ecPoint = new ECPoint(x, y);
        ECPublicKeySpec keySpec = new ECPublicKeySpec(ecPoint, ((ECPrivateKey) privateKey).getParams());

        KeyFactory keyFactory = null;
            keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
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

    public byte[] encryptData(byte[] AESKey, byte[] data) {
           Key key = new SecretKeySpec(AESKey, "AES");
        Cipher c = null;
        try {
            c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(data);
            return encVal;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decryptData(byte[] AESKey, byte[] encryptedData) {
        Key key = new SecretKeySpec(AESKey, "AES");
        Cipher c = null;
        byte[] decValue = null;
        try {
            c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, key);
            decValue = c.doFinal(encryptedData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return decValue;
    }


}