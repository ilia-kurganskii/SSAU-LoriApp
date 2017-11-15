package com.ikvant.loriapp.database.timeentry;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.ikvant.loriapp.database.tags.Tag;
import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.user.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by ikvant.
 */

@Entity
public class TimeEntry {

    public static final String NEW_ID = "NEW-ts$TimeEntry";

    @PrimaryKey
    private String id;

    private String description;

    private Integer timeInMinutes = 0;

    private String taskName;

    private Task task;

    private User user;

    private Date date;

    private boolean sync = true;

    @Expose
    private boolean deleted = false;

    private List<Tag> tags;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTimeInMinutes() {
        return timeInMinutes;
    }

    public void setTimeInMinutes(Integer timeInMinutes) {
        this.timeInMinutes = timeInMinutes;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeEntry entry = (TimeEntry) o;

        return id != null ? id.equals(entry.id) : entry.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public static TimeEntry createNew() {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setDate(new Date());
        timeEntry.setTimeInMinutes(60);
        timeEntry.setTags(new ArrayList<>());
        timeEntry.setDeleted(false);
        timeEntry.setId(NEW_ID + UUID.randomUUID());
        return timeEntry;
    }

    public static boolean isNew(TimeEntry timeEntry) {
        return timeEntry != null && timeEntry.getId().startsWith(NEW_ID);
    }
}
