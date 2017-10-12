package com.schmidthappens.markd.data_objects;

import org.json.JSONObject;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public class Compressor extends AbstractAppliance {
    public Compressor(String manufacturer, String model, String installDate, Integer lifeSpan, String units) {
        super(manufacturer, model, installDate, lifeSpan, units);
    }

    public Compressor() {
        // Default constructor required for calls to DataSnapshot.getValue(Compressor.class)
    }
}
