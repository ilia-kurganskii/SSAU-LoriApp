package com.ikvant.loriapp.state.entry;

import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.utils.Callback;

import java.util.List;

/**
 * Created by ikvant.
 */

public interface EntryController {
    void loadTimeEntry(String id, Callback<TimeEntry> callback);

    void saveTimeEntry(TimeEntry task, Callback<TimeEntry> callback);

    void loadTasks(Callback<List<Task>> callback);

    void loadTimeEntries(Callback<List<TimeEntry>> callback);

    void delete(String id, Callback<Void> callback);

    void synс(Callback<Void> callback);
}
