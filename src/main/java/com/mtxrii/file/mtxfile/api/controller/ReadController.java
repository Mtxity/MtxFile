package com.mtxrii.file.mtxfile.api.controller;

import com.mtxrii.file.mtxfile.api.model.ReadContentsResponse;
import com.mtxrii.file.mtxfile.api.service.ReadService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/read")
public class ReadController {
    private final ReadService readService;

    public ReadController(ReadService readService) {
        this.readService = readService;
    }

    @PostMapping(path = "/contents", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReadContentsResponse> handleReadContents(@RequestParam("file") MultipartFile file) throws IOException {
        String text = new String(file.getBytes(), StandardCharsets.UTF_8);
        return ResponseEntity
                .status(200)
                .body(new ReadContentsResponse(true, 200, file.getOriginalFilename(), text));
    }

    @PostMapping(
            value = "/jsonify/csv",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Map<String, String>>> handleJsonifyCsv(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        List<Map<String, String>> json = this.readService.jsonifyCsv(file);
        return ResponseEntity.status(200).body(json);
    }
}
