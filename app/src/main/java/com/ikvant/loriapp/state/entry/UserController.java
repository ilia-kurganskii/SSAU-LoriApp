package com.ikvant.loriapp.state.entry;

import com.ikvant.loriapp.database.user.User;
import com.ikvant.loriapp.database.user.UserDao;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.network.exceptions.NetworkApiException;
import com.ikvant.loriapp.network.exceptions.NetworkOfflineException;
import com.ikvant.loriapp.utils.AppExecutors;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by ikvant.
 */

@Singleton
public class UserController implements Reloadable {
    private LoriApiService apiService;
    private AppExecutors executors;
    private UserDao userDao;
    private User cacheUser;

    private boolean cacheIsDirty = true;

    @Inject
    public UserController(LoriApiService apiService, AppExecutors executors, UserDao userDao) {
        this.apiService = apiService;
        this.executors = executors;
        this.userDao = userDao;
    }

    public void getUser(LoadDataCallback<User> callback) {
        if (!cacheIsDirty) {
            executors.mainThread().execute(() -> callback.onSuccess(cacheUser));
            return;
        }
        executors.background().execute(() -> {
            try {
                User user = apiService.getUser();
                userDao.save(user);
                cacheUser = user;
                cacheIsDirty = false;
                executors.mainThread().execute(() -> callback.onSuccess(cacheUser));
            } catch (NetworkApiException e) {
                if (e instanceof NetworkOfflineException) {
                    cacheUser = userDao.load();
                    cacheIsDirty = false;
                    executors.mainThread().execute(() -> callback.networkUnreachable(cacheUser));
                } else {
                    executors.mainThread().execute(() -> callback.onFailure(e));
                }
            }
        });
    }


    public void refresh() {
        cacheIsDirty = true;
    }

    @Override
    public void reload(Callback callback) {
        refresh();
        getUser(new LoadDataCallback<User>() {
            @Override
            public void onSuccess(User data) {
                callback.onSuccess();
            }

            @Override
            public void networkUnreachable(User localData) {
                callback.onOffline();
            }

            @Override
            public void onFailure(Throwable e) {
                callback.onFailure(e);
            }
        });
    }
}
