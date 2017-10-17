package com.ikvant.loriapp.state.entry;

/**
 * Created by ikvant.
 */
public interface LoadDataCallback<T> {
    void onSuccess(T data);

    void networkUnreachable(T localData);

    void onFailure(Throwable e);
}
