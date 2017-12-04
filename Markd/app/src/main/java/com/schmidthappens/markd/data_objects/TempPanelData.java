package com.schmidthappens.markd.data_objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Josh on 3/19/2017.
 */

//TODO: Remove
public class TempPanelData {
    private static final TempPanelData myData = new TempPanelData();
    private List<Panel> panels;
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

        this.panels = new ArrayList<Panel>();
        this.panels.add(0, new Panel(true, Panel.TwoHundred, breakerList));
        this.panels.get(0).setPanelDescription("Attic Panel");
        this.panels.get(0).setInstallDate("11", "07", "16");

        this.panels.add(1, new Panel(false, Panel.OneHundredTwentyFive, breakerList2));
        this.panels.get(1).setPanelDescription("Basement Panel");
        this.panels.get(1).setInstallDate("01", "11", "17");
    }

    public static TempPanelData getInstance() {
        return myData;
    }
    public List<Panel> getPanels() {
        return new ArrayList<Panel>(panels);
    }
    public Panel getPanel(int panelId) {
        return panels.get(panelId);
    }

    public List<Panel> addPanel(Panel newPanel) {
        this.panels.add(newPanel);
        this.currentPanel = this.count()-1;
        return this.panels;
    }
    public Panel updatePanel(Panel newPanel) {
        this.panels.set(currentPanel, newPanel);
        return this.panels.get(currentPanel);
    }
    public Panel updateBreaker(int breakerNumber, Breaker newBreaker) {
        this.panels.get(currentPanel).editBreaker(breakerNumber, newBreaker);
        return this.panels.get(currentPanel);
    }
    public Panel addBreaker(Breaker newBreaker) {
        this.panels.get(currentPanel).addBreaker(newBreaker);
        return this.panels.get(currentPanel);
    }
    public Panel updatePanel(String panelDescription, int numberOfBreakers, boolean isMainPanel, String panelInstallDate, @Panel.PanelAmperage String panelAmperage, @Panel.PanelManufacturer String manufacturer) {
        this.panels.get(currentPanel).updatePanel(panelDescription, numberOfBreakers, isMainPanel, panelInstallDate, panelAmperage, manufacturer);
        return this.panels.get(currentPanel);
    }
    public boolean deletePanel(int position) {
        this.panels.remove(position);
        return true;
    }

    public int count() {
        return this.panels.size();
    }
}
