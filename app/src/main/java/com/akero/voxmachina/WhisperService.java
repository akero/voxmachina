package com.akero.voxmachina;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface WhisperService {
    @POST("https://api.openai.com/v1/whisper/asr")
    Call<ResponseBody> transcribeAudio(@Header("Authorization") String authorization, @Body RequestBody audioFile);
}
