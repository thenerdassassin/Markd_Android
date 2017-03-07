package com.schmidthappens.markd.data_objects;

/**
 * Created by Josh on 3/6/2017.
 */

public enum MainPanelAmperage implements PanelAmperage {
    OneHundred {
        @Override
        public String toString() {
            return "100A";
        }
    },
    TwoHundred {
        @Override
        public String toString() {
            return "200A";
        }
    },
    FourHundred {
        @Override
        public String toString() {
            return "400A";
        }
    },
    SixHundred {
        @Override
        public String toString() {
            return "600A";
        }
    },
    EightHundred {
        @Override
        public String toString() {
            return "800A";
        }
    },
    OneThousand {
        @Override
        public String toString() {
            return "1000A";
        }
    },
    OneThousandTwoHundred {
        @Override
        public String toString() {
            return "1200A";
        }
    };

    // Returns the number of possible MainPanelAmperages
    public int count() {
        return BreakerAmperage.values().length;
    }
}
