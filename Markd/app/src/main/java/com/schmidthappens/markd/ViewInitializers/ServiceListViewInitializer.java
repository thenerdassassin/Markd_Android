package com.schmidthappens.markd.ViewInitializers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.ContractorService;

import java.util.List;

/**
 * Created by Josh on 4/28/2017.
 */

public class ServiceListViewInitializer {
    String pathToSaveFiles = "";

    //TODO: may need to paginate at some point
    public static View createServiceListView(final Context ctx, List<ContractorService> services, View.OnClickListener addListener, final String pathToSaveFiles) {
        LayoutInflater viewInflater;
        viewInflater = LayoutInflater.from(ctx);
        View view = viewInflater.inflate(R.layout.view_service_list, null);
        LinearLayout listOfServices = (LinearLayout)view.findViewById(R.id.service_list);
        ImageView addButton = (ImageView)view.findViewById(R.id.add_service_button);
        addButton.setOnClickListener(addListener);

        if(services.size() == 0) {
            View v = viewInflater.inflate(R.layout.list_row_service, null);
            TextView contractorTextView = (TextView) v.findViewById(R.id.contractor_name);
            contractorTextView.setText("No services yet!");
            listOfServices.addView(v);
        }
        //Add all services to list
        for (final ContractorService service : services) {
            View v = viewInflater.inflate(R.layout.list_row_service, null);

            if (service != null) {
                TextView contractorTextView = (TextView) v.findViewById(R.id.contractor_name);
                TextView serviceDate = (TextView) v.findViewById(R.id.service_date);

                if (contractorTextView != null) {
                    contractorTextView.setText(service.getContractor());
                }

                if (serviceDate != null) {
                    serviceDate.setText(service.getDate());
                }
            }
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO move to detailed view
                    Toast.makeText(ctx, service.getComments()+":"+pathToSaveFiles, Toast.LENGTH_LONG).show();

                }
            });
            listOfServices.addView(v);
        }

        return view;
    }
}
