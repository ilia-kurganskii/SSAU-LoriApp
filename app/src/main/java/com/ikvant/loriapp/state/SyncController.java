package com.ikvant.loriapp.state;

import com.ikvant.loriapp.state.entry.ProjectController;
import com.ikvant.loriapp.state.entry.Reloadable;
import com.ikvant.loriapp.state.entry.TagsController;
import com.ikvant.loriapp.state.entry.TaskController;
import com.ikvant.loriapp.state.entry.TimeEntryController;
import com.ikvant.loriapp.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by ikvant.
 */

@Singleton
public class SyncController {
    private List<Reloadable> reloadableList = new ArrayList<>();
    private AppExecutors executors;

    @Inject
    public SyncController(AppExecutors executors,
                          TimeEntryController timeEntryController,
                          TagsController tagsController,
                          ProjectController projectController,
                          TaskController taskController) {
        this.executors = executors;
        add(timeEntryController);
        add(tagsController);
        add(projectController);
        add(taskController);
    }

    private void add(Reloadable reloadable) {
        reloadableList.add(reloadable);
    }

    public void sync(Reloadable.Callback listener) {
        executors.background().execute(() -> {
            AtomicBoolean isOffline = new AtomicBoolean();
            List<Throwable> errors = new ArrayList<>();
            CountDownLatch latch = new CountDownLatch(reloadableList.size());
            for (Reloadable reloadable : reloadableList) {
                reloadable.reload(new Reloadable.Callback() {
                    @Override
                    public void onSuccess() {
                        latch.countDown();
                    }

                    @Override
                    public void onOffline() {
                        isOffline.set(true);
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        errors.add(throwable);
                        latch.countDown();
                    }
                });
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                executors.mainThread().execute(() -> listener.onFailure(e));
            }
            executors.mainThread().execute(() -> {
                if (!errors.isEmpty()) {
                    listener.onFailure(errors.get(0));
                } else if (isOffline.get()) {
                    listener.onOffline();
                } else {
                    listener.onSuccess();
                }
            });
        });

    }
}
