package com.mtxrii.file.mtxfile.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public final class HashUtil {
    private HashUtil() { }

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int SALT_LENGTH_BYTES = 16;

    public static String hashPassword(String password) {
        byte[] salt = generateSalt();
        byte[] hash = pbkdf2(password.toCharArray(), salt);
        return formatHash(salt, hash);
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH_BITS);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Password hashing failed", e);
        }
    }

    private static String formatHash(byte[] salt, byte[] hash) {
        // format: iterations:saltBase64:hashBase64
        return ITERATIONS + ":" +
                Base64.getEncoder().encodeToString(salt) + ":" +
                Base64.getEncoder().encodeToString(hash);
    }


    // --- Verify password ---

    public static boolean verifyPassword(String password, String stored) {
        ParsedHash parsed = parseHash(stored);
        byte[] hash = pbkdf2(password.toCharArray(), parsed.salt);
        return constantTimeEquals(parsed.hash, hash);
    }

    private static ParsedHash parseHash(String stored) {
        String[] parts = stored.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid stored hash format");
        }
        int iterations = Integer.parseInt(parts[0]);
        if (iterations != ITERATIONS) {
            throw new IllegalArgumentException("Unsupported iteration count");
        }
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] hash = Base64.getDecoder().decode(parts[2]);
        return new ParsedHash(salt, hash);
    }

    protected static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }

    private record ParsedHash(byte[] salt, byte[] hash) { }
}
