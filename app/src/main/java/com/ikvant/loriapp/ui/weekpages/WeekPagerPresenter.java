package com.ikvant.loriapp.ui.weekpages;

import android.util.SparseArray;

import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.state.entry.EntryController;
import com.ikvant.loriapp.state.entry.LoadDataCallback;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by ikvant.
 */

@Singleton
public class WeekPagerPresenter implements Contract.Presenter {
    private Contract.View view;
    private boolean isFirstLoad = true;

    private EntryController controller;
    private boolean isPause;

    private AtomicBoolean loading = new AtomicBoolean(false);

    @Inject
    public WeekPagerPresenter(EntryController controller) {
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
    public void onStart() {
        reload(isFirstLoad, false);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void createNewEntry() {
        view.showNewEntryScreen();
    }

    private void reload(boolean force, boolean byUser) {
        isFirstLoad = false;
        if (byUser) {
            view.showLoadingIndicator(true);
        }
        if (force) {
            controller.refresh();
        }
        if (loading.compareAndSet(false, true)) {
            controller.loadTimeEntries(new LoadDataCallback<SparseArray<Set<TimeEntry>>>() {
                @Override
                public void onSuccess(SparseArray<Set<TimeEntry>> data) {
                    loading.set(false);
                    if (!isPause) {
                        view.setWeeks(data);
                        view.showLoadingIndicator(false);
                    }
                }

                @Override
                public void networkUnreachable(SparseArray<Set<TimeEntry>> localData) {
                    loading.set(false);
                    if (!isPause) {
                        view.setWeeks(localData);
                        view.showOfflineMessage();
                        view.showLoadingIndicator(false);
                    }
                }

                @Override
                public void onFailure(Throwable e) {
                    loading.set(false);
                    if (!isPause) {
                        view.showErrorMessage(e.getMessage());
                        view.showLoadingIndicator(false);
                    }
                }
            });
        }
    }
}
