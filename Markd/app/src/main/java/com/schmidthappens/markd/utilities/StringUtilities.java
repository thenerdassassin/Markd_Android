package com.schmidthappens.markd.utilities;

import android.support.annotation.Nullable;
import android.util.Log;

import com.schmidthappens.markd.data_objects.Customer;

import java.util.Calendar;

/**
 * Created by Josh on 8/8/2017.
 */

public class StringUtilities {
    private final static String TAG = "StringUtilities";

    public static String getDateString(int month, int day, int year) {
        return getDateString(month, day, year, ".");
    }
    /*
        Returns string in format mm.dd.yy
        If something is wrong returns null
     */
    @Nullable
    public static String getDateString(int month, int day, int year, String separator) {
        String dateString = "";

        if(month > 12) {
            Log.e(TAG, "Month was greater than 12 it was " + month);
            return null;
        } else if(month < 10) {
            dateString += "0";
        }
        dateString += (month + separator);

        if(day > 31) {
            Log.e(TAG, "Day was greater than 31 it was " + day);
            return null;
        } else if(day < 10) {
            dateString += "0";
        }
        dateString += (day + separator);

        if(year > 9 && year < 100) {
            dateString += year;
        } else {
            int yearModulo100 = year%100;
            if(yearModulo100 < 10) {
                dateString += "0";
            }
            dateString += (year%100);
        }

        return dateString;
    }
    public static String getCurrentDateString() {
        Calendar calendar = Calendar.getInstance();

        int month = calendar.get(Calendar.MONTH)+1; //zero indexed so add 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);

        return getDateString(month, day, year);
    }

    //Input must be in format: mm.dd.yy
    public static int getMonthFromDotFormattedString(String dotFormmatedString) {
        return Integer.parseInt(dotFormmatedString.substring(0, dotFormmatedString.indexOf(".")));
    }
    public static int getDayFromDotFormmattedString(String dotFormmatedString) {
        return Integer.parseInt(dotFormmatedString.substring(dotFormmatedString.indexOf(".")+1, dotFormmatedString.lastIndexOf(".")));
    }
    public static int getYearFromDotFormmattedString(String dotFormmatedString) {
        return Integer.parseInt(dotFormmatedString.substring(dotFormmatedString.lastIndexOf(".")+1));
    }

    public static String getFormattedName(String prefix, String first, String last, String maritalStatus) {
        StringBuilder builder = new StringBuilder();
        if(isNotNullOrEmpty(prefix)) {
            builder.append(prefix).append(" ");
        }

        if(maritalStatus != null && maritalStatus.equals("Married") && isNotNullOrEmpty(prefix)) {
            builder.append("and Mrs. ");
        } else {
            if(isNotNullOrEmpty(first)) {
                builder.append(first).append(" ");
            }
        }

        if(last != null) {
            builder.append(last);
        }

        return builder.toString();
    }

    public static boolean isNullOrEmpty(String string) {
        return (string == null || string.isEmpty() || string.replace(" ", "").isEmpty());
    }

    private static boolean isNotNullOrEmpty(String string) {
        return !isNullOrEmpty(string);
    }
}
