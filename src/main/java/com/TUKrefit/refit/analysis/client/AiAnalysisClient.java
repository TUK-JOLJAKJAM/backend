package com.TUKrefit.refit.analysis.client;

import com.TUKrefit.refit.analysis.exception.AnalysisException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Component
public class AiAnalysisClient {
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final URI analyzeUri;

    public AiAnalysisClient(
            @Value("${app.ai.base-url:http://localhost:8000}") String baseUrl
    ) {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        String normalized = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.analyzeUri = URI.create(normalized + "/api/v1/analyze_session");
    }

    public Map<String, Object> analyze(Map<String, Object> payload) {
        try {
            HttpRequest request = HttpRequest.newBuilder(analyzeUri)
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new AnalysisException(
                        HttpStatus.BAD_GATEWAY,
                        "AI_ANALYSIS_FAILED",
                        "AI server returned HTTP " + response.statusCode()
                );
            }
            return objectMapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
        } catch (AnalysisException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AnalysisException(HttpStatus.BAD_GATEWAY, "AI_ANALYSIS_UNAVAILABLE");
        } catch (Exception e) {
            throw new AnalysisException(HttpStatus.BAD_GATEWAY, "AI_ANALYSIS_UNAVAILABLE");
        }
    }
}
