package com.schmidthappens.markd.data_objects;

/**
 * Created by Josh on 3/6/2017.
 */

public enum BreakerType {
    SinglePole{
        public String toString() {
            return "Single-Pole";
        }
    },
    DoublePole {
        public String toString() {
            return "Double-Pole";
        }
    },
    DoublePoleBottom {
        public String toString() {
            return "Double-Pole";
        }
    };

    // Returns the number of possible BreakerAmperages
    public static int count() {
        return BreakerType.values().length;
    }
}
