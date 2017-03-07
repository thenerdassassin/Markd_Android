package com.schmidthappens.markd.data_objects;

/**
 * Created by Josh on 3/6/2017.
 * Data Object for Breaker
 */

public class Breaker {
    private int number;
    private String breaker_description;
    private BreakerAmperage amperage;
    private BreakerType breakerType;

    // Mark:- Constructors
    public Breaker(int number, String breaker_description, BreakerAmperage amperage, BreakerType breakerType) {
        this.number = number;
        this.breaker_description = breaker_description;
        this.amperage = amperage;
        this.breakerType = breakerType;
    }

    public Breaker(int number, String breaker_description, BreakerType breakerType) {
        this(number, breaker_description, BreakerAmperage.TWENTY ,breakerType);
    }

    public Breaker(int number, String breaker_description) {
        this(number, breaker_description, BreakerAmperage.TWENTY, BreakerType.SinglePole);
    }
    // Mark:- Getters/Setters

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getBreaker_description() {
        return breaker_description;
    }

    public void setBreaker_description(String breaker_description) {
        this.breaker_description = breaker_description;
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
        if (!breaker_description.equals(breaker.breaker_description)) return false;
        if (amperage != breaker.amperage) return false;
        return breakerType == breaker.breakerType;

    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + breaker_description.hashCode();
        result = 31 * result + amperage.hashCode();
        result = 31 * result + breakerType.hashCode();
        return result;
    }
}
