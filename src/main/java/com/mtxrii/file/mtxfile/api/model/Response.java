package com.mtxrii.file.mtxfile.api.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Response {
    public final Map<String, Object> requestMeta;

    public Response(boolean success, int code) {
        this.requestMeta = new LinkedHashMap<>();
        this.requestMeta.put("success", success);
        this.requestMeta.put("code", code);
    }

    public Response() {
        this(true, 200);
    }

    public Response path(String path) {
        this.requestMeta.put("path", path);
        return this;
    }
}
