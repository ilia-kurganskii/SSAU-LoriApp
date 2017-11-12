package com.ikvant.loriapp.state.entry;

/**
 * Created by ikvant.
 */

public interface Reloadable {
    void reload(Callback callback);

    interface Callback {
        void onSuccess();

        void onOffline();

        void onFailure(Throwable throwable);
    }
}
