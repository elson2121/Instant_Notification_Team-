package com.instantnotificationsystem.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SMSService {
    private static final String INFOBIP_BASE_URL = "shttps://gr6xp8.api.infobip.com";
    private static final String API_KEY = "s47833b1572b0b798e95284d5c872c485-52497ecb-ddba-4c00-b07a-fcda073442f4"; // Replace with your actual API key
    private static final String SENDER_ID = "INFO";
    private static final OkHttpClient client = new OkHttpClient();

    public static CompletableFuture<Boolean> sendBulkSMS(List<String> phoneNumbers, String message) {
        if (phoneNumbers == null || phoneNumbers.isEmpty() || message == null || message.trim().isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }

        JSONObject requestBody = new JSONObject();
        requestBody.put("messages", new JSONArray(phoneNumbers.stream()
                .map(phone -> {
                    JSONObject msg = new JSONObject();
                    JSONObject destination = new JSONObject();
                    destination.put("to", formatPhoneNumber(phone));

                    msg.put("from", SENDER_ID);
                    msg.put("destinations", new JSONArray().put(destination));
                    msg.put("text", message);
                    return msg;
                })
                .toList()));

        Request request = new Request.Builder()
                .url(INFOBIP_BASE_URL + "/sms/2/text/advanced")
                .addHeader("Authorization", "App " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .build();

        return CompletableFuture.supplyAsync(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("Failed to send SMS: " + response.body().string());
                    return false;
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    private static String formatPhoneNumber(String phone) {
        // Ensure the phone number is in international format (e.g., +251...)
        String digits = phone.replaceAll("\\D+", "");
        if (digits.startsWith("0")) {
            return "+251" + digits.substring(1);
        } else if (!digits.startsWith("+")) {
            return "+251" + digits;
        }
        return digits;
    }
}
