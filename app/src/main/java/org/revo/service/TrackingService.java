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

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

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
import io.reactivex.functions.BiFunction;
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
        Maybe.zip(location(getApplicationContext(), backServices).defaultIfEmpty(new Location()), calls(getApplicationContext(), backServices).defaultIfEmpty(new Calls()), new BiFunction<Location, Calls, Integer>() {
            @Override
            public Integer apply(Location location, Calls calls) {
                return 0;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer result) {
                Log.d("org.revo.location.resul", result + "");
                stop(getApplicationContext(), backServices);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                Log.d("org.revo.error", throwable.getMessage());
                stop(getApplicationContext(), backServices);
            }
        });
    }

    public static Maybe<Calls> calls(Context context, final BackServices backServices) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED)
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
        else return Maybe.empty();
    }

    public static Maybe<Tracker> tracker(Context context, final BackServices backServices) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED)
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
                    }).toMaybe();
        else return Maybe.empty();
    }


    public static Maybe<Location> location(Context context, final BackServices backServices) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return Flowable.create(new FlowableOnSubscribe<android.location.Location>() {
                @SuppressLint("MissingPermission")
                @Override
                public void subscribe(final FlowableEmitter<android.location.Location> e) {
                    backServices.location(new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if (locationResult.getLastLocation() != null) {
                                Log.d("org.revo.location.new", locationResult.getLastLocation().getLatitude() + "," + locationResult.getLastLocation().getLongitude());
                                e.onNext(locationResult.getLastLocation());
                            }

                        }
                    });
                }
            }, BackpressureStrategy.BUFFER).take(15)
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
        else return Maybe.empty();
    }
}
