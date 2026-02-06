package com.mtxrii.file.mtxfile.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.mtxrii.file.mtxfile.api.model.ErrorResponse;
import com.mtxrii.file.mtxfile.api.model.HashContentsResponse;
import com.mtxrii.file.mtxfile.api.model.JsonifyResponse;
import com.mtxrii.file.mtxfile.api.model.ReadContentsResponse;
import com.mtxrii.file.mtxfile.api.model.Response;
import com.mtxrii.file.mtxfile.api.model.SummarizedContentsResponse;
import com.mtxrii.file.mtxfile.api.model.WordCountResponse;
import com.mtxrii.file.mtxfile.api.model.enumeration.HashType;
import com.mtxrii.file.mtxfile.api.service.ReadService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/read")
public class ReadController {
    private static final String FILE_PARAM = "file";

    private final ReadService readService;

    public ReadController(ReadService readService) {
        this.readService = readService;
    }

    @PostMapping(
            value = "/contents",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> handleReadContents(
            @RequestParam(FILE_PARAM) MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        ReadContentsResponse readContentsResponse = this.readService.readContents(file);
        Response response = readContentsResponse.path(request.getRequestURI());
        return ResponseEntity
                .status(200)
                .body(response);
    }

    @PostMapping(
            value = "/jsonify/csv",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> handleJsonifyCsv(
            @RequestParam(FILE_PARAM) MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        List<Map<String, String>> json = this.readService.jsonifyCsv(file);
        JsonifyResponse jsonifyResponse = new JsonifyResponse(file.getOriginalFilename(), json);
        Response response = jsonifyResponse.path(request.getRequestURI());
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping(
            value = "/jsonify/xls",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> handleJsonifyXls(
            @RequestParam(FILE_PARAM) MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        Map<String, List<Map<String, Object>>> json = this.readService.jsonifyXls(file);
        JsonifyResponse jsonifyResponse = new JsonifyResponse(file.getOriginalFilename(), json);
        Response response = jsonifyResponse.path(request.getRequestURI());
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping(
            value = "/jsonify/xml",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> handleJsonifyXml(
            @RequestParam(FILE_PARAM) MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        JsonNode json = this.readService.jsonifyXml(file);
        JsonifyResponse jsonifyResponse = new JsonifyResponse(file.getOriginalFilename(), json);
        Response response = jsonifyResponse.path(request.getRequestURI());
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping(
            value = "/jsonify/yml",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> handleJsonifyYml(
            @RequestParam(FILE_PARAM) MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        JsonNode json = this.readService.jsonifyYml(file);
        JsonifyResponse jsonifyResponse = new JsonifyResponse(file.getOriginalFilename(), json);
        Response response = jsonifyResponse.path(request.getRequestURI());
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping(
            value = "/wordCount",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> handleWordCount(
            @RequestParam(FILE_PARAM) MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        int wordCount = this.readService.wordCount(file);
        WordCountResponse wordCountResponse = new WordCountResponse(file.getOriginalFilename(), wordCount);
        Response response = wordCountResponse.path(request.getRequestURI());
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping(
            value = "/summarize",
            consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> handleSummarizeContents(
            @RequestParam(FILE_PARAM) MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        SummarizedContentsResponse readContentsResponse = this.readService.summarizeContents(file);
        Response response = readContentsResponse.path(request.getRequestURI());
        return ResponseEntity
                .status(200)
                .body(response);
    }

    @PostMapping(
            value = "/hash",
            consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response> handleHashContents(
            @RequestParam(FILE_PARAM) MultipartFile file,
            @RequestParam(name = "hashAlg", required = false, defaultValue = HashType.DEFAULT_BY_KEY) String hashAlg,
            @RequestParam(name = "salt", required = false, defaultValue = "") String salt,
            @RequestParam(name = "times", required = false, defaultValue = "1") String times,
            HttpServletRequest request
    ) throws IOException {
        HashContentsResponse readContentsResponse = this.readService.hashContents(file, hashAlg, salt);
        if (!times.equals("1")) {
            for (int i = 0; i < Math.min(Integer.parseInt(times), 500); i++) {
                readContentsResponse = this.readService.hashContents(file, hashAlg, readContentsResponse.hash + salt);
            }
        }
        Response response = readContentsResponse.path(request.getRequestURI());
        return ResponseEntity
                .status(200)
                .body(response);
    }
}
