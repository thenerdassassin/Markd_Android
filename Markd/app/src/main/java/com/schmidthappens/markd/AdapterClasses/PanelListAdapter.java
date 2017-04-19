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
            TextView panelDescriptionTextView = (TextView) v.findViewById(R.id.panel_description);
            TextView panelAmperageTextView = (TextView) v.findViewById(R.id.panel_amperage);
            TextView panelInstallDateTextView = (TextView) v.findViewById(R.id.panel_install_date);

            if (panelDescriptionTextView != null) {
                panelDescriptionTextView.setText(p.getPanelDescription());
            }

            if (panelAmperageTextView != null) {
                panelAmperageTextView.setText(p.getAmperage().toString());
            }

            if (panelInstallDateTextView != null) {
                panelInstallDateTextView.setText(p.getInstallDate());
            }
        }

        return v;
    }
}
