package com.mtxrii.file.mtxfile.api.service;

import com.mtxrii.file.mtxfile.util.HashUtil;
import org.springframework.stereotype.Service;

@Service
public class HashService {

    public String hash(String value) {
        return HashUtil.hashPassword(value);
    }

    public boolean verify(String value, String hash) {
        return HashUtil.verifyPassword(value, hash);
    }
}
