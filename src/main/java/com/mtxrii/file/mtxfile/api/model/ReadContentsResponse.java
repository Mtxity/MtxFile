package com.mtxrii.file.mtxfile.api.model;

public class ReadContentsResponse extends Response {
    public final String fileName;
    public final String contents;

    public ReadContentsResponse(boolean success, int code, String fileName, String contents) {
        super(success, code);
        this.fileName = fileName;
        this.contents = contents;
    }
}
