package com.ikvant.loriapp.state.entry;

import com.ikvant.loriapp.database.tags.Tag;
import com.ikvant.loriapp.database.tags.TagDao;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.network.exceptions.NetworkApiException;
import com.ikvant.loriapp.network.exceptions.NetworkOfflineException;
import com.ikvant.loriapp.utils.AppExecutors;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ikvant.
 */

public class TagsController implements Reloadable {
    private LoriApiService apiService;
    private AppExecutors executors;
    private TagDao tagDao;
    private List<Tag> cacheTasks;

    private boolean cacheIsDirty = true;

    @Inject
    public TagsController(LoriApiService apiService, AppExecutors executors, TagDao tagDao) {
        this.apiService = apiService;
        this.executors = executors;
        this.tagDao = tagDao;
    }

    public void refresh() {
        cacheIsDirty = true;
    }

    public void load(final LoadDataCallback<List<Tag>> callback) {
        if (!cacheIsDirty) {
            callback.onSuccess(cacheTasks);
        }
        executors.background().execute(() -> {
            try {
                List<Tag> newTags = apiService.getTags();
                tagDao.saveAll(newTags.toArray(new Tag[newTags.size()]));
                cacheTasks = newTags;
                cacheIsDirty = false;
                executors.mainThread().execute(() -> callback.onSuccess(newTags));
            } catch (NetworkApiException e) {
                if (e instanceof NetworkOfflineException) {
                    cacheTasks = tagDao.loadAll();
                    cacheIsDirty = false;
                    executors.mainThread().execute(() -> callback.networkUnreachable(cacheTasks));
                } else {
                    executors.mainThread().execute(() -> callback.onFailure(e));
                }
            }
        });
    }

    @Override
    public void reload(Callback callback) {
        refresh();
        load(new LoadDataCallback<List<Tag>>() {
            @Override
            public void onSuccess(List<Tag> data) {
                callback.onSuccess();
            }

            @Override
            public void networkUnreachable(List<Tag> localData) {
                callback.onSuccess();
            }

            @Override
            public void onFailure(Throwable e) {
                callback.onFailure(e);
            }
        });
    }
}
