package com.ikvant.loriapp.ui.tasklist;

import android.util.SparseArray;

import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.state.SyncController;
import com.ikvant.loriapp.state.entry.EntryController;
import com.ikvant.loriapp.state.entry.LoadDataCallback;
import com.ikvant.loriapp.state.entry.Reloadable;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by ikvant.
 */
@Singleton
public class TaskEntryPresenter implements Contract.Presenter {
    private static final String TAG = "TaskEntryPresenter";
    private static final int OFFSET_STEP = 8;

    private Contract.View view;

    private EntryController entryController;
    private SyncController syncController;

    private boolean isFirstLoad = true;
    private boolean isActive;

    private int loadedOffset;

    private boolean isLoading = false;

    @Inject
    public TaskEntryPresenter(EntryController entryController, SyncController syncController) {
        this.entryController = entryController;
        this.syncController = syncController;
    }

    public void setView(Contract.View view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void openEntryDetail(String id) {
        view.showEditEntryScreen(id);
    }

    @Override
    public void onResume() {
        isActive = true;
        reload(isFirstLoad, false);
    }

    @Override
    public void onPause() {
        isActive = false;
    }

    @Override
    public void createNewEntry() {
        view.showNewEntryScreen();
    }

    @Override
    public void searchEntries() {
        view.showSearchScreen();
    }

    @Override
    public void reload() {
        reload(true, true);
    }

    private void reload(boolean force, boolean byUser) {
        isFirstLoad = false;
        if (byUser) {
            view.showLoadingIndicator(true);
        }
        if (force) {
            loadedOffset = 0;
            syncController.sync(new Reloadable.Callback() {
                @Override
                public void onSuccess() {
                    loadTimeEntries(0);
                }

                @Override
                public void onOffline() {
                    loadTimeEntries(0);
                }

                @Override
                public void onFailure(Throwable e) {
                    if (isActive) {
                        view.showLoadingIndicator(false);
                        view.showErrorMessage(e.getMessage());
                    }
                }
            });
        } else {
            loadTimeEntries(loadedOffset);
        }

    }

    private void loadTimeEntries(int offset) {
        if (!isLoading) {
            isLoading = true;
            entryController.loadTimeEntries(offset, OFFSET_STEP, new LoadDataCallback<SparseArray<Set<TimeEntry>>>() {
                @Override
                public void onSuccess(SparseArray<Set<TimeEntry>> data) {
                    if (isActive) {
                        view.showLoadingIndicator(false);
                        if (data.size() > 0) {
                            if (offset == 0) {
                                view.setTimeEntries(data);
                            } else {
                                view.addTimeEntries(data);
                            }
                        }
                        loadedOffset = offset + OFFSET_STEP;
                    }
                    isLoading = false;
                }

                @Override
                public void networkUnreachable(SparseArray<Set<TimeEntry>> localData) {
                    if (isActive) {
                        view.showLoadingIndicator(false);
                        if (localData.size() > 0) {
                            if (offset == 0) {
                                view.setTimeEntries(localData);
                            } else {
                                view.addTimeEntries(localData);
                            }
                        }
                        view.showOfflineMessage();
                        loadedOffset = offset + OFFSET_STEP;
                    }
                    isLoading = false;
                }

                @Override
                public void onFailure(Throwable e) {
                    if (isActive) {
                        view.showLoadingIndicator(false);
                        view.showErrorMessage(e.getMessage());
                    }
                    isLoading = false;
                }
            });
        }
    }

    public void onEndOfPage() {
        loadTimeEntries(loadedOffset);
    }

}
