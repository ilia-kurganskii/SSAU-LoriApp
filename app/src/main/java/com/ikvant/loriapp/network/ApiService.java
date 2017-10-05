package com.ikvant.loriapp.network;

import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.token.Token;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by ikvant.
 */

public interface ApiService {
    @Headers({
            "Authorization: Basic Y2xpZW50OnNlY3JldA=="
    })
    @FormUrlEncoded
    @POST("/app/rest/v2/oauth/token")
    Call<Token> login(@Field("username") String username, @Field("password") String password, @Field("grant_type") String grantType);

    @GET("/app/rest/v2/entities/ts$TimeEntry?view=timeEntry-full")
    Call<List<TimeEntry>> getTimeEntries(@Header("Authorization") String token);


}
