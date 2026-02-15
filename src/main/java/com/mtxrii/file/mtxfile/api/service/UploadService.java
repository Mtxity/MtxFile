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

    private record FileDetails (
            String truncateContents,
            int length
    ) { }

    private final ReadService readService;

    public UploadService(ReadService readService) {
        this.readService = readService;
    }

    public UploadContentsResponse uploadFile(MultipartFile file) {
        if (file == null || file.getOriginalFilename() == null) {
            return new UploadContentsResponse(
                    false,
                    "null",
                    null,
                    -1
            );
        }

        String fileName = file.getOriginalFilename().toUpperCase();
        FileDetails fileDetails = this.getFileDetails(file);
        boolean uploaded = UPLOADED_FILES.containsKey(fileName);
        if (!uploaded) {
            UPLOADED_FILES.put(fileName, file);
        }

        return new UploadContentsResponse(
                uploaded,
                fileName,
                fileDetails.truncateContents,
                fileDetails.length
        );
    }

    public UploadContentsResponse getUploadedFilePreview(String fileName) {
        MultipartFile file = UPLOADED_FILES.get(fileName.toUpperCase());
        FileDetails fileDetails = this.getFileDetails(file);
        if (file != null) {
            return new UploadContentsResponse(
                    true,
                    fileName,
                    fileDetails.truncateContents,
                    fileDetails.length
            );
        } else {
            return new UploadContentsResponse(false, fileName, null, -1);
        }
    }

    private FileDetails getFileDetails(MultipartFile file) {
        try {
            String fileContents = this.readService.readContents(file).getContents();
            String truncatedFileContents = fileContents.substring(0, FILE_CONTENTS_TRUNCATE_SIZE);
            int fileContentsLength = fileContents.length();
            return new FileDetails(truncatedFileContents, fileContentsLength);
        } catch (IOException e) {
            return new FileDetails("Error reading file contents", -1);
        }
    }
}
