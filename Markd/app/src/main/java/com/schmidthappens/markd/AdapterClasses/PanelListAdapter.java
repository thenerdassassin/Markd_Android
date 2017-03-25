package com.schmidthappens.markd.AdapterClasses;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.Panel;

import java.util.List;

/**
 * Created by Josh on 3/24/2017.
 */

public class PanelListAdapter extends ArrayAdapter<Panel> {

    public PanelListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public PanelListAdapter(Context context, int resource, List<Panel> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.panel_list_row, null);
        }

        Panel p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.panel_description);
            TextView tt2 = (TextView) v.findViewById(R.id.panel_amperage);
            TextView tt3 = (TextView) v.findViewById(R.id.panel_install_date);

            if (tt1 != null) {
                tt1.setText(p.getPanelDescription());
            }

            if (tt2 != null) {
                tt2.setText(p.getAmperage().toString());
            }

            if (tt3 != null) {
                tt3.setText(p.getInstallDate());
            }
        }

        return v;
    }
}
