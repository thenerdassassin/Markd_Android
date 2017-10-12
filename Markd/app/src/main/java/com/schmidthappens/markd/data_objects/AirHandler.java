package com.schmidthappens.markd.data_objects;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONObject;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */
@IgnoreExtraProperties
public class AirHandler extends AbstractAppliance {

    public AirHandler(String manufacturer, String model, String installDate, Integer lifeSpan, String units) {
        super(manufacturer, model, installDate, lifeSpan, units);
    }
    public AirHandler() {
        // Default constructor required for calls to DataSnapshot.getValue(AirHandler.class)
    }
}
