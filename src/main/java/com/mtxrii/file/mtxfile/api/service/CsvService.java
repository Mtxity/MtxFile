package com.mtxrii.file.mtxfile.api.service;

import com.mtxrii.file.mtxfile.api.model.CsvDescriptResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CsvService {
    private final ReadService readService;

    public CsvService(ReadService readService) {
        this.readService = readService;
    }

    public List<Map<String, String>> jsonifyCsv(MultipartFile file) throws IOException {
        this.readService.validateFileAndExtension(file, ".csv");
        try (
                Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
                CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim().parse(reader)
        ) {
            List<Map<String, String>> records = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                Map<String, String> row = new LinkedHashMap<>();
                for (String header : csvParser.getHeaderMap().keySet()) {
                    row.put(header, record.get(header));
                }
                records.add(row);
            }
            return records;
        }
    }

    public CsvDescriptResponse descriptCsv(MultipartFile file) throws IOException {
        List<Map<String, String>> csvData = this.jsonifyCsv(file);
        // @TODO: DO analytics
    }
}
