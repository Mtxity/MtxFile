package com.mtxrii.file.mtxfile.api.model;

public class WordCountResponse extends Response {
    public final String fileName;
    public final int wordCount;

    public WordCountResponse(String fileName, int wordCount) {
        super(true, 200);
        this.fileName = fileName;
        this.wordCount = wordCount;
    }
}
