package com.schmidthappens.markd.data_objects;

/**
 * Created by Josh on 3/6/2017.
 * Amperage Values of a Breaker
 */

public enum BreakerAmperage {
    FIFTEEN {
        public String toString() {
            return "15A";
        }
    },
    TWENTY{
        public String toString() {
            return "20A";
        }
    },
    THIRTY{
        public String toString() {
            return "30A";
        }
    },
    FOURTY{
        public String toString() {
            return "40A";
        }
    },
    FIFTY{
        public String toString() {
            return "50A";
        }
    },
    SIXTY{
        public String toString() {
            return "60A";
        }
    },
    SEVENTY{
        public String toString() {
            return "70A";
        }
    },
    EIGHTY{
        public String toString() {
            return "80A";
        }
    },
    ONEHUNDRED{
        public String toString() {
            return "100A";
        }
    },
    ONEHUNDREDTWENTYFIVE{
        public String toString() {
            return "125A";
        }
    },
    ONEHUNDREDFIFTY{
        public String toString() {
            return "150A";
        }
    },
    TWOHUNDRED{
        public String toString() {
            return "200A";
        }
    };

    // Returns the number of possible BreakerAmperages
    public static int count() {
        return BreakerAmperage.values().length;
    }
}
