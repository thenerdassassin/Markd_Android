package com.schmidthappens.markd.data_objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josh on 6/1/2017.
 */

public class TempLandscapingData {
    private static final TempLandscapingData myData = new TempLandscapingData();
    private List<LandscapingSeason> seasonList;

    private TempLandscapingData() {
        seasonList = new ArrayList<LandscapingSeason>();
        seasonList.add(new LandscapingSeason("Summer", 2016, 4.5, 3, "Lawn looked good in the backyard but was brown towards the end of the season in the front yard."));
        seasonList.add(new LandscapingSeason("Summer", 2015, 5.3, 5, "Excellent!"));
    }

    public static TempLandscapingData getInstance() {
        return myData;
    }

    public List<LandscapingSeason> getHistory() {
        return seasonList;
    }
}
