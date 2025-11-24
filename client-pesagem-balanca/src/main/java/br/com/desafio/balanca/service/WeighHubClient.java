package main.java.br.com.desafio.balanca.service;

import com.google.gson.Gson;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class WeighHubClient {
    
    private static final Logger logger = LoggerFactory.getLogger(WeighHubClient.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private final OkHttpClient client;
    private final Gson gson;
    private final String serverUrl;
    
    public WeighHubClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }
    
    public SendResult sendWeighingRecord(String scaleId, String plate, BigDecimal weight) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("scaleId", scaleId);
            payload.put("plate", plate);
            payload.put("weight", weight);
            
            String jsonPayload = gson.toJson(payload);
            
            RequestBody body = RequestBody.create(jsonPayload, JSON);
            Request request = new Request.Builder()
                    .url(serverUrl + "/api/v1/weighing/records")
                    .post(body)
                    .build();
            
            logger.info("Sending weighing record: scaleId={}, plate={}, weight={}", scaleId, plate, weight);
            
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    logger.info("Successfully sent weighing record. Response: {}", responseBody);
                    return SendResult.success(responseBody);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    logger.error("Failed to send weighing record. Status: {}, Error: {}", response.code(), errorBody);
                    return SendResult.failure("Server error: " + response.code() + " - " + errorBody);
                }
            }
        } catch (IOException e) {
            logger.error("Network error while sending weighing record", e);
            return SendResult.failure("Network error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while sending weighing record", e);
            return SendResult.failure("Unexpected error: " + e.getMessage());
        }
    }
    
    public static class SendResult {
        private final boolean success;
        private final String message;
        
        private SendResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public static SendResult success(String message) {
            return new SendResult(true, message);
        }
        
        public static SendResult failure(String message) {
            return new SendResult(false, message);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
