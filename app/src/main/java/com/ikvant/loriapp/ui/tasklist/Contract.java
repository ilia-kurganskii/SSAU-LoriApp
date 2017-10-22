package com.ikvant.loriapp.ui.tasklist;

import com.ikvant.loriapp.database.timeentry.TimeEntry;

import java.util.Date;
import java.util.List;

/**
 * Created by ikvant.
 */

public interface Contract {
    interface View {
        void showTimeEntries(List<TimeEntry> entryList);

        void setPresenter(Presenter presenter);

        void showEditEntryScreen(String id);

        void showDiapasonLabel(Date start, Date end);
    }

    interface Presenter {

        void openEntryDetail(int position);

        void onResume();

        void onPause();

    }
}
