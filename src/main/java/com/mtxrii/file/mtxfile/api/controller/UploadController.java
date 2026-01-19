package com.mtxrii.file.mtxfile.api.controller;

import com.mtxrii.file.mtxfile.api.service.UploadService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UploadController {
    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping(path = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleUpload() {
        return ResponseEntity
                .status(200)
                .body("{\"test\": true}");
    }
}
