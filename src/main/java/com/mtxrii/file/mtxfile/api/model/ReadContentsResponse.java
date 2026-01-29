package com.mtxrii.file.mtxfile.api.model;

import jakarta.annotation.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReadContentsResponse extends Response {
    public final String fileName;
    public final String contents;
    public final Map<String, Object> fileMeta;

    public ReadContentsResponse(boolean success, int code, String fileName, long fileSizeBytes, String fileContentType, String contents, @Nullable Map<String, Object> metadata) {
        super(success, code);
        this.fileName = fileName;
        this.contents = contents;
        this.fileMeta = new LinkedHashMap<>();
        this.fileMeta.put("fileSizeBytes", fileSizeBytes);
        this.fileMeta.put("fileContentType", fileContentType);
        if (metadata != null) {
            this.fileMeta.put("extraFileMeta", metadata);
        }
    }

    public ReadContentsResponse(String fileName, long fileSizeBytes, String fileContentType, String contents, @Nullable Map<String, Object> metadata) {
        this.fileName = fileName;
        this.contents = contents;
        this.fileMeta = new LinkedHashMap<>();
        this.fileMeta.put("fileSizeBytes", fileSizeBytes);
        this.fileMeta.put("fileContentType", fileContentType);
        if (metadata != null) {
            this.fileMeta.put("extraFileMeta", metadata);
        }
    }

    public String getContents() {
        return this.contents;
    }
}
