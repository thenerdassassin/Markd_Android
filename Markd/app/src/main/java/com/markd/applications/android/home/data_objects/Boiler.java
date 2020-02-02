package com.markd.applications.android.home.data_objects;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Josh Schmidt on 9/23/17.
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
