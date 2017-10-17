package com.ikvant.loriapp.ui.tasklist;

import com.ikvant.loriapp.database.timeentry.TimeEntry;

import java.util.List;

/**
 * Created by ikvant.
 */

public interface Contract {
    interface View {
        void showTimeEntries(List<TimeEntry> entryList);

        void setRefresh(boolean refresh);

        void setPresenter(Presenter presenter);

        void showNewEntryScreen();

        void showEditEntryScreen(String id);

        void showOfflineMessage();

        void showErrorMessage(String message);
    }

    interface Presenter {
        void reload();

        void createNewEntry();

        void openEntryDetail(int position);


        void onResume();

        void onPause();

    }
}
