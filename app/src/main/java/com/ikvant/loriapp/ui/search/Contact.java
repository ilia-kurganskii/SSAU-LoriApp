package com.ikvant.loriapp.ui.search;

import android.util.SparseArray;

import com.ikvant.loriapp.database.timeentry.TimeEntry;

import java.util.Date;
import java.util.Set;

/**
 * Created by ikvant.
 */

public interface Contact {
    interface View {
        void setDateFrom(Date date);

        void setDateTo(Date to);

        void showDateFromDialog(int day, int month, int year);

        void showDateToDialog(int day, int month, int year);

        void showTimeEntries(SparseArray<Set<TimeEntry>> entryList);

        void showEditEntryScreen(String id);

        void showLoadingIndicator(boolean isLoading);

        void setPresenter(Presenter presenter);
    }

    interface Presenter {
        void openEntryDetail(String id);

        void onResume();

        void onPause();

        void search();

        void searchByDateEnable(boolean enable);

        void setDateFrom(int dayOfMonth, int month, int year);

        void setDateTo(int dayOfMonth, int month, int year);

        void setText(String text);

        void openFromDialog();

        void openToDialog();

        void searchByDate(Date from, Date to);

        void searchByText(String text);
    }
}
