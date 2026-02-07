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
        // @TODO: DO analytics
    }

    private int calculateTotalColumns(List<Map<String, String>> csvData) {
        if (csvData == null || csvData.isEmpty()) {
            return 0;
        }
        Map<String, String> firstRow = csvData.getFirst();
        return firstRow == null ? 0 : firstRow.size();
    }
}
