package org.revo.Helper;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.revo.service.MediaService;
import org.revo.service.TrackingService;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isServiceRunning(context, TrackingService.class))
            TrackingService.start(context);
/*
        if (!isServiceRunning(context, MediaService.class))
            MediaService.start(context);
*/
    }

    public static boolean isServiceRunning(Context context, Class aClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (aClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}