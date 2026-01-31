package com.mtxrii.file.mtxfile.api.model;

public class HashContentsResponse extends Response {
    public final String fileName;
    public final String hash;

    public HashContentsResponse(String fileName, String hash) {
        this.fileName = fileName;
        this.hash = hash;
    }
}
