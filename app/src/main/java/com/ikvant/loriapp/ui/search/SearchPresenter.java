package com.ikvant.loriapp.ui.search;

import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.state.entry.EntryController;
import com.ikvant.loriapp.state.entry.LoadDataCallback;
import com.ikvant.loriapp.ui.tasklist.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by ikvant.
 */

@Singleton
public class SearchPresenter implements Contact.Presenter {

    private EntryController controller;

    private Contract.View view;

    private List<TimeEntry> entryList = Collections.emptyList();

    @Inject
    public SearchPresenter(EntryController controller) {
        this.controller = controller;
    }

    public void setView(Contract.View view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void searchByText(String text) {
        controller.loadByText(text, new LoadDataCallback<Set<TimeEntry>>() {
            @Override
            public void onSuccess(Set<TimeEntry> data) {
                entryList = new ArrayList<>(data);
                view.showTimeEntries(entryList);
            }

            @Override
            public void networkUnreachable(Set<TimeEntry> data) {
                entryList = new ArrayList<>(data);
                view.showTimeEntries(entryList);
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    @Override
    public void searchByDate(Date from, Date to) {
        controller.loadByDate(from, to, new LoadDataCallback<Set<TimeEntry>>() {
            @Override
            public void onSuccess(Set<TimeEntry> data) {
                entryList = new ArrayList<>(data);
                view.showTimeEntries(entryList);
            }

            @Override
            public void networkUnreachable(Set<TimeEntry> data) {
                entryList = new ArrayList<>(data);
                view.showTimeEntries(entryList);
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    @Override
    public void openEntryDetail(int position) {
        String id = entryList.get(position).getId();
        view.showEditEntryScreen(id);
    }

    @Override
    public void onResume() {
        view.showTimeEntries(entryList);
    }

    @Override
    public void onPause() {

    }
}
