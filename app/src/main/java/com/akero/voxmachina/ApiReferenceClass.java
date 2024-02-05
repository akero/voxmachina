package com.akero.voxmachina;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.chromium.net.CronetEngine;
import org.chromium.net.UploadDataProvider;
import org.chromium.net.UploadDataSink;
import org.chromium.net.UrlRequest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ApiReferenceClass {

    private MutableLiveData<SendOtpResponseModel> successresponse = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public MutableLiveData<SendOtpResponseModel> getSuccessresponse() {
        return successresponse;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    Executor cronetExecutor = Executors.newSingleThreadExecutor();

    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";


    public ApiReferenceClass(String prompt, Context context){
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

        Map<String, String> headers= new HashMap<>();
        headers.put("Authorization", "Bearer " + API_KEY);
        headers.put("Content-Type", "application/json");

        callapi(headers, json.toString(), context, 0, API_URL);

    }

    public void callapi(Map<String, String> headers, String jsonPayload, Context context, int querytype, String url) {

        try {
            //adding api here
            Log.d("tag21","2");

            CronetEngine.Builder builder = new CronetEngine.Builder(context);
            CronetEngine cronetEngine = builder.build();

            // Create a JSON payload with the email and password
            //String jsonPayload = "{\"email\":\"" + email + "\",\"password\":\"" + pass + "\"}";

            // Convert the JSON payload to bytes for uploading
            Log.d("tag21",jsonPayload);
            final byte[] postData = jsonPayload.getBytes(StandardCharsets.UTF_8);


            // Create an upload data provider to send the POST data
            UploadDataProvider uploadDataProvider = new UploadDataProvider() {
                @Override
                public long getLength() throws IOException {
                    return postData.length;
                }

                @Override
                public void read(UploadDataSink uploadDataSink, ByteBuffer byteBuffer) throws IOException {
                    byteBuffer.put(postData);
                    uploadDataSink.onReadSucceeded(false);
                }

                @Override
                public void rewind(UploadDataSink uploadDataSink) throws IOException {
                    uploadDataSink.onRewindSucceeded();
                }

                @Override
                public void close() throws IOException {
                    // No-op
                }
            };

            UrlRequest.Builder requestBuilder;
            requestBuilder = cronetEngine.newUrlRequestBuilder(
                            url, new MyUrlRequestCallback((ApiInterface) context), cronetExecutor)
                    .setHttpMethod("POST")
                    .addHeader("Content-Type", "application/json")  // Indicate we're sending JSON data
                    .setUploadDataProvider(uploadDataProvider, cronetExecutor);  // Attach the payload

            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }

            UrlRequest request = requestBuilder.build();
            request.start();Log.d("tag21","4");
        } catch (Exception e) {
            Log.d("tag21", e.toString());
        }
    }
}
