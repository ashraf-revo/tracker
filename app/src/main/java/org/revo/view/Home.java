package org.revo.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import org.revo.Helper.MediaAlarmManager;
import org.revo.Helper.TrackingAlarmManager;
import org.revo.R;
import org.revo.domain.Tracker;
import org.revo.service.BackServices;
import org.revo.service.TrackingService;

import io.reactivex.functions.Consumer;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MediaAlarmManager.setAlarm(getApplicationContext());
        ask();
        TrackingAlarmManager.setAlarm(getApplicationContext());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED)
            TrackingService.tracker(new BackServices(getApplicationContext())).subscribe(new Consumer<Tracker>() {
                @Override
                public void accept(Tracker tracker) {

                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) {

                }
            });
    }

    public void ask() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_CALL_LOG}, 1000);

    }

}
