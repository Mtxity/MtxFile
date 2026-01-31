package com.mtxrii.file.mtxfile.api.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

@RestController
@RequestMapping("/download")
public class DownloadController {
    private final WebClient webClient;

    public DownloadController() {
        this.webClient = WebClient.builder().build();
    }

    @GetMapping(
            value = "/direct",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    ResponseEntity<ByteArrayResource> handleDownloadDirect(
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
}
