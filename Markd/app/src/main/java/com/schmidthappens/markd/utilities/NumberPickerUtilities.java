package com.schmidthappens.markd.utilities;

import android.widget.NumberPicker;

/**
 * Created by joshua.schmidtibm.com on 11/22/17.
 */

public class NumberPickerUtilities {
    public static void setPicker(NumberPicker picker, String value, String[] values) {
        for(int i = 0; i < values.length; i++) {
            if(values[i].equalsIgnoreCase(value)) {
                picker.setValue(i);
                break;
            }
        }
    }
}
