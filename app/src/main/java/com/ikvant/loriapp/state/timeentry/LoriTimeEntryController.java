package com.ikvant.loriapp.state.timeentry;

import android.util.Log;

import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.task.TaskDao;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.timeentry.TimeEntryDao;
import com.ikvant.loriapp.database.user.User;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.network.NetworkApiException;
import com.ikvant.loriapp.utils.AppExecutors;
import com.ikvant.loriapp.utils.Callback;
import com.ikvant.loriapp.utils.SimpleCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ikvant.
 */

public class LoriTimeEntryController implements TimeEntryController {
    private static final String TAG = "LoriTimeEntryController";

    private TimeEntryDao timeEntryDao;
    private LoriApiService apiService;
    private AppExecutors executors;
    private TaskDao taskDao;
    private User user;

    public LoriTimeEntryController(TimeEntryDao timeEntryDao, LoriApiService apiService, AppExecutors executors, TaskDao taskDao) {
        this.timeEntryDao = timeEntryDao;
        this.apiService = apiService;
        this.executors = executors;
        this.taskDao = taskDao;
    }

    @Override
    public boolean needSync() {
        return false;
    }

    @Override
    public void load(String id, Callback<TimeEntry> callback) {
        executors.diskIO().execute(()->{
            try {
                TimeEntry task = timeEntryDao.load(id);
                executors.mainThread().execute(()->callback.onSuccess(task));
            } catch (Exception e){
                executors.mainThread().execute(()->callback.onFailure(e));
            }
        });
    }

    @Override
    public void save(TimeEntry timeEntry, Callback<TimeEntry> callback) {
        executors.diskIO().execute(()->{
            try {
                timeEntryDao.save(timeEntry);
                if (TimeEntry.isNew(timeEntry)) {
                    timeEntry.setUser(getUser());
                    apiService.createTimeEntry(timeEntry);
                } else {
                    apiService.updateTimeEntry(timeEntry);
                }
                executors.mainThread().execute(()->callback.onSuccess(timeEntry));
            } catch (Exception e){
                executors.mainThread().execute(()->callback.onFailure(e));
            }
        });
    }

    @Override
    public void loadTasks(final Callback<List<Task>> callback){
        executors.diskIO().execute(()->{
            Set<Task> tasks = new HashSet<>();
            tasks.addAll(taskDao.loadAll());
            executors.networkIO().execute(()->{
                try {
                    List<Task> newTasks = apiService.getTasks();
                    taskDao.deleteAll();
                    taskDao.saveAll(newTasks.toArray(new Task[newTasks.size()]));
                    tasks.addAll(newTasks);
                } catch (NetworkApiException e) {
                    Log.d(TAG, "loadTasks() called with: callback = [" + callback + "]");
                }
                executors.mainThread().execute(()->callback.onSuccess(new ArrayList<>(tasks)));
            });
        });
    }


    @Override
    public void loadEntries(final Callback<List<TimeEntry>> callback) {

            final Set<TimeEntry> set = new HashSet<>();
            executors.diskIO().execute(() -> {
                set.addAll(timeEntryDao.loadAll());
                executors.networkIO().execute(() -> {
                    try {
                        List<TimeEntry> newEntryList = apiService.getTimeEntries();
                        set.addAll(newEntryList);
                        executors.diskIO().execute(() -> timeEntryDao.saveAll(newEntryList.toArray(new TimeEntry[newEntryList.size()])));
                        executors.mainThread().execute(() -> callback.onSuccess(new ArrayList<>(set)));
                    } catch (NetworkApiException e) {
                        Log.d(TAG, "onFailure() called with: call = [" + e + "]");
                        executors.mainThread().execute(() -> callback.onSuccess(new ArrayList<>(set)));
                    }
                });
            });

    }

    @Override
    public void delete(String id, Callback<Void> callback) {
        executors.diskIO().execute(()->{
            timeEntryDao.delete(id);
            executors.networkIO().execute(()-> {
                try {
                    apiService.deleteTimeEntry(id);
                    callback.onSuccess(null);
                } catch (NetworkApiException e) {
                    callback.onFailure(e);
                }
            });
        });
    }

    private User getUser() throws NetworkApiException {
        if (user == null){
            user = apiService.getUser();
        }
        return user;
    }

    public void sync(Runnable callback){
        executors.networkIO().execute(()->{

        });
    }
}
