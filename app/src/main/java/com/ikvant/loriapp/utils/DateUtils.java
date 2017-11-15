package com.ikvant.loriapp.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ikvant.
 */

public class DateUtils {
    public static Date getStartDate(int weekIndex) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.WEEK_OF_YEAR, weekIndex);
        return calendar.getTime();
    }

    public static Date getEndDate(int weekIndex) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        calendar.set(Calendar.WEEK_OF_YEAR, weekIndex);
        return calendar.getTime();
    }

    public static int getWeekIndex(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }
}
