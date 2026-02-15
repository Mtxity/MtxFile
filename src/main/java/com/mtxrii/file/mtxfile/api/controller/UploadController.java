package com.mtxrii.file.mtxfile.api.controller;

import com.mtxrii.file.mtxfile.api.model.Response;
import com.mtxrii.file.mtxfile.api.model.UploadContentsResponse;
import com.mtxrii.file.mtxfile.api.service.UploadService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {
    private static final String FILE_PARAM = "file";

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping(
            value = "/contents",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> handleUploadContents(
            @RequestParam(FILE_PARAM) MultipartFile file
    ) {
        Response uploadContentsResponse = uploadService.uploadFile(file);
        return ResponseEntity
                .status(uploadContentsResponse.getCode())
                .body(uploadContentsResponse);
    }

    @GetMapping(
            value = "/contents/{fileName}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> handleGetContents(
            @RequestParam("fileName") String fileName
    ) {
        Response uploadContentsResponse = this.uploadService.getUploadedFilePreview(fileName);
        return ResponseEntity
                .status(uploadContentsResponse.getCode())
                .body(uploadContentsResponse);
    }
}
