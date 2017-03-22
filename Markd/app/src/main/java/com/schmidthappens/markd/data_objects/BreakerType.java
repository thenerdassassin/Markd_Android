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
            return "Double-Pole Bottom";
        }
    };

    // Returns the number of possible BreakerAmperages
    public static int count() {
        return BreakerType.values().length;
    }

    public static String[] getValues() {
        String[] tmp = {"Single-Pole", "Double-Pole"};
        return tmp;
    }

    public static BreakerType fromString(String text) {
        for (BreakerType b : BreakerType.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
