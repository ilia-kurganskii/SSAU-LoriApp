package com.ikvant.loriapp.state.entry;

import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.task.TaskDao;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.timeentry.TimeEntryDao;
import com.ikvant.loriapp.database.user.User;
import com.ikvant.loriapp.database.user.UserDao;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.network.exceptions.NetworkApiException;
import com.ikvant.loriapp.utils.AppExecutors;
import com.ikvant.loriapp.utils.Callback;

import java.util.List;

/**
 * Created by ikvant.
 */

public class LoriEntryController implements EntryController {
    private static final String TAG = "LoriEntryController";

    private TimeEntryDao timeEntryDao;
    private LoriApiService apiService;
    private AppExecutors executors;
    private TaskDao taskDao;
    private UserDao userDao;
    private User user;

    public LoriEntryController(TimeEntryDao timeEntryDao, UserDao userDao, LoriApiService apiService, AppExecutors executors, TaskDao taskDao) {
        this.timeEntryDao = timeEntryDao;
        this.apiService = apiService;
        this.executors = executors;
        this.taskDao = taskDao;
        this.userDao = userDao;

        executors.diskIO().execute(() -> {
            user = userDao.load();
        });
    }

    @Override
    public void loadTimeEntry(String id, Callback<TimeEntry> callback) {
        //localId update after update
        executors.diskIO().execute(() -> {
            try {
                TimeEntry task = timeEntryDao.load(id);
                executors.mainThread().execute(() -> callback.onSuccess(task));
            } catch (Exception e) {
                executors.mainThread().execute(() -> callback.onFailure(e));
            }
        });
    }

    @Override
    public void saveTimeEntry(TimeEntry timeEntry, Callback<TimeEntry> callback) {
        executors.diskIO().execute(() -> {
            if (TimeEntry.isNew(timeEntry)) {
                timeEntry.setUser(user);
            }
            timeEntry.setSync(false);
            timeEntryDao.save(timeEntry);
            callback.onSuccess(timeEntry);
        });
    }

    @Override
    public void loadTasks(final Callback<List<Task>> callback) {
        executors.diskIO().execute(() -> {
            List<Task> localTasks = taskDao.loadAll();
            executors.mainThread().execute(() -> callback.onSuccess(localTasks));
        });
    }


    @Override
    public void loadTimeEntries(final Callback<List<TimeEntry>> callback) {
        executors.diskIO().execute(() -> {
            List<TimeEntry> localTasks = timeEntryDao.loadAll(false);
            executors.mainThread().execute(() -> callback.onSuccess(localTasks));
        });
    }

    @Override
    public void delete(String id, Callback<Void> callback) {
        executors.diskIO().execute(() -> {
            TimeEntry entry = timeEntryDao.load(id);
            entry.setDeleted(true);
            timeEntryDao.save(entry);
            callback.onSuccess(null);
        });
    }

    @Override
    public void synс(Callback<Void> callback) {
        executors.networkIO().execute(() -> {
            try {
                reloadUser();
                reloadTasks();
                reloadTimeEntries();
                executors.mainThread().execute(() -> callback.onSuccess(null));
            } catch (NetworkApiException e) {
                executors.mainThread().execute(() -> callback.onFailure(e));
            }
        });
    }

    private void reloadUser() throws NetworkApiException {
        User user = apiService.getUser();
        userDao.save(user);
        this.user = user;
    }

    private void reloadTasks() throws NetworkApiException {
        List<Task> tasks = apiService.getTasks();
        taskDao.saveAll(tasks.toArray(new Task[tasks.size()]));
    }

    private void reloadTimeEntries() throws NetworkApiException {
        List<TimeEntry> notSync = timeEntryDao.loadUnSync(false);
        for (TimeEntry entry : notSync) {
            if (TimeEntry.isNew(entry)) {
                if (!entry.isDeleted()) {
                    entry.setId(TimeEntry.NEW_ID);
                    apiService.createTimeEntry(entry);
                }
            } else if (entry.isDeleted()) {
                apiService.deleteTimeEntry(entry.getId());
            } else {
                apiService.updateTimeEntry(entry);
            }
        }
        List<TimeEntry> newEntryList = apiService.getTimeEntries();
        timeEntryDao.deleteAll();
        timeEntryDao.saveAll(newEntryList.toArray(new TimeEntry[newEntryList.size()]));
        timeEntryDao.loadAll(false);
    }
}
