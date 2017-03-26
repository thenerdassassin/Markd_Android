package com.schmidthappens.markd.AdapterClasses;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.MenuItem;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Josh on 3/25/2017.
 */

public class MenuDrawerListAdapter extends ArrayAdapter<MenuItem> {
    public MenuDrawerListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public MenuDrawerListAdapter(Context context, int resource, List<MenuItem> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.drawer_list_item, null);
        }

        MenuItem item = getItem(position);

        if (item != null) {
            ImageView tt1 = (ImageView) v.findViewById(R.id.list_icon);
            TextView tt2 = (TextView) v.findViewById(R.id.list_title);

            try {
                Class res = R.drawable.class;
                Field field = res.getField(item.menuIcon);
                int drawableId = field.getInt(null);
                if (tt1 != null) {
                    tt1.setImageResource(drawableId);
                }
            }
            catch (Exception e) {
                Log.e("MyTag", "Failure to get drawable id.", e);
            }

            if (tt2 != null) {
                tt2.setText(item.menuTitle);
            }
        }

        return v;
    }
}
