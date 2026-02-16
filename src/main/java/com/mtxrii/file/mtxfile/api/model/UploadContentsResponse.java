package com.mtxrii.file.mtxfile.api.model;

public class UploadContentsResponse extends Response {
    public final boolean uploaded;
    public final String fileName;
    public final String contentsPreview;
    public final int length;
    public final Boolean passwordProtected;

    public UploadContentsResponse(boolean uploaded, String fileName, String contentsPreview, int length) {
        super(uploaded, uploaded ? 201 : 202);
        this.uploaded = uploaded;
        this.fileName = fileName;
        this.contentsPreview = contentsPreview;
        this.length = length;
        this.passwordProtected = null;
    }

    public UploadContentsResponse(boolean uploaded, String fileName, String contentsPreview, int length, boolean passwordProtected) {
        super(uploaded, uploaded ? 201 : 202);
        this.uploaded = uploaded;
        this.fileName = fileName;
        this.contentsPreview = contentsPreview;
        this.length = length;
        this.passwordProtected = passwordProtected;
    }
}
