package com.schmidthappens.markd.data_objects;

import android.util.Log;

import com.schmidthappens.markd.utilities.StringUtilities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

public class HotWater extends AbstractAppliance{
    public HotWater(String manufacturer, String model, String installDate, Integer lifeSpan, String units) {
        super(manufacturer, model, installDate, lifeSpan, units);
    }
    HotWater(HotWater oldHotWater) {
        super(oldHotWater);

    }
    HotWater(JSONObject hotWater) throws JSONException{
        super(hotWater);
    }
}
