package com.ikvant.loriapp.database.project;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by ikvant.
 */

@Dao
public interface ProjectDao {
    @Query("SELECT * FROM Project")
    List<Project> loadAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(Project... timeEntries);
}
