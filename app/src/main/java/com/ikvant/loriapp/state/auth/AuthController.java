package com.ikvant.loriapp.state.auth;

import com.ikvant.loriapp.database.token.Token;
import com.ikvant.loriapp.utils.Callback;

/**
 * Created by ikvant.
 */

public interface AuthController {
    void executeLogin(String login, String password, Callback<Boolean> callback);
    void isLogin(Callback<Boolean> callback);
    void logout();
}
