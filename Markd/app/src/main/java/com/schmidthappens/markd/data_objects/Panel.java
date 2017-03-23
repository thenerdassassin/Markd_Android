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

    //Helper functions
    public int breakerCount(){
        return breakerList.size();
    }
    public Panel deleteBreaker(int breakerNumber) {
        int breakerIndex = breakerNumber-1;

        //Error check
        if(breakerNumber > breakerList.size()) {
            return this;
        }
        Breaker lastBreaker = breakerList.get(breakerList.size()-1);
        Breaker breakerToDelete = breakerList.get(breakerIndex);

        if(breakerToDelete.getBreakerType().equals(BreakerType.DoublePoleBottom)) {
            Breaker previousBreaker = breakerList.get(breakerIndex-2);
            previousBreaker.setBreakerType(BreakerType.SinglePole);
        }
        else if(breakerToDelete.getBreakerType().equals(BreakerType.DoublePole)) {
            breakerToDelete.setBreakerType(BreakerType.SinglePole);
            Breaker nextBreaker = breakerList.get(breakerIndex+2);
            nextBreaker.setBreakerType(BreakerType.SinglePole);
        }
        if(breakerToDelete.equals(lastBreaker)) {
            breakerList.remove(breakerToDelete);
        }
        else {
            //Reset to default values
            breakerToDelete.setBreakerDescription("");
            breakerToDelete.setBreakerType(BreakerType.SinglePole);
            breakerToDelete.setAmperage(BreakerAmperage.TWENTY);
        }
        return this;
    }
    public Panel editBreaker(int breakerNumber, Breaker updatedBreaker) {
        int breakerIndex = breakerNumber-1;
        this.breakerList.set(breakerIndex, updatedBreaker);
        // Updated Breaker is top of Double-Pole
        if(updatedBreaker.getBreakerType().equals(BreakerType.DoublePole)) {
            // Add Breakers to Panel if needed
            if(breakerIndex + 2  >= this.breakerCount()) {
                while(breakerIndex+2 > this.breakerCount()) {
                    this.breakerList.add(this.breakerCount(), new Breaker(this.breakerCount()+1, ""));
                }
                this.breakerList.add(this.breakerCount(), new Breaker(this.breakerCount()+1, updatedBreaker.getBreakerDescription(), updatedBreaker.getAmperage(), BreakerType.DoublePoleBottom));
            }
            // Simply update the bottom of Double Pole
            else {
                Breaker bottomDoublePole = this.breakerList.get(breakerIndex+2);
                bottomDoublePole.setBreakerType(BreakerType.DoublePoleBottom);
                bottomDoublePole.setBreakerDescription(updatedBreaker.getBreakerDescription());
                bottomDoublePole.setAmperage(updatedBreaker.getAmperage());
            }
        }
        // Updated Breaker is bottom of Double-Pole
        else if(updatedBreaker.getBreakerType().equals(BreakerType.DoublePoleBottom)) {
            //Copy Changes to Upper Part of Double-Pole
            Breaker topDoublePole = this.breakerList.get(breakerIndex-2);
            topDoublePole.setBreakerDescription(updatedBreaker.getBreakerDescription());
            topDoublePole.setAmperage(updatedBreaker.getAmperage());
        }
        // Updated Breaker is Single-Pole
        else {
            //Set above breaker to single pole
            if(breakerIndex > 1) {
                Breaker aboveBreaker = this.getBreakerList().get(breakerIndex-2);
                if(aboveBreaker.getBreakerType().equals(BreakerType.DoublePole)) {
                    aboveBreaker.setBreakerType(BreakerType.SinglePole);
                }
            }
            //Set below breaker to single pole
            if(breakerIndex+2 < this.breakerCount()) {
                Breaker belowBreaker = this.getBreakerList().get(breakerIndex+2);
                if(belowBreaker.getBreakerType().equals(BreakerType.DoublePoleBottom)) {
                    belowBreaker.setBreakerType(BreakerType.SinglePole);
                }
            }
        }

        return this;
    }
    public Panel addBreaker(Breaker newBreaker) {
        this.breakerList.add(newBreaker);

        if(newBreaker.getBreakerType().equals(BreakerType.DoublePole)) {
            this.breakerList.add(new Breaker(this.breakerCount()+1, ""));
            this.breakerList.add(new Breaker(this.breakerCount()+1, newBreaker.getBreakerDescription(), newBreaker.getAmperage(), BreakerType.DoublePoleBottom));
        }
        return this;
    }
    public Panel updatePanel(boolean isMainPanel, PanelAmperage amperage, PanelManufacturer manufacturer) {
        this.isMainPanel = isMainPanel;
        this.amperage = amperage;
        this.manufacturer = manufacturer;
        return this;
    }
}
