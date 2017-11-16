package com.ikvant.loriapp.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.utils.ActivityUtils;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends DaggerAppCompatActivity {

    @Inject
    protected LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_login);

        setContentView(R.layout.content);

        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if (loginFragment == null) {
            loginFragment = LoginFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), loginFragment, R.id.content);
        }

        presenter.setView(loginFragment);
    }


    public static void startMe(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }
}

