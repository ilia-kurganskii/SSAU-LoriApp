package com.ikvant.loriapp.database.timeentry;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by ikvant.
 */

@Entity
public class TimeEntry {

    @PrimaryKey
    private String id;

    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
