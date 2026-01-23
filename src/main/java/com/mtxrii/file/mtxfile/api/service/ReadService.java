package com.mtxrii.file.mtxfile.api.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ReadService {

    public List<Map<String, String>> jsonifyCsv(MultipartFile file) throws IOException {
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

    public Map<String, List<Map<String, Object>>> jsonifyXls(MultipartFile file) throws IOException {
        try (
                InputStream is = file.getInputStream();
                Workbook workbook = WorkbookFactory.create(is);
        ) {
            DataFormatter formatter = new DataFormatter(Locale.US, true);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
            for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
                Sheet sheet = workbook.getSheetAt(s);
                if (sheet == null) {
                    continue;
                }

                Row headerRow = sheet.getRow(sheet.getFirstRowNum());
                if (headerRow == null) {
                    result.put(sheet.getSheetName(), List.of());
                    continue;
                }

                // Headers
                Map<Integer, String> headerMap = new LinkedHashMap<>();
                short lastCelNum = headerRow.getLastCellNum();
                for (int c = 0; c < lastCelNum; c++) {
                    Cell cell = headerRow.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    String header = cell == null ? null : formatter.formatCellValue(cell).trim();
                    if (header != null && !header.isBlank()) {
                        headerMap.put(c, header);
                    }
                }

                // Rows
                List<Map<String, Object>> rows = new ArrayList<>();
                int firstDataRow = headerRow.getRowNum() + 1;
                int lastRow = sheet.getLastRowNum();
                for (int r = firstDataRow; r <= lastRow; r++) {
                    Row row = sheet.getRow(r);
                    if (row == null) {
                        continue;
                    }

                    Map<String, Object> rowMap = new LinkedHashMap<>();
                    for (Map.Entry<Integer, String> header : headerMap.entrySet()) {
                        Cell cell = row.getCell(header.getKey(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        Object value = cell == null ? null : formatter.formatCellValue(cell, evaluator); // @TODO: Consider parsing different data types differently
                        rowMap.put(header.getValue(), value);
                    }
                    rows.add(rowMap);
                }
                result.put(sheet.getSheetName(), rows);
            }
            return result;
        }
    }
}
