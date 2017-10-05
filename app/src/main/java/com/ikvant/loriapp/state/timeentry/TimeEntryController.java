package com.ikvant.loriapp.state.timeentry;

import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.utils.Callback;

import java.util.List;

/**
 * Created by ikvant.
 */

public interface TimeEntryController {
    boolean needSync();

    void loadEntries(Callback<List<TimeEntry>> callback);

}
