package com.ikvant.loriapp.network;

import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.token.Token;
import com.ikvant.loriapp.database.user.User;

import java.sql.Time;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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

    @PUT("/app/rest/v2/entities/ts$TimeEntry/{id}")
    Call<TimeEntry> updateTimeEntry(@Path("id") String id, @Body TimeEntry timeEntry, @Header("Authorization") String token);

    @DELETE("/app/rest/v2/entities/ts$TimeEntry/{id}")
    Call<Void> deleteTimeEntry(@Path("id") String id, @Header("Authorization") String token);

    @POST("/app/rest/v2/entities/ts$TimeEntry")
    Call<TimeEntry> createTimeEntry(@Body TimeEntry timeEntry, @Header("Authorization") String token);

    @GET("/app/rest/v2/entities/ts$TimeEntry?view=timeEntry-full")
    Call<List<TimeEntry>> getTimeEntries(@Header("Authorization") String token);

    @GET("/app/rest/v2/entities/ts$Task?view=task-full")
    Call<List<Task>> getTasks(@Header("Authorization") String token);

    @GET("/app/rest/v2/userInfo")
    Call<User> getUser(@Header("Authorization") String token);
}
