package com.example.retrofit2.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public abstract class NetworkConnectionInterceptor implements Interceptor {

    public abstract boolean isInternetAvailabe();

    public abstract void onInternetUnavailable();

    public abstract void onCacheUnavailable();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if(!isInternetAvailabe()){
            onInternetUnavailable();

            request = request.newBuilder().addHeader("Cache-Control",
                    "public, only-if-cached, max-stale=" + 60 * 60 * 24).build();
            Response response = chain.proceed(request);

            if(response.cacheResponse() == null)
                onCacheUnavailable();

            return response;
        }

        return chain.proceed(request);

    }
}