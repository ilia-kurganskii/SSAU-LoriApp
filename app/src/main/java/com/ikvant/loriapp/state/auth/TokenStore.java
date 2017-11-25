package com.ikvant.loriapp.state.auth;

import android.content.Context;

import com.ikvant.loriapp.LoriApp;

import javax.inject.Inject;

/**
 * Created by ikvant.
 */

public class TokenStore {
    private static final String PREFERENCE = "PREF";
    private static final String KEY_TOKEN = "token";

    private Context context;

    @Inject
    public TokenStore(LoriApp context) {
        this.context = context;
    }

    public void saveToken(String token) {
        context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE).edit().putString(KEY_TOKEN, token).apply();
    }

    public void clearToken() {
        saveToken(null);
    }

    public String loadToken() {
        return context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE).getString(KEY_TOKEN, null);
    }
}
