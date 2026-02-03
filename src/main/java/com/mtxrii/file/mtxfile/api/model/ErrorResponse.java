package com.mtxrii.file.mtxfile.api.model;

import java.time.Instant;

public class ErrorResponse extends Response {
    public final Instant timestamp;
    public final String message;

    public ErrorResponse(int code, String message) {
        super(false, code);
        this.timestamp = Instant.now();
        this.message = message;
    }
}
