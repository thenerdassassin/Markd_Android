package com.schmidthappens.markd.data_objects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public class Boiler extends AbstractAppliance {
    //TODO: Implement Boiler
    public Boiler(String manufacturer, String model, String installDate, Integer lifeSpan, String units) {
        super(manufacturer, model, installDate, lifeSpan, units);
    }
    Boiler(Boiler oldBoiler) {
        super(oldBoiler);

    }
    Boiler(JSONObject boiler) throws JSONException {
        super(boiler);
    }
}
