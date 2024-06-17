// AlarmReceiver.java
package com.example.medicinetracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "MedicineTrackerChannel";
    private static final int NOTIFICATION_ID = 1;
    private static Ringtone ringtone;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Show a notification when the alarm triggers
        showNotification(context);
        // Play the alarm sound
        playAlarmSound(context);
    }

    private void showNotification(Context context) {
        Log.d("AlarmReceiver", "Alarm received!");
        // Create an intent to stop the alarm sound when the notification is clicked
        Intent stopAlarmIntent = new Intent(context, StopAlarmService.class);
        PendingIntent stopAlarmPendingIntent = PendingIntent.getService(context, 0, stopAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_access_alarms_24)
                .setContentTitle("Medication Reminder")
                .setContentText("It's time to take your medication!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(stopAlarmPendingIntent)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

        // Create a notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // For API 26+, create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Medicine Tracker";
            String description = "Channel for medication reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void playAlarmSound(Context context) {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(context, alarmUri);
        if (ringtone != null) {
            ringtone.play();
            StopAlarmService.setRingtone(ringtone);
        }
    }
}
