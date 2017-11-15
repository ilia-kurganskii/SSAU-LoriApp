package com.ikvant.loriapp.database.timeentry;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by ikvant.
 */


@Dao
public interface TimeEntryDao {
    @Query("SELECT * FROM TimeEntry WHERE deleted=:deleted LIMIT :offset")
    List<TimeEntry> loadAll(Boolean deleted, int offset);

    @Query("DELETE FROM TimeEntry WHERE id=:id")
    void delete(String id);

    @Query("SELECT * FROM TimeEntry WHERE id=:id LIMIT 1")
    TimeEntry load(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(TimeEntry entry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(TimeEntry... timeEntries);

    @Query("SELECT * FROM TimeEntry WHERE sync=:sync")
    List<TimeEntry> loadUnSync(boolean sync);

    @Query("DELETE FROM TimeEntry")
    void deleteAll();

    @Query("SELECT * FROM TimeEntry WHERE description LIKE'%'||:text||'%'")
    List<TimeEntry> findByText(String text);

    @Query("SELECT * FROM TimeEntry WHERE date BETWEEN :from AND :to")
    List<TimeEntry> findByDate(Date from, Date to);
}