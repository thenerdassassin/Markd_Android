package com.schmidthappens.markd.data_objects;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public class Home {
    private static final String TAG = "Home_Bean";
    private Double bedrooms;
    private Double squareFootage;
    private Double bathrooms;

    public Home(JSONObject home) {
        try {
            this.bedrooms = home.getDouble("bedrooms");
            this.squareFootage = home.getDouble("squareFootage");
            this.bathrooms = home.getDouble("bathrooms");
        } catch(JSONException exception) {
            Log.e(TAG, exception.getMessage());
        }
    }
}
