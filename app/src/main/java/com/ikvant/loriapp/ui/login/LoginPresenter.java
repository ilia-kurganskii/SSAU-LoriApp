package com.ikvant.loriapp.ui.login;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.state.auth.LoriAuthController;
import com.ikvant.loriapp.state.entry.LoadDataCallback;

import javax.inject.Inject;

/**
 * Created by ikvant.
 */

public class LoginPresenter implements Contract.Presenter {
    protected LoriAuthController authController;

    private Contract.View view;

    @Inject
    protected LoginPresenter(LoriAuthController authController) {
        this.authController = authController;
    }

    public void setView(Contract.View view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void login(String user, String password) {
        view.showProgress();
        authController.executeLogin(user, password, new LoadDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                view.showNextScreen();
                view.hideProgress();
            }

            @Override
            public void networkUnreachable(Boolean localData) {
                view.hideProgress();
                view.showError(R.string.error_offline);
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
        authController.isLogin(new LoadDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                if (data) {
                    view.showNextScreen();
                }
            }

            @Override
            public void networkUnreachable(Boolean localData) {

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
