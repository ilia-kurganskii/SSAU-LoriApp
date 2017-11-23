package com.ikvant.loriapp.dagger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ikvant.loriapp.LoriApp;
import com.ikvant.loriapp.network.ApiService;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.network.NetworkChecker;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.support.AndroidSupportInjectionModule;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zmei9 on 17-09-14.
 */

@Module(includes = {AndroidSupportInjectionModule.class})
public class NetworkModule {

    @Singleton
    @Provides
    ApiService provideApiService() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        return new Retrofit.Builder()
                .baseUrl("http://192.168.0.103:8080")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ApiService.class);
    }

    @Singleton
    @Provides
    NetworkChecker networkChecker(LoriApp app) {
        return () -> {
            ConnectivityManager internetManager = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = internetManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable() && networkInfo.isConnectedOrConnecting();
        };
    }
}