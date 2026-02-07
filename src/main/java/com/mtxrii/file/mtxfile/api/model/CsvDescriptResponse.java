package com.mtxrii.file.mtxfile.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CsvDescriptResponse extends Response {
    public final String fileName;
    public final int totalRows;
    public final int totalColumns;
    public final int totalEmptyValues;
    public final Map<String, Integer> uniqueValuesPerHeader;
    public final Map<String, String> mostCommonValuesPerHeader;

    public CsvDescriptResponse(
            String fileName,
            int totalRows,
            int totalColumns,
            int totalEmptyValues,
            Map<String, Integer> uniqueValuesPerHeader,
            Map<String, String> mostCommonValuesPerHeader
    ) {
        this.fileName = fileName;
        this.totalRows = totalRows;
        this.totalColumns = totalColumns;
        this.totalEmptyValues = totalEmptyValues;
        this.uniqueValuesPerHeader = uniqueValuesPerHeader;
        this.mostCommonValuesPerHeader = mostCommonValuesPerHeader;
    }
}
