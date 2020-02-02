package com.markd.applications.android.home.data_objects;

import com.google.firebase.database.IgnoreExtraProperties;
import com.markd.applications.android.home.utilities.StringUtilities;

/**
 * Created by Josh Schmidt on 9/23/17.
 */
@IgnoreExtraProperties
public abstract class AbstractAppliance {
    protected String manufacturer;
    protected String model;
    protected Integer month;
    protected Integer day;
    protected Integer year;
    protected Integer lifeSpan;
    protected String units;

    public AbstractAppliance() {
        // Default constructor required for calls to DataSnapshot.getValue(AbstractAppliance.class)
    }

    AbstractAppliance(String manufacturer, String model, Integer month, Integer day, Integer year, Integer lifeSpan, String units) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.month = month;
        this.day = day;
        this.year = year;
        this.lifeSpan = lifeSpan;
        this.units = units;
    }
    AbstractAppliance(String manufacturer, String model, String installDate, Integer lifeSpan, String units) {
        this(
                manufacturer,
                model,
                StringUtilities.getMonthFromDotFormattedString(installDate),
                StringUtilities.getDayFromDotFormmattedString(installDate),
                StringUtilities.getYearFromDotFormmattedString(installDate),
                lifeSpan,
                units
        );
    }

    //Mark:- Getters/Setters
    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public Integer getMonth() {
        return month;
    }
    public void setMonth(Integer month) {
        this.month = month;
    }
    public Integer getDay() {
        return day;
    }
    public void setDay(Integer day) {
        this.day = day;
    }
    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }
    public Integer getLifeSpan() {
        return lifeSpan;
    }
    public void setLifeSpan(Integer lifeSpan) {
        this.lifeSpan = lifeSpan;
    }
    public String getUnits() {
        return units;
    }
    public void setUnits(String units) {
        this.units = units;
    }

    //Mark:- Helper Methods
    /*public boolean isUninitialized() {
        if(manufacturer == null && model == null && month == null && day == null && year == null && lifeSpan == null && units == null) {
            return true;
        }
        return false;
    }*/
    public String installDateAsString() {
        return StringUtilities.getDateString(month, day, year);
    }
    public void updateInstallDate(String installDate) {
        this.day = StringUtilities.getDayFromDotFormmattedString(installDate);
        this.month = StringUtilities.getMonthFromDotFormattedString(installDate);
        this.year = StringUtilities.getYearFromDotFormmattedString(installDate);
    }
    public String lifeSpanAsString() {
        if (lifeSpan == null) {
            return "";
        }
        return lifeSpan.toString() + " " + units;
    }
    public void updateLifeSpan(Integer lifeSpanInteger, String lifeSpanUnits) {
        this.lifeSpan = lifeSpanInteger;
        this.units = lifeSpanUnits;
    }
}
