package com.ikvant.loriapp.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ikvant.
 */

public class DateUtils {
    public static Date getStartDate(int weekIndex) {
        int weekInYear = (weekIndex % 1000) / 10;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.WEEK_OF_YEAR, weekInYear);
        return calendar.getTime();
    }

    public static Date getEndDate(int weekIndex) {
        int weekInYear = (weekIndex % 1000) / 10;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        calendar.set(Calendar.WEEK_OF_YEAR, weekInYear);
        return calendar.getTime();
    }

    //2017_46_5-year-week_in_year-day_in_week
    public static int getWeekIndex(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year * 1000 + calendar.get(Calendar.WEEK_OF_YEAR) * 10;
    }

    public static int getWeekDayIndex(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        //0 - FOR week name
        int day = 8 - calendar.get(Calendar.DAY_OF_WEEK);
        return year * 1000 + week * 10 + day;
    }
}
