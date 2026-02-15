package com.mtxrii.file.mtxfile.api.service;

import com.mtxrii.file.mtxfile.api.model.UploadContentsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UploadService {
    private static final Map<String, MultipartFile> UPLOADED_FILES = new ConcurrentHashMap<>();
    private static final int FILE_CONTENTS_TRUNCATE_SIZE = 300;

    private final ReadService readService;

    public UploadService(ReadService readService) {
        this.readService = readService;
    }

    public boolean uploadFile(MultipartFile file) {
        String fileName = file.getOriginalFilename().toUpperCase();
        if (UPLOADED_FILES.containsKey(fileName)) {
            return false;
        } else {
            UPLOADED_FILES.put(fileName, file);
            return true;
        }
    }

    public UploadContentsResponse getUploadedFilePreview(String fileName) {
        MultipartFile file = UPLOADED_FILES.get(fileName.toUpperCase());
        if (file != null) {
            return new UploadContentsResponse(
                    true,
                    fileName,
                    this.getTruncatedFileContents(file),
                    -1 // @TODO: Implement this
            );
        } else {
            return new UploadContentsResponse(false, fileName, null, -1);
        }
    }

    private String getTruncatedFileContents(MultipartFile file) {
        try {
            String fileContents = this.readService.readContents(file).getContents();
            return fileContents.substring(0, FILE_CONTENTS_TRUNCATE_SIZE);
        } catch (IOException e) {
            return "Error reading file contents";
        }
    }
}
