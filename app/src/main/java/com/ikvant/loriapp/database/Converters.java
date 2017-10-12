package com.ikvant.loriapp.database;

import android.arch.persistence.room.TypeConverter;

import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.user.User;

/**
 * Created by ikvant.
 */

public class Converters {
    @TypeConverter
    public static Task taskFromTaskId(String id) {
        return id == null ? null : new Task(id);
    }

    @TypeConverter
    public static String taskToTaskId(Task task) {
        return task == null ? null : task.getId();
    }

    @TypeConverter
    public static User userFromUserId(String id) {
        return id == null ? null : new User(id);
    }

    @TypeConverter
    public static String userToUserId(User user) {
        return user == null ? null : user.getId();
    }
}
