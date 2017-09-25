package com.schmidthappens.markd.data_objects;

import com.schmidthappens.markd.utilities.StringUtilities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public abstract class AbstractAppliance {
    protected String manufacturer;
    protected String model;
    protected Integer month;
    protected Integer day;
    protected Integer year;
    protected Integer lifeSpan;
    protected String units;

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
    AbstractAppliance(JSONObject appliance) {
        this(
                appliance.optString("manufacturer"),
                appliance.optString("model"),
                appliance.optInt("month"),
                appliance.optInt("day"),
                appliance.optInt("year"),
                appliance.optInt("lifeSpan"),
                appliance.optString("units")
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
    public String getLifeSpanString() {
        return lifeSpan.toString() + " " + units;
    }
    public void setLifeSpan(Integer lifeSpanInteger, String lifeSpanUnits) {
        this.lifeSpan = lifeSpanInteger;
        this.units = lifeSpanUnits;
    }
    public Integer getLifeSpanInteger() {
        return lifeSpan;
    }
    public String getUnits() {
        return units;
    }
}
