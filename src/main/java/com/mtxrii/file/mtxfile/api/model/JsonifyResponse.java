package com.mtxrii.file.mtxfile.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonifyResponse extends Response {
    public final String fileName;
    public final List<Map<String, String>> csvContents;
    public final Map<String, List<Map<String, Object>>> xlsContents;
    @JsonProperty("contents")
    public final JsonNode xmlContents;

    public JsonifyResponse(String fileName, List<Map<String, String>> csvContents) {
        super(true, 200);
        this.fileName = fileName;
        this.csvContents = csvContents;
        this.xlsContents = null;
        this.xmlContents = null;
    }

    public JsonifyResponse(String fileName, Map<String, List<Map<String, Object>>> xlsContents) {
        super(true, 200);
        this.fileName = fileName;
        this.csvContents = null;
        this.xlsContents = xlsContents;
        this.xmlContents = null;
    }

    public JsonifyResponse(String fileName, JsonNode xmlContents) {
        super(true, 200);
        this.fileName = fileName;
        this.csvContents = null;
        this.xlsContents = null;
        this.xmlContents = xmlContents;
    }
}
