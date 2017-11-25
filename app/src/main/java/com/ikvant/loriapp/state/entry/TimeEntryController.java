package com.ikvant.loriapp.state.entry;

import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.timeentry.TimeEntryDao;
import com.ikvant.loriapp.database.user.User;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.network.exceptions.NetworkApiException;
import com.ikvant.loriapp.network.exceptions.NetworkOfflineException;
import com.ikvant.loriapp.network.exceptions.NotFoundException;
import com.ikvant.loriapp.utils.AppExecutors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by ikvant.
 */
@Singleton
public class TimeEntryController implements Reloadable {
    private final static int OFFSET_STEP = 20;

    private TimeEntryDao timeEntryDao;
    private LoriApiService apiService;
    private AppExecutors executors;

    private boolean cacheIsDirty = true;
    private List<TimeEntry> cacheWeekEntries = new ArrayList<>();
    private int cacheSize;
    private UserController userController;

    @Inject
    public TimeEntryController(TimeEntryDao timeEntryDao, UserController userController, LoriApiService apiService, AppExecutors executors) {
        this.timeEntryDao = timeEntryDao;
        this.apiService = apiService;
        this.executors = executors;
        this.userController = userController;
    }


    public void refresh() {
        cacheIsDirty = true;
        cacheSize = 0;
        cacheWeekEntries.clear();
    }

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

    public void loadByText(String text, LoadDataCallback<List<TimeEntry>> callback) {
        executors.background().execute(() -> {
            List<TimeEntry> list = timeEntryDao.findByText(text);
            executors.mainThread().execute(() -> callback.onSuccess(list));
        });
    }

    public void loadByDate(Date from, Date to, LoadDataCallback<List<TimeEntry>> callback) {
        executors.background().execute(() -> {
            List<TimeEntry> list = timeEntryDao.findByDate(from, to);
            executors.mainThread().execute(() -> callback.onSuccess(list));
        });
    }

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

    public void updateTimeEntry(TimeEntry timeEntry, LoadDataCallback<TimeEntry> callback) {
        executors.background().execute(() -> {
            timeEntry.setSync(false);
            timeEntryDao.save(timeEntry);

            cacheWeekEntries.remove(timeEntry);

            try {
                TimeEntry newTimeEntry = apiService.updateTimeEntry(timeEntry);

                timeEntryDao.save(newTimeEntry);
                addToCache(newTimeEntry);
                callback.onSuccess(newTimeEntry);
            } catch (NetworkApiException e) {
                addToCache(timeEntry);
                if (e instanceof NetworkOfflineException) {
                    executors.mainThread().execute(() -> callback.networkUnreachable(timeEntry));
                } else {
                    executors.mainThread().execute(() -> callback.onFailure(e));
                }
            }

        });
    }

    public void loadTimeEntries(int offset, int size, final LoadDataCallback<List<TimeEntry>> callback) {
        if (!cacheIsDirty && offset + size <= cacheSize) {
            callback.onSuccess(getCacheWeekEntries(offset, size));
            return;
        }
        executors.background().execute(() -> {
            try {
                syncIfNeed();
                List<TimeEntry> newEntries = apiService.getTimeEntries(offset, size);

                if (cacheIsDirty) {
                    cacheWeekEntries.clear();
                    timeEntryDao.deleteAll();
                }
                timeEntryDao.saveAll(newEntries.toArray(new TimeEntry[newEntries.size()]));
                cacheWeekEntries.addAll(newEntries);
                cacheIsDirty = false;
                cacheSize = offset + newEntries.size();
                executors.mainThread().execute(() -> callback.onSuccess(newEntries));
            } catch (NetworkApiException e) {
                if (e instanceof NetworkOfflineException) {
                    cacheWeekEntries.addAll(timeEntryDao.loadAll(false, offset, size));
                    cacheIsDirty = false;
                    cacheSize = offset + size;
                    executors.mainThread().execute(() -> callback.networkUnreachable(getCacheWeekEntries(offset, size)));
                } else {
                    executors.mainThread().execute(() -> callback.onFailure(e));
                }
            }
        });
    }

    public void delete(String id, LoadDataCallback<TimeEntry> callback) {
        executors.background().execute(() -> {
            TimeEntry entry = timeEntryDao.load(id);
            entry.setDeleted(true);
            timeEntryDao.save(entry);
            removeFromCache(entry);
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

    public void loadById(String id, LoadDataCallback<TimeEntry> callback) {
        executors.background().execute(() -> {
            TimeEntry entry = timeEntryDao.load(id);
            executors.mainThread().execute(() -> callback.onSuccess(entry));
        });
    }

    private void createNewTimeEntry(TimeEntry timeEntry, User user, LoadDataCallback<TimeEntry> callback) {
        executors.background().execute(() -> {
            timeEntry.setUser(user);
            timeEntry.setSync(false);
            timeEntryDao.save(timeEntry);
            addToCache(timeEntry);
            String localId = timeEntry.getId();
            try {
                timeEntry.setId(TimeEntry.NEW_ID);

                TimeEntry newTimeEntry = apiService.createTimeEntry(timeEntry);

                timeEntryDao.delete(localId);
                removeFromCache(timeEntry);

                timeEntryDao.save(newTimeEntry);
                addToCache(newTimeEntry);
                callback.onSuccess(newTimeEntry);
            } catch (NetworkApiException e) {
                if (e instanceof NetworkOfflineException) {
                    timeEntry.setId(localId);
                    executors.mainThread().execute(() -> callback.networkUnreachable(timeEntry));
                } else {
                    executors.mainThread().execute(() -> callback.onFailure(e));
                }
            }

        });
    }


    private List<TimeEntry> getCacheWeekEntries(int offset, int size) {
        List<TimeEntry> temp = new ArrayList<>(offset);
        for (int i = offset; i < Math.min(offset + size, cacheWeekEntries.size()); i++) {
            temp.add(cacheWeekEntries.get(i));
        }
        return temp;
    }

    private void syncIfNeed() throws NetworkApiException {
        List<TimeEntry> notSync = timeEntryDao.loadUnSync(false);
        for (TimeEntry entry : notSync) {
            uploadTimeEntry(entry);
        }
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

    private void addToCache(TimeEntry entry) {
        cacheWeekEntries.add(entry);
        Collections.sort(cacheWeekEntries, (t1, t2) -> t1.getDate().compareTo(t2.getDate()));
    }

    private void removeFromCache(TimeEntry entry) {
        cacheWeekEntries.remove(entry);
    }

    @Override
    public void reload(Callback callback) {
        refresh();
        loadTimeEntries(0, 20, new LoadDataCallback<List<TimeEntry>>() {
            @Override
            public void onSuccess(List<TimeEntry> data) {
                callback.onSuccess();
            }

            @Override
            public void networkUnreachable(List<TimeEntry> localData) {
                callback.onOffline();
            }

            @Override
            public void onFailure(Throwable e) {
                callback.onFailure(e);
            }
        });
    }


}
