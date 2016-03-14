package com.itertk.app.mpos.utility;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Base64;

/**
 * aes 加密
 */
public class AESEncryptor {

    private final static String TAG = AESEncryptor.class.getSimpleName();
    private static byte[] mMACAddress_MD5Bytes;

    private static AESEncryptor aesEncryptor;
    public  static  AESEncryptor getInstance(Context context ){
        if(aesEncryptor == null){
            aesEncryptor = new AESEncryptor(context);
        }
        return aesEncryptor;
    }

    private AESEncryptor(Context context) {
        WifiManager wm = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wif = wm.getConnectionInfo();
        String strMACAddress = (null == wif) ? null : wif.getMacAddress();
        if (null == strMACAddress)
            strMACAddress = "04:16:65:22:51:20";
        mMACAddress_MD5Bytes = getMd5Digest(strMACAddress);
    }

    public String encryption(String plaintext) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(
                    mMACAddress_MD5Bytes, "AES"));
            byte[] encrypt = cipher.doFinal(plaintext.getBytes());
            return new String(Base64.encode(encrypt, Base64.DEFAULT));
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
        return plaintext;
    }

    public String decryption(String ciphertext) {
        try {
            byte[] encrypted = Base64.decode(ciphertext, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(
                    mMACAddress_MD5Bytes, "AES"));
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        
        return ciphertext;
    }

    private static byte[] getMd5Digest(String strSource) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] byteArray = strSource.getBytes();
        
        return md5.digest(byteArray);
    }

    public static String convertToMD5(String strSource) {
        // Log.d(TAG, "Wifi Mac Address: " + strSource);
        byte[] md5Bytes = getMd5Digest(strSource);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString().toUpperCase();
    }

}
