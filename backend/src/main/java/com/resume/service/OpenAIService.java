package com.resume.service;

import okhttp3.*;
import com.google.gson.*;
import org.springframework.stereotype.Service;

@Service
public class OpenAIService {

    // ✅ Hardcoded API Key
    String apiKey = System.getenv("API_KEY");

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public String analyzeResume(String prompt) {

        try {
            OkHttpClient client = new OkHttpClient();

            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.addProperty("content", prompt);

            JsonArray messages = new JsonArray();
            messages.add(message);

            JsonObject body = new JsonObject();
            body.addProperty("model", "llama-3.3-70b-versatile");
            body.add("messages", messages);

           String apiKey = System.getenv("API_KEY");

Request request = new Request.Builder()
        .url("https://api.groq.com/openai/v1/chat/completions")
        .addHeader("Authorization", "Bearer " + apiKey)
        .addHeader("Content-Type", "application/json")
        .post(RequestBody.create(body.toString(), JSON))
        .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body() != null ? response.body().string() : "";

            if (!response.isSuccessful()) {
                return "API Error (" + response.code() + "): " + responseBody;
            }

            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

            return json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();

        } catch (Exception e) {
            return "Error connecting to AI: " + e.getMessage();
        }
    }
}