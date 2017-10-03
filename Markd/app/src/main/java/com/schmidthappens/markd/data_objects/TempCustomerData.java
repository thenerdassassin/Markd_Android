package com.schmidthappens.markd.data_objects;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public class TempCustomerData {
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
        //customer = Customer(new JSONObject(message))
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
    private void updateCustomer(List<PaintSurface> surfaces, boolean isExterior) {
        if(isExterior) {
            customer.setExteriorPaintSurfaces(surfaces);
        } else {
            customer.setInteriorPaintSurfaces(surfaces);
        }
    }

    private TempCustomerData() {
        customer = getCustomer();
        //Remove when database is implemented
        JSONObject customerJson = new JSONObject();
        try {
            customerJson.put("email", "schmidt.uconn@gmail.com");
            customerJson.put("password", "Spongebob28");

            customerJson.put("namePrefix", "Mr.");
            customerJson.put("firstName", "Joshua");
            customerJson.put("lastName", "Schmidt");
            customerJson.put("maritalStatus", "Married");
            customerJson.put("address", null);
            customerJson.put("home", null);
            customerJson.put("architect_id", null);
            customerJson.put("builder_id", null);

            //Plumbing
            customerJson.put("hotWater", initialHotWater());
            customerJson.put("boiler", initialBoiler());
            customerJson.put("plumber_id", initialPlumber());
            customerJson.put("plumbing_services", null);

            //HVAC
            customerJson.put("airHandler", initialAirHandler());
            customerJson.put("compressor", initialCompressor());
            customerJson.put("hvactechnician_id", initialHvacTechnician());
            customerJson.put("hvac_services", null);

            //Electrical
            customerJson.put("panels", null);
            customerJson.put("electrician_id", null);
            customerJson.put("electrical_services", null);

            //Painting
            customerJson.put("interiorPaintSurfaces", initialInteriorSurfaces());
            customerJson.put("exteriorPaintSurfaces", initialExteriorSurfaces());
            customerJson.put("painter_id", initialPainter());

        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        customer = new Customer(customerJson);
    }

    //Mark:- Home Page
    public String getName() {
        return getCustomer().getName();
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
    public ContractorDetails getPlumber() {return getCustomer().getPlumber();}

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
    public ContractorDetails getHvacTechnician() {
        return getCustomer().getHvacTechnician();
    }

    //Mark:- Painting
    public List<PaintSurface> getExteriorSurfaces() {
        return getCustomer().getExteriorPaintSurfaces();
    }
    public boolean updateExteriorPaintSurface(int paintId, PaintSurface paintSurface) {
        //TODO: change to use db calls
        Customer originalCustomer = getCustomer();                                         //causes update from db
        Customer customerToUpdate = originalCustomer; //new Customer(originalCustomer);   //make copy
        customerToUpdate.setExteriorPaintSurface(paintId, paintSurface);                 //change component to a copy
        if(putCustomer(customerToUpdate)) {                                             //send to database
            this.updateCustomer(customerToUpdate.getExteriorPaintSurfaces(), true);    //update TempCustomerData
            return true;
        }
        return false;
    }
    public boolean removeExteriorPaintSurface(int paintId){
        //TODO: change to use db calls
        Customer originalCustomer = getCustomer();                                         //causes update from db
        Customer customerToUpdate = originalCustomer; //new Customer(originalCustomer);   //make copy
        customerToUpdate.deleteExteriorPaintSurface(paintId);                            //change component to a copy
        if(putCustomer(customerToUpdate)) {                                             //send to database
            this.updateCustomer(customerToUpdate.getExteriorPaintSurfaces(), true);    //update TempCustomerData
            return true;
        }
        return false;
    }
    public List<PaintSurface> getInteriorSurfaces() {
        return getCustomer().getInteriorPaintSurfaces();
    }
    public boolean updateInteriorPaintSurface(int paintId, PaintSurface paintSurface) {
        //TODO: change to use db calls
        Customer originalCustomer = getCustomer();                                         //causes update from db
        Customer customerToUpdate = originalCustomer; //new Customer(originalCustomer);   //make copy
        customerToUpdate.setInteriorPaintSurface(paintId, paintSurface);                 //change component to a copy
        if(putCustomer(customerToUpdate)) {                                             //send to database
            this.updateCustomer(customerToUpdate.getInteriorPaintSurfaces(), false);   //update TempCustomerData
            return true;
        }
        return false;
    }
    public boolean removeInteriorPaintSurface(int paintId) {
        //TODO: change to use db calls
        Customer originalCustomer = getCustomer();                                         //causes update from db
        Customer customerToUpdate = originalCustomer; //new Customer(originalCustomer);   //make copy
        customerToUpdate.deleteInteriorPaintSurface(paintId);                            //change component to a copy
        if(putCustomer(customerToUpdate)) {                                             //send to database
            this.updateCustomer(customerToUpdate.getExteriorPaintSurfaces(), true);    //update TempCustomerData
            return true;
        }
        return false;
    }
    public ContractorDetails getPainter() {
        return getCustomer().getPainter();
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
            hvactechnician.put("profession", "hvac");
            hvactechnician.put("zipCode", "06903");
        } catch(JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return hvactechnician;
    }
    private JSONArray initialExteriorSurfaces() {
        JSONArray exteriorSurfaces = new JSONArray();
        try {
            JSONObject surface1 = new JSONObject();
            surface1.put("month", "2");
            surface1.put("day", "4");
            surface1.put("year", "2017");
            surface1.put("surface", "Siding");
            surface1.put("brand", "Behr");
            surface1.put("color", "Cream");
            exteriorSurfaces.put(surface1);

            JSONObject surface2 = new JSONObject();
            surface2.put("month", "6");
            surface2.put("day", "24");
            surface2.put("year", "2006");
            surface2.put("surface", "Garage");
            surface2.put("brand", "Behr");
            surface2.put("color", "Translucent Silk");
            exteriorSurfaces.put(surface2);

        } catch(JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return exteriorSurfaces;
    }
    private JSONArray initialInteriorSurfaces() {
        JSONArray interiorSurfaces = new JSONArray();
        try {
            JSONObject surface1 = new JSONObject();
            surface1.put("month", "8");
            surface1.put("day", "8");
            surface1.put("year", "17");
            surface1.put("surface", "Living Room");
            surface1.put("brand", "Sherwin Williams");
            surface1.put("color", "Light Blue");
            interiorSurfaces.put(surface1);

            JSONObject surface2 = new JSONObject();
            surface2.put("month", "8");
            surface2.put("day", "1");
            surface2.put("year", "17");
            surface2.put("surface", "Master bedroom");
            surface2.put("brand", "Sherwin Williams");
            surface2.put("color", "Yellow");
            interiorSurfaces.put(surface2);

            JSONObject surface3 = new JSONObject();
            surface3.put("month", "1");
            surface3.put("day", "5");
            surface3.put("year", "14");
            surface3.put("surface", "Bathroom");
            surface3.put("brand", "Sherwin Williams");
            surface3.put("color", "Loch Blue");
            interiorSurfaces.put(surface3);

            JSONObject surface4 = new JSONObject();
            surface4.put("month", "12");
            surface4.put("day", "6");
            surface4.put("year", "13");
            surface4.put("surface", "Dining Room");
            surface4.put("brand", "Sherwin Williams");
            surface4.put("color", "Grape Harvest");
            interiorSurfaces.put(surface4);

            JSONObject surface5 = new JSONObject();
            surface5.put("month", "12");
            surface5.put("day", "6");
            surface5.put("year", "13");
            surface5.put("surface", "Kitchen");
            surface5.put("brand", "Sherwin Williams");
            surface5.put("color", "Decor White");
            interiorSurfaces.put(surface5);
        } catch(JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return interiorSurfaces;
    }
    private JSONObject initialPainter() {
        JSONObject painter = new JSONObject();
        try {
            painter.put("companyName", "MDF Painting & Power Washing");
            painter.put("telephoneNumber", "203.348.2295");
            painter.put("websiteUrl", "mdfpainting.com");
            painter.put("profession", "painter");
            painter.put("zipCode", "06903");
        } catch(JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return painter;
    }

    //Used to Test Contractor page
    //TODO: Change to use http calls
    public void switchCustomerData(Customer customer) {
        this.customer = customer;
    }

}
