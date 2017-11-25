package com.ikvant.loriapp.ui.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.ui.tasklist.ListEntriesActivity;

/**
 * Created by ikvant.
 */

public class LoginFragment extends Fragment implements Contract.View {
    private EditText mEmailView;
    private EditText mPasswordView;
    private View progress;

    private Contract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.a_login, container, false);
        mEmailView = root.findViewById(R.id.login);
        progress = root.findViewById(R.id.progress);
        mPasswordView = root.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button mEmailSignInButton = root.findViewById(R.id.login_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void setPresenter(Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void showError(String error) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.login_error_title)
                .setMessage(error)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    @Override
    public void showError(@StringRes int errorRes) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.login_error_title)
                .setMessage(errorRes)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    @Override
    public void showNextScreen() {
        ListEntriesActivity.startMe(this);
        getActivity().finish();
    }

    private void attemptLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            presenter.login(email, password);
        }
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }
}
