package com.ikvant.loriapp.dagger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ikvant.loriapp.LoriApp;
import com.ikvant.loriapp.network.ApiService;
import com.ikvant.loriapp.network.NetworkChecker;
import com.ikvant.loriapp.utils.AppExecutors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.mockito.Mockito.mock;

@Module
public class TestNetworkModule {
	@Provides
	LoriApp provideApp(){
		return mock(LoriApp.class);
	}

	@Singleton
	@Provides
	ApiService provideApiService() {
		return mock(ApiService.class);
	}

	@Singleton
	@Provides
	NetworkChecker networkChecker(LoriApp app) {
		return mock(NetworkChecker.class);
	}


	@Provides
	@Singleton
	AppExecutors executors(){
		AppExecutors executors = new AppExecutors(Executors.newFixedThreadPool(3), Executors.newSingleThreadExecutor());
		return executors;
	}
}
