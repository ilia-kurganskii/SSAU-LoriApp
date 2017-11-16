package com.ikvant.loriapp.database;

import android.arch.persistence.room.TypeConverter;

import com.ikvant.loriapp.database.project.Project;
import com.ikvant.loriapp.database.tags.Tag;
import com.ikvant.loriapp.database.task.Task;
import com.ikvant.loriapp.database.user.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ikvant.
 */

public class Converters {
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    @TypeConverter
    public static Project projectFromId(String id) {
        return id == null ? null : new Project(id);
    }

    @TypeConverter
    public static String projectToId(Project project) {
        return project == null ? null : project.getId();
    }

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

    @TypeConverter
    public static Date dateFromString(String formatString) {
        try {
            return dateFormat.parse(formatString);
        } catch (ParseException e) {
            return null;
        }
    }

    @TypeConverter
    public static String dateToString(Date date) {
        return dateFormat.format(date);
    }

    @TypeConverter
    public static String tagsToString(List<Tag> tags) {
        StringBuilder buffer = new StringBuilder();
        for (Tag tag : tags) {
            buffer.append(tag.getId()).append(":").append(tag.getName()).append(";");
        }
        return buffer.toString();
    }

    @TypeConverter
    public static List<Tag> stringToTags(String str) {
        List<Tag> tags = new ArrayList<>();
        if (!"".equals(str)) {
            String[] tagStrings = str.split(";");
            for (String tag : tagStrings) {
                String[] info = tag.split(":");
                tags.add(new Tag(info[0], info[1]));
            }
        }
        return tags;
    }
}
