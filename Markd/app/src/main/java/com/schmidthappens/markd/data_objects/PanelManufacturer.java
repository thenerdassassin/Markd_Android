package com.schmidthappens.markd.data_objects;

/**
 * Created by Josh on 3/6/2017.
 */

public enum PanelManufacturer {
    BRYANT {
        @Override
        public String toString() {
            return "Bryant";
        }
    },
    GENERAL_ELECTRIC {
        @Override
        public String toString() {
            return "General Electric";
        }
    },
    MURRY {
        @Override
        public String toString() {
            return "Murry";
        }
    },
    SQUARE_D_HOMELINE {
        @Override
        public String toString() {
            return "Square D Homeline";
        }
    },
    SQUARE_D_QO_SERIES {
        @Override
        public String toString() {
            return "Square D QO";
        }
    },
    SIEMENS_ITE {
        @Override
        public String toString() {
            return "Siemens ITE";
        }
    },
    WADSWORTH {
        @Override
        public String toString() {
            return "Wadsworth";
        }
    },
    WESTINGHOUSE {
        @Override
        public String toString() {
            return "Westinghouse";
        }
    },
    UNKNOWN {
        @Override
        public String toString() {
            return "Unknown";
        }
    };

    public static int count() { return PanelManufacturer.values().length;}

}
