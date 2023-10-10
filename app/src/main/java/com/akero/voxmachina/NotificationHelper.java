package com.akero.voxmachina;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    private Context context;

    public NotificationHelper(Context context) {
        this.context = context;
    }

    void sendNotification() {
        // Create a Notification builder using the correct channel ID.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyMe")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Reminder")
                .setContentText("This is your reminder!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Log.d("NotificationDebug", "Preparing to send notification...");

        // Initialize NotificationManager with context.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Send the notification.
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());

        Log.d("NotificationDebug", "Notification Sent!");
    }
}
