package com.ikvant.loriapp.database.task;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.ForeignKey;

/**
 * Created by ikvant.
 */

@Entity
public class Task {

    @PrimaryKey
    private String id;

    protected String name;

    public Task(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}