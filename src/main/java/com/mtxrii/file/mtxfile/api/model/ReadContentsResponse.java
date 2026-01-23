package com.mtxrii.file.mtxfile.api.model;

import jakarta.annotation.Nullable;

import java.util.Map;

public class ReadContentsResponse extends Response {
    public final String fileName;
    public final String contents;
    public final long fileSizeBytes;
    public final String fileContentType;
    public final Map<String, Object> fileMetadata; // @TODO: Refactor this to have all metadata in nested classes (file and request meta)

    public ReadContentsResponse(boolean success, int code, String fileName, long fileSizeBytes, String fileContentType, String contents, @Nullable Map<String, Object> metadata) {
        super(success, code);
        this.fileName = fileName;
        this.fileSizeBytes = fileSizeBytes;
        this.fileContentType = fileContentType;
        this.contents = contents;
        this.fileMetadata = metadata;
    }

    public ReadContentsResponse(String fileName, long fileSizeBytes, String fileContentType, String contents, @Nullable Map<String, Object> metadata) {
        this.fileName = fileName;
        this.fileSizeBytes = fileSizeBytes;
        this.fileContentType = fileContentType;
        this.contents = contents;
        this.fileMetadata = metadata;
    }
}
