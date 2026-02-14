package com.mtxrii.file.mtxfile.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.mtxrii.file.mtxfile.api.model.enumeration.FileType;
import com.mtxrii.file.mtxfile.api.model.HashContentsResponse;
import com.mtxrii.file.mtxfile.api.model.ReadContentsResponse;
import com.mtxrii.file.mtxfile.api.model.SummarizedContentsResponse;
import com.mtxrii.file.mtxfile.api.model.enumeration.HashType;
import com.mtxrii.file.mtxfile.client.SummarizationClient;
import com.mtxrii.file.mtxfile.util.FileUtil;
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
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class ReadService {
    private static final XmlMapper XML_MAPPER = new XmlMapper();
    private static final YAMLMapper YAML_MAPPER = new YAMLMapper();

    private final SummarizationClient summarizationClient;

    public ReadService(SummarizationClient summarizationClient) {
        this.summarizationClient = summarizationClient;
    }

    public ReadContentsResponse readContents(MultipartFile file) throws IOException {
        this.validateFileAndExtension(file, ".txt", ".md", ".pdf");
        String fileContents;
        if (FileUtil.getFileType(file) == FileType.PDF) {
            try (PDDocument document = PDDocument.load(file.getInputStream())) {
                PDFTextStripper stripper = new PDFTextStripper();
                fileContents = stripper.getText(document).trim();
            }
        } else {
            fileContents = new String(file.getBytes(), StandardCharsets.UTF_8);
        }

        Metadata metadata = new Metadata();
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, file.getOriginalFilename());
        Map<String, Object> foundMeta;
        try (InputStream is = file.getInputStream()) {
            AutoDetectParser parser = new AutoDetectParser();
            parser.parse(is, new BodyContentHandler(-1), metadata);
            foundMeta = new LinkedHashMap<>();
            for (String key : metadata.names()) {
                foundMeta.put(key, metadata.get(key));
            }
        } catch (SAXException | TikaException e) {
            foundMeta = null;
        }

        return new ReadContentsResponse(
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType(),
                fileContents,
                foundMeta
        );
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

    public int wordCount(MultipartFile file) throws IOException {
        this.validateFileAndExtension(file, ".txt", ".md", ".pdf", ".csv", ".xls", ".xlsx", ".xml", ".yml", ".yaml");
        return switch (FileUtil.getFileType(file)) {
            case CSV -> {
                List<Map<String, String>> contents = this.jsonifyCsv(file);
                int words = 0;
                if (contents.isEmpty()) {
                    yield words;
                }
                for (String headers : contents.getFirst().keySet()) {
                    words += headers.split(" ").length;
                }
                for (Map<String, String> row : contents) {
                    for (String cell : row.values()) {
                        words += cell.split(" ").length;
                    }
                }
                yield words;
            }
            case EXCEL, EXCEL_MAC -> {
                Map<String, List<Map<String, Object>>> contents = this.jsonifyXls(file);
                int words = 0;
                if (contents.isEmpty()) {
                    yield words;
                }
                for (List<Map<String, Object>> sheet : contents.values()) {
                    for (String headers : sheet.getFirst().keySet()) {
                        words += headers.split(" ").length;
                    }
                    for (Map<String, Object> row : sheet) {
                        for (Object cell : row.values()) {
                            if (cell instanceof String) {
                                words += ((String) cell).split(" ").length;
                            } else {
                                words ++;
                            }
                        }
                    }
                }
                yield words;
            }
            case XML -> {
                JsonNode contents = this.jsonifyXml(file);
                if (contents.isEmpty()) {
                    yield 0;
                } else {
                    yield FileUtil.xmlJsonNodeWordCount(contents, 0);
                }
            }
            case YML, YAML -> {
                String contents = new String(file.getBytes(), StandardCharsets.UTF_8);
                yield contents.replaceAll("\n", " ").replaceAll("-", "").split("\\s+").length;
            }
            case TEXT, MARKDOWN -> {
                String contents = new String(file.getBytes(), StandardCharsets.UTF_8);
                yield contents.replaceAll("\n", " ").split("\\s+").length;
            }
            case PDF -> {
                String contents;
                try (PDDocument document = PDDocument.load(file.getInputStream())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    contents = stripper.getText(document).trim();
                }
                yield contents.replaceAll("\n", " ").replaceAll("-", "").split("\\s+").length;
            }
            case UNKNOWN -> 0;
        };
    }

    public SummarizedContentsResponse summarizeContents(MultipartFile file) throws IOException {
        ReadContentsResponse contents = this.readContents(file);
        String summary = this.summarizationClient.summarize(contents.getContents());
        return new SummarizedContentsResponse(
                file.getOriginalFilename(),
                summary
        );
    }

    public HashContentsResponse hashContents(MultipartFile file, String hashAlg, String salt) throws IOException {
        ReadContentsResponse contents = this.readContents(file);
        hashAlg = hashAlg.toUpperCase(Locale.ROOT);
        HashType hashAlgToUse = HashType.fromKey(Optional.of(hashAlg).orElse("SHA-256"));
        if (hashAlgToUse == null) {
            throw new IllegalArgumentException("Invalid hash algorithm: " + hashAlg);
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(hashAlgToUse.getKey());
            String contentsToHash = contents.getContents() + salt;
            byte[] hashBytes = digest.digest(contentsToHash.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            String hashedVal = hexString.toString();
            return new HashContentsResponse(file.getOriginalFilename(), hashedVal);
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException(hashAlg + " algorithm not available", nsae);
        } catch (NullPointerException npe) {
            throw new RuntimeException(hashAlg + " algorithm unknown", npe);

        }
    }

    public void validateFileAndExtension(MultipartFile file, String... validExtensions) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse("").toLowerCase(Locale.ROOT);
        for (String validExtension : validExtensions) {
            if (fileName.endsWith(validExtension.toLowerCase(Locale.ROOT))) {
                return;
            }
        }
        throw new IllegalArgumentException("Only " + this.stringJoin(", ", " or ", validExtensions) + " files are supported");
    }

    protected String stringJoin(String delimiter, String lastDelimiter, String... elements) {
        if (delimiter == null || lastDelimiter == null || elements == null) {
            throw new NullPointerException();
        }

        int len = elements.length;
        if (len == 0) {
            return "";
        } else if (len == 1) {
            if (elements[0] == null) {
                throw new NullPointerException();
            }
            return elements[0];
        } else if (len == 2) {
            if (elements[0] == null || elements[1] == null) {
                throw new NullPointerException();
            }
            return elements[0] + lastDelimiter + elements[1];
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i++) {
            String s = elements[i];
            if (s == null) {
                throw new NullPointerException();
            }

            if (i > 0) {
                sb.append(i == len - 1 ? lastDelimiter : delimiter);
            }
            sb.append(s);
        }

        return sb.toString();
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
