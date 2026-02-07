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
        // @TODO: DO analytics
    }
}
