package com.ikvant.loriapp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.ikvant.loriapp.state.auth.LoriAuthController;
import com.ikvant.loriapp.ui.login.LoginActivity;

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
                logoutReceiver, new IntentFilter(LoriAuthController.LOGOUT_ACTION));
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutReceiver);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void enableHomeUp() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
