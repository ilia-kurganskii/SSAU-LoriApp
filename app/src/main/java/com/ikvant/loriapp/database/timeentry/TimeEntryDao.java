package com.ikvant.loriapp.database.timeentry;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ikvant.loriapp.database.token.Token;

import java.util.List;

/**
 * Created by ikvant.
 */


@Dao
public interface TimeEntryDao {
    @Query("SELECT * FROM TimeEntry LIMIT 1")
    List<TimeEntry> load();

    @Query("DELETE FROM TimeEntry")
    void delete();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(TimeEntry entry);
}