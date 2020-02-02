package com.markd.applications.android.home.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Josh Schmidt on 12/2/17.
 */

public class DateUtitilities {
    private static final SimpleDateFormat formatter = new SimpleDateFormat(
            "MM.dd.yyyy", Locale.ENGLISH);
    private static final GregorianCalendar calendar = new GregorianCalendar();

    public static Integer getCurrentMonth() {
        return calendar.get(Calendar.MONTH);
    }

    public static Integer getCurrentDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Integer getCurrentYear() {
        return calendar.get(Calendar.YEAR);
    }

    public static Date getCurrentDate() {
        return calendar.getTime();
    }

    public static Date getDateFromString(final String dateString) throws ParseException {
        return formatter.parse(dateString);
    }
    public static String getFormattedDate(final Date date) {
        if (date == null) {
            return "";
        } else {
            return formatter.format(date);
        }
    }

    public static Date addDays(final Date current, final int amount) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.DATE, amount);
        return calendar.getTime();
    }
}
