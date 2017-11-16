package com.ikvant.loriapp.ui.login;

import com.ikvant.loriapp.state.auth.AuthController;
import com.ikvant.loriapp.utils.Callback;

import javax.inject.Inject;

/**
 * Created by ikvant.
 */

public class LoginPresenter implements Contract.Presenter {
    protected AuthController authController;

    private Contract.View view;

    @Inject
    protected LoginPresenter(AuthController authController) {
        this.authController = authController;
    }

    public void setView(Contract.View view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void login(String user, String password) {
        view.showProgress();
        authController.executeLogin(user, password, new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                view.showNextScreen();
                view.hideProgress();
            }

            @Override
            public void onFailure(Throwable throwable) {
                view.hideProgress();
                view.showError(throwable.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        authController.isLogin(new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                if (data) {
                    view.showNextScreen();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    @Override
    public void onStop() {

    }
}
