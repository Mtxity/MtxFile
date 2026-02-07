package com.mtxrii.file.mtxfile.api.service;

import com.mtxrii.file.mtxfile.api.model.CsvDescriptResponse;
import com.mtxrii.file.mtxfile.api.model.CsvFequenciesResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        Map<String, Integer> uniqueValuesPerHeader = this.countUniqueValuesPerHeader(csvData);
        Map<String, String> mostCommonValuesPerHeader = this.mostCommonValuesPerHeader(csvData);

        return new CsvDescriptResponse(
                file.getOriginalFilename(),
                totalRows,
                totalColumns,
                totalEmptyValues,
                uniqueValuesPerHeader,
                mostCommonValuesPerHeader
        );
    }

    public CsvFequenciesResponse csvFrequencies(MultipartFile file) throws IOException {
        List<Map<String, String>> csvData = this.readService.jsonifyCsv(file);
    }

    private int calculateTotalColumns(List<Map<String, String>> csvData) {
        if (csvData == null || csvData.isEmpty()) {
            return 0;
        }
        Map<String, String> firstRow = csvData.getFirst();
        return firstRow == null ? 0 : firstRow.size();
    }

    private int countEmptyValues(List<Map<String, String>> csvData) {
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

    private Map<String, Integer> countUniqueValuesPerHeader(List<Map<String, String>> csvData) {
        Map<String, Set<String>> uniques = new HashMap<>();
        if (csvData == null || csvData.isEmpty()) {
            return Map.of();
        }

        for (Map<String, String> row : csvData) {
            if (row == null || row.isEmpty()) {
                continue;
            }
            for (Map.Entry<String, String> entry : row.entrySet()) {
                String header = entry.getKey();
                String value = entry.getValue();
                uniques.computeIfAbsent(header, k -> new HashSet<>()).add(value == null ? "" : value);
            }
        }

        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : uniques.entrySet()) {
            result.put(entry.getKey(), entry.getValue().size());
        }
        return result;
    }

    private Map<String, String> mostCommonValuesPerHeader(List<Map<String, String>> csvData) {
        if (csvData == null || csvData.isEmpty()) {
            return Map.of();
        }

        Map<String, Map<String, Integer>> countsByHeader = new HashMap<>();
        Map<String, String> firstSeenByHeader = new HashMap<>();
        for (Map<String, String> row : csvData) {
            if (row == null || row.isEmpty()) {
                continue;
            }
            for (Map.Entry<String, String> entry : row.entrySet()) {
                String header = entry.getKey();
                String value = entry.getValue();
                if (value == null) {
                    continue;
                }
                countsByHeader.computeIfAbsent(header, k -> new HashMap<>()).merge(value, 1, Integer::sum);
                firstSeenByHeader.putIfAbsent(header, value);
            }
        }

        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : countsByHeader.entrySet()) {
            String header = entry.getKey();
            Map<String, Integer> valueCounts = entry.getValue();

            String mostCommon = firstSeenByHeader.get(header);
            int bestCount = -1;

            for (Map.Entry<String, Integer> vc : valueCounts.entrySet()) {
                String value = vc.getKey();
                int count = vc.getValue();
                if (count > bestCount) {
                    bestCount = count;
                    mostCommon = value;
                }
            }
            result.put(header, mostCommon);
        }
        return result;
    }
}
