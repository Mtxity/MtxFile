package com.mtxrii.file.mtxfile.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/download")
public class DownloadController {
    private final WebClient webClient;

    public DownloadController() {
        this.webClient = WebClient.builder().build();
    }
}
