package com.schmidthappens.markd.data_objects;

/**
 * Created by Josh on 3/6/2017.
 */

public enum SubPanelAmperage implements PanelAmperage {
    OneHundred {
        @Override
        public String toString() {
            return "100A";
        }
    },
    OneHundredTwentyFive {
        @Override
        public String toString() {
            return "200A";
        }
    },
    OneHundredFifty {
        @Override
        public String toString() {
            return "400A";
        }
    },
    TwoHundred {
        @Override
        public String toString() {
            return "600A";
        }
    };

    // Returns the number of possible MainPanelAmperages
    public int count() {
            return BreakerAmperage.values().length;
        }

}
