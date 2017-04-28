package com.schmidthappens.markd.ViewInitializers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.schmidthappens.markd.R;

/**
 * Created by Josh on 4/28/2017.
 */

public class ContractorFooterViewInitializer {

    public static View createFooterView(Context ctx) {
        LayoutInflater vi;
        vi = LayoutInflater.from(ctx);
        View v = vi.inflate(R.layout.footer_view, null);
        return v;
    }

}
