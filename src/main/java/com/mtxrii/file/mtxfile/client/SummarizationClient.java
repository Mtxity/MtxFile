package com.mtxrii.file.mtxfile.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SummarizationClient {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String apiKey;

    public SummarizationClient() {
        this.webClient = WebClient.builder()
                                  .baseUrl("https://api.openai.com/v1")
                                  .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                  .build();
        this.objectMapper = new ObjectMapper();
    }

    public String summarize(String text) {
        String requestBody =
        """
        {
          "model": "gpt-4.1-mini",
          "input": "Summarize the following text:\\n\\n%s"
        }
        """.formatted(escapeJson(text));

        String response = webClient.post()
                                   .uri("/chat/completions")
                                   .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.apiKey)
                                   .bodyValue(requestBody)
                                   .retrieve()
                                   .bodyToMono(String.class)
                                   .block();

        return extractSummary(response);
    }

    private String extractSummary(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            return root.path("choices")
                       .get(0)
                       .path("message")
                       .path("content")
                       .asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse summary response", e);
        }
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n");
    }
}
