package com.ikvant.loriapp.state.timeentry;

import android.util.ArraySet;
import android.util.Log;

import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.timeentry.TimeEntryDao;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.network.NetworkApiException;
import com.ikvant.loriapp.state.auth.AuthController;
import com.ikvant.loriapp.utils.AppExecutors;
import com.ikvant.loriapp.utils.Callback;

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
    private AuthController authController;

    public LoriTimeEntryController(TimeEntryDao timeEntryDao, LoriApiService apiService, AppExecutors executors, AuthController authController) {
        this.timeEntryDao = timeEntryDao;
        this.apiService = apiService;
        this.executors = executors;
        this.authController = authController;
    }

    @Override
    public boolean needSync() {
        return false;
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
}
