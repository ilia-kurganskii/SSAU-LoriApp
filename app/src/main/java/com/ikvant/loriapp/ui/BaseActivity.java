package com.ikvant.loriapp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.ikvant.loriapp.state.auth.AuthController;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * Created by ikvant.
 */

public abstract class BaseActivity extends DaggerAppCompatActivity {
    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LoginActivity.startMe(BaseActivity.this);
            finish();
        }
    };

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                logoutReceiver, new IntentFilter(AuthController.LOGOUT_ACTION));
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutReceiver);
        super.onPause();
    }
}
