package com.schmidthappens.markd.data_objects;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Josh on 3/6/2017.
 * Data Object for Breaker
 */
@IgnoreExtraProperties
public class Breaker {
    private int number;
    private String breakerDescription;
    private BreakerAmperage amperage;
    private BreakerType breakerType;

    // Mark:- Constructors
    public Breaker(int number, String breakerDescription, BreakerAmperage amperage, BreakerType breakerType) {
        this.number = number;
        this.breakerDescription = breakerDescription;
        this.amperage = amperage;
        this.breakerType = breakerType;
    }
    public Breaker(int number, String breaker_description, BreakerType breakerType) {
        this(number, breaker_description, BreakerAmperage.TWENTY ,breakerType);
    }
    public Breaker(int number, String breaker_description) {
        this(number, breaker_description, BreakerAmperage.TWENTY, BreakerType.SinglePole);
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

    public BreakerAmperage getAmperage() {
        return amperage;
    }
    public void setAmperage(BreakerAmperage amperage) {
        this.amperage = amperage;
    }

    public BreakerType getBreakerType() {
        return breakerType;
    }
    public void setBreakerType(BreakerType breakerType) {
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
        if (amperage != breaker.amperage) return false;
        return breakerType == breaker.breakerType;

    }
    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + breakerDescription.hashCode();
        result = 31 * result + amperage.hashCode();
        result = 31 * result + breakerType.hashCode();
        return result;
    }
}
