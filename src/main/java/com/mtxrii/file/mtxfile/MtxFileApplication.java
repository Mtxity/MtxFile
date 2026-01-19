package com.mtxrii.file.mtxfile;

import com.google.gson.Gson;
import com.mtxrii.file.mtxfile.api.model.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MtxFileApplication {
    private static final Gson GSON = new Gson();

    static void main(String[] args) {
        SpringApplication.run(MtxFileApplication.class, args);
    }

    public static String toJson(Response response) {
        return GSON.toJson(response);
    }
}
