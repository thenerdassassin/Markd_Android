package com.schmidthappens.markd.data_objects;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */
@IgnoreExtraProperties
public class Boiler extends AbstractAppliance {

    public Boiler(String manufacturer, String model, String installDate, Integer lifeSpan, String units) {
        super(manufacturer, model, installDate, lifeSpan, units);
    }

    public Boiler() {
        // Default constructor required for calls to DataSnapshot.getValue(Boiler.class)
    }
}
