package com.ikvant.loriapp.state.entry;

import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.task.TaskDao;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.network.exceptions.NetworkApiException;
import com.ikvant.loriapp.network.exceptions.NetworkOfflineException;
import com.ikvant.loriapp.utils.AppExecutors;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by ikvant.
 */

@Singleton
public class TaskController implements Reloadable {

    private LoriApiService apiService;
    private AppExecutors executors;
    private TaskDao taskDao;
    private List<Task> cacheTasks;

    private boolean cacheIsDirty = true;

    @Inject
    public TaskController(LoriApiService apiService, AppExecutors executors, TaskDao taskDao) {
        this.apiService = apiService;
        this.executors = executors;
        this.taskDao = taskDao;
    }

    public void refresh() {
        cacheIsDirty = true;
    }

    public void load(final LoadDataCallback<List<Task>> callback) {
        if (!cacheIsDirty) {
            callback.onSuccess(cacheTasks);
        }
        executors.background().execute(() -> {
            try {
                List<Task> newTasks = apiService.getTasks();
                taskDao.saveAll(newTasks.toArray(new Task[newTasks.size()]));
                cacheTasks = newTasks;
                cacheIsDirty = false;
                executors.mainThread().execute(() -> callback.onSuccess(newTasks));
            } catch (NetworkApiException e) {
                if (e instanceof NetworkOfflineException) {
                    cacheTasks = taskDao.loadAll();
                    cacheIsDirty = false;
                    executors.mainThread().execute(() -> callback.networkUnreachable(cacheTasks));
                } else {
                    executors.mainThread().execute(() -> callback.onFailure(e));
                }
            }
        });
    }

    public void loadTasksForProject(String projectId, final LoadDataCallback<List<Task>> callback) {
        executors.background().execute(() -> {
            List<Task> data = taskDao.loadTaskForProject(projectId);
            executors.mainThread().execute(() -> callback.onSuccess(data));
        });
    }

    public void loadTask(String id, final LoadDataCallback<Task> callback) {
        executors.background().execute(() -> {
            Task data = taskDao.load(id);
            if (data != null) {
                executors.mainThread().execute(() -> callback.onSuccess(data));
            } else {
                executors.mainThread().execute(() -> callback.onFailure(new NonEntryException()));
            }
        });
    }

    @Override
    public void reload(Callback callback) {
        refresh();
        load(new LoadDataCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> data) {
                callback.onSuccess();
            }

            @Override
            public void networkUnreachable(List<Task> localData) {
                callback.onOffline();
            }

            @Override
            public void onFailure(Throwable e) {
                callback.onFailure(e);
            }
        });
    }
}
