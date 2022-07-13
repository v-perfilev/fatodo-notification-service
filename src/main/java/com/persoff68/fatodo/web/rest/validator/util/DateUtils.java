package com.persoff68.fatodo.web.rest.validator.util;

import java.util.Set;
import java.util.TimeZone;

public class DateUtils {

    private DateUtils() {
    }

    public static boolean isDateValid(int month, int year) {
        return year >= 0 && year <= 2100 && month >= 0 && month <= 11;
    }

    public static boolean isTimezoneValid(String timezone) {
        return Set.of(TimeZone.getAvailableIDs()).contains(timezone);
    }

}
