package com.mtxrii.file.mtxfile.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UploadService {
    private static final Map<String, MultipartFile> UPLOADED_FILES = new ConcurrentHashMap<>();

    public boolean uploadFile(MultipartFile file) {
        if (UPLOADED_FILES.containsKey(file.getOriginalFilename())) {
            return false;
        } else {
            UPLOADED_FILES.put(file.getOriginalFilename(), file);
            return true;
        }
    }
}
