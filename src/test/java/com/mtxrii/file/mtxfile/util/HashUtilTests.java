package com.mtxrii.file.mtxfile.util;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.crypto.SecretKeyFactory;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HashUtilTests {
    private static final String PASSWORD = "12345";

    @Test
    void testVerifyPassword_success() {
        String hashedPassword = HashUtil.hashPassword(PASSWORD);
        assertTrue(HashUtil.verifyPassword(PASSWORD, hashedPassword));
    }

    @Test
    void testVerifyPassword_wrongPassword() {
        String hashedPassword = HashUtil.hashPassword(PASSWORD);
        assertFalse(HashUtil.verifyPassword("wrong-password", hashedPassword));
    }

    @Test
    void testVerifyPassword_differentHashesForSamePassword() {
        String hash1 = HashUtil.hashPassword(PASSWORD);
        String hash2 = HashUtil.hashPassword(PASSWORD);
        assertNotEquals(hash1, hash2, "Hashes should differ because of random salt");
        assertTrue(HashUtil.verifyPassword(PASSWORD, hash1));
        assertTrue(HashUtil.verifyPassword(PASSWORD, hash2));
    }

    @Test
    void testVerifyPassword_invalidFormat() {
        assertThrows(
                IllegalArgumentException.class,
                () -> HashUtil.verifyPassword(PASSWORD, "invalid-format")
        );
    }

    @Test
    void testVerifyPassword_unsupportedIterations() {
        String hashedPassword = HashUtil.hashPassword(PASSWORD);
        String tampered = "1:" + hashedPassword.split(":", 3)[1] + ":" + hashedPassword.split(":", 3)[2];
        assertThrows(
                IllegalArgumentException.class,
                () -> HashUtil.verifyPassword(PASSWORD, tampered)
        );
    }

    @Test
    void testVerifyPassword_emptyPassword() {
        String hashedPassword = HashUtil.hashPassword("");
        assertTrue(HashUtil.verifyPassword("", hashedPassword));
        assertFalse(HashUtil.verifyPassword(" ", hashedPassword));
    }

    @Test
    void testVerifyPassword_constantTimeEquals_mismatchLength() {
        assertFalse(HashUtil.constantTimeEquals(new byte[2], new byte[0]));
    }

    @Test
    void testHashPassword_throwsIllegalStateExceptionWhenPbkdf2Fails() {
        try (MockedStatic<SecretKeyFactory> mocked = Mockito.mockStatic(SecretKeyFactory.class)) {
            mocked.when(() -> SecretKeyFactory.getInstance(Mockito.anyString()))
                    .thenThrow(new NoSuchAlgorithmException("boom"));

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> HashUtil.hashPassword(PASSWORD)
            );

            assertEquals("Password hashing failed", exception.getMessage());
        }
    }
}
