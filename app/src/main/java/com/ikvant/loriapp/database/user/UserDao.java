package com.ikvant.loriapp.database.user;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

/**
 * Created by ikvant.
 */


@Dao
public interface UserDao {
    @Query("SELECT * FROM User LIMIT 1")
    User load();

    @Query("DELETE FROM User")
    void delete();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(User token);

}
