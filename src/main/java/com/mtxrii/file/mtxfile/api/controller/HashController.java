package com.mtxrii.file.mtxfile.api.controller;

import com.mtxrii.file.mtxfile.api.model.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hashing")
public class HashController {

    @GetMapping("/hash")
    ResponseEntity<Response> handleHash(
            @RequestParam("value") String value
    ) {
        //
    }

    @GetMapping("/verify")
    ResponseEntity<Response> handleVerify(
            @RequestParam("hash") String hash,
            @RequestParam("value") String value
    ) {
        //
    }
}
