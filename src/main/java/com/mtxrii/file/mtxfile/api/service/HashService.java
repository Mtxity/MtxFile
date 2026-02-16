package com.mtxrii.file.mtxfile.api.service;

import com.mtxrii.file.mtxfile.api.model.HashResponse;
import com.mtxrii.file.mtxfile.util.HashUtil;
import org.springframework.stereotype.Service;

@Service
public class HashService {

    public HashResponse.NewHashResponse hash(String value) {
        if (value == null || value.isEmpty() || value.equals(" ")) {
            return new HashResponse.NewHashResponse("Cannot hash empty value");
        }
        String hash = this.hashUtil(value);
        return new HashResponse.NewHashResponse("Successfully hashed input", hash);
    }

    private String hashUtil(String value) {
        return HashUtil.hashPassword(value);
    }

    public HashResponse.ExistingHashResponse verify(String value, String hash) {
        boolean matches = this.verifyUtil(value, hash);
        return new HashResponse.ExistingHashResponse(matches, "String value " + (matches ? "matches" : " does not match") + " hash");
    }

    private boolean verifyUtil(String value, String hash) {
        return HashUtil.verifyPassword(value, hash);
    }
}
