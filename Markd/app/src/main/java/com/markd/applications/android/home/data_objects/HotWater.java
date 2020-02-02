package com.markd.applications.android.home.data_objects;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Josh Schmidt on 9/23/17.
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
