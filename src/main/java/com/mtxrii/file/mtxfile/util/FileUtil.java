package com.mtxrii.file.mtxfile.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.mtxrii.file.mtxfile.api.model.enumeration.FileType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class FileUtil {
    private FileUtil() { }

    public static int xmlJsonNodeWordCount(JsonNode node, int wordCount) {
        Set<Map.Entry<String, JsonNode>> fields = node.properties();
        for (Map.Entry<String, JsonNode> field : fields) {
            String fieldName = field.getKey();
            JsonNode value = field.getValue();

            wordCount += fieldName.split(" ").length * 2;
            if (value.isObject()) {
                wordCount += xmlJsonNodeWordCount(value, wordCount);
            } else if (value.isArray()) {
                Iterator<JsonNode> elements = value.elements();
                while (elements.hasNext()) {
                    wordCount = xmlJsonNodeWordCount(elements.next(), wordCount + 2);
                }
            } else {
                wordCount += value.asText().split(" ").length;
            }
        }
        return wordCount;
    }

    public static FileType getFileType(MultipartFile file) {
        if (fileHasNoName(file) || !file.getOriginalFilename().contains(".")
        ) {
            return FileType.UNKNOWN;
        }

        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")).toLowerCase(Locale.ROOT);
        return FileType.fromExtension(ext);
    }

    private static boolean fileHasNoName(MultipartFile file){
        return file == null ||
                file.getOriginalFilename() == null ||
                file.getOriginalFilename().isEmpty();
    }
}
