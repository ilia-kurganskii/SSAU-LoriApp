package com.ikvant.loriapp.database.timeentry;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;

import com.google.gson.annotations.Expose;
import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.user.User;

import java.util.Date;

/**
 * Created by ikvant.
 */

@Entity
public class TimeEntry {

    public static final String NEW_ID = "NEW-ts$TimeEntry";
    private String id;

    @PrimaryKey(autoGenerate = true)
    @Expose
    private int localId;

    private String description;

    private Integer timeInMinutes = 0;

    private String taskName;

    private Task task;

    private User user;

    private Date date;

    public String getId() {
        return id;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
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

    public static TimeEntry createNew(){
        TimeEntry timeEntry =new TimeEntry();
        timeEntry.setDate(new Date());
        timeEntry.setTimeInMinutes(0);
        timeEntry.setId(NEW_ID);
        return timeEntry;
    }

    public static boolean isNew(TimeEntry timeEntry){
        return timeEntry != null && NEW_ID.equals(timeEntry.getId());
    }
}
