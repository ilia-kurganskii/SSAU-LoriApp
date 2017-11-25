package com.ikvant.loriapp.ui.search;

import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.state.entry.LoadDataCallback;
import com.ikvant.loriapp.state.entry.TimeEntryController;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by ikvant.
 */

public class SearchPresenter implements Contact.Presenter {

    private TimeEntryController controller;

    private Contact.View view;
    private boolean dateEnable;
    private Date from = new Date();
    private Date to = new Date();
    private String text = "";

    private List<TimeEntry> entryList = Collections.emptyList();

    @Inject
    public SearchPresenter(TimeEntryController controller) {
        this.controller = controller;
    }

    public void setView(Contact.View view) {
        this.view = view;
        view.setPresenter(this);
    }


    @Override
    public void openEntryDetail(String id) {
        view.showEditEntryScreen(id);
    }

    @Override
    public void onResume() {
        view.setDateFrom(from);
        view.setDateTo(to);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void search() {
        if (dateEnable) {
            searchByDate(from, to);
        } else {
            searchByText(text);
        }
    }

    @Override
    public void searchByDateEnable(boolean enable) {
        dateEnable = enable;
    }

    @Override
    public void setDateFrom(int dayOfMonth, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        from = calendar.getTime();
        view.setDateFrom(calendar.getTime());
    }

    @Override
    public void setDateTo(int dayOfMonth, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        to = calendar.getTime();
        view.setDateTo(calendar.getTime());
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void openFromDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(to);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        view.showDateFromDialog(day, month, year);
    }

    @Override
    public void openToDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(to);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        view.showDateToDialog(day, month, year);
    }

    @Override
    public void searchByDate(Date from, Date to) {
        controller.loadByDate(from, to, new LoadDataCallback<List<TimeEntry>>() {
            @Override
            public void onSuccess(List<TimeEntry> data) {
                view.showTimeEntries(data);
            }

            @Override
            public void networkUnreachable(List<TimeEntry> data) {
                view.showTimeEntries(data);
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    @Override
    public void searchByText(String text) {
        controller.loadByText(text, new LoadDataCallback<List<TimeEntry>>() {
            @Override
            public void onSuccess(List<TimeEntry> data) {
                view.showTimeEntries(data);
            }

            @Override
            public void networkUnreachable(List<TimeEntry> data) {
                view.showTimeEntries(data);
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    @Override
    public void onDeleteEntry(String id) {
        controller.loadById(id, new LoadDataCallback<TimeEntry>() {
            @Override
            public void onSuccess(TimeEntry data) {
                view.deleteItem(data);
            }

            @Override
            public void networkUnreachable(TimeEntry localData) {
                view.deleteItem(localData);
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    @Override
    public void onChangeEntry(String id) {
        controller.loadById(id, new LoadDataCallback<TimeEntry>() {
            @Override
            public void onSuccess(TimeEntry data) {
                view.changeItem(data);
            }

            @Override
            public void networkUnreachable(TimeEntry localData) {
                view.changeItem(localData);
            }

            @Override
            public void onFailure(Throwable e) {
            }
        });
    }
}
