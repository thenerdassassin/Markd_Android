package com.schmidthappens.markd.data_objects;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public class Address {
    private static final String TAG = "Address_Bean";
    public Address(JSONObject address) {
        try {
            this.state = address.getString("state");
            this.street = address.getString("street");
            this.city = address.getString("city");
            this.zipCode = address.getString("zipCode");
        } catch (JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
    }

    private String street;
    private String city;
    private String state;
    private String zipCode;

    public String getStreet() {
        return street;
    }
    public String getCity() {
        return city;
    }
    public String getState() {
        return state;
    }
    public String getZipCode() {
        return zipCode;
    }
}
