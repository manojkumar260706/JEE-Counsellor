package com.jee_counsellor.jee_counsellor.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordUtil {

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null || !storedHash.contains(":")) {
            return false;
        }
        String[] parts = storedHash.split(":");
        if (parts.length != 2) {
            return false;
        }
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] hash = Base64.getDecoder().decode(parts[1]);

        byte[] testHash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        if (testHash.length != hash.length) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < hash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }
}
