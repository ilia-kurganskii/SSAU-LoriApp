package com.ikvant.loriapp.database;

/**
 * Created by ikvant.
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.ikvant.loriapp.database.token.Token;
import com.ikvant.loriapp.database.token.TokenDao;


@Database(entities = {Token.class}, version = 1)
public abstract class LoriDatabase extends RoomDatabase {
    public abstract TokenDao tokenDao();
}