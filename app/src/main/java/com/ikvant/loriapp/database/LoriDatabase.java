package com.ikvant.loriapp.database;

/**
 * Created by ikvant.
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.ikvant.loriapp.database.project.Project;
import com.ikvant.loriapp.database.project.ProjectDao;
import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.task.TaskDao;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.timeentry.TimeEntryDao;
import com.ikvant.loriapp.database.token.Token;
import com.ikvant.loriapp.database.token.TokenDao;
import com.ikvant.loriapp.database.user.User;
import com.ikvant.loriapp.database.user.UserDao;


@Database(entities = {Token.class, TimeEntry.class, Task.class, User.class, Project.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class LoriDatabase extends RoomDatabase {
    public abstract TokenDao tokenDao();

    public abstract TimeEntryDao timeEntryDao();

    public abstract TaskDao taskDao();

    public abstract ProjectDao projectDao();

    public abstract UserDao userDao();
}