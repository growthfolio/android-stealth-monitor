package com.research.stealthmonitor.utils;

import android.util.Base64;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {
    
    private static final String ALGORITHM = "AES";
    private SecretKeySpec secretKey;
    
    public CryptoUtils() {
        generateKey();
    }
    
    private void generateKey() {
        try {
            long seed = System.currentTimeMillis() + System.nanoTime();
            byte[] keyBytes = new byte[16];
            SecureRandom random = new SecureRandom();
            random.setSeed(seed);
            random.nextBytes(keyBytes);
            secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
        } catch (Exception e) {
            byte[] keyBytes = "ResearchKey12345".getBytes();
            secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
        }
    }
    
    public String encryptXOR(String data) {
        if (data == null || data.isEmpty()) return "";
        
        byte[] dataBytes = data.getBytes();
        byte[] keyBytes = secretKey.getEncoded();
        byte[] encrypted = new byte[dataBytes.length];
        
        for (int i = 0; i < dataBytes.length; i++) {
            encrypted[i] = (byte) (dataBytes[i] ^ keyBytes[i % keyBytes.length]);
        }
        
        return Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }
    
    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception e) {
            return encryptXOR(data);
        }
    }
    
    public static String obfuscateString(String input) {
        if (input == null) return null;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            result.append((char) (input.charAt(i) ^ 0xAA));
        }
        return result.toString();
    }
}