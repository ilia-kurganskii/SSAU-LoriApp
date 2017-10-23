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
public class TaskController {

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

    public void loadTasks(final LoadDataCallback<List<Task>> callback) {
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
}
