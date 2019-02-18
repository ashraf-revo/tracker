package org.revo.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import org.revo.R;

import java.util.Date;

public class MediaService extends Service {


    private MediaPlayer mediaPlayer;

    public static void start(Context context) {
        Intent starter = new Intent(context, MediaService.class);
        context.startService(starter);
    }

    public void stop(Context context) {
        Intent starter = new Intent(context, MediaService.class);
        context.stopService(starter);
    }

    @Override
    public void onCreate() {
        mediaPlayer = MediaPlayer.create(this, R.raw.sound_file_1);
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                stop(getApplicationContext());
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        run();
        return START_STICKY;
    }


    @SuppressLint("CheckResult")
    private void run() {
        Log.d("org.revo.media.fired", new Date().toString());
        mediaPlayer.start();
    }
}
