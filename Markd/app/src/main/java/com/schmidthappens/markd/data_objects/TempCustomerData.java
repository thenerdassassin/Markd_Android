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
        //TODO: Add http call to ensure customer is up to date
        //Get user id and sessionToken from SessionManager
        return customer;
    }

    private boolean putCustomer(Customer customer) {
        //TODO: Add http call to set in database
        return true;
    }

    private void updateCustomer(HotWater hotWater) {
        this.customer.setHotWater(hotWater);
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
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        customer = new Customer(customerJson);
    }
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
    //Remove when database is implemented
    private JSONObject initialBoiler() {
        JSONObject boilerJSON = new JSONObject();
        try {
            boilerJSON.put("manufacturer", "Bosch");
            boilerJSON.put("model", "C950 ES NG");
            boilerJSON.put("month", "01");
            boilerJSON.put("day", "17");
            boilerJSON.put("year", "14");
            boilerJSON.put("lifespan", "12");
            boilerJSON.put("units", "years");
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return boilerJSON;
    }

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
}
