package com.ikvant.loriapp.state.entry;

import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.timeentry.TimeEntryDao;
import com.ikvant.loriapp.database.user.User;
import com.ikvant.loriapp.database.user.UserDao;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.network.exceptions.NetworkApiException;
import com.ikvant.loriapp.network.exceptions.NetworkOfflineException;
import com.ikvant.loriapp.network.exceptions.NotFoundException;
import com.ikvant.loriapp.utils.AppExecutors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ikvant.
 */

public class LoriEntryController implements EntryController {
    private static final String TAG = "LoriEntryController";

    private TimeEntryDao timeEntryDao;
    private LoriApiService apiService;
    private AppExecutors executors;
    private User user;

    private boolean cacheIsDirty = true;
    private Set<TimeEntry> cacheTimeEntry = new HashSet<>();

    public LoriEntryController(TimeEntryDao timeEntryDao, UserDao userDao, LoriApiService apiService, AppExecutors executors) {
        this.timeEntryDao = timeEntryDao;
        this.apiService = apiService;
        this.executors = executors;

        executors.diskIO().execute(() -> {
            user = userDao.load();
        });
    }


    @Override
    public void refresh() {
        cacheIsDirty = true;
    }

    @Override
    public void loadTimeEntry(String id, LoadDataCallback<TimeEntry> callback) {
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
    public void createNewTimeEntry(TimeEntry timeEntry, LoadDataCallback<TimeEntry> callback) {
        executors.diskIO().execute(() -> {
            timeEntry.setUser(user);
            timeEntry.setSync(false);
            timeEntryDao.save(timeEntry);
            cacheTimeEntry.add(timeEntry);

            try {
                String oldId = timeEntry.getId();
                timeEntry.setId(TimeEntry.NEW_ID);
                TimeEntry newTimeEntry = apiService.createTimeEntry(timeEntry);
                cacheTimeEntry.add(newTimeEntry);

                timeEntryDao.delete(oldId);
                timeEntry.setId(oldId);
                cacheTimeEntry.remove(timeEntry);

                callback.onSuccess(timeEntry);
            } catch (NetworkApiException e) {
                if (e instanceof NetworkOfflineException) {
                    executors.mainThread().execute(() -> callback.networkUnreachable(timeEntry));
                } else {
                    executors.mainThread().execute(() -> callback.onFailure(e));
                }
            }

        });
    }

    @Override
    public void updateTimeEntry(TimeEntry timeEntry, LoadDataCallback<TimeEntry> callback) {
        executors.diskIO().execute(() -> {
            timeEntry.setSync(false);
            timeEntryDao.save(timeEntry);
            cacheTimeEntry.add(timeEntry);

            try {
                TimeEntry newTimeEntry = apiService.updateTimeEntry(timeEntry);
                cacheTimeEntry.add(newTimeEntry);
            } catch (NetworkApiException e) {
                executors.mainThread().execute(() -> callback.networkUnreachable(timeEntry));
            }

            callback.onSuccess(timeEntry);
        });
    }

    @Override
    public void loadTimeEntries(final LoadDataCallback<List<TimeEntry>> callback) {
        if (!cacheIsDirty) {
            callback.onSuccess(new ArrayList<>(cacheTimeEntry));
            return;
        }
        executors.diskIO().execute(() -> {
            try {
                List<TimeEntry> newEntries = reloadTimeEntries();
                timeEntryDao.deleteAll();
                timeEntryDao.saveAll(newEntries.toArray(new TimeEntry[newEntries.size()]));
                cacheTimeEntry.clear();
                cacheTimeEntry.addAll(newEntries);
                cacheIsDirty = false;
                executors.mainThread().execute(() -> callback.onSuccess(newEntries));
            } catch (NetworkApiException e) {
                if (e instanceof NetworkOfflineException) {
                    List<TimeEntry> entries = timeEntryDao.loadAll(false);
                    cacheTimeEntry.clear();
                    cacheTimeEntry.addAll(entries);
                    cacheIsDirty = true;
                    executors.mainThread().execute(() -> callback.onSuccess(entries));
                } else {
                    executors.mainThread().execute(() -> callback.onFailure(e));
                }
            }
        });
    }

    @Override
    public void delete(String id, LoadDataCallback<Void> callback) {
        executors.diskIO().execute(() -> {
            TimeEntry entry = timeEntryDao.load(id);
            entry.setDeleted(true);
            timeEntryDao.save(entry);
            cacheTimeEntry.remove(entry);
            try {
                apiService.deleteTimeEntry(entry.getId());
                executors.mainThread().execute(() -> callback.onSuccess(null));
            } catch (NetworkApiException e) {
                if (e instanceof NetworkOfflineException) {
                    executors.mainThread().execute(() -> callback.networkUnreachable(null));
                } else {
                    executors.mainThread().execute(() -> callback.onFailure(e));
                }
            }

        });
    }


    private List<TimeEntry> reloadTimeEntries() throws NetworkApiException {
        List<TimeEntry> notSync = timeEntryDao.loadUnSync(false);
        for (TimeEntry entry : notSync) {
            uploadTimeEntry(entry);
        }
        return apiService.getTimeEntries();
    }

    private void uploadTimeEntry(TimeEntry entry) throws NetworkApiException {
        if (TimeEntry.isNew(entry)) {
            if (!entry.isDeleted()) {
                entry.setId(TimeEntry.NEW_ID);
                apiService.createTimeEntry(entry);
            }
        } else if (entry.isDeleted()) {
            try {
                apiService.deleteTimeEntry(entry.getId());
            } catch (NetworkApiException e) {
                if (!(e instanceof NotFoundException)) {
                    throw e;
                }
            }
        } else {
            apiService.updateTimeEntry(entry);
        }
    }
}
