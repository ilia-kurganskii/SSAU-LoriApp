package com.ikvant.loriapp.state.timeentry;

import android.util.Log;

import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.timeentry.TimeEntryDao;
import com.ikvant.loriapp.database.token.Token;
import com.ikvant.loriapp.network.ApiService;
import com.ikvant.loriapp.network.LoriApiService;
import com.ikvant.loriapp.state.auth.AuthController;
import com.ikvant.loriapp.utils.AppExecutors;
import com.ikvant.loriapp.utils.Callback;
import com.ikvant.loriapp.utils.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

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
        final List<TimeEntry> result = new ArrayList<>();
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                result.addAll(timeEntryDao.load());
                apiService.getTimeEntries().enqueue(new retrofit2.Callback<List<TimeEntry>>() {
                    @Override
                    public void onResponse(Call<List<TimeEntry>> call, Response<List<TimeEntry>> response) {
                        List<TimeEntry> entryList = response.body();
                        if (entryList != null) {
                            result.addAll(entryList);
                        }
                        executors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(result);
                            }
                        });

                    }

                    @Override
                    public void onFailure(Call<List<TimeEntry>> call, Throwable t) {
                        Log.d(TAG, "onFailure() called with: call = [" + call + "], t = [" + t + "]");
                        executors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(result);
                            }
                        });
                    }
                });

            }
        });
    }
}
