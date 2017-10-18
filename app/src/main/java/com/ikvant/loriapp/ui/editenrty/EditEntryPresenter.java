package com.ikvant.loriapp.ui.editenrty;

import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.state.entry.EntryController;
import com.ikvant.loriapp.state.entry.LoadDataCallback;
import com.ikvant.loriapp.state.entry.TaskController;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by ikvant.
 */

@Singleton
public class EditEntryPresenter implements Contract.Presenter {
    private String id;

    private TimeEntry currentTimeEntry;
    private List<Task> taskList = Collections.emptyList();

    private EntryController entryController;
    private TaskController taskController;

    private Contract.View view;

    @Inject
    public EditEntryPresenter(EntryController entryController, TaskController taskController) {
        this.entryController = entryController;
        this.taskController = taskController;
    }

    void setView(String id, Contract.View view) {
        this.id = id;
        this.view = view;
        view.setPresenter(this);
    }

    private LoadDataCallback<TimeEntry> saveEntryCallback = new LoadDataCallback<TimeEntry>() {
        @Override
        public void onSuccess(TimeEntry data) {
            view.goBack();
        }

        @Override
        public void networkUnreachable(TimeEntry localData) {
            view.goBack();
        }

        @Override
        public void onFailure(Throwable e) {

        }
    };

    @Override
    public void saveEntry() {
        if (id != null) {
            entryController.updateTimeEntry(currentTimeEntry, saveEntryCallback);
        } else {
            entryController.createNewTimeEntry(currentTimeEntry, saveEntryCallback);
        }
    }

    @Override
    public void deleteEntry() {
        if (id != null) {
            entryController.delete(id, saveEntryCallback);
        }
    }

    @Override
    public void navigateBack() {

    }

    @Override
    public void setTask(int position) {
        Task task = taskList.get(position);
        currentTimeEntry.setTask(task);
        currentTimeEntry.setTaskName(task.getName());
    }

    @Override
    public void setDescription(String text) {
        currentTimeEntry.setDescription(text);
    }


    @Override
    public void setDate(int dayOfMonth, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTimeEntry.getDate());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        currentTimeEntry.setDate(calendar.getTime());
        view.setDate(calendar.getTime());
    }

    @Override
    public void setTime(int hourOfDay, int minute) {
        currentTimeEntry.setTimeInMinutes(hourOfDay * 60 + minute);
        view.setTime(hourOfDay * 60 + minute);
    }

    @Override
    public void openTimePickerDialog() {
        int hour = currentTimeEntry.getTimeInMinutes() / 60;
        int minute = currentTimeEntry.getTimeInMinutes() % 60;
        view.showTimePicker(hour, minute);
    }

    @Override
    public void openDataPickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTimeEntry.getDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        view.showDatePicker(day, month, year);
    }

    @Override
    public void onStart() {
        view.showLoadingIndicator(true);
        loadTasks();
    }

    @Override
    public void onPause() {

    }

    private void setFieldForEntry(TimeEntry entry) {
        view.setTask(taskList.indexOf(entry.getTask()));
        view.setDate(entry.getDate());
        view.setTime(entry.getTimeInMinutes());
        view.setDescription(entry.getDescription());
    }

    private void loadTimeEntry() {
        if (id != null) {
            view.showModifyButton();
            view.showDeleteButton();

            entryController.loadTimeEntry(id, new LoadDataCallback<TimeEntry>() {
                @Override
                public void onSuccess(TimeEntry data) {
                    currentTimeEntry = data;
                    setFieldForEntry(data);
                    view.showLoadingIndicator(false);
                }

                @Override
                public void networkUnreachable(TimeEntry data) {
                    currentTimeEntry = data;
                    view.showOfflineMessage();
                    setFieldForEntry(data);
                    view.showLoadingIndicator(false);
                }

                @Override
                public void onFailure(Throwable e) {
                    view.showErrorMessage(e.getMessage());
                }
            });
        } else {
            view.showCreateButton();
            currentTimeEntry = TimeEntry.createNew();
            if (taskList.size() > 0) {
                currentTimeEntry.setTask(taskList.get(0));
            }
            setFieldForEntry(currentTimeEntry);
            view.showLoadingIndicator(false);
        }
    }

    private void loadTasks() {
        taskController.loadTasks(new LoadDataCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> data) {
                taskList = data;
                view.setTasks(data);
                loadTimeEntry();
            }

            @Override
            public void networkUnreachable(List<Task> data) {
                view.setTasks(data);
                view.showOfflineMessage();
                loadTimeEntry();
            }

            @Override
            public void onFailure(Throwable e) {
                view.showErrorMessage(e.getMessage());
            }
        });
    }
}
