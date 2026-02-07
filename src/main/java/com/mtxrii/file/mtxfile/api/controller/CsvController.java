package com.mtxrii.file.mtxfile.api.controller;

import com.mtxrii.file.mtxfile.api.model.CsvDescriptResponse;
import com.mtxrii.file.mtxfile.api.model.Response;
import com.mtxrii.file.mtxfile.api.service.CsvService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/csv")
public class CsvController {
    private static final String FILE_PARAM = "file";

    private final CsvService csvService;

    public CsvController(CsvService csvService) {
        this.csvService = csvService;
    }

    @PostMapping(
            value = "/analytics/descript",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> handleAnalyticsDescript(
            @RequestParam(FILE_PARAM) MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        CsvDescriptResponse csvDescriptResponse = this.csvService.descriptCsv(file);
        Response response = csvDescriptResponse.path(request.getRequestURI());
        return ResponseEntity.status(200).body(response);
    }
}
