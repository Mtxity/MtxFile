package com.mtxrii.file.mtxfile.api.model;

public class Response {
    private final boolean success;
    private final int code;

    public Response(boolean success, int code) {
        this.success = success;
        this.code = code;
    }
}
