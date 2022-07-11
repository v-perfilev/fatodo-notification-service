package com.persoff68.fatodo.service.util;

import com.persoff68.fatodo.model.DateParams;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {
    private static final int MINUTES_IN_HOUR = 60;

    private DateUtils() {
    }

    public static Instant createInstant(DateParams dateParams) {
        String timezone = dateParams.getTimezone();
        int date = dateParams.getDate();
        int month = dateParams.getMonth();
        int year = dateParams.getYear();
        int time = dateParams.getTime();
        int hours = getHours(time);
        int minutes = getMinutes(time);
        Calendar calendar = createCalendar(timezone);
        calendar.set(year, month, date, hours, minutes);
        return calendar.toInstant();
    }

    public static Instant createRelativeInstant(DateParams dateParams, int addDays) {
        String timezone = dateParams.getTimezone();

        Calendar today = createCalendar(timezone);
        today.add(Calendar.DAY_OF_MONTH, addDays);
        int date = today.get(Calendar.DAY_OF_MONTH);
        int month = today.get(Calendar.MONTH);
        int year = today.get(Calendar.YEAR);
        int time = dateParams.getTime();
        int hours = getHours(time);
        int minutes = getMinutes(time);

        Calendar calendar = createCalendar(timezone);
        calendar.set(year, month, date, hours, minutes);
        return calendar.toInstant();
    }

    public static Instant createYearlyInstant(DateParams dateParams) {
        String timezone = dateParams.getTimezone();

        int date = dateParams.getDate();
        int month = dateParams.getMonth();
        int time = dateParams.getTime();
        int hours = getHours(time);
        int minutes = getMinutes(time);

        Calendar today = createCalendar(timezone);
        int year = today.get(Calendar.YEAR);

        Calendar calendar = createCalendar(timezone);
        calendar.set(year, month, date, hours, minutes);
        addConditionalYearOffset(today, year, calendar);
        return calendar.toInstant();
    }

    private static int getHours(int time) {
        return time / MINUTES_IN_HOUR;
    }

    private static int getMinutes(int time) {
        return time % MINUTES_IN_HOUR;
    }

    private static void addConditionalYearOffset(Calendar today, int year, Calendar calendar) {
        if (today.compareTo(calendar) > 0) {
            calendar.set(Calendar.YEAR, year + 1);
        }
    }

    private static Calendar createCalendar(String timezone) {
        return Calendar.getInstance(TimeZone.getTimeZone(timezone));
    }

}
