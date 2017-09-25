package com.schmidthappens.markd.data_objects;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public class TempCustomerData {
    //TODO: Finish TempCustomerData
    private static final String TAG = "CustomerDataSingleton";
    private static final TempCustomerData customerData = new TempCustomerData();
    public static TempCustomerData getInstance() {
        return customerData;
    }

    private Customer customer;
    private Customer getCustomer() {
        //TODO: Add get http call to ensure customer is up to date
        //Get user id and sessionToken from SessionManager
        //HTTP call receive back body data
        //Check for success in body data
        //Get message as string
        //new JSONObject(message)
        //return Customer(new JSONObject(message))
        return customer;
    }
    private boolean putCustomer(Customer customer) {
        //TODO: Add http put call to set in database
        return true;
    }

    private void updateCustomer(HotWater hotWater) {
        customer.setHotWater(hotWater);
    }
    private void updateCustomer(Boiler boiler) {
        customer.setBoiler(boiler);
    }
    private void updateCustomer(AirHandler airHandler) {
        customer.setAirHandler(airHandler);
    }
    private void updateCustomer(Compressor compressor) {
        customer.setCompressor(compressor);
    }

    private TempCustomerData() {
        //customer = getCustomer();
        //Remove when database is implemented
        JSONObject customerJson = new JSONObject();
        try {
            customerJson.put("email", "schmidt.uconn@gmail.com");
            customerJson.put("password", "Spongebob28");
            customerJson.put("namePrefix", "Mr.");
            customerJson.put("firstName", "Joshua");
            customerJson.put("lastName", "Schmidt");
            customerJson.put("maritalStatus", "Married");

            //Plumbing
            customerJson.put("hotWater", initialHotWater());
            customerJson.put("boiler", initialBoiler());
            customerJson.put("plumber_id", initialPlumber());

            //HVAC
            customerJson.put("airHandler", initialAirHandler());
            customerJson.put("compressor", initialCompressor());
            customerJson.put("hvactechnician_id", initialHvacTechnician());
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        customer = new Customer(customerJson);
    }

    //Mark:- Plumbing
    public HotWater getHotWater() {
        return getCustomer().getHotWater();
    }
    public boolean updateHotWater(HotWater hotWater) {
        //TODO: change to use db calls
        Customer originalCustomer = getCustomer();                                        //causes update from db
        Customer customerToUpdate = originalCustomer; //new Customer(originalCustomer);  //make copy
        customerToUpdate.setHotWater(hotWater);                                         //change component to a copy
        if(putCustomer(customerToUpdate)) {                                            //send to database
            this.updateCustomer(hotWater);                                            //update TempCustomerData
            return true;
        }
        return false;
    }
    public Boiler getBoiler() {
        return getCustomer().getBoiler();
    }
    public boolean updateBoiler(Boiler boiler) {
        //TODO: change to use db calls
        Customer originalCustomer = getCustomer();                                        //causes update from db
        Customer customerToUpdate = originalCustomer; //new Customer(originalCustomer);  //make copy
        customerToUpdate.setBoiler(boiler);                                             //change component to a copy
        if(putCustomer(customerToUpdate)) {                                            //send to database
            this.updateCustomer(boiler);                                             //update TempCustomerData
            return true;
        }
        return false;
    }
    public Contractor getPlumber() {return getCustomer().getPlumber();}

    //Mark:- HVAC
    public AirHandler getAirHandler() {
        return getCustomer().getAirHandler();
    }
    public boolean updateAirHandler(AirHandler airHandler) {
        //TODO: change to use db calls
        Customer originalCustomer = getCustomer();                                        //causes update from db
        Customer customerToUpdate = originalCustomer; //new Customer(originalCustomer);  //make copy
        customerToUpdate.setAirHandler(airHandler);                                     //change component to a copy
        if(putCustomer(customerToUpdate)) {                                            //send to database
            this.updateCustomer(airHandler);                                          //update TempCustomerData
            return true;
        }
        return false;
    }
    public Compressor getCompressor() {
        return getCustomer().getCompressor();
    }
    public boolean updateCompressor(Compressor compressor) {
        //TODO: change to use db calls
        Customer originalCustomer = getCustomer();                                        //causes update from db
        Customer customerToUpdate = originalCustomer; //new Customer(originalCustomer);  //make copy
        customerToUpdate.setCompressor(compressor);                                     //change component to a copy
        if(putCustomer(customerToUpdate)) {                                            //send to database
            this.updateCustomer(compressor);                                          //update TempCustomerData
            return true;
        }
        return false;
    }

    public Contractor getHvacTechnician() {
        return getCustomer().getHvacTechnician();
    }

    //TODO: Delete when http calls are here
    //Remove when database is implemented
    private JSONObject initialHotWater() {
        JSONObject hotWaterJSON = new JSONObject();
        try {
            hotWaterJSON.put("manufacturer", "Bosch");
            hotWaterJSON.put("model", "C950 ES NG");
            hotWaterJSON.put("month", "01");
            hotWaterJSON.put("day", "17");
            hotWaterJSON.put("year", "14");
            hotWaterJSON.put("lifeSpan", "12");
            hotWaterJSON.put("units", "years");
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return hotWaterJSON;
    }
    private JSONObject initialBoiler() {
        JSONObject boilerJSON = new JSONObject();
        try {
            boilerJSON.put("manufacturer", "Westinghouse");
            boilerJSON.put("model", "WBRCLP140W");
            boilerJSON.put("month", "11");
            boilerJSON.put("day", "07");
            boilerJSON.put("year", "12");
            boilerJSON.put("lifeSpan", "9");
            boilerJSON.put("units", "years");
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return boilerJSON;
    }
    private JSONObject initialPlumber() {
        JSONObject plumber = new JSONObject();
        try {
            plumber.put("companyName", "SDR Plumbing & Heating Inc");
            plumber.put("telephoneNumber", "203.348.2295");
            plumber.put("websiteUrl", "sdrplumbing.com");
            plumber.put("profession", "plumber");
            plumber.put("zipCode", "06903");
        } catch(JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return plumber;
    }
    private JSONObject initialAirHandler() {
        JSONObject airHandlerJSON = new JSONObject();
        try {
            airHandlerJSON.put("manufacturer", "Goodman");
            airHandlerJSON.put("model", "ARUF24B14");
            airHandlerJSON.put("month", "08");
            airHandlerJSON.put("day", "13");
            airHandlerJSON.put("year", "13");
            airHandlerJSON.put("lifeSpan", "20");
            airHandlerJSON.put("units", "years");
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return airHandlerJSON;
    }
    private JSONObject initialCompressor() {
        JSONObject compressorJSON = new JSONObject();
        try {
            compressorJSON.put("manufacturer", "Goodman");
            compressorJSON.put("model", "GSX130361");
            compressorJSON.put("month", "01");
            compressorJSON.put("day", "27");
            compressorJSON.put("year", "14");
            compressorJSON.put("lifeSpan", "6");
            compressorJSON.put("units", "years");
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return compressorJSON;
    }
    private JSONObject initialHvacTechnician() {
        JSONObject hvactechnician = new JSONObject();
        try {
            hvactechnician.put("companyName", "AireServ");
            hvactechnician.put("telephoneNumber", "203.348.2295");
            hvactechnician.put("websiteUrl", "aireserv.com");
            hvactechnician.put("profession", "hvactechnician");
            hvactechnician.put("zipCode", "06903");
        } catch(JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return hvactechnician;
    }

}
