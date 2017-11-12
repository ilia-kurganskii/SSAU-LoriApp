package com.ikvant.loriapp.state.entry;

import android.util.SparseArray;

import com.ikvant.loriapp.database.timeentry.TimeEntry;

import java.util.Date;
import java.util.Set;

/**
 * Created by ikvant.
 */

public interface EntryController extends Reloadable {
    void loadTimeEntry(String id, LoadDataCallback<TimeEntry> callback);

    void loadByText(String text, LoadDataCallback<SparseArray<Set<TimeEntry>>> callback);

    void loadByDate(Date from, Date to, LoadDataCallback<SparseArray<Set<TimeEntry>>> callback);

    void createNewTimeEntry(TimeEntry task, LoadDataCallback<TimeEntry> callback);

    void updateTimeEntry(TimeEntry timeEntry, LoadDataCallback<TimeEntry> callback);

    void loadTimeEntries(LoadDataCallback<SparseArray<Set<TimeEntry>>> callback);

    void loadTimeEntriesByWeek(int weekIndex, LoadDataCallback<Set<TimeEntry>> callback);

    void delete(String id, LoadDataCallback<TimeEntry> callback);

    void refresh();

}
