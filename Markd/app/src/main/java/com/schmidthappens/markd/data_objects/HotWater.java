package com.schmidthappens.markd.data_objects;

import android.util.Log;

import com.schmidthappens.markd.utilities.StringUtilities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public class HotWater {
    //TODO: Implement HotWater
    private static final String TAG = "HotWater_Bean";
    private String manufacturer;
    private String model;
    private Integer month;
    private Integer day;
    private Integer year;
    private Integer lifeSpan;
    private String units;

    private HotWater(String manufacturer, String model, Integer month, Integer day, Integer year, Integer lifeSpan, String units) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.month = month;
        this.day = day;
        this.year = year;
        this.lifeSpan = lifeSpan;
        this.units = units;
    }
    public HotWater(String manufacturer, String model, String installDate, Integer lifeSpan, String units) {
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
    HotWater(HotWater oldHotWater) {
        this(
                oldHotWater.getManufacturer(),
                oldHotWater.getModel(),
                oldHotWater.getInstallDate(),
                oldHotWater.getLifeSpanInteger(),
                oldHotWater.getUnits()
        );

    }
    HotWater(JSONObject hotWater) throws JSONException{
        this(
                    hotWater.getString("manufacturer"),
                    hotWater.getString("model"),
                    hotWater.getInt("month"),
                    hotWater.getInt("day"),
                    hotWater.getInt("year"),
                    hotWater.getInt("lifeSpan"),
                    hotWater.getString("units")
        );
    }
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
    public String getInstallDate() {
        return StringUtilities.getDateString(month, day, year);
    }
    public void setInstallDate(String installDate) {
        this.day = StringUtilities.getDayFromDotFormmattedString(installDate);
        this.month = StringUtilities.getMonthFromDotFormattedString(installDate);
        this.year = StringUtilities.getYearFromDotFormmattedString(installDate);
    }
    public String getLifeSpan() {
        return lifeSpan.toString() + " " + units;
    }
    public void setLifeSpan(Integer lifeSpanInteger, String lifeSpanUnits) {
        this.lifeSpan = lifeSpanInteger;
        this.units = lifeSpanUnits;
    }
    private Integer getLifeSpanInteger() {
        return lifeSpan;
    }
    private String getUnits() {
        return units;
    }
}
