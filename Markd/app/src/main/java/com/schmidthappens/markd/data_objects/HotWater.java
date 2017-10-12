package com.schmidthappens.markd.data_objects;

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;
import com.schmidthappens.markd.utilities.StringUtilities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshua.schmidtibm.com on 9/23/17.
 */

@IgnoreExtraProperties
public class HotWater extends AbstractAppliance {
    public HotWater() {
        // Default constructor required for calls to DataSnapshot.getValue(HotWater.class)
        super();
    }

    public HotWater(String manufacturer, String model, String installDate, Integer lifeSpan, String units) {
        super(manufacturer, model, installDate, lifeSpan, units);
    }

}
