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
        try {
            //TODO: finish Customer JSON
            //Home Page
            this.namePrefix = customer.getString("namePrefix");
            this.firstName = customer.getString("firstName");
            this.lastName = customer.getString("lastName");
            if(customer.has("nameSuffix")) {
                this.nameSuffix = customer.getString("nameSuffix");
            }
            this.maritalStatus = customer.getString("maritalStatus");
            //this.address = new Address(customer.getJSONObject("address"));
            //this.home = new Home(customer.getJSONObject("home"));

            //Plumbing Page
            this.hotWater = new HotWater(customer.getJSONObject("hotWater"));
            //this.boiler = new Boiler(customer.getJSONObject("boiler"));
            //this.plumbingServices = buildServicesListFromJSONArray(customer.getJSONArray("plubming_services"));

            //Contractors
            //this.architect = new Contractor(customer.getJSONObject("architect_id"));
            //this.builder = new Contractor(customer.getJSONObject("builder_id"));
            //this.plumber = new Contractor(customer.getJSONObject("plumber_id"));

        } catch(JSONException exception) {
            Log.e(TAG, exception.getMessage());
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

    HotWater getHotWater() {
        return new HotWater(hotWater);
    }
    boolean setHotWater(HotWater hotWater) {
        this.hotWater = hotWater;
        return true;
    }
}
