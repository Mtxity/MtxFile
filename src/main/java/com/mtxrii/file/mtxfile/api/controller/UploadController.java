package com.mtxrii.file.mtxfile.api.controller;

import com.mtxrii.file.mtxfile.api.service.UploadService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping(path = "/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleUpload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity
                .status(200)
                .body("{\"fileName\": \"" + file.getOriginalFilename() + "\"}");
    }
}
