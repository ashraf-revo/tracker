package org.revo.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.provider.CallLog;
import android.provider.Settings;
import android.support.annotation.RequiresPermission;
import android.support.v4.util.Consumer;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.revo.domain.Call;
import org.revo.domain.CallType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BackServices {
    private FusedLocationProviderClient locationProviderClient;
    private Context context;
    private AccountManager accountManager;

    public BackServices(Context context) {
        this.context = context;
        locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        accountManager = AccountManager.get(context);
    }


    @RequiresPermission(anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"})
    public void location(final Consumer<Location> locationConsumer) {
        locationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            locationConsumer.accept(location);
                        }
                    }
                });
    }


    @RequiresPermission(anyOf = {"android.permission.GET_ACCOUNTS"})
    public String[] accounts() {
        Account[] accountsByType = accountManager.getAccountsByType("com.google");
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

}
