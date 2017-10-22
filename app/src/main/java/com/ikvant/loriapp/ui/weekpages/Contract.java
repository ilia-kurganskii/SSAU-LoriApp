package com.ikvant.loriapp.ui.weekpages;

import android.util.SparseArray;

import com.ikvant.loriapp.database.timeentry.TimeEntry;

import java.util.Set;

/**
 * Created by ikvant.
 */

public interface Contract {
    interface View {
        void setWeeks(SparseArray<Set<TimeEntry>> weekArray);

        void showLoadingIndicator(boolean loading);

        void showNewEntryScreen();

        void showOfflineMessage();

        void showErrorMessage(String message);

        void setPresenter(Presenter presenter);

        void showSearchScreen();
    }

    interface Presenter {
        void reload();

        void onStart();

        void onPause();

        void createNewEntry();

        void searchEntries();
    }
}
