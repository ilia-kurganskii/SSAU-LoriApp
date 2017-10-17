package com.ikvant.loriapp.ui.tasklist;

import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.state.entry.EntryController;
import com.ikvant.loriapp.state.entry.LoadDataCallback;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by ikvant.
 */

@Singleton
public class TaskEntryPresenter implements Contract.Presenter {

    private Contract.View view;
    private AtomicBoolean loading = new AtomicBoolean(false);

    private boolean isPause;

    private EntryController controller;
    private List<TimeEntry> entries;
    private boolean isFirstLoad = true;

    @Inject
    public TaskEntryPresenter(EntryController controller) {
        this.controller = controller;
    }

    public void setView(Contract.View view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void reload() {
        reload(true, true);
    }

    @Override
    public void createNewEntry() {
        view.showNewEntryScreen();
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
        isPause = false;
        reload(isFirstLoad, false);
    }

    @Override
    public void onPause() {
        isPause = true;
    }

    private void reload(boolean force, boolean byUser) {
        isFirstLoad = false;
        if (byUser) {
            view.setRefresh(true);
        }
        if (force) {
            controller.refresh();
        }
        if (loading.compareAndSet(false, true)) {
            controller.loadTimeEntries(new LoadDataCallback<List<TimeEntry>>() {
                @Override
                public void onSuccess(List<TimeEntry> data) {
                    loading.set(false);
                    entries = data;
                    if (!isPause) {
                        view.showTimeEntries(data);
                        view.setRefresh(false);
                    }
                }

                @Override
                public void networkUnreachable(List<TimeEntry> localData) {
                    loading.set(false);
                    entries = localData;
                    if (!isPause) {
                        view.showTimeEntries(localData);
                        view.showOfflineMessage();
                        view.setRefresh(false);
                    }
                }

                @Override
                public void onFailure(Throwable e) {
                    loading.set(false);
                    if (!isPause) {
                        view.showErrorMessage(e.getMessage());
                        view.setRefresh(false);
                    }
                }
            });
        }

    }
}
