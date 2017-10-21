package com.ikvant.loriapp.ui.tasklist;

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

    private Contract.View view;

    private EntryController entryController;

    private List<TimeEntry> entries;
    private int weekIndex;

    public TaskEntryPresenter(int weekIndex, EntryController entryController) {
        this.weekIndex = weekIndex;
        this.entryController = entryController;
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
        reload();
    }

    @Override
    public void onPause() {
    }

    private void reload() {
        view.showDiapason(getStartDate(), getEndDate());
        entryController.loadTimeEntriesByWeek(weekIndex, new LoadDataCallback<Set<TimeEntry>>() {
            @Override
            public void onSuccess(Set<TimeEntry> data) {
                entries = new ArrayList<>(data);
                view.showTimeEntries(entries);
            }

            @Override
            public void networkUnreachable(Set<TimeEntry> localData) {
                entries = new ArrayList<>(localData);
                view.showTimeEntries(entries);
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    private Date getStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.WEEK_OF_YEAR, weekIndex);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTime();
    }

    private Date getEndDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.WEEK_OF_YEAR, weekIndex);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        return calendar.getTime();
    }
}
