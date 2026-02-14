package com.mtxrii.file.mtxfile.exception;

import java.io.IOException;

public class MtxIoException extends IOException {
    Exception e;

    public MtxIoException(Exception e) {
        this.e = e;
    }
}
