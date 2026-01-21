package com.mtxrii.file.mtxfile.api.model;

public class ReadContentsResponse extends Response {
    protected final String fileName;
    protected final String contents;

    public ReadContentsResponse(boolean success, int code, String fileName, String contents) {
        super(success, code);
        this.fileName = fileName;
        this.contents = contents;
    }
}
