package com.mtxrii.file.mtxfile.api.model;

public class UnauthorizedResponse extends Response {
    public final String message;

    public UnauthorizedResponse(String message) {
        super(false, 403);
        this.message = message;
    }
}
