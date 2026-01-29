package com.mtxrii.file.mtxfile.api.model;

public class SummarizedContentsResponse extends Response {
    public final String fileName;
    public final String summary;

    public SummarizedContentsResponse(boolean success, int code, String fileName, String summary) {
        super(success, code);
        this.fileName = fileName;
        this.summary = summary;
    }

    public SummarizedContentsResponse(String fileName, String summary) {
        this.fileName = fileName;
        this.summary = summary;
    }
}
