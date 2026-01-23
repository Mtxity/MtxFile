package com.mtxrii.file.mtxfile.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.mtxrii.file.mtxfile.api.model.ReadContentsResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
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
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReadService {
    private static final XmlMapper XML_MAPPER = new XmlMapper();
    private static final YAMLMapper YAML_MAPPER = new YAMLMapper();

    public ReadContentsResponse readContents(MultipartFile file) throws IOException {
        this.validateFileAndExtension(file, ".txt", ".md", ".pdf");
        String fileContents;
        if (Objects.requireNonNull(file.getOriginalFilename()).toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            try (PDDocument document = PDDocument.load(file.getInputStream())) {
                PDFTextStripper stripper = new PDFTextStripper();
                fileContents = stripper.getText(document).trim();
            }
        } else {
            fileContents = new String(file.getBytes(), StandardCharsets.UTF_8);
        }
        return new ReadContentsResponse(file.getOriginalFilename(), fileContents);
    }

    public List<Map<String, String>> jsonifyCsv(MultipartFile file) throws IOException {
        this.validateFileAndExtension(file, ".csv");
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
        this.validateFileAndExtension(file, ".xls", ".xlsx");
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
                        Object value = this.xlsCellToJsonValue(cell, formatter, evaluator);
                        rowMap.put(header.getValue(), value);
                    }
                    rows.add(rowMap);
                }
                result.put(sheet.getSheetName(), rows);
            }
            return result;
        }
    }

    public JsonNode jsonifyXml(MultipartFile file) throws IOException {
        this.validateFileAndExtension(file, ".xml");
        return XML_MAPPER.readTree(file.getInputStream());
    }

    public JsonNode jsonifyYml(MultipartFile file) throws IOException {
        this.validateFileAndExtension(file, ".yml", ".yaml");
        return YAML_MAPPER.readTree(file.getInputStream());
    }

    private void validateFileAndExtension(MultipartFile file, String... validExtensions) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse("").toLowerCase(Locale.ROOT);
        for (String validExtension : validExtensions) {
            if (fileName.endsWith(validExtension.toLowerCase(Locale.ROOT))) {
                return;
            }
        }
        throw new IllegalArgumentException("Only " + String.join(", ", validExtensions) + " files are supported"); // @TODO: Add method to add an "or" between the last two elements in .join()
    }

    private Object xlsCellToJsonValue(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (cell == null) {
            return null;
        }

        CellType type = cell.getCellType();
        if (type == CellType.FORMULA) {
            type = evaluator.evaluateFormulaCell(cell);
        }

        return switch (type) {
            case STRING -> {
                String v = cell.getStringCellValue();
                yield v != null ? v.trim() : null;
            }
            case BOOLEAN -> cell.getBooleanCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toInstant().atZone(ZoneOffset.UTC).toOffsetDateTime().toString();
                } else {
                    yield cell.getNumericCellValue();
                }
            }
            case BLANK -> null;
            case ERROR -> formatter.formatCellValue(cell); // e.g., "#DIV/0!"
            default -> formatter.formatCellValue(cell, evaluator);
        };
    }
}
