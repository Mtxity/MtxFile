package com.mtxrii.file.mtxfile.api.model;

public class HashContentsResponse extends Response {
    public final String fileName;
    public final String hash;
    public int timesHashed;

    public HashContentsResponse(String fileName, String hash) {
        this.fileName = fileName;
        this.hash = hash;
        this.timesHashed = 1;
    }

    public void setTimesHashed(int timesHashed) {
        this.timesHashed = timesHashed;
    }
}
