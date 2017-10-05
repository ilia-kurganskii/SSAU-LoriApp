package com.ikvant.loriapp.dagger;

import android.arch.persistence.room.Room;

import com.ikvant.loriapp.LoriApp;
import com.ikvant.loriapp.database.LoriDatabase;
import com.ikvant.loriapp.database.timeentry.TimeEntryDao;
import com.ikvant.loriapp.database.token.TokenDao;
import com.ikvant.loriapp.network.ApiService;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.state.auth.AuthController;
import com.ikvant.loriapp.state.auth.LoriAuthConrtoller;
import com.ikvant.loriapp.state.timeentry.LoriTimeEntryController;
import com.ikvant.loriapp.state.timeentry.TimeEntryController;
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
    ApiService provideApiService() {
        return new Retrofit.Builder()
                .baseUrl("http://192.168.0.107:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    @Singleton
    @Provides
    LoriApiService provideLoriApiService(ApiService baseApi){
        return new LoriApiService(baseApi);
    }



    @Singleton
    @Provides
    AuthController provideAuthManager(LoriApiService service, TokenDao dao, AppExecutors appExecutors) {
        return new LoriAuthConrtoller(service, dao, appExecutors);
    }

    @Singleton
    @Provides
    TimeEntryController provideTimeEntryController(LoriApiService service, TimeEntryDao dao, AppExecutors appExecutors, AuthController controller) {
        return new LoriTimeEntryController(dao, service, appExecutors, controller);
    }

}