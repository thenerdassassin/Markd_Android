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
            return "125A";
        }
    },
    OneHundredFifty {
        @Override
        public String toString() {
            return "150A";
        }
    },
    TwoHundred {
        @Override
        public String toString() {
            return "200A";
        }
    };

    // Returns the number of possible SubPanelAmperages
    public int count() {
            return SubPanelAmperage.values().length;
        }

    // String array of Values
    public static String[] getValues() {
        String[] tmp = new String[TwoHundred.count()];
        for(SubPanelAmperage amp: SubPanelAmperage.values())
            tmp[amp.ordinal()] = amp.toString();
        return tmp;
    }

    public static SubPanelAmperage fromString(String text) {
        for (SubPanelAmperage b : SubPanelAmperage.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
