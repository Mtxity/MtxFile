package com.mtxrii.file.mtxfile.api.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@RestController
@RequestMapping("/download")
public class DownloadController {
    private final WebClient webClient;

    public DownloadController() {
        this.webClient = WebClient.builder().build();
    }

    @GetMapping("/direct/v1")
    ResponseEntity<ByteArrayResource> handleDownloadDirectV1(
            @RequestParam("url") String url
    ) {
        byte[] fileBytes = webClient.get()
                                    .uri(URI.create(url))
                                    .retrieve()
                                    .bodyToMono(byte[].class)
                                    .block();

        if (fileBytes == null) {
            return ResponseEntity.badRequest().build();
        }

        int slashPos = url.lastIndexOf('/');
        String filename = (slashPos >= 0) ? url.substring(slashPos + 1) : "downloaded-file";

        ByteArrayResource resource = new ByteArrayResource(fileBytes);
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                             .contentType(MediaType.APPLICATION_OCTET_STREAM)
                             .contentLength(fileBytes.length)
                             .body(resource);
    }

    @GetMapping("/direct/v2")
    public ResponseEntity<InputStreamResource> handleDownloadDirectV2(
            @RequestParam("url") String fileUrl
    ) throws Exception {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(30_000);

        int status = connection.getResponseCode();
        if (status >= 400) {
            throw new IllegalArgumentException("Failed to download file. HTTP status: " + status);
        }

        InputStream inputStream = connection.getInputStream();

        String contentType = connection.getContentType();
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        int slashPos = fileUrl.lastIndexOf('/');
        String filename = (slashPos >= 0) ? fileUrl.substring(slashPos + 1) : "downloaded-file";

        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                             .contentType(MediaType.parseMediaType(contentType))
                             .body(new InputStreamResource(inputStream));
    }
}
