package com.mtxrii.file.mtxfile.api.controller;

import com.mtxrii.file.mtxfile.api.model.Response;
import com.mtxrii.file.mtxfile.api.service.UploadService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            @RequestParam(FILE_PARAM) MultipartFile file,
            @RequestParam(name = "password", required = false) String password
    ) {
        Response uploadContentsResponse = uploadService.uploadFile(file, password);
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
            @PathVariable String fileName,
            @RequestParam(name = "password", required = false) String password
    ) {
        Response uploadContentsResponse = this.uploadService.getUploadedFilePreview(fileName, password);
        return ResponseEntity
                .status(uploadContentsResponse.getCode())
                .body(uploadContentsResponse);
    }

    @DeleteMapping(
            value = "/delete/{fileName}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> handleDeleteContents(
            @PathVariable String fileName,
            @RequestParam(name = "password", required = false) String password
    ) {
        Response deleteContentsResponse = this.uploadService.deleteUploadedFile(fileName, password);
        return ResponseEntity
                .status(deleteContentsResponse.getCode())
                .body(deleteContentsResponse);
    }

    // @TODO: Test this
    @GetMapping(
            value = "/download/{fileName}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> handleDownloadContents(
            @PathVariable String fileName,
            @RequestParam(name = "password", required = false) String password
    ) {
        UploadService.DownloadOutcome outcome = uploadService.getUploadedFileForDownload(fileName, password);
        if (outcome.response() != null) {
            return ResponseEntity
                    .status(outcome.response().getCode())
                    .body(outcome.response());
        }

        try {
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + outcome.file().getOriginalFilename() + "\""
                    )
                    .body(new InputStreamResource(outcome.file().getInputStream()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
