package com.schmidthappens.markd.AdapterClasses;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.ContractorService;

import java.util.List;

/**
 * Created by Josh on 4/19/2017.
 */

public class ServiceListAdapter extends ArrayAdapter<ContractorService>{
    public ServiceListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ServiceListAdapter(Context context, int resource, List<ContractorService> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.service_list_row, null);
        }

        ContractorService service = getItem(position);
        Log.d("service", service.getContractor());
        if(service != null) {
            TextView contractorTextView = (TextView) v.findViewById(R.id.contractor_name);
            TextView serviceDate = (TextView) v.findViewById(R.id.service_date);

            if(contractorTextView != null) {
                contractorTextView.setText(service.getContractor());
            }

            if(serviceDate != null) {
                serviceDate.setText(service.getDate());
            }
        }

        return v;
    }
}
