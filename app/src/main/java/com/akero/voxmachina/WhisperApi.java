package com.akero.voxmachina;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class WhisperApi {
    private static final String BASE_URL = "https://api.openai.com/";
    private static WhisperService whisperService;

    public static WhisperService getWhisperService() {
        if (whisperService == null) {
            // Optional: Add Logging Interceptor for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            whisperService = retrofit.create(WhisperService.class);
        }
        return whisperService;
    }
}
