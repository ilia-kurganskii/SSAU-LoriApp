package com.ikvant.loriapp.database.tags;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by ikvant.
 */


@Dao
public interface TagDao {
    @Query("SELECT * FROM Tag")
    List<Tag> loadAll();

    @Query("SELECT * FROM Tag WHERE id in (:ids)")
    List<Tag> loadTagsByIds(String[] ids);

    @Query("DELETE FROM Tag")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Tag entry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(Tag... timeEntries);
}