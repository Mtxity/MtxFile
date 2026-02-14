package com.mtxrii.file.mtxfile.exception;

import java.io.IOException;

public class MtxIoException extends IOException {
    // @TODO: Figure out where this is needed
    Exception e;

    public MtxIoException(Exception e) {
        this.e = e;
    }
}
