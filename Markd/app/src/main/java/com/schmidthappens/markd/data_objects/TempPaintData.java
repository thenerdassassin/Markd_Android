package com.schmidthappens.markd.data_objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josh on 3/19/2017.
 */

public class TempPaintData {
    private static final TempPaintData myData = new TempPaintData();
    private List<PaintSurface> interiorPaintSurfaceList;
    private List<PaintSurface> exteriorPaintSurfaceList;

    private TempPaintData() {
        interiorPaintSurfaceList = new ArrayList<PaintSurface>();
        interiorPaintSurfaceList.add(new PaintSurface("Living Room", "Sherwin Williams", "Light Blue", 8, 8, 17));
        interiorPaintSurfaceList.add(new PaintSurface("Master bedroom", "Sherwin Williams", "Yellow", 8, 1, 17));
        interiorPaintSurfaceList.add(new PaintSurface("Bathroom", "Sherwin Williams", "Loch Blue", 1, 5, 14));
        interiorPaintSurfaceList.add(new PaintSurface("Dining Room", "Sherwin Williams", "Grape Harvest", 12, 6, 13));
        interiorPaintSurfaceList.add(new PaintSurface("Kitchen", "Sherwin Williams", "Decor White", 12, 6, 13));

        exteriorPaintSurfaceList = new ArrayList<PaintSurface>();
        exteriorPaintSurfaceList.add(new PaintSurface("Siding", "Behr", "Cream", 2, 4, 2017));
        exteriorPaintSurfaceList.add(new PaintSurface("Garage", "Behr", "Translucent Silk", 6, 24, 2006));
    }

    public static TempPaintData getInstance() {
        return myData;
    }

    public List<PaintSurface> getInteriorPaints() {
        return interiorPaintSurfaceList;
    }
    public List<PaintSurface> getExteriorPaints(){
        return exteriorPaintSurfaceList;
    }

    public void putExteriorPaint(PaintSurface paintSurface) {
        exteriorPaintSurfaceList.add(paintSurface);
    }

    public void putInteriorPaint(PaintSurface paintSurface) {
        interiorPaintSurfaceList.add(paintSurface);
    }

    public void updatePaint(int position, String location, String brand, String color, boolean isExterior, int month, int day, int year) {
        PaintSurface paintSurface = new PaintSurface(location, brand, color, month, day, year);
        if(isExterior)
            exteriorPaintSurfaceList.set(position, paintSurface);
        else
            interiorPaintSurfaceList.set(position, paintSurface);
    }


    public void deletePaintSurface(int position, boolean isExterior) {
        if(isExterior) {
            this.exteriorPaintSurfaceList.remove(position);
        } else {
            this.interiorPaintSurfaceList.remove(position);
        }
    }
}
