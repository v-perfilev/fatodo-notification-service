package com.persoff68.fatodo.service.util;

import com.persoff68.fatodo.model.DateParams;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.TimeZone;

public class DateUtils {
    private static final int MINUTES_IN_HOUR = 60;

    private DateUtils() {
    }

    public static Date createStartMonthsDate(int year, int month, String timezone) {
        Calendar calendar = createCalendar(timezone);
        calendar.set(year, month, 1, 0, 0, 0);
        return Date.from(calendar.toInstant());
    }

    public static Date createEndMonthsDate(int year, int month, String timezone) {
        Calendar calendar = createCalendar(timezone);
        calendar.set(year, month, 1, 0, 0, 0);
        calendar.add(Calendar.MONTH, 1);
        return Date.from(calendar.toInstant());
    }

    public static Date createDate(DateParams dateParams) {
        String timezone = dateParams.getTimezone();
        int date = dateParams.getDate();
        int month = dateParams.getMonth();
        int year = dateParams.getYear();
        int time = dateParams.getTime();
        int hours = getHours(time);
        int minutes = getMinutes(time);
        Calendar calendar = createCalendar(timezone);
        calendar.set(year, month, date, hours, minutes, 0);
        return Date.from(calendar.toInstant());
    }

    public static Date createRelativeDate(DateParams dateParams, int addDays,
                                          Optional<Calendar> startCalendarOptional) {
        String timezone = dateParams.getTimezone();

        Calendar startCalendar = (Calendar) startCalendarOptional.orElse(createCalendar(timezone)).clone();
        startCalendar.add(Calendar.DAY_OF_MONTH, addDays);
        int date = startCalendar.get(Calendar.DAY_OF_MONTH);
        int month = startCalendar.get(Calendar.MONTH);
        int year = startCalendar.get(Calendar.YEAR);
        int time = dateParams.getTime();
        int hours = getHours(time);
        int minutes = getMinutes(time);

        Calendar calendar = createCalendar(timezone);
        calendar.set(year, month, date, hours, minutes, 0);
        return Date.from(calendar.toInstant());
    }

    public static Date createYearlyDate(DateParams dateParams) {
        String timezone = dateParams.getTimezone();

        int date = dateParams.getDate();
        int month = dateParams.getMonth();
        int time = dateParams.getTime();
        int hours = getHours(time);
        int minutes = getMinutes(time);

        Calendar today = createCalendar(timezone);
        int year = today.get(Calendar.YEAR);

        Calendar calendar = createCalendar(timezone);
        calendar.set(year, month, date, hours, minutes, 0);
        addConditionalYearOffset(today, year, calendar);
        return Date.from(calendar.toInstant());
    }

    private static Calendar createCalendar(String timezone) {
        return Calendar.getInstance(TimeZone.getTimeZone(timezone));
    }

    public static Calendar createCalendar(String timezone, Date date) {
        Instant instant = date.toInstant();
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of(timezone));
        return GregorianCalendar.from(zonedDateTime);
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

}
