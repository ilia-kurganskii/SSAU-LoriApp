package com.ikvant.loriapp.ui.tasklist;

import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.state.SyncController;
import com.ikvant.loriapp.state.entry.LoadDataCallback;
import com.ikvant.loriapp.state.entry.Reloadable;
import com.ikvant.loriapp.state.entry.TimeEntryController;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ikvant.
 */
public class TaskEntryPresenter implements Contract.Presenter {
    private static final String TAG = "TaskEntryPresenter";
    private static final int OFFSET_STEP = 8;

    private Contract.View view;

    private TimeEntryController timeEntryController;
    private SyncController syncController;

    private boolean isFirstLoad = true;
    private boolean isActive;

    private int loadedOffset;

    private boolean isLoading = false;

    @Inject
    public TaskEntryPresenter(TimeEntryController timeEntryController, SyncController syncController) {
        this.timeEntryController = timeEntryController;
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
            timeEntryController.loadTimeEntries(offset, OFFSET_STEP, new LoadDataCallback<List<TimeEntry>>() {
                @Override
                public void onSuccess(List<TimeEntry> data) {
                    if (isActive) {
                        view.showLoadingIndicator(false);
                        if (offset == 0) {
                            view.setTimeEntries(data);
                        } else {
                            view.addTimeEntries(data);
                        }
                        loadedOffset = offset + OFFSET_STEP;
                    }
                    isLoading = false;
                }

                @Override
                public void networkUnreachable(List<TimeEntry> localData) {
                    if (isActive) {
                        view.showLoadingIndicator(false);
                        if (offset == 0) {
                            view.setTimeEntries(localData);
                        } else {
                            view.addTimeEntries(localData);
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

    @Override
    public void onDeleteEntry(String id) {
        timeEntryController.loadById(id, new LoadDataCallback<TimeEntry>() {
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
    public void onCreateEntry(String id) {
        timeEntryController.loadById(id, new LoadDataCallback<TimeEntry>() {
            @Override
            public void onSuccess(TimeEntry data) {
                view.insertItem(data);
            }

            @Override
            public void networkUnreachable(TimeEntry localData) {
                view.insertItem(localData);
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    @Override
    public void onChangeEntry(String id) {
        timeEntryController.loadById(id, new LoadDataCallback<TimeEntry>() {
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
