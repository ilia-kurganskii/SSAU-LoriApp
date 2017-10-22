package com.ikvant.loriapp.ui.tasklist;

import android.util.Log;

import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.state.entry.EntryController;
import com.ikvant.loriapp.state.entry.LoadDataCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by ikvant.
 */

public class TaskEntryPresenter implements Contract.Presenter {
    private static final String TAG = "TaskEntryPresenter";

    private Contract.View view;

    private EntryController entryController;

    private List<TimeEntry> entries;
    private int weekIndex;
    private boolean isActive;

    public TaskEntryPresenter(int weekIndex, EntryController entryController) {
        this.weekIndex = weekIndex;
        this.entryController = entryController;
        Log.d(TAG, "TaskEntryPresenter() called with: weekIndex = [" + weekIndex + "]");
    }

    public void setView(Contract.View view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void openEntryDetail(int position) {
        TimeEntry entry = entries.get(position);
        if (entry != null) {
            view.showEditEntryScreen(entry.getId());
        }
    }

    @Override
    public void onResume() {
        isActive = true;
        reload();
    }

    @Override
    public void onPause() {
        isActive = false;
    }

    private void reload() {
        entryController.loadTimeEntriesByWeek(weekIndex, new LoadDataCallback<Set<TimeEntry>>() {
            @Override
            public void onSuccess(Set<TimeEntry> data) {
                entries = new ArrayList<>(data);
                if (isActive) {
                    view.showTimeEntries(entries);
                    view.showDiapasonLabel(getStartDate(), getEndDate());
                }
            }

            @Override
            public void networkUnreachable(Set<TimeEntry> localData) {
                entries = new ArrayList<>(localData);
                if (isActive) {
                    view.showTimeEntries(entries);
                    view.showDiapasonLabel(getStartDate(), getEndDate());
                }
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    private Date getStartDate() {
        Log.d(TAG, "getStartDate() called" + weekIndex);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.WEEK_OF_YEAR, weekIndex);
        return calendar.getTime();
    }

    private Date getEndDate() {
        Log.d(TAG, "getStartDate() called" + weekIndex);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        calendar.set(Calendar.WEEK_OF_YEAR, weekIndex);
        return calendar.getTime();
    }
}
