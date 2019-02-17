package org.revo.Helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class TrackingAlarmManager {

    public static void setAlarm(Context context) {
        if (isAlarmRunning(context)) {
            return;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                60 * 1000,
                pendingIntent);
    }

    public static void cancelAlarm(Context context) {
        if (!isAlarmRunning(context)) {
            return;
        }
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public static boolean isAlarmRunning(Context context) {
        return (PendingIntent.getBroadcast(context, 0,
                new Intent(context, AlarmBroadcastReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);
    }
}
