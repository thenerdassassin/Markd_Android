package com.schmidthappens.markd.utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by joshua.schmidtibm.com on 12/2/17.
 */

public class DateUtitilities {
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
}
