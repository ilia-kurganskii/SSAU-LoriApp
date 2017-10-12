package com.ikvant.loriapp.network;

import android.util.Log;

import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.token.Token;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.Time;
import java.util.List;

import javax.annotation.Nonnull;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by ikvant.
 */

public class LoriApiService {
    private static final String TAG = "LoriApiService";

    private ApiService service;
    private String token;
    private UnauthorizedListener listener;

    public LoriApiService(ApiService service) {
        this.service = service;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Token login(String name, String password) throws NetworkApiException {
        return executeRequest(service.login(name, password, "password"));
    }

    @Nonnull
    public List<TimeEntry> getTimeEntries() throws NetworkApiException {
        return executeRequest(service.getTimeEntries(getFormattedToken()));
    }

    @Nonnull
    public List<Task> getTasks() throws NetworkApiException {
        return executeRequest(service.getTasks(getFormattedToken()));
    }

    public void updateTimeEntry(TimeEntry timeEntry) throws NetworkApiException {
        executeRequest(service.updateTimeEntry(timeEntry.getId(), timeEntry, getFormattedToken()));
    }

    private String getFormattedToken() {
        return "Bearer " + token;
    }

    private <T> T executeRequest(Call<T> callable) throws NetworkApiException {
        try {
            Response<T> response = callable.execute();
            Log.d(TAG, "getTimeEntries() called" + response);
            if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                if (listener != null) {
                    listener.onUnauthorized();
                }
                throw new UnauthorizedException();
            }
            if (response.isSuccessful()) {
                return response.body();
            }
            throw new NetworkApiException();
        } catch (IOException e) {
            throw new NetworkApiException(e);
        }
    }

    public void setListener(UnauthorizedListener listener) {
        this.listener = listener;
    }

    public class UnauthorizedException extends NetworkApiException {
    }
}
