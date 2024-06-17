// StopAlarmService.java
package com.example.medicinetracker;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class StopAlarmService extends Service {

    private static final String TAG = "StopAlarmService";
    private static Ringtone ringtone;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Stop the ringtone if it is playing
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
            ringtone = null;
            Log.d(TAG, "Ringtone stopped");
        }
        // Stop the service
        stopSelf();
        return START_NOT_STICKY;
    }

    public static void setRingtone(Ringtone rt) {
        ringtone = rt;
    }
}
