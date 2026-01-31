package com.mtxrii.file.mtxfile.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

@Service
public class DownloadService {
    private final WebClient webClient;

    public DownloadService() {
        this.webClient = WebClient.builder().build();
    }

    public byte[] downloadDirectV1(String url) {
        return webClient.get()
                        .uri(URI.create(url))
                        .retrieve()
                        .bodyToMono(byte[].class)
                        .block();
    }

    public String getContentDispositionHeader(String url) {
        int slashPos = url.lastIndexOf('/');
        String filename = (slashPos >= 0) ? url.substring(slashPos + 1) : "downloaded-file";
        return "attachment; filename=\"" + filename + "\"";
    }
}
