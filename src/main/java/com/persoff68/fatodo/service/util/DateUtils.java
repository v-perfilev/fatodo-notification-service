package com.persoff68.fatodo.service.util;

import com.persoff68.fatodo.model.DateParams;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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
        return convertCalendarToDate(calendar);
    }

    public static Date createEndMonthsDate(int year, int month, String timezone) {
        Calendar calendar = createCalendar(timezone);
        calendar.set(year, month, 1, 0, 0, 0);
        calendar.add(Calendar.MONTH, 1);
        return convertCalendarToDate(calendar);
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
        return convertCalendarToDate(calendar);
    }

    public static Date createRelativeDate(DateParams dateParams, Optional<Calendar> startOptional, int addDays) {
        String timezone = dateParams.getTimezone();

        Calendar startCalendar = (Calendar) startOptional.orElse(createCalendar(timezone)).clone();
        startCalendar.add(Calendar.DAY_OF_MONTH, addDays);
        int date = startCalendar.get(Calendar.DAY_OF_MONTH);
        int month = startCalendar.get(Calendar.MONTH);
        int year = startCalendar.get(Calendar.YEAR);
        int time = dateParams.getTime();
        int hours = getHours(time);
        int minutes = getMinutes(time);

        Calendar calendar = createCalendar(timezone);
        calendar.set(year, month, date, hours, minutes, 0);
        Instant resultInstant = calendar.toInstant().truncatedTo(ChronoUnit.SECONDS);

        return startOptional.isPresent() || resultInstant.isAfter(Instant.now())
                ? Date.from(resultInstant)
                : Date.from(Instant.now());
    }

    public static Date createYearlyDate(DateParams dateParams, int year) {
        String timezone = dateParams.getTimezone();

        int date = dateParams.getDate();
        int month = dateParams.getMonth();
        int time = dateParams.getTime();
        int hours = getHours(time);
        int minutes = getMinutes(time);

        Calendar calendar = createCalendar(timezone);
        calendar.set(year, month, date, hours, minutes, 0);
        return convertCalendarToDate(calendar);
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

    private static Date convertCalendarToDate(Calendar calendar) {
        Instant instant = calendar.toInstant().truncatedTo(ChronoUnit.SECONDS);
        return Date.from(instant);
    }

}
