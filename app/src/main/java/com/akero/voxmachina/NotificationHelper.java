package com.akero.voxmachina;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    private Context context;
    private static final int PERMISSION_REQUEST_CODE_POST_NOTIFICATIONS = 5678;

    public NotificationHelper(Context context) {
        this.context = context;
    }

    void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyMe")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Reminder")
                .setContentText("This is your reminder!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Log.d("tag2", "1");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Log.d("tag2", "2");

        // Check permission before sending the notification.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, you can request for it or handle this scenario according to your needs.
            Log.d("tag5", "POST_NOTIFICATIONS permission not granted");
            // You can request for the permission if you want to handle this scenario here.
        } else {
            // Permission has already been granted, send the notification
            notificationManager.notify(1, builder.build());
            Log.d("tag2", "4");
        }
    }
}
