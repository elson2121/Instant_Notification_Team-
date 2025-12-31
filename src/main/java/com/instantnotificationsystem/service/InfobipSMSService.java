package com.instantnotificationsystem.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InfobipSMSService {
    private static final String INFOBIP_BASE_URL = "https://gr6xp8.api.infobip.com";
    private static final String INFOBIP_API_URL = INFOBIP_BASE_URL + "/sms/2/text/advanced";
    private static final String SENDER_NAME = "Sami"; // Or your registered sender name
    private static final String CONFIG_FILE = "config.properties";
    private static final Logger LOGGER = Logger.getLogger(InfobipSMSService.class.getName());

    private final HttpClient httpClient;
    private final String apiKey;

    public InfobipSMSService() {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = loadApiKey();
    }

    private String loadApiKey() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            String key = properties.getProperty("infobip.api.key");
            if (key == null || key.trim().isEmpty()) {
                throw new IOException("API key not found in " + CONFIG_FILE);
            }
            return key;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load API key from " + CONFIG_FILE, e);
            throw new RuntimeException("Failed to load API key", e);
        }
    }

    public void sendSMS(String to, String message) {
        try {
            String jsonPayload = buildJsonPayload(to, message);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(INFOBIP_API_URL))
                    .header("Authorization", "App " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(System.out::println)
                    .exceptionally(e -> {
                        System.err.println("Failed to send SMS: " + e.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            System.err.println("Error creating SMS request: " + e.getMessage());
        }
    }

    private String buildJsonPayload(String to, String message) {
        return "{"
                + "\"messages\": ["
                + "{"
                + "\"from\": \"" + SENDER_NAME + "\","
                + "\"destinations\": ["
                + "{\"to\": \"" + to + "\"}"
                + "],"
                + "\"text\": \"" + escapeJson(message) + "\""
                + "}"
                + "]"
                + "}";
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}