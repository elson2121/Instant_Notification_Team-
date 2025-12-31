package com.instantnotificationsystem.service;

import okhttp3.*;
import java.io.IOException;

public class InfobipEmailService {
    private static final String INFOBIP_BASE_URL = "https://gr6xp8.api.infobip.com";
    private static final String API_KEY = "47833b1572b0b798e95284d5c872c485-52497ecb-ddba-4c00-b07a-fcda073442f4";

    // Name changed to match your trial endpoint
    private static final String INFOBIP_API_URL = INFOBIP_BASE_URL + "/email/3/send";

    // Name changed to your specific trial sender from image_1218e6.png
    private static final String SENDER_EMAIL = "yooomiki89@selfserve.worlds-connected.co";

    private final OkHttpClient httpClient;

    public InfobipEmailService() {
        this.httpClient = new OkHttpClient();
    }

    public void sendEmail(String to, String subject, String text) {
        try {
            // Keeping your original MultipartBody format
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("from", SENDER_EMAIL)
                    .addFormDataPart("to", to)
                    .addFormDataPart("subject", subject)
                    .addFormDataPart("text", text)
                    .build();

            Request request = new Request.Builder()
                    .url(INFOBIP_API_URL)
                    .header("Authorization", "App " + API_KEY)
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