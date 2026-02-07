package com.mtxrii.file.mtxfile.api.controller;

import com.mtxrii.file.mtxfile.api.model.JsonifyResponse;
import com.mtxrii.file.mtxfile.api.model.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/csv")
public class CsvController {
    private static final String FILE_PARAM = "file";

    @PostMapping(
            value = "/analytics/descript",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> handleAnalyticsDescript(
            @RequestParam(FILE_PARAM) MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        List<Map<String, String>> json = this.csvService.jsonifyCsv(file);
        JsonifyResponse jsonifyResponse = new JsonifyResponse(file.getOriginalFilename(), json);
        Response response = jsonifyResponse.path(request.getRequestURI());
        return ResponseEntity.status(200).body(response);
    }
}
