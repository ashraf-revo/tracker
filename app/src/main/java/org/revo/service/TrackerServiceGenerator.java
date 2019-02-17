package org.revo.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.revo.domain.Token;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrackerServiceGenerator {
    private static final String BASE_URL = "http://astrack.cfapps.io/";
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create();
    private static Retrofit.Builder builder
            = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson));


    private static OkHttpClient.Builder httpClient
            = new OkHttpClient.Builder();

    public static <S> S createService(Class<S> serviceClass) {
        final Token token = new Token();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Log.d("org.revo.url", chain.request().url().encodedPath());
                if (chain.request().url().encodedPath().equals("/auth/user")) {
                    Response proceed = chain.proceed(chain.request());
                    token.setValue(proceed.headers().get("set-cookie"));
                    return proceed;
                } else {
                    Log.d("org.revo.token", token.getValue());
                    return chain.proceed(chain.request().newBuilder()
                            .header("X-XSRF-TOKEN", token.getValue())
                            .header("Cookie", "XSRF-TOKEN=" + token.getValue())
                            .build());
                }
            }
        });
        builder.client(httpClient.build()).addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        return builder.build().create(serviceClass);
    }
}