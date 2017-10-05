package com.ikvant.loriapp.dagger;

import android.arch.persistence.room.Room;

import com.ikvant.loriapp.LoriApp;
import com.ikvant.loriapp.database.LoriDatabase;
import com.ikvant.loriapp.database.token.TokenDao;
import com.ikvant.loriapp.network.ApiService;
import com.ikvant.loriapp.state.AuthController;
import com.ikvant.loriapp.state.LoriAuthConrtoller;
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
    ApiService provideApiService() {
        return new Retrofit.Builder()
                .baseUrl("http://192.168.0.107:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    @Singleton
    @Provides
    AuthController provideAuthManager(ApiService service, TokenDao dao, AppExecutors appExecutors) {
        return new LoriAuthConrtoller(service, dao, appExecutors);
    }

}