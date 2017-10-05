package com.ikvant.loriapp.network;

import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.token.Token;

import java.util.List;

import retrofit2.Call;

/**
 * Created by ikvant.
 */

public class LoriApiService {
    private ApiService service;
    private String token;

    public LoriApiService(ApiService service) {
        this.service = service;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Call<Token> login(String name, String password){
        return service.login(name, password, "password");
    }

    public Call<List<TimeEntry>> getTimeEntries(){
        return service.getTimeEntries(getFormatedToken());
    }

    private String getFormatedToken(){
        return "Bearer " + token;
    }
}
