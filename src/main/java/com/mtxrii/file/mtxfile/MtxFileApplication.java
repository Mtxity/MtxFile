package com.mtxrii.file.mtxfile;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MtxFileApplication {
    private static final Gson GSON = new Gson();

    static void main(String[] args) {
        SpringApplication.run(MtxFileApplication.class, args);
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }
}
