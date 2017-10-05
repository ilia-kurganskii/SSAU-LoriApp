package com.ikvant.loriapp.utils;

import android.util.Log;

/**
 * Created by ikvant.
 */

public class SimpleCallback<T> implements Callback<T> {
    private static final String TAG = "SimpleCallback";

    @Override
    public void onSuccess(T data) {
        Log.d(TAG, "onSuccess: ");
    }

    @Override
    public void onFailure(Throwable throwable) {
        Log.w(TAG, "onFailure: " + throwable.getMessage(), throwable);
    }
}
