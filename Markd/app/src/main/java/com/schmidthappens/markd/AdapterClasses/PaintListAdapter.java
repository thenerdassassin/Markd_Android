package com.schmidthappens.markd.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.PaintObject;

import java.util.List;

/**
 * Created by Josh on 5/29/2017.
 */

public class PaintListAdapter extends ArrayAdapter<PaintObject> {

    public PaintListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public PaintListAdapter(Context context, int resource, List<PaintObject> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View panelListView = convertView;

        if (panelListView == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            panelListView = inflater.inflate(R.layout.paint_list_row, null);
        }

        PaintObject paintObject = getItem(position);

        if (paintObject != null) {
            TextView paintLocationView = (TextView) panelListView.findViewById(R.id.paint_location);
            TextView paintManufacturerView = (TextView) panelListView.findViewById(R.id.paint_manufacturer);
            TextView paintColorView = (TextView) panelListView.findViewById(R.id.paint_color);

            if (paintLocationView != null) {
                paintLocationView.setText(paintObject.getLocation());
            }

            if (paintManufacturerView != null) {
                paintManufacturerView.setText(paintObject.getManufacturer());
            }

            if (paintColorView != null) {
                paintColorView.setText(paintObject.getColor());
            }
        }

        return panelListView;
    }
}
