package com.mtxrii.file.mtxfile;

public enum FileType {
    TEXT(".txt"),
    MARKDOWN(".md"),
    PDF(".pdf"),
    CSV(".csv"),
    EXCEL(".xls"),
    EXCEL_MAC(".xlsx"),
    XML(".xml"),
    YML(".yml"),
    YAML(".yaml");

    public final String extension;

    FileType(String extension) {
        this.extension = extension;
    }
}
