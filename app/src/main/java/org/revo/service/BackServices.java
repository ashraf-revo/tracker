package org.revo.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Looper;
import android.provider.CallLog;
import android.provider.Settings;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import org.revo.domain.Call;
import org.revo.domain.CallType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BackServices {
    private FusedLocationProviderClient locationProviderClient;
    private Context context;

    public BackServices(Context context) {
        this.context = context;
        locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    private static LocationRequest getLocationRequest() {
        LocationRequest myLocationRequest = new LocationRequest();
        myLocationRequest.setInterval(1000);
        myLocationRequest.setFastestInterval(1000);
        myLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return myLocationRequest;
    }


    public void stopLocationUpdate() {
        Task<Void> voidTask = locationProviderClient.removeLocationUpdates(new LocationCallback() {
        });
        Log.d("org.revo.location.stop", "say "+voidTask.isSuccessful() + " ! ");

    }


    @RequiresPermission(anyOf = {"android.permission.GET_ACCOUNTS"})
    public String[] accounts() {
        Account[] accountsByType = AccountManager.get(context).getAccountsByType("com.google");
        String accounts[] = new String[accountsByType.length];
        for (int i = 0; i < accountsByType.length; i++) {
            accounts[i] = accountsByType[i].name;
        }
        return accounts;
    }

    @RequiresPermission(allOf = {"android.permission.READ_CALL_LOG"})
    public List<Call> calls(Date lastUpdateCall) {
        List<Call> calls = new ArrayList<>();
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        Log.d("org.revo.in.call", "done");
        while (managedCursor.moveToNext()) {
            Date callDate = new Date(Long.valueOf(managedCursor.getString(date)));
            if (lastUpdateCall != null && !lastUpdateCall.before(callDate)) break;
            calls.add(new Call(null, getId(), callDate, CallType.type(Integer.valueOf(managedCursor.getString(type))), managedCursor.getString(number), Integer.valueOf(managedCursor.getString(duration))));
        }
        managedCursor.close();
        return lastUpdateCall == null ? calls : calls.subList(0, calls.size() - 1);
    }

    @SuppressLint("HardwareIds")
    public String getId() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @RequiresPermission(anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"})
    public void location(LocationCallback locationCallback) {
        locationProviderClient.requestLocationUpdates(getLocationRequest(), locationCallback, Looper.getMainLooper());
    }
}