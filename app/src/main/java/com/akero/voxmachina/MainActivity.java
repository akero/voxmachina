package com.akero.voxmachina;

import android.Manifest;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import retrofit2.Callback;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.RequestBody;
//import okhttp3.Response;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1234;
    //private static final int PERMISSION_REQUEST_CODE_POST_NOTIFICATIONS = 5678;

    private static final String API_KEY= "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";
    private NotificationHelper notificationHelper; // Declare the notification helper

    private EditText editText;
    private String inputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request POST_NOTIFICATIONS permission
        if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.POST_NOTIFICATIONS"}, PERMISSION_REQUEST_CODE);
        }



        notificationHelper = new NotificationHelper(this); // Initialize the helper

        // Create the notification channel
        createNotificationChannel();
        checkAndRequestPermissions();

        editText = findViewById(R.id.edt_input);

        // Set up the test button
        Button testButton = findViewById(R.id.btn_test_notification);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testNotification();
            }
        });

        // Set up the accept input button
        Button acceptInputButton = findViewById(R.id.btn_accept_input);
        acceptInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptInput();
            }
        });

        //calling api
        String prompt="Translate the following English text to French: 'Hello, World!'"; //TODO: accept prompt as input
        callapi(prompt);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "POST_NOTIFICATIONS permission granted");
            } else {
                Log.d("MainActivity", "POST_NOTIFICATIONS permission denied");
            }
        }
    }

    void callapi(String prompt){
        APIclass.makeRequest(prompt, new Callback<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    InputStreamReader reader = new InputStreamReader(response.body().byteStream());
                    BufferedReader br = new BufferedReader(reader);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    reader.close();
                    String responseBody = sb.toString();
                    Log.d("tag8", responseBody);
                    // Parse JSON and update UI
                    parsejson(responseBody);
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle the exception
                }
            }


        } //TODO here
    }


    //send audio bytearray then this fn will send it to whisper and get the response
    private void transcribeAudio(byte[] audioData) {
        RequestBody audioFile = RequestBody.create(audioData, MediaType.parse("audio/wav"));

        WhisperApi.getWhisperService().transcribeAudio(API_KEY, audioFile).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Handle successful response
                        String transcribedText = response.body().string();
                        // Do something with the transcribed text
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Handle the exception
                    }
                } else {
                    // Handle error
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
            }
        });
    }
    void parsejson(String responseBody){
        String messageToShow = "";
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONArray choicesArray = jsonObject.getJSONArray("choices");
            if(choicesArray.length() > 0) {
                JSONObject choiceObject = choicesArray.getJSONObject(0);
                JSONObject messageObject = choiceObject.getJSONObject("message");
                messageToShow = messageObject.getString("content");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("NotificationDebug", "Parsed Message: " + messageToShow);

        // Send a notification with the parsed message
        notificationHelper.sendNotification(messageToShow);
    }

    void sendNotificationWithText(String title, String content) {
        Log.d("NotificationDebug", "SendNotification Method Entered");

        // Creating a notification channel
        String channelId = "notifyMe";  // Ensure this ID is used when creating the notification channel

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("NotificationDebug", "Permission Granted");

            // Building the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.notification_icon)  // Ensure this icon exists in your drawables
                    .setContentTitle(title)
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // Obtaining the NotificationManager service
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            // Attempting to send the notification
            try {
                notificationManager.notify(1, builder.build());
                Log.d("NotificationDebug", "Notification Sent");
            } catch (SecurityException e) {
                // Handle the SecurityException
                e.printStackTrace();
                Log.e("NotificationDebug", "Security Exception: " + e.getMessage());
            }
        } else {
            Log.e("NotificationDebug", "Permission Not Granted");
            // Consider prompting the user to enable the necessary permissions
        }
    }





    private void acceptInput() {
        inputText = editText.getText().toString();
        Log.d("tag6", inputText);
        // You can add code here to perform an action with the inputText.
    }


    //can test notifications with this
    private void testNotification() {
        Log.d("tag9", "in testnotif");
        notificationHelper.sendNotification("test notification");

    }




    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "notifyMe";
            String channelName = "Notification Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }



    private void checkAndRequestPermissions() {
        Log.d("tag1", "in checkpermissions");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {

            Log.d("tag1", "checkreqperm asking for perm");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.FOREGROUND_SERVICE}, PERMISSION_REQUEST_CODE);
        } else {
            Log.d("tag1", "checkreqperm permission already granted");
            onPermissionAlreadyGranted();
        }
    }

    private void onPermissionAlreadyGranted() {
        //todo delete this fn
        Log.d("tag1", "onPermissionAlreadyGranted");
        notificationHelper.sendNotification("test notification");  // Using NotificationHelper to send the notification immediately for testing purposes
        scheduleReminder();
    }



    private void scheduleReminder() {
        PeriodicWorkRequest reminderRequest =
                new PeriodicWorkRequest.Builder(ReminderWorker.class, 24, TimeUnit.HOURS)
                        .build();

        WorkManager.getInstance(this).enqueue(reminderRequest);
    }
}
