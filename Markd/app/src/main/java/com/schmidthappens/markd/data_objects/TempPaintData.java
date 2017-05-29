package com.schmidthappens.markd.data_objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josh on 3/19/2017.
 */

public class TempPaintData {
    private static final TempPaintData myData = new TempPaintData();
    private List<PaintObject> paintObjectList;

    private TempPaintData() {
        paintObjectList = new ArrayList<PaintObject>();
        paintObjectList.add(new PaintObject("Living Room", "Sherwin Williams", "Light Blue"));
        paintObjectList.add(new PaintObject("Master bedroom", "Sherwin Williams", "Yellow"));
        paintObjectList.add(new PaintObject("Bathroom", "Sherwin Williams", "Loch Blue"));
        paintObjectList.add(new PaintObject("Dining Room", "Sherwin Williams", "Grape Harvest"));
        paintObjectList.add(new PaintObject("Kitchen", "Sherwin Williams", "Decor White"));
    }

    public static TempPaintData getInstance() {
        return myData;
    }

    public List<PaintObject> getPaints() {
        return paintObjectList;
    }

}
