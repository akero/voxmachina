package com.akero.voxmachina;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1234;
    private static final int PERMISSION_REQUEST_CODE_POST_NOTIFICATIONS = 5678;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the notification channel
        createNotificationChannel();
        checkAndRequestPermissions();
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
            // If the app already has the permission, proceed with whatever you want to do next.
            onPermissionAlreadyGranted();
            //TODO for testing remove this and implement properly
            Log.d("tag4","11");
            sendNotification();  // Send the notification immediately
            Log.d("tag4","12");
            //scheduleReminder();
        }
    }

    private void onPermissionAlreadyGranted() {
        Log.d("tag1", "onPermissionAlreadyGranted");

        // Implement the next steps you want to perform after verifying the permission is granted.
        // For now, I'm placing your notification and reminder methods here:
        Log.d("tag4","1");
        sendNotification();  // Send the notification immediately for testing purposes
        Log.d("tag4","2");
        scheduleReminder();
        Log.d("tag4","3");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, schedule the reminder
                Log.d("tag1", "permission granted");

                scheduleReminder();
            } else {

                Log.d("tag1", "permission not granted");
                // Handle the permission denial according to your app's user experience
                // Maybe show a message explaining why the permission is crucial.
            }
        }
    }

    private void scheduleReminder() {
        PeriodicWorkRequest reminderRequest =
                new PeriodicWorkRequest.Builder(ReminderWorker.class, 24, TimeUnit.HOURS)
                        .build();

        WorkManager.getInstance(this).enqueue(reminderRequest);
    }

    void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "notifyMe")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Reminder")
                .setContentText("This is your reminder!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Log.d("tag2", "1");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        Log.d("tag2", "2");

        // Since there isn't a runtime permission for posting notifications,
        // we can directly notify without checking for permissions.
        Log.d("tag2", "4");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, request for it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE_POST_NOTIFICATIONS);
            Log.d("tag5", "Requesting POST_NOTIFICATIONS permission");
        } else {
            // Permission has already been granted
            notificationManager.notify(1, builder.build());
            //sendNotification();
            Log.d("tag5", "POST_NOTIFICATIONS permission already granted");
        }
    }
   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // POST_NOTIFICATIONS permission granted
                sendNotification();
                Log.d("tag5", "POST_NOTIFICATIONS permission granted");
            } else {
                // POST_NOTIFICATIONS permission denied
                Log.d("tag5", "POST_NOTIFICATIONS permission denied");
            }
        }
    }*/

}
