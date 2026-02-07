package com.mtxrii.file.mtxfile.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CsvFequenciesResponse extends Response {
    public final String fileName;
    public final Map<String, Map<String, Integer>> valueFrequencies;

    public CsvFequenciesResponse(String fileName, Map<String, Map<String, Integer>> valueFrequencies) {
        this.fileName = fileName;
        this.valueFrequencies = valueFrequencies;
    }
}
