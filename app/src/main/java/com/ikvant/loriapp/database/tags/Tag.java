package com.ikvant.loriapp.database.tags;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by ikvant.
 */

@Entity
public class Tag {

    @PrimaryKey
    private String id;

    private String name;

    public Tag(String id) {
        this.id = id;
    }

    @Ignore
    public Tag(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag task = (Tag) o;

        return id != null ? id.equals(task.id) : task.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}