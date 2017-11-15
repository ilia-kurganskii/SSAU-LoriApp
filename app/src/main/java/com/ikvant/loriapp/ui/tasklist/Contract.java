package com.ikvant.loriapp.ui.tasklist;

import android.util.SparseArray;

import com.ikvant.loriapp.database.timeentry.TimeEntry;

import java.util.Set;

/**
 * Created by ikvant.
 */

public interface Contract {
    interface View {
        void setTimeEntries(SparseArray<Set<TimeEntry>> entryList);

        void addTimeEntries(SparseArray<Set<TimeEntry>> entryList);

        void setPresenter(Presenter presenter);

        void showEditEntryScreen(String id);

        void showNewEntryScreen();

        void showSearchScreen();

        void showOfflineMessage();

        void showErrorMessage(String message);

        void showLoadingIndicator(boolean isLoading);
    }

    interface Presenter {

        void openEntryDetail(String id);

        void onResume();

        void onPause();

        void createNewEntry();

        void searchEntries();

        void reload();

        void onEndOfPage();
    }
}
