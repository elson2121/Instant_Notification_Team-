package com.instantnotificationsystem.service;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InfobipSMSService {
    private static final String INFOBIP_BASE_URL = "https://gr6xp8.api.infobip.com";
    private static final String API_KEY = "47833b1572b0b798e95284d5c872c485-52497ecb-ddba-4c00-b07a-fcda073442f4";
    private static final String INFOBIP_API_URL = INFOBIP_BASE_URL + "/sms/2/text/advanced";
    private static final String SENDER_NAME = "Sami"; // Or your registered sender name

    private final HttpClient httpClient;
    private final Gson gson;

    public InfobipSMSService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public void sendSMS(String to, String message) {
        try {
            Map<String, Object> destination = new HashMap<>();
            destination.put("to", to);

            Map<String, Object> messagePayload = new HashMap<>();
            messagePayload.put("from", SENDER_NAME);
            messagePayload.put("destinations", Collections.singletonList(destination));
            messagePayload.put("text", message);

            Map<String, Object> payload = new HashMap<>();
            payload.put("messages", Collections.singletonList(messagePayload));

            String jsonPayload = gson.toJson(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(INFOBIP_API_URL))
                    .header("Authorization", "App " + API_KEY)
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
}
