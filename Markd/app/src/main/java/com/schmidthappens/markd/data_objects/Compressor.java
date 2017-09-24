package com.schmidthappens.markd.data_objects;

import org.json.JSONObject;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public class Compressor extends AbstractAppliance {
    public Compressor(String manufacturer, String model, String installDate, Integer lifeSpan, String units) {
        super(manufacturer, model, installDate, lifeSpan, units);
    }
    Compressor(Compressor oldCompressor) {
        super(oldCompressor);

    }
    Compressor(JSONObject compressor){
        super(compressor);
    }
}
