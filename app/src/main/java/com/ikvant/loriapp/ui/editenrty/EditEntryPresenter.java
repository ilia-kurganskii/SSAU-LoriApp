package com.ikvant.loriapp.ui.editenrty;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.project.Project;
import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.state.entry.EntryController;
import com.ikvant.loriapp.state.entry.LoadDataCallback;
import com.ikvant.loriapp.state.entry.ProjectController;
import com.ikvant.loriapp.state.entry.TaskController;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

/**
 * Created by ikvant.
 */

public class EditEntryPresenter implements Contract.Presenter {
    private static final int FIRST_INDEX = 0;
    private String id;

    private TimeEntry currentTimeEntry;
    private Task currentTask;
    private Project currentProject;

    private List<Task> taskList = Collections.emptyList();
    private List<Project> projectList = Collections.emptyList();

    private EntryController entryController;
    private TaskController taskController;
    private ProjectController projectController;

    private Contract.View view;

    @Inject
    public EditEntryPresenter(EntryController entryController, TaskController taskController, ProjectController projectController) {
        this.entryController = entryController;
        this.taskController = taskController;
        this.projectController = projectController;
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
        if (currentTask == null) {
            view.showErrorMessage(R.string.error_task_is_empty);
            return;
        }
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
    public void setProject(int position) {
        Project project = projectList.get(position);
        if (!Objects.equals(project, currentProject)) {
            currentProject = project;
            currentTask = null;
            loadTasksForProject(project.getId());
        }
    }

    @Override
    public void setTask(int position) {
        Task task = taskList.get(position);
        currentTask = task;
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
        loadProjects();
    }

    @Override
    public void onPause() {

    }


    private void loadProjects() {
        projectController.loadProjects(new LoadDataCallback<List<Project>>() {
            @Override
            public void onSuccess(List<Project> data) {
                projectList = data;
                view.setProjects(projectList);
                loadTimeEntry();
            }

            @Override
            public void networkUnreachable(List<Project> localData) {
                projectList = localData;
                view.setProjects(projectList);
                view.showOfflineMessage();
                loadTimeEntry();
            }

            @Override
            public void onFailure(Throwable e) {
                view.showErrorMessage(e.getMessage());
            }
        });
    }


    private void loadTimeEntry() {
        if (id != null) {
            view.showLoadingIndicator(true);
            view.showModifyButton();
            view.showDeleteButton();

            entryController.loadTimeEntry(id, new LoadDataCallback<TimeEntry>() {
                @Override
                public void onSuccess(TimeEntry data) {
                    currentTimeEntry = data;
                    fillView(currentTimeEntry);
                    loadTask(currentTimeEntry.getTask().getId());
                }

                @Override
                public void networkUnreachable(TimeEntry data) {
                    currentTimeEntry = data;
                    fillView(currentTimeEntry);
                    loadTask(currentTimeEntry.getTask().getId());
                }

                @Override
                public void onFailure(Throwable e) {
                    view.showErrorMessage(e.getMessage());
                }
            });
        } else {
            view.showCreateButton();
            currentTimeEntry = TimeEntry.createNew();
            fillView(currentTimeEntry);
            if (!projectList.isEmpty()) {
                setProject(FIRST_INDEX);
            }
        }
    }

    private void fillView(TimeEntry entry) {
        view.setDate(entry.getDate());
        view.setTime(entry.getTimeInMinutes());
        view.setDescription(entry.getDescription());
    }

    private void loadTask(String id) {
        taskController.loadTask(id, new LoadDataCallback<Task>() {
            @Override
            public void onSuccess(Task data) {
                setTask(data);
            }

            @Override
            public void networkUnreachable(Task localData) {
                setTask(localData);
            }

            @Override
            public void onFailure(Throwable e) {

            }

            private void setTask(Task data) {
                currentTask = data;
                currentProject = currentTask.getProject();
                view.setProject(projectList.indexOf(currentTask.getProject()));
                loadTasksForProject(currentTask.getProject().getId());
            }
        });
    }

    private void loadTasksForProject(String id) {
        view.showLoadingIndicator(true);
        taskController.loadTasksForProject(id, new LoadDataCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> data) {
                setTasks(data);
                view.showLoadingIndicator(false);
            }


            @Override
            public void networkUnreachable(List<Task> localData) {
                setTasks(localData);
                view.showLoadingIndicator(false);
            }

            @Override
            public void onFailure(Throwable e) {

            }

            private void setTasks(List<Task> data) {
                taskList = data;
                view.setTasks(taskList);
                if (currentTask == null && !taskList.isEmpty()) {
                    setTask(FIRST_INDEX);
                } else if (currentTask != null) {
                    setTask(taskList.indexOf(currentTask));
                }
            }
        });
    }
}
