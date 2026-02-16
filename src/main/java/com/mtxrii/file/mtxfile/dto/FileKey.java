package com.mtxrii.file.mtxfile.dto;

public record FileKey (
        String fileName,
        String passwordHash
) {

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileKey otherFileKey)) {
            return false;
        }
        return otherFileKey.fileName.equals(fileName) &&
               otherFileKey.passwordHash.equals(passwordHash);
    }
}
