package com.ikvant.loriapp.state.auth;

import com.ikvant.loriapp.database.token.Token;
import com.ikvant.loriapp.database.token.TokenDao;
import com.ikvant.loriapp.network.ApiService;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.utils.AppExecutors;
import com.ikvant.loriapp.utils.Callback;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by ikvant.
 */

public class LoriAuthConrtoller implements AuthController {
    private LoriApiService service;
    private TokenDao tokenDao;
    private AppExecutors executors;

    public LoriAuthConrtoller(final LoriApiService service, final TokenDao tokenDao, AppExecutors executors) {
        this.service = service;
        this.tokenDao = tokenDao;
        this.executors = executors;
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Token token = tokenDao.load();
                if (tokenDao.load() != null) {
                    service.setToken(token.getToken());
                }
            }
        });
    }

    @Override
    public void executeLogin(String login, String password, final Callback<Boolean> callback) {
        service.login(login, password).enqueue(new retrofit2.Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, final Response<Token> response) {
                executors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        Token token = response.body();
                        if (response.code() == 200 && token != null) {
                            tokenDao.save(token);
                            service.setToken(token.getToken());
                            executors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onSuccess(true);
                                }
                            });
                        } else {
                            callback.onFailure(new Exception("Login failure"));
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    @Override
    public void isLogin(final Callback<Boolean> callback) {
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(tokenDao.load() != null);
            }
        });
    }

    @Override
    public void logout() {
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                tokenDao.delete();
            }
        });
    }
}
