package com.instantnotificationsystem.service;

import okhttp3.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InfobipEmailService {
    private static final String INFOBIP_BASE_URL = "https://gr6xp8.api.infobip.com";
    private static final String INFOBIP_API_URL = INFOBIP_BASE_URL + "/email/3/send";
    private static final String SENDER_EMAIL = "yooomiki89@selfserve.worlds-connected.co";
    private static final String CONFIG_FILE = "config.properties";
    private static final Logger LOGGER = Logger.getLogger(InfobipEmailService.class.getName());

    private final OkHttpClient httpClient;
    private final String apiKey;

    public InfobipEmailService() {
        this.httpClient = new OkHttpClient();
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

    public void sendEmail(String to, String subject, String text) {
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("from", SENDER_EMAIL)
                    .addFormDataPart("to", to)
                    .addFormDataPart("subject", subject)
                    .addFormDataPart("text", text)
                    .build();

            Request request = new Request.Builder()
                    .url(INFOBIP_API_URL)
                    .header("Authorization", "App " + apiKey)
                    .post(requestBody)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.err.println("Failed to send email: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (!response.isSuccessful()) {
                        System.err.println("Failed to send email: " + responseBody);
                    } else {
                        System.out.println("Success! Response: " + responseBody);
                    }
                    response.close();
                }
            });

        } catch (Exception e) {
            System.err.println("Error creating email request: " + e.getMessage());
        }
    }
}