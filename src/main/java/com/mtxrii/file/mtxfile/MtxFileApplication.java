package com.mtxrii.file.mtxfile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mtxrii.file.mtxfile.api.model.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MtxFileApplication {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static void main(String[] args) {
        SpringApplication.run(MtxFileApplication.class, args);
    }

    public static String toJson(Response response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException jpe) {
            return jpe.getMessage();
        }
    }
}
