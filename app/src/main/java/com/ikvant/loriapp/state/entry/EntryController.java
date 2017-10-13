package com.ikvant.loriapp.state.entry;

import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.utils.Callback;

import java.util.List;

/**
 * Created by ikvant.
 */

public interface EntryController {
    boolean needSync();

    void load(String id, Callback<TimeEntry> callback);

    void save(TimeEntry task, Callback<TimeEntry> callback);

    void loadTasks(Callback<List<Task>> callback);

    void loadEntries(Callback<List<TimeEntry>> callback);

    void delete(String id, Callback<Void> callback);
}
