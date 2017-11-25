package com.ikvant.loriapp.ui.tasklist;

import com.ikvant.loriapp.database.timeentry.TimeEntry;

import java.util.List;

/**
 * Created by ikvant.
 */

public interface Contract {
    interface View {
        void setTimeEntries(List<TimeEntry> entryList);

        void addTimeEntries(List<TimeEntry> entryList);

        void setPresenter(Presenter presenter);

        void showEditEntryScreen(String id);

        void showNewEntryScreen();

        void showSearchScreen();

        void showOfflineMessage();

        void showErrorMessage(String message);

        void showLoadingIndicator(boolean isLoading);

        void deleteItem(TimeEntry item);

        void insertItem(TimeEntry item);

        void changeItem(TimeEntry item);
    }

    interface Presenter {

        void openEntryDetail(String id);

        void onResume();

        void onPause();

        void createNewEntry();

        void searchEntries();

        void reload();

        void onEndOfPage();

        void onDeleteEntry(String id);

        void onCreateEntry(String id);

        void onChangeEntry(String id);

        void logout();
    }
}
