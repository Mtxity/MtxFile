package com.mtxrii.file.mtxfile.api.service;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.mtxrii.file.mtxfile.api.model.Response;
import com.mtxrii.file.mtxfile.api.model.UnauthorizedResponse;
import com.mtxrii.file.mtxfile.api.model.UploadContentsResponse;
import com.mtxrii.file.mtxfile.api.model.enumeration.UnauthorizedReason;
import com.mtxrii.file.mtxfile.util.HashUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UploadService {
    private static final Map<String, MultipartFile> UPLOADED_FILES = new ConcurrentHashMap<>();
    private static final Map<String, String> FILE_PASSWORDS = new ConcurrentHashMap<>();
    private static final int FILE_CONTENTS_TRUNCATE_SIZE = 300;

    private record FileDetails (
            String truncateContents,
            int length
    ) { }

    private final ReadService readService;

    public UploadService(ReadService readService) {
        this.readService = readService;
    }

    public Response uploadFile(@NotNull MultipartFile file, @Nullable String password) {
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
        boolean passwordProtected = FILE_PASSWORDS.containsKey(fileName);
        boolean uploaded = UPLOADED_FILES.containsKey(fileName);
        if (!uploaded) {
            UPLOADED_FILES.put(fileName, file);
            if (password != null) {
                FILE_PASSWORDS.put(fileName, HashUtil.hashPassword(password));
                passwordProtected = true;
            }
        }

        boolean authorized = (!FILE_PASSWORDS.containsKey(fileName)) ||
                             (password != null && HashUtil.verifyPassword(password, FILE_PASSWORDS.get(fileName)));
        if (!authorized) {
            UnauthorizedReason unauthorizedReason = FILE_PASSWORDS.containsKey(fileName)
                                                    ? UnauthorizedReason.WRONG_PASSWORD
                                                    : UnauthorizedReason.NO_PASSWORD;
            return new UnauthorizedResponse(unauthorizedReason, true);
        }
        return new UploadContentsResponse(
                uploaded,
                fileName,
                fileDetails.truncateContents,
                fileDetails.length,
                passwordProtected
        );
    }

    public Response getUploadedFilePreview(@NotNull String fileName, @Nullable String password) {
        boolean passwordProtected = FILE_PASSWORDS.containsKey(fileName);
        if (passwordProtected) {
            if (password == null) {
                return new UnauthorizedResponse(UnauthorizedReason.NO_PASSWORD, false);
            }
            String hashedPassword = FILE_PASSWORDS.get(fileName);
            if (!HashUtil.verifyPassword(password, hashedPassword)) {
                return new UnauthorizedResponse(UnauthorizedReason.WRONG_PASSWORD, false);
            }
        }

        MultipartFile file = UPLOADED_FILES.get(fileName.toUpperCase());
        FileDetails fileDetails = this.getFileDetails(file);
        if (file != null) {
            return new UploadContentsResponse(
                    true,
                    fileName,
                    fileDetails.truncateContents,
                    fileDetails.length,
                    passwordProtected
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
