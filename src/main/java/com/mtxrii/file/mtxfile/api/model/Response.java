package com.mtxrii.file.mtxfile.api.model;

public class Response {
    public final boolean success;
    public final int code;

    public Response(boolean success, int code) {
        this.success = success;
        this.code = code;
    }

    public Response() {
        this(true, 200);
    }
}
