package com.akero.voxmachina;
import okhttp3.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class APIclass {
    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public static void makeRequest(String prompt, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "You are a helpful assistant.");

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", prompt);

        JsonArray messagesArray = new JsonArray();
        messagesArray.add(systemMessage);
        messagesArray.add(userMessage);


        JsonObject json = new JsonObject();
        json.addProperty("model", "gpt-4");
        json.add("messages", messagesArray);
        //json.addProperty("prompt", prompt);
        json.addProperty("max_tokens", 50);

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
