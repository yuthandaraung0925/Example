package com.example.testing.Utility;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Utils {
    public static String encryptData(String data){
        if(data == "" || data == null){return data;}
        String encryptData = null;

        try{
            encryptData = Encrypt(data, Constants.ENCRYPT_KEY);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return encryptData;
    }

    public static String decryptData(String data){
        if(data == "" || data == null){return data;}
        String decryptData = null;

        try{
            decryptData = Decrypt(data, Constants.ENCRYPT_KEY);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return decryptData;
    }

    public static String Encrypt(String text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes= new byte[16];
        byte[] b= key.getBytes("UTF-8");
        int len= b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.ENCRYPT_MODE,keySpec,ivSpec);

        byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.encodeToString(results, Base64.DEFAULT);
    }

    public static String Decrypt(String text, String key) throws Exception{
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes= new byte[16];
        byte[] b= key.getBytes("UTF-8");
        int len= b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.DECRYPT_MODE,keySpec,ivSpec);

        byte[] results = cipher.doFinal(Base64.decode(text, 0));
        return new String(results,"UTF-8");
    }
}
