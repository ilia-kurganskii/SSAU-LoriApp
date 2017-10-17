package com.ikvant.loriapp.state.entry;

import com.ikvant.loriapp.database.timeentry.TimeEntry;

import java.util.List;

/**
 * Created by ikvant.
 */

public interface EntryController {
    void loadTimeEntry(String id, LoadDataCallback<TimeEntry> callback);

    void createNewTimeEntry(TimeEntry task, LoadDataCallback<TimeEntry> callback);

    void updateTimeEntry(TimeEntry timeEntry, LoadDataCallback<TimeEntry> callback);

    void loadTimeEntries(LoadDataCallback<List<TimeEntry>> callback);

    void delete(String id, LoadDataCallback<Void> callback);

    void refresh();

}
