package com.schmidthappens.markd.data_objects;

import org.json.JSONObject;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public class AirHandler extends AbstractAppliance {
    public AirHandler(String manufacturer, String model, String installDate, Integer lifeSpan, String units) {
        super(manufacturer, model, installDate, lifeSpan, units);
    }
    AirHandler(AirHandler oldAirHandler) {
        super(oldAirHandler);
    }
    AirHandler(JSONObject airHandler){
        super(airHandler);
    }
}
