package com.ikvant.loriapp.dagger;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ikvant.loriapp.LoriApp;
import com.ikvant.loriapp.database.LoriDatabase;
import com.ikvant.loriapp.database.project.ProjectDao;
import com.ikvant.loriapp.database.task.TaskDao;
import com.ikvant.loriapp.database.timeentry.TimeEntryDao;
import com.ikvant.loriapp.database.token.TokenDao;
import com.ikvant.loriapp.database.user.UserDao;
import com.ikvant.loriapp.network.ApiService;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.network.NetworkChecker;
import com.ikvant.loriapp.state.auth.AuthController;
import com.ikvant.loriapp.state.auth.LoriAuthController;
import com.ikvant.loriapp.state.entry.EntryController;
import com.ikvant.loriapp.state.entry.LoriEntryController;
import com.ikvant.loriapp.state.entry.UserController;
import com.ikvant.loriapp.utils.AppExecutors;

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
class AppModule {

    @Singleton
    @Provides
    LoriDatabase provideDb(LoriApp app) {
        return Room.databaseBuilder(app, LoriDatabase.class, "lori.db").build();
    }

    @Singleton
    @Provides
    TokenDao provideTokenDao(LoriDatabase db) {
        return db.tokenDao();
    }

    @Singleton
    @Provides
    TimeEntryDao provideTimeEntryDao(LoriDatabase db) {
        return db.timeEntryDao();
    }

    @Singleton
    @Provides
    TaskDao provideTaskDao(LoriDatabase db) {
        return db.taskDao();
    }

    @Singleton
    @Provides
    UserDao provideUserDao(LoriDatabase db) {
        return db.userDao();
    }

    @Singleton
    @Provides
    ProjectDao provideProjectDao(LoriDatabase db) {
        return db.projectDao();
    }


    @Singleton
    @Provides
    ApiService provideApiService() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        return new Retrofit.Builder()
                .baseUrl("http://10.60.8.252:8080")
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

    @Singleton
    @Provides
    LoriApiService provideLoriApiService(ApiService baseApi, NetworkChecker networkChecker) {
        return new LoriApiService(networkChecker, baseApi);
    }


    @Singleton
    @Provides
    AuthController provideAuthManager(LoriApp app, LoriApiService service, TokenDao dao, AppExecutors appExecutors) {
        return new LoriAuthController(LocalBroadcastManager.getInstance(app), service, dao, appExecutors);
    }

    @Singleton
    @Provides
    EntryController provideTimeEntryController(LoriApiService service, UserController userDao, TimeEntryDao dao, AppExecutors appExecutors, TaskDao taskDao) {
        return new LoriEntryController(dao, userDao, service, appExecutors);
    }

}