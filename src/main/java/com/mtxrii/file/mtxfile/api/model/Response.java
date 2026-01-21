package com.mtxrii.file.mtxfile.api.model;

public class Response {
    protected final boolean success;
    protected final int code;

    public Response(boolean success, int code) {
        this.success = success;
        this.code = code;
    }
}
