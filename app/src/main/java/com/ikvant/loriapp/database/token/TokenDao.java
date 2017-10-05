package com.ikvant.loriapp.database.token;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

/**
 * Created by ikvant.
 */


@Dao
public interface TokenDao {
    @Query("SELECT * FROM Token LIMIT 1")
    Token load();

    @Query("DELETE FROM Token")
    void delete();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Token token);
}