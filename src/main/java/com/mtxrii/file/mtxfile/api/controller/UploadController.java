package com.mtxrii.file.mtxfile.api.controller;

import com.mtxrii.file.mtxfile.api.service.UploadService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
public class UploadController {
    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping(path = "/test/read", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleUpload(@RequestParam("file") MultipartFile file) throws IOException {
        String text = new String(file.getBytes(), StandardCharsets.UTF_8);
        return ResponseEntity
                .status(200)
                .body("{\"fileName\": \"" + file.getOriginalFilename() + "\", \"contents\": \"" + text + "\"}");
    }

    @PostMapping(
            value = "/convert/csv",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Map<String, String>>> handleConvertCsv(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        List<Map<String, String>> json = UploadService.handleConvertCsv(file);
        return ResponseEntity.status(200).body(json);
    }
}
