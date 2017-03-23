package com.schmidthappens.markd.data_objects;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Josh on 3/19/2017.
 */

public  class TempPanelData {
    private static final TempPanelData myData = new TempPanelData();
    private Panel[] panels;
    public int currentPanel;

    private TempPanelData() {
        List<Breaker> breakerList = new LinkedList<Breaker>();
        breakerList.add(new Breaker(1, "Master Bedroom Receptacles"));
        breakerList.add(new Breaker(2, "Master Bedroom Lighting"));
        breakerList.add(new Breaker(3, "Master Bathroom GFCI"));
        breakerList.add(new Breaker(4, "Master Bathroom Floor Heat"));
        breakerList.add(new Breaker(5, "Bedroom Receptacles"));
        breakerList.add(new Breaker(6, "2nd Floor Hallway Lighting"));
        breakerList.add(new Breaker(7, "Washing Machine"));
        breakerList.add(new Breaker(8, "Dryer"));
        breakerList.add(new Breaker(9, "Hot water Heater"));
        breakerList.add(new Breaker(10, "Well pump"));
        breakerList.add(new Breaker(11, "Refrigerator"));
        breakerList.add(new Breaker(12, "Microwave"));
        breakerList.add(new Breaker(13, "Oven"));
        breakerList.add(new Breaker(14, "Kitchen Receptacles"));
        breakerList.add(new Breaker(15, "Kitchen Island Receptacles"));
        breakerList.add(new Breaker(16, "Kitchen Lighting"));
        breakerList.add(new Breaker(17, "Spot Lights"));
        breakerList.add(new Breaker(18, "Garbage Disposal"));
        breakerList.add(new Breaker(19, "Dishwasher"));
        breakerList.add(new Breaker(20, "Kitchen Hood"));
        breakerList.add(new Breaker(21, "Dining Room Receptacles"));
        breakerList.add(new Breaker(22, "Dining Room Lighting"));
        breakerList.add(new Breaker(23, "Living Room Receptacles"));
        breakerList.add(new Breaker(24, "Family Room Lighting"));
        breakerList.add(new Breaker(25, "Foyer Receptacles"));
        breakerList.add(new Breaker(26, "Foyer Lighting"));
        breakerList.add(new Breaker(27, "Furnace"));
        breakerList.add(new Breaker(28, "Air Compressor"));
        breakerList.add(new Breaker(29, "Air Handler"));
        breakerList.add(new Breaker(30, "Central Vacuum"));
        breakerList.add(new Breaker(31, "Sump Pump"));
        breakerList.add(new Breaker(32, "Basement Lighting"));
        breakerList.add(new Breaker(33, "Exterior Lighting"));
        breakerList.add(new Breaker(34, "Landscape Lighting"));
        breakerList.add(new Breaker(35, "Garage Door Receptacles"));

        List<Breaker> breakerList2 = new LinkedList<Breaker>();
        breakerList2.add(new Breaker(1, "Master Bedroom Receptacles"));
        breakerList2.add(new Breaker(2, "Master Bedroom Lighting"));
        breakerList2.add(new Breaker(3, "Master Bathroom GFCI"));
        breakerList2.add(new Breaker(4, "Master Bathroom Floor Heat"));
        breakerList2.add(new Breaker(5, "Bedroom Receptacles"));
        breakerList2.add(new Breaker(6, "2nd Floor Hallway Lighting"));
        breakerList2.add(new Breaker(7, "Washing Machine"));
        breakerList2.add(new Breaker(8, "Dryer"));
        breakerList2.add(new Breaker(9, "Hot water Heater"));
        breakerList2.add(new Breaker(10, "Well pump"));
        breakerList2.add(new Breaker(11, "Refrigerator"));
        breakerList2.add(new Breaker(12, "Microwave"));

        this.panels = new Panel[2];
        this.panels[0] = new Panel(breakerList);
        this.panels[1] = new Panel(false, SubPanelAmperage.OneHundredTwentyFive, breakerList2);
    }

    public static TempPanelData getInstance() {
        return myData;
    }

    public Panel getPanel(int currentPanel) {
        return panels[currentPanel];
    }

    public Panel updatePanel(Panel newPanel) {
        this.panels[currentPanel] = newPanel;
        return this.panels[currentPanel];
    }

    public Panel updateBreaker(int breakerNumber, Breaker newBreaker) {
        this.panels[currentPanel].editBreaker(breakerNumber, newBreaker);
        return this.panels[currentPanel];
    }

    public Panel addBreaker(Breaker newBreaker) {
        this.panels[currentPanel].addBreaker(newBreaker);
        return this.panels[currentPanel];
    }

    public Panel updatePanel(boolean isMainPanel, PanelAmperage panelAmperage, PanelManufacturer manufacturer) {
        this.panels[currentPanel].updatePanel(isMainPanel, panelAmperage, manufacturer);
        return this.panels[currentPanel];
    }

    public int count() {
        return this.panels.length;
    }
}
