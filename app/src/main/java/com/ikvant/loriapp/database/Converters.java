package com.ikvant.loriapp.database;

import android.arch.persistence.room.TypeConverter;

import com.ikvant.loriapp.database.task.Task;

/**
 * Created by ikvant.
 */

public class Converters {
    @TypeConverter
    public static Task TaskFromTaskId(String id) {
        return id == null ? null : new Task(id);
    }

    @TypeConverter
    public static String TaskToTaskId(Task task) {
        return task == null ? null : task.getId();
    }
}
