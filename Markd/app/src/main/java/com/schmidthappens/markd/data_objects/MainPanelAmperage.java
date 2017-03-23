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
        return MainPanelAmperage.values().length;
    }

    // String array of Values
    public static String[] getValues() {
        String[] tmp = new String[OneThousandTwoHundred.count()];
        for(MainPanelAmperage amp: MainPanelAmperage.values())
            tmp[amp.ordinal()] = amp.toString();
        return tmp;
    }

    public static MainPanelAmperage fromString(String text) {
        for (MainPanelAmperage b : MainPanelAmperage.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
