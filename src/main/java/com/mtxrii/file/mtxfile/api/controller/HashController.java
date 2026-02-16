package com.mtxrii.file.mtxfile.api.controller;

import com.mtxrii.file.mtxfile.api.model.Response;
import com.mtxrii.file.mtxfile.api.service.HashService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hashing")
public class HashController {
    private final HashService hashService;

    public HashController(HashService hashService) {
        this.hashService = hashService;
    }

    @GetMapping("/hash")
    ResponseEntity<Response> handleHash(
            @RequestParam("value") String value
    ) {
        Response hashResponse = this.hashService.hash(value);
        return ResponseEntity.status(hashResponse.getCode())
                             .body(hashResponse);
    }

    @GetMapping("/verify")
    ResponseEntity<Response> handleVerify(
            @RequestParam("hash") String hash,
            @RequestParam("value") String value
    ) {
        Response verifyResponse = this.hashService.verify(value, hash);
        return ResponseEntity.status(verifyResponse.getCode())
                             .body(verifyResponse);
    }
}
