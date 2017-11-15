package com.ikvant.loriapp.network;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.ikvant.loriapp.database.project.Project;
import com.ikvant.loriapp.database.tags.Tag;
import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.token.Token;
import com.ikvant.loriapp.database.user.User;
import com.ikvant.loriapp.network.exceptions.NetworkApiException;
import com.ikvant.loriapp.network.exceptions.NetworkOfflineException;
import com.ikvant.loriapp.network.exceptions.NotFoundException;
import com.ikvant.loriapp.network.exceptions.UnauthorizedException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import javax.annotation.Nonnull;

import okhttp3.ResponseBody;
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
    private NetworkChecker checker;

    public LoriApiService(NetworkChecker checker, ApiService service) {
        this.service = service;
        this.checker = checker;
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
    public List<TimeEntry> getTimeEntries(int offset, int size) throws NetworkApiException {
        return executeRequest(service.getTimeEntries(offset, size, getFormattedToken()));
    }

    @Nonnull
    public List<Task> getTasks() throws NetworkApiException {
        return executeRequest(service.getTasks(getFormattedToken()));
    }

    public TimeEntry createTimeEntry(TimeEntry entry) throws NetworkApiException {
        return executeRequest(service.createTimeEntry(entry, getFormattedToken()));
    }

    public TimeEntry updateTimeEntry(TimeEntry timeEntry) throws NetworkApiException {
        return executeRequest(service.updateTimeEntry(timeEntry.getId(), timeEntry, getFormattedToken()));
    }

    public void deleteTimeEntry(String id) throws NetworkApiException {
        executeRequest(service.deleteTimeEntry(id, getFormattedToken()));
    }

    public User getUser() throws NetworkApiException {
        return executeRequest(service.getUser(getFormattedToken()));
    }

    public List<Project> getProjects() throws NetworkApiException {
        return executeRequest(service.getProjects(getFormattedToken()));
    }

    public List<Tag> getTags() throws NetworkApiException {
        return executeRequest(service.getTags(getFormattedToken()));
    }

    private String getFormattedToken() {
        return "Bearer " + token;
    }

    private <T> T executeRequest(Call<T> callable) throws NetworkApiException {
        handleNetworkException();
        try {
            Response<T> response = callable.execute();
            Log.d(TAG, "getTimeEntries() called" + response);

            if (response.isSuccessful()) {
                return response.body();
            }
            switch (response.code()) {
                case HttpURLConnection.HTTP_UNAUTHORIZED: {
                    if (listener != null) {
                        listener.onUnauthorized();
                    }
                    throw new UnauthorizedException();
                }
                case HttpURLConnection.HTTP_NOT_FOUND:
                    throw new NotFoundException();
                default:
                    ResponseBody errorBody = response.errorBody();
                    if (errorBody != null) {
                        try {
                            Gson gson = new Gson();
                            ErrorMessage message = gson.fromJson(errorBody.string(), ErrorMessage.class);
                            if (!TextUtils.isEmpty(message.description)) {
                                throw new NetworkApiException(message.description);
                            }
                        } catch (IOException e) {
                            Log.w(TAG, "convert to error message error: ", e);
                        }
                    }
                    throw new NetworkApiException("Response code " + response.code());
            }
        } catch (IOException e) {
            throw new NetworkApiException(e);
        }
    }

    public void setListener(UnauthorizedListener listener) {
        this.listener = listener;
    }

    private void handleNetworkException() throws NetworkOfflineException {
        if (!checker.hasNetwork()) {
            throw new NetworkOfflineException();
        }
    }

    private class ErrorMessage {
        String error;
        @SerializedName("error_description")
        String description;
    }


}
