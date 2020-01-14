package com.example.retrofit2.Utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.retrofit2.Interfaces.ApiService;
import com.example.retrofit2.Interfaces.InternetConnectionListener;
import com.google.gson.Gson;

import java.io.File;
import java.net.NetworkInterface;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    public static final int DISK_CACHE_SIZE = 10 * 1024 * 1024; // 10MB

    private ApiService apiService;
    private InternetConnectionListener mInternetConnectionListener;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setmInternetConnectionListener(InternetConnectionListener listener) {
        this.mInternetConnectionListener = listener;
    }

    public void removeInternetConnectionListener(){
        mInternetConnectionListener = null;
    }

    private boolean isInternetAvailable(){

        ConnectivityManager connectivityManager
                = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo
                = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public Cache getCache(){

        File cacheDir = new File(getCacheDir(), "cache");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        return cache;
    }
    public ApiService getApiService(){

        if(apiService == null)
            apiService = provideRetrofit(ApiService.URL).create(ApiService.class);

        return apiService;
    }
    private Retrofit provideRetrofit(String url){

        return new Retrofit.Builder()
                .baseUrl(url)
                .client(provideOkhttpClient())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    private OkHttpClient provideOkhttpClient(){

        OkHttpClient.Builder okhttpClientBuilder
                = new OkHttpClient.Builder();
        okhttpClientBuilder.connectTimeout(30, TimeUnit.SECONDS);
        okhttpClientBuilder.readTimeout(30, TimeUnit.SECONDS);
        okhttpClientBuilder.writeTimeout(30, TimeUnit.SECONDS);
        okhttpClientBuilder.cache(getCache());
        okhttpClientBuilder.addInterceptor(new NetworkConnectionInterceptor() {
            @Override
            public boolean isInternetAvailabe() {

                return App.this.isInternetAvailable();
            }

            @Override
            public void onInternetUnavailable() {

                // we can broadcast this event to activity/fragment/service
                // through LocalBroadcastReceiver or
                // RxBus/EventBus
                // also we can call our own interface method
                // like this.

                if(mInternetConnectionListener != null)
                    mInternetConnectionListener.onInternetUnavailable();
            }

            @Override
            public void onCacheUnavailable() {

                if(mInternetConnectionListener != null)
                    mInternetConnectionListener.onCacheUnavailable();
            }
        });

        return okhttpClientBuilder.build();
    }
}
