package com.schmidthappens.markd.data_objects;

/**
 * Created by Josh on 6/6/2017.
 */

public class TempHvacData {
    private static final TempHvacData myData = new TempHvacData();
    private String airHandlerManufacturer;
    private String airHandlerModel;
    private String airHandlerInstallDate;
    private String airHandlerLifeSpan;

    private String compressorManufacturer;
    private String compressorModel;
    private String compressorInstallDate;
    private String compressorLifeSpan;

    private TempHvacData() {
        airHandlerManufacturer = "Goodman";
        airHandlerModel = "ARUF24B14";
        airHandlerInstallDate = "08.13.13";
        airHandlerLifeSpan = "20 years";

        compressorManufacturer = "Goodman";
        compressorModel = "GSX130361";
        compressorInstallDate = "01.27.14";
        compressorLifeSpan = "6 years";
    }

    public static TempHvacData getInstance() {
        return myData;
    }

    public String getAirHandlerManufacturer() {
        return airHandlerManufacturer;
    }

    public void setAirHandlerManufacturer(String airHandlerManufacturer) {
        this.airHandlerManufacturer = airHandlerManufacturer;
    }

    public String getAirHandlerModel() {
        return airHandlerModel;
    }

    public void setAirHandlerModel(String airHandlerModel) {
        this.airHandlerModel = airHandlerModel;
    }

    public String getAirHandlerInstallDate() {
        return airHandlerInstallDate;
    }

    public void setAirHandlerInstallDate(String airHandlerInstallDate) {
        this.airHandlerInstallDate = airHandlerInstallDate;
    }

    public String getAirHandlerLifeSpan() {
        return airHandlerLifeSpan;
    }

    public void setAirHandlerLifeSpan(String airHandlerLifeSpan) {
        this.airHandlerLifeSpan = airHandlerLifeSpan;
    }

    public String getCompressorManufacturer() {
        return compressorManufacturer;
    }

    public void setCompressorManufacturer(String compressorManufacturer) {
        this.compressorManufacturer = compressorManufacturer;
    }

    public String getCompressorModel() {
        return compressorModel;
    }

    public void setCompressorModel(String compressorModel) {
        this.compressorModel = compressorModel;
    }

    public String getCompressorInstallDate() {
        return compressorInstallDate;
    }

    public void setCompressorInstallDate(String compressorInstallDate) {
        this.compressorInstallDate = compressorInstallDate;
    }

    public String getCompressorLifeSpan() {
        return compressorLifeSpan;
    }

    public void setCompressorLifeSpan(String compressorLifeSpan) {
        this.compressorLifeSpan = compressorLifeSpan;
    }
}
