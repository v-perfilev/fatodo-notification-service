package com.persoff68.fatodo.service.util;

import com.persoff68.fatodo.model.DateParams;

import java.time.Instant;
import java.util.Calendar;

public class DateUtils {
    private static final int MINUTES_IN_HOUR = 60;

    private DateUtils() {
    }

    public static Instant createInstant(DateParams dateParams) {
        int date = dateParams.getDate();
        int month = dateParams.getMonth();
        int year = dateParams.getYear();
        int time = dateParams.getTime();
        int hours = getHours(time);
        int minutes = getMinutes(time);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hours, minutes);
        return calendar.toInstant();
    }

    public static Instant createRelativeInstant(DateParams dateParams, int addDays) {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_MONTH, addDays);
        int date = today.get(Calendar.DAY_OF_MONTH);
        int month = today.get(Calendar.MONTH);
        int year = today.get(Calendar.YEAR);
        int time = dateParams.getTime();
        int hours = getHours(time);
        int minutes = getMinutes(time);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hours, minutes);
        return calendar.toInstant();
    }

    public static Instant createYearlyInstant(DateParams dateParams) {
        int date = dateParams.getDate();
        int month = dateParams.getMonth();
        int time = dateParams.getTime();
        int hours = getHours(time);
        int minutes = getMinutes(time);

        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hours, minutes);
        if (today.compareTo(calendar) > 0) {
            calendar.set(Calendar.YEAR, year + 1);
        }
        return calendar.toInstant();
    }

    private static int getHours(int time) {
        return time / MINUTES_IN_HOUR;
    }

    private static int getMinutes(int time) {
        return time % MINUTES_IN_HOUR;
    }

}
