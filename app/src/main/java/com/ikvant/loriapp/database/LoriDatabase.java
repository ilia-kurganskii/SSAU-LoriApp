package com.ikvant.loriapp.database;

/**
 * Created by ikvant.
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.ikvant.loriapp.database.project.Project;
import com.ikvant.loriapp.database.project.ProjectDao;
import com.ikvant.loriapp.database.tags.Tag;
import com.ikvant.loriapp.database.tags.TagDao;
import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.task.TaskDao;
import com.ikvant.loriapp.database.timeentry.TimeEntry;
import com.ikvant.loriapp.database.timeentry.TimeEntryDao;
import com.ikvant.loriapp.database.user.User;
import com.ikvant.loriapp.database.user.UserDao;


@Database(entities = {TimeEntry.class, Task.class, User.class, Project.class, Tag.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class LoriDatabase extends RoomDatabase {
    public abstract TimeEntryDao timeEntryDao();

    public abstract TaskDao taskDao();

    public abstract ProjectDao projectDao();

    public abstract UserDao userDao();

    public abstract TagDao tagDao();
}