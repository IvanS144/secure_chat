package com.sni.secure_chat.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CryptoUtil {

    private static final Base64.Encoder base64Encoder = Base64.getEncoder();
    private static final Base64.Decoder base64Decoder = Base64.getDecoder();
    private static final SecureRandom secureRandom = new SecureRandom();
    public static String keyToPem(Key key){
        String base64String = base64Encoder.encodeToString(key.getEncoded());
        List<String> chunks = splitStringToChunks(base64String, 64);
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN PUBLIC KEY-----\n");
        for(String chunk : chunks){
            sb.append(chunk).append("\n");
        }
        sb.append("-----END PUBLIC KEY-----");
        return sb.toString();
    }

    public static String keyToBase64(Key key){
        return base64Encoder.encodeToString(key.getEncoded());
    }

    public static List<String> splitStringToChunks(String text, int n) {
        List<String> results = new ArrayList<>();
        int length = text.length();

        for (int i = 0; i < length; i += n) {
            results.add(text.substring(i, Math.min(length, i + n)));
        }

        return results;
    }

    public static String encryptStringWithRSAPublicKey(String stringToEncrypt, String base64PublicKey ){
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] publicKeyBytes = base64Decoder.decode(base64PublicKey.getBytes());
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytesToEncrypt = stringToEncrypt.getBytes();
            byte[] encryptedBytes = encryptCipher.doFinal(bytesToEncrypt);
            return new String(encryptedBytes);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String encryptBytesWithRSAPublicKey(byte[] bytesToEncrypt, String base64PublicKey){//base64 enkoduje rezultat
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] publicKeyBytes = base64Decoder.decode(base64PublicKey.getBytes());
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = encryptCipher.doFinal(bytesToEncrypt);
            return base64Encoder.encodeToString(encryptedBytes);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static byte[] decryptWithRSAPrivateKey(byte[] bytesToDecrypt, String base64PrivateKey){
        try{
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] publicKeyBytes = base64Decoder.decode(base64PrivateKey.getBytes());
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(publicKeyBytes);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            return decryptCipher.doFinal(bytesToDecrypt);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    //base64 enkoduje rezultat
    public static String symmetricEncrypt(String algorithm, String input, SecretKey key,
                                 IvParameterSpec iv){
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] cipherText = cipher.doFinal(input.getBytes());
            return base64Encode(cipherText);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String symmetricDecrypt(String algorithm, String base64cipherText, SecretKey key,
                                 IvParameterSpec iv){
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] plainText = cipher.doFinal(Base64.getDecoder()
                    .decode(base64cipherText));
            return new String(plainText);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static SecretKey generateKey(int n){
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(n);
            return keyGenerator.generateKey();
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String base64Encode(String stringToEncode){
        return base64Encoder.encodeToString(stringToEncode.getBytes());
    }

    public static String base64Encode(byte[] bytesToEncode){
        return base64Encoder.encodeToString(bytesToEncode);
    }

    public static String base64Decode(String base64String){
        return  new String(base64Decoder.decode(base64String.getBytes()));
    }


    public static byte[] base64DecodeToBytes(String base64String){
        return base64Decoder.decode(base64String.getBytes());
    }
}
