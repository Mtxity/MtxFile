package com.mtxrii.file.mtxfile.api.service;

import com.mtxrii.file.mtxfile.api.model.UploadContentsResponse;
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

    public UploadContentsResponse getUploadedFilePreview(String fileName) {
        MultipartFile file = UPLOADED_FILES.get(fileName);
        if (file != null) {
            return new UploadContentsResponse(
                    true,
                    fileName,
                    "To be implemented...",
                    -1 // @TODO: Implement this
            );
        } else {
            return new UploadContentsResponse(false, fileName, null, -1);
        }
    }
}
