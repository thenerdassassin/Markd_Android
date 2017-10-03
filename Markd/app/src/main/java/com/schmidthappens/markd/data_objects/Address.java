package com.schmidthappens.markd.data_objects;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public class Address {
    private static final String TAG = "Address_Bean";
    Address(JSONObject address) {
        if(address == null) {
            this.state = "";
            this.street = "";
            this.city = "";
            this.zipCode = "";
        } else {
            this.state = address.optString("state");
            this.street = address.optString("street");
            this.city = address.optString("city");
            this.zipCode = address.optString("zipCode");
        }
    }

    private String street;
    private String city;
    private String state;
    private String zipCode;

    @Override
    public String toString() {
        return street + " " + city + ", " + state + " " + zipCode;
    }
}
