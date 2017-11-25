package com.ikvant.loriapp.ui.login;

import android.support.annotation.StringRes;

/**
 * Created by ikvant.
 */

public interface Contract {
    interface View {
        void setPresenter(Presenter presenter);

        void showProgress();

        void hideProgress();

        void showError(String error);

        void showError(@StringRes int errorRes);

        void showNextScreen();
    }

    interface Presenter {
        void login(String user, String password);

        void onResume();

        void onStop();
    }
}
