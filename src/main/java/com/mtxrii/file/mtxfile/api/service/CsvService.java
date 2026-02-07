package com.mtxrii.file.mtxfile.api.service;

import com.mtxrii.file.mtxfile.api.model.CsvDescriptResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CsvService {
    private final ReadService readService;

    public CsvService(ReadService readService) {
        this.readService = readService;
    }

    public CsvDescriptResponse descriptCsv(MultipartFile file) throws IOException {
        List<Map<String, String>> csvData = this.readService.jsonifyCsv(file);
        int totalRows = csvData.size();
        int totalColumns = this.calculateTotalColumns(csvData);
        int totalEmptyValues = this.countEmptyValues(csvData);
        // @TODO: DO analytics
    }

    private int calculateTotalColumns(List<Map<String, String>> csvData) {
        if (csvData == null || csvData.isEmpty()) {
            return 0;
        }
        Map<String, String> firstRow = csvData.getFirst();
        return firstRow == null ? 0 : firstRow.size();
    }

    public int countEmptyValues(List<Map<String, String>> csvData) {
        if (csvData == null || csvData.isEmpty()) {
            return 0;
        }

        int emptyCount = 0;
        for (Map<String, String> row : csvData) {
            if (row == null || row.isEmpty()) {
                continue;
            }
            for (String value : row.values()) {
                if (value == null || value.trim().isEmpty()) {
                    emptyCount ++;
                }
            }
        }
        return emptyCount;
    }
}
