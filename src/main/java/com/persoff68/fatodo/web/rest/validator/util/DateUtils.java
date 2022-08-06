package com.persoff68.fatodo.web.rest.validator.util;

import java.util.Set;
import java.util.TimeZone;

public class DateUtils {

    private DateUtils() {
    }

    public static boolean isYearValid(int year) {
        return year >= 0 && year <= 2100;
    }

    public static boolean isMonthValid(int month) {
        return month >= 0 && month <= 11;
    }

    public static boolean isTimezoneValid(String timezone) {
        return Set.of(TimeZone.getAvailableIDs()).contains(timezone);
    }

}
