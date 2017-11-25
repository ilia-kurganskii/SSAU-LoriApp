package com.ikvant.loriapp.state.auth;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ikvant.loriapp.LoriApp;
import com.ikvant.loriapp.database.token.Token;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.network.UnauthorizedListener;
import com.ikvant.loriapp.network.exceptions.NetworkApiException;
import com.ikvant.loriapp.network.exceptions.NetworkOfflineException;
import com.ikvant.loriapp.state.entry.LoadDataCallback;
import com.ikvant.loriapp.utils.AppExecutors;

import javax.inject.Inject;

/**
 * Created by ikvant.
 */

public class LoriAuthController implements UnauthorizedListener {
    private static final String TAG = "LoriAuthController";
    public static final String LOGOUT_ACTION = "LOGOUT_ACTION";

    private LoriApiService service;
    private AppExecutors executors;
    private LocalBroadcastManager broadcastManager;
    private TokenStore tokenStore;

    @Inject
    public LoriAuthController(LoriApp app, TokenStore tokenStore, final LoriApiService service, AppExecutors executors) {
        this.service = service;
        this.executors = executors;
        this.broadcastManager = LocalBroadcastManager.getInstance(app);
        this.service.setListener(this);
        this.tokenStore = tokenStore;
        executors.background().execute(new Runnable() {
            @Override
            public void run() {
                String token = tokenStore.loadToken();
                service.setToken(token);
            }
        });
    }

    public void executeLogin(final String login, final String password, final LoadDataCallback<Boolean> callback) {
        executors.background().execute(() -> {
            try {
                Token token = service.login(login, password);
                tokenStore.saveToken(token.getToken());
                service.setToken(token.getToken());
                executors.mainThread().execute(() -> callback.onSuccess(true));
            } catch (final NetworkApiException e) {
                if (e instanceof NetworkOfflineException) {
                    executors.mainThread().execute(() -> callback.networkUnreachable(false));
                } else {
                    executors.mainThread().execute(() -> callback.onFailure(e));
                }
            }
        });

    }

    public void isLogin(final LoadDataCallback<Boolean> callback) {
        executors.background().execute(() -> callback.onSuccess(tokenStore.loadToken() != null));
    }

    public void logout() {
        Log.d(TAG, "logout() called");
        broadcastManager.sendBroadcast(new Intent(LOGOUT_ACTION));
        executors.background().execute(() -> tokenStore.clearToken());
    }

    public void onUnauthorized() {
        logout();
    }
}
