package com.ikvant.loriapp.ui.login;

/**
 * Created by ikvant.
 */

public interface Contract {
    interface View {
        void setPresenter(Presenter presenter);

        void showProgress();

        void hideProgress();

        void showError(String error);

        void showNextScreen();
    }

    interface Presenter {
        void login(String user, String password);

        void onResume();

        void onStop();
    }
}
