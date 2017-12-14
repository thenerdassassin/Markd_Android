package com.schmidthappens.markd.data_objects;

import android.support.annotation.StringDef;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Josh on 3/6/2017.
 * Data Object for Breaker
 */
@IgnoreExtraProperties
public class Breaker {
    private int number;
    private String breakerDescription;
    private @BreakerAmperage String amperage;
    private @BreakerType String breakerType;

    // Mark:- Constructors
    public Breaker(int number, String breakerDescription, @BreakerAmperage String amperage, @BreakerType String breakerType) {
        this.number = number;
        this.breakerDescription = breakerDescription;
        this.amperage = amperage;
        this.breakerType = breakerType;
    }
    public Breaker(int number, String breaker_description, @BreakerType String breakerType) {
        this(number, breaker_description, Breaker.TWENTY_AMP, breakerType);
    }
    public Breaker(int number, String breaker_description) {
        this(number, breaker_description, Breaker.TWENTY_AMP, Breaker.SinglePole);
    }
    public Breaker() {
        // Default constructor required for calls to DataSnapshot.getValue(Breaker.class)
    }

    // Mark:- Getters/Setters
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public String getBreakerDescription() {
        return breakerDescription;
    }
    public void setBreakerDescription(String breakerDescription) {
        this.breakerDescription = breakerDescription;
    }
    public @BreakerAmperage String getAmperage() {
        return amperage;
    }
    public void setAmperage(@BreakerAmperage String amperage) {
        this.amperage = amperage;
    }
    public @BreakerType String getBreakerType() {
        return breakerType;
    }
    public void setBreakerType(@BreakerType String breakerType) {
        this.breakerType = breakerType;
    }

    // Auto Generated equals/hashcode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Breaker breaker = (Breaker) o;

        if (number != breaker.number) return false;
        if (!breakerDescription.equals(breaker.breakerDescription)) return false;
        if (!amperage.equals(breaker.amperage)) return false;
        return breakerType.equals(breaker.breakerType);

    }
    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + breakerDescription.hashCode();
        result = 31 * result + amperage.hashCode();
        result = 31 * result + breakerType.hashCode();
        return result;
    }

    //BreakerType Constants
    public static final String SinglePole = "Single-Pole";
    public static final String DoublePole = "Double-Pole";
    public static final String DoublePoleBottom = "Double-Pole Bottom";

    private final static int BREAKER_TYPE_COUNT = 3;
    private final static String[] breakerTypeValues = {
            SinglePole, DoublePole
    };
    @Exclude
    public static String[] getBreakerTypeValues() {
        return breakerTypeValues;
    }
    @Exclude
    public static int getBreakerTypeCount() {
        return BREAKER_TYPE_COUNT;
    }
    @StringDef({SinglePole, DoublePole, DoublePoleBottom})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BreakerType {}

    //BreakerAmperage Constants
    public static final String FIFTEEN_AMP = "15A";
    public static final String TWENTY_AMP = "20A";
    public static final String THIRTY_AMP = "30A";
    public static final String FOURTY_AMP = "40A";
    public static final String FIFTY_AMP = "50A";
    public static final String SIXTY_AMP = "60A";
    public static final String SEVENTY_AMP = "70A";
    public static final String EIGHTY_AMP = "80A";
    public static final String ONEHUNDRED_AMP = "100A";
    public static final String ONEHUNDREDTWENTYFIVE_AMP = "125A";
    public static final String ONEHUNDREDFIFTY_AMP = "150A";
    public static final String TWOHUNDRED_AMP = "200A";

    private final static String[] amperageValues = {
            FIFTEEN_AMP, TWENTY_AMP, THIRTY_AMP, FOURTY_AMP, FIFTY_AMP, SIXTY_AMP, SEVENTY_AMP, EIGHTY_AMP, ONEHUNDRED_AMP, ONEHUNDREDTWENTYFIVE_AMP, ONEHUNDREDFIFTY_AMP, TWOHUNDRED_AMP
    };

    @Exclude
    public static String[] getAmperageValues() {
        return amperageValues;
    }
    @Exclude
    public static int getAmperageTypeCount() {
        return amperageValues.length;
    }
    @StringDef({FIFTEEN_AMP, TWENTY_AMP, THIRTY_AMP, FOURTY_AMP, FIFTY_AMP, SIXTY_AMP, SEVENTY_AMP, EIGHTY_AMP, ONEHUNDRED_AMP, ONEHUNDREDTWENTYFIVE_AMP, ONEHUNDREDFIFTY_AMP, TWOHUNDRED_AMP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BreakerAmperage {}
}
