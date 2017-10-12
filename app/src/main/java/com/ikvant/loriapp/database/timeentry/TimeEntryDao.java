package com.ikvant.loriapp.database.timeentry;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.token.Token;

import java.sql.Time;
import java.util.List;

/**
 * Created by ikvant.
 */


@Dao
public interface TimeEntryDao {
    @Query("SELECT * FROM TimeEntry")
    List<TimeEntry> loadAll();

    @Query("DELETE FROM TimeEntry")
    void deleteAll();

    @Query("SELECT * FROM TimeEntry WHERE id=:id LIMIT 1")
    TimeEntry load(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(TimeEntry entry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(TimeEntry...timeEntries);
}