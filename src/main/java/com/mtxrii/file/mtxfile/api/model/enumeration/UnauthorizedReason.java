package com.mtxrii.file.mtxfile.api.model.enumeration;

public enum UnauthorizedReason {
    NO_PASSWORD("This resource requires a password"),
    WRONG_PASSWORD("Incorrect password provided");

    private final String message;

    UnauthorizedReason(String message) {
        this.message = message;
    }

    public String getMessage(boolean resourceAlreadyExists) {
        StringBuilder messageBuilder = new StringBuilder();
        if (resourceAlreadyExists) {
            messageBuilder.append("A resource with the same name already exists and is inaccessible. ");
        }
        return messageBuilder.append(this.message).toString();
    }
}
