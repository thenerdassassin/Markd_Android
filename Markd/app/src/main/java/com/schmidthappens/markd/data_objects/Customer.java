package com.schmidthappens.markd.data_objects;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public class Customer {
    private static final String TAG = "Customer_Bean";
    private String email;
    private String password;

    //For Home Page
    private String namePrefix;
    private String firstName;
    private String lastName;
    private String nameSuffix;
    private String maritalStatus; //TODO: make enum["Married", "Single"]
    private Address address;
    private Home home;
    private Contractor architect;
    private Contractor builder;

    //For Plumbing Page
    private HotWater hotWater;
    private Boiler boiler;
    private Contractor plumber;
    private List<ContractorService> plumbingServices;

    //For HVAC Page
    private AirHandler airHandler;
    private Compressor compressor;
    private Contractor hvactechnician;
    private List<ContractorService> hvacServices;

    //For Electrical Page
    private List<Panel> panels;
    private Contractor electrician;
    private List<ContractorService> electricalServices;

    //For Painting Page
    private List<PaintSurface> interiorPaintSurfaces;
    private List<PaintSurface> exteriorPaintSurfaces;
    private Contractor painter;

    public Customer(JSONObject customer) {
        //TODO: finish Customer JSON
        //Home Page
        this.namePrefix = customer.optString("namePrefix");
        this.firstName = customer.optString("firstName");
        this.lastName = customer.optString("lastName");
        this.nameSuffix = customer.optString("nameSuffix");
        this.maritalStatus = customer.optString("maritalStatus");
        //this.address = new Address(customer.getJSONObject("address"));
        //this.home = new Home(customer.getJSONObject("home"));

        //Plumbing Page
        if(customer.optJSONObject("hotWater") != null) {
            this.hotWater = new HotWater(customer.optJSONObject("hotWater"));
        }
        if(customer.optJSONObject("boiler") != null) {
            this.boiler = new Boiler(customer.optJSONObject("boiler"));
        }
        //this.plumbingServices = buildServicesListFromJSONArray(customer.getJSONArray("plubming_services"));

        //HVAC Page
        if(customer.optJSONObject("airHandler") != null) {
            this.airHandler = new AirHandler(customer.optJSONObject("airHandler"));
        }
        if(customer.optJSONObject("compressor") != null) {
            this.compressor = new Compressor(customer.optJSONObject("compressor"));
        }

        //Contractors
        //this.architect = new Contractor(customer.getJSONObject("architect_id"));
        //this.builder = new Contractor(customer.getJSONObject("builder_id"));
        if(customer.optJSONObject("plumber_id") != null) {
            this.plumber = new Contractor(customer.optJSONObject("plumber_id"));
        }
        if(customer.optJSONObject("hvactechnician_id") != null) {
            this.hvactechnician = new Contractor(customer.optJSONObject("hvactechnician_id"));
        }
    }
    private List<ContractorService> buildServicesListFromJSONArray(JSONArray plumbingServiceList) {
        List<ContractorService> servicesToReturn = new ArrayList<ContractorService>();
        for (int i = 0 ; i < plumbingServiceList.length(); i++) {
            try {
                JSONObject jsonService = plumbingServiceList.getJSONObject(i);
                ContractorService service = new ContractorService(
                        jsonService.getInt("month"),
                        jsonService.getInt("day"),
                        jsonService.getInt("year"),
                        jsonService.getString("contractor"),
                        jsonService.getString("comments")
                );
                servicesToReturn.add(service);
            } catch(JSONException exception) {
                Log.e(TAG, exception.getMessage());
            }
        }
        return servicesToReturn;
    }

    //Mark:- Plumbing Page
    HotWater getHotWater() {
        if(hotWater == null) {
            return null;
        }
        return new HotWater(hotWater);
    }
    Boiler getBoiler() {
        if(boiler == null) {
            return null;
        }
        return new Boiler(boiler);
    }
    void setHotWater(HotWater hotWater) {
        this.hotWater = hotWater;
    }
    void setBoiler(Boiler boiler) {
        this.boiler = boiler;
    }
    Contractor getPlumber() {
        if(plumber == null) {
            return null;
        }
        return new Contractor(plumber);
    }

    //Mark:- HVAC Page
    AirHandler getAirHandler() {
        if(airHandler == null) {
            return null;
        }
        return new AirHandler(airHandler);
    }
    void setAirHandler(AirHandler airHandler) {
        this.airHandler = airHandler;
    }
    Compressor getCompressor() {
        if(compressor == null) {
            return null;
        }
        return new Compressor(compressor);
    }
    void setCompressor(Compressor compressor) {
        this.compressor = compressor;
    }
    Contractor getHvacTechnician() {
        if(hvactechnician == null) {
            return null;
        }
        return new Contractor(hvactechnician);
    }
}
