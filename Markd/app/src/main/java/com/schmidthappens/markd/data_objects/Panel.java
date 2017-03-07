package com.schmidthappens.markd.data_objects;

import java.util.List;

/**
 * Created by Josh on 3/6/2017.
 */

public class Panel {
    private boolean isMainPanel;
    private PanelAmperage amperage;
    private List<Breaker> breakerList;
    private PanelManufacturer manufacturer;

    // Mark:- Constructors
    public Panel(boolean isMainPanel, PanelAmperage amperage, List<Breaker> breakerList, PanelManufacturer manufacturer) {
        this.isMainPanel = isMainPanel;
        this.amperage = amperage;
        this.breakerList = breakerList;
        this.manufacturer = manufacturer;
    }

    public Panel(boolean isMainPanel, PanelAmperage amperage, List<Breaker> breakerList) {
        this(isMainPanel, amperage, breakerList, PanelManufacturer.UNKNOWN);
    }

    public Panel(PanelAmperage amperage, List<Breaker> breakerList) {
        this(true, amperage, breakerList, PanelManufacturer.UNKNOWN);
    }

    public Panel(List<Breaker> breakerList) {
        this(true, MainPanelAmperage.OneThousand, breakerList, PanelManufacturer.UNKNOWN);
    }

    // Mark:- Getters/Setters
    public boolean getIsMainPanel() {
        return isMainPanel;
    }

    public void setIsMainPanel(boolean isMainPanel) {
        this.isMainPanel = isMainPanel;
    }

    public PanelAmperage getAmperage() {
        return amperage;
    }

    public void setAmperage(PanelAmperage amperage) {
        this.amperage = amperage;
    }

    public List<Breaker> getBreakerList() {
        return breakerList;
    }

    public void setBreakerList(List<Breaker> breakerList) {
        this.breakerList = breakerList;
    }

    public PanelManufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(PanelManufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    //Generates PanelTitle String
    public String getPanelTitle() {
        String panelTitleString = "";

        if(this.isMainPanel) {
            panelTitleString += "Main Panel ";
        } else {
            panelTitleString += "Sub Panel ";
        }

        panelTitleString += this.amperage.toString();

        return panelTitleString;
    }
}
