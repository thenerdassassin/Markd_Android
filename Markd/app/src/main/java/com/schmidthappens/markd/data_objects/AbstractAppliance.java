package com.schmidthappens.markd.data_objects;

import com.schmidthappens.markd.utilities.StringUtilities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

abstract class AbstractAppliance {
    protected String manufacturer;
    protected String model;
    protected Integer month;
    protected Integer day;
    protected Integer year;
    Integer lifeSpan;
    String units;

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
    AbstractAppliance(AbstractAppliance appliance) throws NullPointerException {
        this(
                appliance.getManufacturer(),
                appliance.getModel(),
                appliance.getInstallDate(),
                appliance.getLifeSpanInteger(),
                appliance.getUnits()
        );

    }
    AbstractAppliance(JSONObject appliance) throws JSONException {
        this(
                appliance.getString("manufacturer"),
                appliance.getString("model"),
                appliance.getInt("month"),
                appliance.getInt("day"),
                appliance.getInt("year"),
                appliance.getInt("lifeSpan"),
                appliance.getString("units")
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
