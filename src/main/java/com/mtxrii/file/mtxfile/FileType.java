package com.mtxrii.file.mtxfile;

import java.util.Locale;

public enum FileType {
    TEXT(".txt"),
    MARKDOWN(".md"),
    PDF(".pdf"),
    CSV(".csv"),
    EXCEL(".xls"),
    EXCEL_MAC(".xlsx"),
    XML(".xml"),
    YML(".yml"),
    YAML(".yaml"),
    UNKNOWN(null);

    public final String extension;

    FileType(String extension) {
        this.extension = extension;
    }

    public static FileType fromExtension(String extension) {
        for (FileType fileType : FileType.values()) {
            if (fileType.extension.equals(extension.toLowerCase(Locale.ROOT))) {
                return fileType;
            }
        }
        return UNKNOWN;
    }
}
