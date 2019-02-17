package org.revo.service;


import org.revo.domain.Call;
import org.revo.domain.Calls;
import org.revo.domain.Location;
import org.revo.domain.Locations;
import org.revo.domain.Tracker;
import org.revo.domain.User;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static org.revo.domain.Role.Paths.USER_PATH;

public interface TrackerApiService {
    @GET("/auth/user")
    Single<User> getCurrentUser();

    @POST(USER_PATH + "/tracker")
    Single<Tracker> tracker(@Body Tracker tracker);

    @GET(USER_PATH + "/tracker/{id}")
    Single<Tracker> findOne(@Path("id") String id);

    @POST(USER_PATH + "/call")
    Single<Call> call(@Body Call call);

    @POST(USER_PATH + "/location")
    Single<Location> location(@Body Location location);

    @POST(USER_PATH + "/calls")
    Single<Calls> calls(@Body Calls calls);

    @POST(USER_PATH + "/locations")
    Single<Locations> locations(@Body Locations locations);
}