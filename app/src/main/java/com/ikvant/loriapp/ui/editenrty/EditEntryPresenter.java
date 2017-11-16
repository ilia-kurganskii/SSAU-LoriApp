package com.ikvant.loriapp.ui.editenrty;

import com.ikvant.loriapp.R;
import com.ikvant.loriapp.database.project.Project;
import com.ikvant.loriapp.database.tags.Tag;
import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.state.entry.LoadDataCallback;
import com.ikvant.loriapp.state.entry.ProjectController;
import com.ikvant.loriapp.state.entry.TagsController;
import com.ikvant.loriapp.state.entry.TaskController;
import com.ikvant.loriapp.state.entry.TimeEntryController;

import java.util.ArrayList;
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
    private List<Tag> tagList = Collections.emptyList();

    private TimeEntryController timeEntryController;
    private TaskController taskController;
    private ProjectController projectController;
    private TagsController tagsController;

    private List<SelectableTag> selectableTags = new ArrayList<>();

    private Contract.View view;

    @Inject
    public EditEntryPresenter(TimeEntryController timeEntryController, TaskController taskController, ProjectController projectController, TagsController tagsController) {
        this.timeEntryController = timeEntryController;
        this.taskController = taskController;
        this.projectController = projectController;
        this.tagsController = tagsController;
    }

    void setView(String id, Contract.View view) {
        this.id = id;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void saveEntry() {
        if (currentTask == null) {
            view.showErrorMessage(R.string.error_task_is_empty);
            return;
        }
        if (id != null) {
            timeEntryController.updateTimeEntry(currentTimeEntry, new LoadDataCallback<TimeEntry>() {
                @Override
                public void onSuccess(TimeEntry data) {
                    view.setResultChanged(data.getId());
                    view.finish();
                }

                @Override
                public void networkUnreachable(TimeEntry localData) {
                    view.setResultChanged(localData.getId());
                    view.finish();
                }

                @Override
                public void onFailure(Throwable e) {
                    view.showErrorMessage(e.getMessage());
                }
            });
        } else {
            timeEntryController.createNewTimeEntry(currentTimeEntry, new LoadDataCallback<TimeEntry>() {
                @Override
                public void onSuccess(TimeEntry data) {
                    view.setResultCreated(data.getId());
                    view.finish();
                }

                @Override
                public void networkUnreachable(TimeEntry localData) {
                    view.setResultCreated(localData.getId());
                    view.finish();
                }

                @Override
                public void onFailure(Throwable e) {
                    view.showErrorMessage(e.getMessage());
                }
            });
        }
    }

    @Override
    public void deleteEntry() {
        if (id != null) {
            timeEntryController.delete(id, new LoadDataCallback<TimeEntry>() {
                @Override
                public void onSuccess(TimeEntry data) {
                    view.setResultDeleted(id);
                    view.finish();
                }

                @Override
                public void networkUnreachable(TimeEntry localData) {
                    view.setResultDeleted(id);
                    view.finish();
                }

                @Override
                public void onFailure(Throwable e) {
                    view.showErrorMessage(e.getMessage());
                }
            });
        }
    }

    @Override
    public void navigateBack() {
        view.finish();
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

    @Override
    public void checkTag(int index, boolean checked) {
        selectableTags.get(index).isSelect = checked;
    }

    @Override
    public void saveTags() {
        currentTimeEntry.getTags().clear();
        for (SelectableTag tag : selectableTags) {
            if (tag.isSelect) {
                currentTimeEntry.getTags().add(new Tag(tag.id, tag.name));
            }
        }
        updateTags(currentTimeEntry.getTags());
    }


    private void loadProjects() {
        projectController.load(new LoadDataCallback<List<Project>>() {
            @Override
            public void onSuccess(List<Project> data) {
                projectList = data;
                view.setProjects(projectList);
                loadTags();
            }

            @Override
            public void networkUnreachable(List<Project> localData) {
                projectList = localData;
                view.setProjects(projectList);
                view.showOfflineMessage();
                loadTags();
            }

            @Override
            public void onFailure(Throwable e) {
                view.showErrorMessage(e.getMessage());
            }
        });
    }


    private void loadTags() {
        tagsController.load(new LoadDataCallback<List<Tag>>() {
            @Override
            public void onSuccess(List<Tag> data) {
                tagList = data;
                loadTimeEntry();
            }

            @Override
            public void networkUnreachable(List<Tag> localData) {
                tagList = localData;
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

            timeEntryController.loadTimeEntry(id, new LoadDataCallback<TimeEntry>() {
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
        updateTags(entry.getTags());
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

    private void updateTags(List<Tag> tags) {
        String[] names = new String[tagList.size()];
        boolean[] checked = new boolean[tagList.size()];
        List<String> listTags = new ArrayList<>();

        selectableTags.clear();
        for (int i = 0; i < tagList.size(); i++) {
            SelectableTag selectableTag = new SelectableTag();
            selectableTag.name = tagList.get(i).getName();
            selectableTag.id = tagList.get(i).getId();
            selectableTag.isSelect = false;

            names[i] = tagList.get(i).getName();
            checked[i] = false;
            for (Tag tag : tags) {
                if (tag.equals(tagList.get(i))) {
                    checked[i] = true;
                    selectableTag.isSelect = true;
                    listTags.add(tagList.get(i).getName());
                    break;
                }
            }
            selectableTags.add(selectableTag);
        }
        view.setTags(names, checked, listTags);
    }

    private class SelectableTag {
        String name;
        String id;
        boolean isSelect;
    }
}
