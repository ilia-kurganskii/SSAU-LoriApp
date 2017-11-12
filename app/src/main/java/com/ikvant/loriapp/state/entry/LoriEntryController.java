package com.ikvant.loriapp.state.entry;

import android.util.SparseArray;

import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.timeentry.TimeEntryDao;
import com.ikvant.loriapp.database.user.User;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.network.exceptions.NetworkApiException;
import com.ikvant.loriapp.network.exceptions.NetworkOfflineException;
import com.ikvant.loriapp.network.exceptions.NotFoundException;
import com.ikvant.loriapp.utils.AppExecutors;

import java.util.Calendar;
import java.util.Date;
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

    private boolean cacheIsDirty = true;
    private SparseArray<Set<TimeEntry>> cacheWeekEntries = new SparseArray<>();
    private UserController userController;

    public LoriEntryController(TimeEntryDao timeEntryDao, UserController userController, LoriApiService apiService, AppExecutors executors) {
        this.timeEntryDao = timeEntryDao;
        this.apiService = apiService;
        this.executors = executors;
        this.userController = userController;
    }


    @Override
    public void refresh() {
        cacheIsDirty = true;
    }

    @Override
    public void loadTimeEntry(String id, LoadDataCallback<TimeEntry> callback) {
        //localId update after update
        executors.background().execute(() -> {
            try {
                TimeEntry task = timeEntryDao.load(id);
                executors.mainThread().execute(() -> callback.onSuccess(task));
            } catch (Exception e) {
                executors.mainThread().execute(() -> callback.onFailure(e));
            }
        });
    }

    @Override
    public void loadByText(String text, LoadDataCallback<SparseArray<Set<TimeEntry>>> callback) {
        executors.background().execute(() -> {
            SparseArray<Set<TimeEntry>> list = sortEntryByWeek(timeEntryDao.findByText(text));
            executors.mainThread().execute(() -> callback.onSuccess(list));
        });
    }

    @Override
    public void loadByDate(Date from, Date to, LoadDataCallback<SparseArray<Set<TimeEntry>>> callback) {
        executors.background().execute(() -> {
            SparseArray<Set<TimeEntry>> list = sortEntryByWeek(timeEntryDao.findByDate(from, to));
            executors.mainThread().execute(() -> callback.onSuccess(list));
        });
    }

    @Override
    public void createNewTimeEntry(TimeEntry timeEntry, LoadDataCallback<TimeEntry> callback) {
        userController.getUser(new LoadDataCallback<User>() {
            @Override
            public void onSuccess(User data) {
                createNewTimeEntry(timeEntry, data, callback);
            }

            @Override
            public void networkUnreachable(User localData) {
                createNewTimeEntry(timeEntry, localData, callback);
            }

            @Override
            public void onFailure(Throwable e) {
                callback.onFailure(e);
            }
        });

    }

    @Override
    public void updateTimeEntry(TimeEntry timeEntry, LoadDataCallback<TimeEntry> callback) {
        executors.background().execute(() -> {
            timeEntry.setSync(false);
            timeEntryDao.save(timeEntry);

            cacheWeekEntries.get(getWeekNumber(timeEntry.getDate())).remove(timeEntry);

            try {
                TimeEntry newTimeEntry = apiService.updateTimeEntry(timeEntry);
                getWeekSetFromSource(timeEntry, cacheWeekEntries).add(newTimeEntry);
                callback.onSuccess(newTimeEntry);
            } catch (NetworkApiException e) {
                cacheWeekEntries.get(getWeekNumber(timeEntry.getDate())).add(timeEntry);
                if (e instanceof NetworkOfflineException) {
                    executors.mainThread().execute(() -> callback.networkUnreachable(timeEntry));
                } else {
                    executors.mainThread().execute(() -> callback.onFailure(e));
                }
            }

        });
    }

    @Override
    public void loadTimeEntries(final LoadDataCallback<SparseArray<Set<TimeEntry>>> callback) {
        if (!cacheIsDirty) {
            callback.onSuccess(getCacheWeekEntries());
            return;
        }
        executors.background().execute(() -> {
            try {
                List<TimeEntry> newEntries = reloadTimeEntries();
                timeEntryDao.deleteAll();
                timeEntryDao.saveAll(newEntries.toArray(new TimeEntry[newEntries.size()]));
                cacheWeekEntries = sortEntryByWeek(newEntries);
                cacheIsDirty = false;
                executors.mainThread().execute(() -> callback.onSuccess(getCacheWeekEntries()));
            } catch (NetworkApiException e) {
                if (e instanceof NetworkOfflineException) {
                    List<TimeEntry> entries = timeEntryDao.loadAll(false);
                    cacheWeekEntries = sortEntryByWeek(entries);
                    cacheIsDirty = false;
                    executors.mainThread().execute(() -> callback.networkUnreachable(getCacheWeekEntries()));
                } else {
                    executors.mainThread().execute(() -> callback.onFailure(e));
                }
            }
        });
    }

    @Override
    public void loadTimeEntriesByWeek(int weekIndex, LoadDataCallback<Set<TimeEntry>> callback) {
        loadTimeEntries(new LoadDataCallback<SparseArray<Set<TimeEntry>>>() {
            @Override
            public void onSuccess(SparseArray<Set<TimeEntry>> data) {
                callback.onSuccess(data.get(weekIndex));
            }

            @Override
            public void networkUnreachable(SparseArray<Set<TimeEntry>> localData) {
                callback.networkUnreachable(localData.get(weekIndex));
            }

            @Override
            public void onFailure(Throwable e) {
                callback.onFailure(e);
            }
        });
    }

    @Override
    public void delete(String id, LoadDataCallback<TimeEntry> callback) {
        executors.background().execute(() -> {
            TimeEntry entry = timeEntryDao.load(id);
            entry.setDeleted(true);
            timeEntryDao.save(entry);
            getWeekSetFromSource(entry, cacheWeekEntries).remove(entry);
            try {
                apiService.deleteTimeEntry(entry.getId());
                executors.mainThread().execute(() -> callback.onSuccess(entry));
            } catch (NetworkApiException e) {
                if (e instanceof NetworkOfflineException) {
                    executors.mainThread().execute(() -> callback.networkUnreachable(entry));
                } else {
                    executors.mainThread().execute(() -> callback.onFailure(e));
                }
            }

        });
    }

    private void createNewTimeEntry(TimeEntry timeEntry, User user, LoadDataCallback<TimeEntry> callback) {
        executors.background().execute(() -> {
            timeEntry.setUser(user);
            timeEntry.setSync(false);
            timeEntryDao.save(timeEntry);
            getWeekSetFromSource(timeEntry, cacheWeekEntries).add(timeEntry);

            try {
                String oldId = timeEntry.getId();
                timeEntry.setId(TimeEntry.NEW_ID);
                TimeEntry newTimeEntry = apiService.createTimeEntry(timeEntry);

                timeEntryDao.delete(oldId);
                timeEntry.setId(oldId);

                getWeekSetFromSource(timeEntry, cacheWeekEntries).remove(timeEntry);
                getWeekSetFromSource(timeEntry, cacheWeekEntries).add(newTimeEntry);
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

    private SparseArray<Set<TimeEntry>> getCacheWeekEntries() {
        return cacheWeekEntries.clone();
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
            entry.setSync(true);
        }
    }

    private SparseArray<Set<TimeEntry>> sortEntryByWeek(List<TimeEntry> entries) {
        SparseArray<Set<TimeEntry>> entrySparseArray = new SparseArray<>();
        for (TimeEntry entry : entries) {
            getWeekSetFromSource(entry, entrySparseArray).add(entry);
        }
        return entrySparseArray;
    }

    private Set<TimeEntry> getWeekSetFromSource(TimeEntry entry, SparseArray<Set<TimeEntry>> source) {
        int order = getWeekNumber(entry.getDate());
        Set<TimeEntry> entries = source.get(getWeekNumber(entry.getDate()));
        if (entries == null) {
            entries = new HashSet<>();
            source.put(order, entries);
        }
        return entries;
    }

    private int getWeekNumber(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    @Override
    public void reload(Callback callback) {
        refresh();
        loadTimeEntries(new LoadDataCallback<SparseArray<Set<TimeEntry>>>() {
            @Override
            public void onSuccess(SparseArray<Set<TimeEntry>> data) {
                callback.onSuccess();
            }

            @Override
            public void networkUnreachable(SparseArray<Set<TimeEntry>> localData) {
                callback.onOffline();
            }

            @Override
            public void onFailure(Throwable e) {
                callback.onFailure(e);
            }
        });
    }
}
