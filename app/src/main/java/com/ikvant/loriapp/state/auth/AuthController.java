package com.ikvant.loriapp.state.auth;

import com.ikvant.loriapp.utils.Callback;

/**
 * Created by ikvant.
 */

public interface AuthController {
    public static final String LOGOUT_ACTION = "ACTION_LOGOUT";

    void executeLogin(String login, String password, Callback<Boolean> callback);
    void isLogin(Callback<Boolean> callback);
    void logout();
}
