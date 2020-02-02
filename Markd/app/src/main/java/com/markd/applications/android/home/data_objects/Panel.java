package com.markd.applications.android.home.data_objects;

import androidx.annotation.StringDef;
import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josh on 3/6/2017.
 */

@IgnoreExtraProperties
public class Panel {
    @Exclude
    private static final String TAG = "PanelDataObject";
    private boolean isMainPanel;
    private @PanelAmperage String amperage;
    private String panelDescription;
    private String installDate;
    private List<Breaker> breakerList;
    private int numberOfBreakers;
    private @PanelManufacturer String manufacturer;

    // Mark:- Constructors
    public Panel(boolean isMainPanel, @PanelAmperage String amperage, List<Breaker> breakerList, @PanelManufacturer String manufacturer) {
        this.isMainPanel = isMainPanel;
        this.amperage = amperage;
        this.breakerList = breakerList;
        this.manufacturer = manufacturer;
        this.numberOfBreakers = breakerList.size();
    }
    public Panel(boolean isMainPanel, @PanelAmperage String amperage, List<Breaker> breakerList) {
        this(isMainPanel, amperage, breakerList, Panel.OTHER);
    }
    public Panel(@PanelAmperage String amperage, List<Breaker> breakerList) {
        this(true, amperage, breakerList, Panel.OTHER);
    }
    public Panel(int panelId, List<Breaker> breakerList) {
        this(true, Panel.OneThousand, breakerList, Panel.OTHER);
    }
    public Panel() {
        // Default constructor required for calls to DataSnapshot.getValue(Panel.class)
    }

    // Mark:- Getters/Setters
    public boolean getIsMainPanel() {
        return isMainPanel;
    }
    public void setIsMainPanel(boolean isMainPanel) {
        this.isMainPanel = isMainPanel;
    }
    public @PanelAmperage String getAmperage() {
        return amperage;
    }
    public void setAmperage(@PanelAmperage String amperage) {
        this.amperage = amperage;
    }
    public String getPanelDescription() {
        return this.panelDescription;
    }
    public void setPanelDescription(String panelDescription) {
        this.panelDescription = panelDescription;
    }
    public String getInstallDate() {
        return this.installDate;
    }
    public void setInstallDate(String installDate) {
        this.installDate = installDate;
    }
    @Exclude
    public boolean setInstallDate(String month, String day, String year) {
        String newInstallDate = "";
        if(month.length() == 2 && day.length() == 2 && year.length() == 2) {
            try {
                Integer.parseInt(month);
                newInstallDate += month;
                Integer.parseInt(day);
                newInstallDate += "." + day;
                Integer.parseInt(year);
                newInstallDate += "." + year;
            } catch(NumberFormatException e) {
                return false;
            }
            installDate = newInstallDate;

        } else {
            return false;
        }
        return true;
    }
    public List<Breaker> getBreakerList() {
        return breakerList;
    }
    public void setBreakerList(List<Breaker> breakerList) {
        this.breakerList = breakerList;
    }
    public int getNumberOfBreakers() {
        return numberOfBreakers;
    }
    public void setNumberOfBreakers(int numberOfBreakers) {
        this.numberOfBreakers = numberOfBreakers;
        if(breakerList == null) {
            breakerList = new ArrayList<>();
        }
        while(breakerList.size() < numberOfBreakers) {
            breakerList.add(new Breaker(breakerList.size()+1, ""));
        }

        while(breakerList.size() > numberOfBreakers) {
            deleteBreaker(breakerList.size());
        }
    }
    public @PanelManufacturer String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(@PanelManufacturer String manufacturer) {
        this.manufacturer = manufacturer;
    }
    //Generates PanelTitle String
    @Exclude
    public String getPanelTitle() {
        String panelTitleString = "";

        if(isMainPanel) {
            panelTitleString += "Main Panel ";
        } else {
            panelTitleString += "Sub Panel ";
        }

        panelTitleString += this.amperage;

        return panelTitleString;
    }

    //Helper functions
    public int breakerCount(){
        return numberOfBreakers;
    }
    public Panel deleteBreaker(int breakerNumber) {
        int breakerIndex = breakerNumber-1;

        //Error check
        if(breakerNumber > breakerList.size()) {
            return this;
        }
        Breaker lastBreaker = breakerList.get(breakerList.size()-1);
        Breaker breakerToDelete = breakerList.get(breakerIndex);

        if(breakerToDelete.getBreakerType().equals(Breaker.DoublePoleBottom)) {
            Breaker previousBreaker = breakerList.get(breakerIndex-2);
            previousBreaker.setBreakerType(Breaker.SinglePole);
        }
        else if(breakerToDelete.getBreakerType().equals(Breaker.DoublePole)) {
            breakerToDelete.setBreakerType(Breaker.SinglePole);
            Breaker nextBreaker = breakerList.get(breakerIndex+2);
            nextBreaker.setBreakerType(Breaker.SinglePole);
        }
        if(breakerToDelete.equals(lastBreaker)) {
            breakerList.remove(breakerToDelete);
        }
        else {
            //Reset to default values
            breakerToDelete.setBreakerDescription("");
            breakerToDelete.setBreakerType(Breaker.SinglePole);
            breakerToDelete.setAmperage(Breaker.TWENTY_AMP);
        }
        return this;
    }
    public Panel editBreaker(int breakerNumber, Breaker updatedBreaker) {
        Log.d(TAG, "BreakerNumber:" + breakerNumber);
        int breakerIndex = breakerNumber-1;
        this.breakerList.set(breakerIndex, updatedBreaker);
        // Updated Breaker is top of Double-Pole
        if(updatedBreaker.getBreakerType().equals(Breaker.DoublePole)) {
            Log.d(TAG, "Updated Breaker is top of Double Pole");
            // Add Breakers to Panel if needed
            if(breakerIndex+2  >= this.breakerCount()) {
                Log.d(TAG, "Need to add breakers to panel");
                while(breakerIndex+2 > this.breakerCount()) {
                    Log.d(TAG, "Breaker added at:" + (breakerCount()+1));
                    this.breakerList.add(this.breakerCount(), new Breaker(this.breakerCount()+1, ""));
                    numberOfBreakers++;
                }
                Log.d(TAG, "Adding breaker at:" + (breakerCount()+1));
                this.breakerList.add(this.breakerCount(), new Breaker(this.breakerCount()+1, updatedBreaker.getBreakerDescription(), updatedBreaker.getAmperage(), Breaker.DoublePoleBottom));
                numberOfBreakers++;
            }
            // Simply update the bottom of Double Pole
            else {
                Breaker bottomDoublePole = this.breakerList.get(breakerIndex+2);
                bottomDoublePole.setBreakerType(Breaker.DoublePoleBottom);
                bottomDoublePole.setBreakerDescription(updatedBreaker.getBreakerDescription());
                bottomDoublePole.setAmperage(updatedBreaker.getAmperage());
            }
        }
        // Updated Breaker is bottom of Double-Pole
        else if(updatedBreaker.getBreakerType().equals(Breaker.DoublePoleBottom)) {
            Log.d(TAG, "Updated Breaker is bottom of Double Pole");
            //Copy Changes to Upper Part of Double-Pole
            Breaker topDoublePole = this.breakerList.get(breakerIndex-2);
            topDoublePole.setBreakerDescription(updatedBreaker.getBreakerDescription());
            topDoublePole.setAmperage(updatedBreaker.getAmperage());
        }
        // Updated Breaker is Single-Pole
        else {
            Log.d(TAG, "Updated Breaker is top of Single Pole");
            //Set above breaker to single pole
            if(breakerIndex > 1) {
                Breaker aboveBreaker = this.getBreakerList().get(breakerIndex-2);
                if(aboveBreaker.getBreakerType().equals(Breaker.DoublePole)) {
                    aboveBreaker.setBreakerType(Breaker.SinglePole);
                }
            }
            //Set below breaker to single pole
            if(breakerIndex+2 < this.breakerCount()) {
                Breaker belowBreaker = this.getBreakerList().get(breakerIndex+2);
                if(belowBreaker.getBreakerType().equals(Breaker.DoublePoleBottom)) {
                    belowBreaker.setBreakerType(Breaker.SinglePole);
                }
            }
        }
        return this;
    }
    public Panel addBreaker(Breaker newBreaker) {
        this.breakerList.add(newBreaker);
        numberOfBreakers++;

        if(newBreaker.getBreakerType().equals(Breaker.DoublePole)) {
            this.breakerList.add(new Breaker(this.breakerCount()+1, ""));
            numberOfBreakers++;
            this.breakerList.add(new Breaker(this.breakerCount()+1, newBreaker.getBreakerDescription(), newBreaker.getAmperage(), Breaker.DoublePoleBottom));
            numberOfBreakers++;
        }
        return this;
    }
    public Panel updatePanel(String panelDescription, int numberOfBreakers, boolean isMainPanel, String panelInstallDate, @PanelAmperage String amperage, @PanelManufacturer String manufacturer) {
        this.panelDescription = panelDescription;
        this.setNumberOfBreakers(numberOfBreakers);
        this.isMainPanel = isMainPanel;
        this.installDate = panelInstallDate;
        this.amperage = amperage;
        this.manufacturer = manufacturer;
        return this;
    }

    //MARK:- StringDefs
    //PanelAmperageConstants
    public static final String OneHundred = "100A";
    public static final String TwoHundred = "200A";

    //MainPanelAmperage Constants
    public static final String FourHundred = "400A";
    public static final String SixHundred = "600A";
    public static final String EightHundred = "800A";
    public static final String OneThousand = "1000A";
    public static final String OneThousandTwoHundred = "1200A";

    //SubPanelAmperageConstants
    public static final String OneHundredTwentyFive = "125A";
    public static final String OneHundredFifty = "150A";

    private final static String[] mainPanelAmperageValues = {
            OneHundred, TwoHundred, FourHundred, SixHundred, EightHundred, OneThousand, OneThousandTwoHundred
    };
    private final static String[] subPanelAmperageValues = {
            OneHundred, OneHundredTwentyFive, OneHundredFifty, TwoHundred
    };
    @Exclude
    public static String[] getMainPanelAmperageValues() {
        return mainPanelAmperageValues;
    }
    @Exclude
    public static String[] getSubPanelAmperageValues() {
        return subPanelAmperageValues;
    }
    @Exclude
    public static int getMainPanelAmperageCount() {
        return mainPanelAmperageValues.length;
    }
    public static int getSubPanelAmperageCount() {
        return subPanelAmperageValues.length;
    }
    @StringDef({OneHundred, OneHundredTwentyFive, OneHundredFifty, TwoHundred, FourHundred, SixHundred, EightHundred, OneThousand, OneThousandTwoHundred})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PanelAmperage {}

    //PanelManufacturer Constants
    public static final String BRYANT = "Bryant";
    public static final String GENERAL_ELECTRIC = "General Electric";
    public static final String MURRY = "Murry";
    public static final String SQUARE_D_HOMELINE = "Square D Homeline";
    public static final String SQUARE_D_QO_SERIES = "Square D QO";
    public static final String SIEMENS_ITE = "Siemens ITE";
    public static final String WADSWORTH = "Wadsworth";
    public static final String WESTINGHOUSE = "Westinghouse";
    public static final String OTHER = "Other";

    public final static String[] panelManfacturerValues = {
            BRYANT, GENERAL_ELECTRIC, MURRY, SQUARE_D_HOMELINE, SQUARE_D_QO_SERIES, SIEMENS_ITE, WADSWORTH, WESTINGHOUSE, OTHER
    };
    @Exclude
    public static String[] getPanelManufacturers() {
        return panelManfacturerValues;
    }
    @Exclude
    public static int getPanelManufcaturersCount() {
        return panelManfacturerValues.length;
    }
    @StringDef({BRYANT, GENERAL_ELECTRIC, MURRY, SQUARE_D_HOMELINE, SQUARE_D_QO_SERIES, SIEMENS_ITE, WADSWORTH, WESTINGHOUSE, OTHER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PanelManufacturer {}

}
