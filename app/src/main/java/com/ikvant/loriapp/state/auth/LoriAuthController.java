package com.ikvant.loriapp.state.auth;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ikvant.loriapp.LoriApp;
import com.ikvant.loriapp.database.token.Token;
import com.ikvant.loriapp.database.token.TokenDao;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.network.UnauthorizedListener;
import com.ikvant.loriapp.network.exceptions.NetworkApiException;
import com.ikvant.loriapp.utils.AppExecutors;
import com.ikvant.loriapp.utils.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by ikvant.
 */

@Singleton
public class LoriAuthController implements UnauthorizedListener {
    public static final String LOGOUT_ACTION = "LOGOUT ACTION";

    private static final String TAG = "LoriAuthController";
    private LoriApiService service;
    private TokenDao tokenDao;
    private AppExecutors executors;
    private LocalBroadcastManager broadcastManager;

    @Inject
    public LoriAuthController(LoriApp app, final LoriApiService service, final TokenDao tokenDao, AppExecutors executors) {
        this.service = service;
        this.tokenDao = tokenDao;
        this.executors = executors;
        this.broadcastManager = LocalBroadcastManager.getInstance(app);
        this.service.setListener(this);
        executors.background().execute(new Runnable() {
            @Override
            public void run() {
                Token token = tokenDao.load();
                if (tokenDao.load() != null) {
                    service.setToken(token.getToken());
                }
            }
        });
    }

    public void executeLogin(final String login, final String password, final Callback<Boolean> callback) {
        executors.background().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Token token = service.login(login, password);
                    tokenDao.save(token);
                    service.setToken(token.getToken());
                    executors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(true);
                        }
                    });
                } catch (final NetworkApiException e) {
                    executors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(e);
                        }
                    });
                }
            }
        });

    }

    public void isLogin(final Callback<Boolean> callback) {
        executors.background().execute(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(tokenDao.load() != null);
            }
        });
    }

    public void logout() {
        Log.d(TAG, "logout() called");
        broadcastManager.sendBroadcast(new Intent(LOGOUT_ACTION));
        executors.background().execute(new Runnable() {
            @Override
            public void run() {
                tokenDao.delete();
            }
        });
    }

    @Override
    public void onUnauthorized() {
        logout();
    }
}
