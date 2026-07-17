package com.TUKrefit.refit.analysis.client;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiAnalysisClientTest {
    private HttpServer server;
    private final AtomicReference<String> requestProtocol = new AtomicReference<>();
    private final AtomicReference<String> requestBody = new AtomicReference<>();

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/v1/analyze_session", this::handleAnalysis);
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void sendsAnalysisAsHttp11Json() {
        String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
        AiAnalysisClient client = new AiAnalysisClient(baseUrl);

        Map<String, Object> result = client.analyze(Map.of(
                "historyId", "history-v2",
                "schemaVersion", "2.0",
                "gameData", java.util.List.of(Map.of("actionId", "1"))
        ));

        assertEquals("HTTP/1.1", requestProtocol.get());
        assertTrue(requestBody.get().contains("\"schemaVersion\":\"2.0\""));
        assertEquals("analysis-test", result.get("analysis_id"));
    }

    private void handleAnalysis(HttpExchange exchange) throws IOException {
        requestProtocol.set(exchange.getProtocol());
        requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
        byte[] response = "{\"analysis_id\":\"analysis-test\"}".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }
}
