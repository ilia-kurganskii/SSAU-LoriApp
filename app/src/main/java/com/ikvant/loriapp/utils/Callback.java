package com.ikvant.loriapp.utils;

/**
 * Created by ikvant.
 */

public interface Callback<T> {
    void onSuccess(T data);
    void onFailure(Throwable throwable);
}
