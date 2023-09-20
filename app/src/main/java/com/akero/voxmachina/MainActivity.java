package com.akero.voxmachina;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1234;
    private static final int PERMISSION_REQUEST_CODE_POST_NOTIFICATIONS = 5678;

    private NotificationHelper notificationHelper; // Declare the notification helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationHelper = new NotificationHelper(this); // Initialize the helper

        // Create the notification channel
        createNotificationChannel();
        checkAndRequestPermissions();

        Button testButton = findViewById(R.id.btn_test_notification);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testNotification();
            }
        });
    }

    private void testNotification() {
        notificationHelper.sendNotification();
    }




    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notifyMe", "Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
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
        Log.d("tag1", "onPermissionAlreadyGranted");
        notificationHelper.sendNotification();  // Using NotificationHelper to send the notification immediately for testing purposes
        scheduleReminder();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("tag1", "permission granted");
                scheduleReminder();
            } else {
                Log.d("tag1", "permission not granted");
            }
        }
    }

    private void scheduleReminder() {
        PeriodicWorkRequest reminderRequest =
                new PeriodicWorkRequest.Builder(ReminderWorker.class, 24, TimeUnit.HOURS)
                        .build();

        WorkManager.getInstance(this).enqueue(reminderRequest);
    }
}
