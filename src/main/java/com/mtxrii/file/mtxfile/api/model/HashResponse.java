package com.mtxrii.file.mtxfile.api.model;

public class HashResponse extends Response {
    public final String message;

    public HashResponse(String message, boolean success) {
        super(true, (success ? 200 : 400));
        this.message = message;
    }

    public class NewHashResponse extends HashResponse {
        public final boolean created;
        public final String hash;

        public NewHashResponse(String failureMessage) {
            super(failureMessage, false);
            this.created = false;
            this.hash = null;
        }

        public NewHashResponse(String successMessage, String hash) {
            super(successMessage, true);
            this.created = true;
            this.hash = hash;
        }
    }

    public class ExistingHashResponse extends HashResponse {
        public final boolean matches;

        public ExistingHashResponse(boolean matches, String message) {
            super(message, matches);
            this.matches = matches;
        }
    }
}
