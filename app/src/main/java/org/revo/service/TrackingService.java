package org.revo.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.revo.domain.Calls;
import org.revo.domain.Location;
import org.revo.domain.Tracker;
import org.revo.domain.User;

import java.util.Date;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class TrackingService extends Service {
    private static final TrackerApiService service = TrackerServiceGenerator.createService(TrackerApiService.class);


    public static void start(Context context) {
        Intent starter = new Intent(context, TrackingService.class);
        context.startService(starter);
    }

    public void stop(Context context, BackServices backServices) {
        Intent starter = new Intent(context, TrackingService.class);
        backServices.stopLocationUpdate();
        context.stopService(starter);
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
        Log.d("org.revo.track.fired", new Date().toString());
        final BackServices backServices = new BackServices(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED)
            calls(backServices).subscribe(new Consumer<Calls>() {
                @Override
                public void accept(Calls calls) {

                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) {
                    Log.d("org.revo.error", throwable.getMessage());
                }
            });
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            location(backServices).subscribe(new Consumer<Location>() {
                @Override
                public void accept(Location location) {

                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) {
                    Log.d("org.revo.error", throwable.getMessage());
                }
            });


        stop(getApplicationContext(), backServices);
    }

    public static Maybe<Calls> calls(final BackServices backServices) {
        return service.getCurrentUser()
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
                .onErrorResumeNext(new Function<Throwable, SingleSource<? extends User>>() {
                    @Override
                    public SingleSource<? extends User> apply(Throwable throwable) {
                        return Single.just(new User());
                    }

                })
                .flatMap(new Function<User, SingleSource<Tracker>>() {
                    @Override
                    public SingleSource<Tracker> apply(User user) {
                        return service.findOne(backServices.getId());
                    }
                })
                .flatMap(new Function<Tracker, SingleSource<Calls>>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public SingleSource<Calls> apply(Tracker tracker) {
                        return service.calls(new Calls(backServices.calls(tracker.getLastUpdateCall())));
                    }
                })
                .filter(new Predicate<Calls>() {
                    @Override
                    public boolean test(Calls calls) throws Exception {
                        return calls.getCalls().size() > 0;
                    }
                });
    }

    public static Single<Tracker> tracker(final BackServices backServices) {
        return service.getCurrentUser()
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
                .onErrorResumeNext(new Function<Throwable, SingleSource<? extends User>>() {
                    @Override
                    public SingleSource<? extends User> apply(Throwable throwable) {
                        return Single.just(new User());
                    }
                })

                .flatMap(new Function<User, SingleSource<Tracker>>() {
                    @Override
                    public SingleSource<Tracker> apply(User user) {
                        return service.tracker(new Tracker(backServices.getId(), backServices.accounts(), new Date(), null, null));
                    }
                });
    }


    public static Maybe<Location> location(final BackServices backServices) {

/*
        new android.support.v4.util.Consumer<android.location.Location>() {
            @Override
            public void accept(android.location.Location location) {
                e.onNext(new Location(null, backServices.getId(), new Date(), location.getLatitude(), location.getLongitude()));
            }
        }
*/
        return Flowable.create(new FlowableOnSubscribe<android.location.Location>() {
            @SuppressLint("MissingPermission")
            @Override
            public void subscribe(final FlowableEmitter<android.location.Location> e) {
                backServices.location(e);
            }
        }, BackpressureStrategy.BUFFER)
                .lastElement()
                .map(new Function<android.location.Location, Location>() {
                    @Override
                    public Location apply(android.location.Location location) {
                        return new Location(null, backServices.getId(), new Date(), location.getLatitude(), location.getLongitude());
                    }
                })
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io())

                .flatMap(new Function<Location, MaybeSource<Location>>() {
                    @Override
                    public MaybeSource<Location> apply(Location location) {
                        return service.location(location).toMaybe();
                    }
                });
    }
}
