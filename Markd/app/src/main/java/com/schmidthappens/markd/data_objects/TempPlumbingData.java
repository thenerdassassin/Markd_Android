package com.schmidthappens.markd.data_objects;

/**
 * Created by Josh on 6/6/2017.
 */

public class TempPlumbingData {
    private static final TempPlumbingData myData = new TempPlumbingData();
    private String hotWaterManufacturer;
    private String hotWaterModel;
    private String hotWaterInstallDate;
    private String hotWaterLifeSpan;

    private String boilerManufacturer;
    private String boilerModel;
    private String boilerInstallDate;
    private String boilerLifeSpan;

    private TempPlumbingData() {
        hotWaterManufacturer = "Bosch";
        hotWaterModel = "C950 ES NG";
        hotWaterInstallDate = "1/17/14";
        hotWaterLifeSpan = "12 years";

        boilerManufacturer = "Westinghouse";
        boilerModel = "WBRCLP140W";
        boilerInstallDate = "11/7/12";
        boilerLifeSpan = "9 years";
    }

    public static TempPlumbingData getInstance() {
        return myData;
    }

    public String getHotWaterManufacturer() {
        return hotWaterManufacturer;
    }

    public void setHotWaterManufacturer(String hotWaterManufacturer) {
        this.hotWaterManufacturer = hotWaterManufacturer;
    }

    public String getHotWaterModel() {
        return hotWaterModel;
    }

    public void setHotWaterModel(String hotWaterModel) {
        this.hotWaterModel = hotWaterModel;
    }

    public String getHotWaterInstallDate() {
        return hotWaterInstallDate;
    }

    public void setHotWaterInstallDate(String hotWaterInstallDate) {
        this.hotWaterInstallDate = hotWaterInstallDate;
    }

    public String getHotWaterLifeSpan() {
        return hotWaterLifeSpan;
    }

    public void setHotWaterLifeSpan(String hotWaterLifeSpan) {
        this.hotWaterLifeSpan = hotWaterLifeSpan;
    }

    public String getBoilerManufacturer() {
        return boilerManufacturer;
    }

    public void setBoilerManufacturer(String boilerManufacturer) {
        this.boilerManufacturer = boilerManufacturer;
    }

    public String getBoilerModel() {
        return boilerModel;
    }

    public void setBoilerModel(String boilerModel) {
        this.boilerModel = boilerModel;
    }

    public String getBoilerInstallDate() {
        return boilerInstallDate;
    }

    public void setBoilerInstallDate(String boilerInstallDate) {
        this.boilerInstallDate = boilerInstallDate;
    }

    public String getBoilerLifeSpan() {
        return boilerLifeSpan;
    }

    public void setBoilerLifeSpan(String boilerLifeSpan) {
        this.boilerLifeSpan = boilerLifeSpan;
    }
}
