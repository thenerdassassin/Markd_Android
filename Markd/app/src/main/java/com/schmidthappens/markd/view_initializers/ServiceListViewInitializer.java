package com.schmidthappens.markd.view_initializers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.ContractorService;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Josh on 4/28/2017.
 */

public class ServiceListViewInitializer {
    String pathToSaveFiles = "";

    //TODO: may need to paginate at some point
    public static View createServiceListView(final Context ctx, final List<ContractorService> services,final String contractor, final String pathToSaveFiles) {
        LayoutInflater viewInflater;
        viewInflater = LayoutInflater.from(ctx);
        View view = viewInflater.inflate(R.layout.view_service_list, null);
        LinearLayout listOfServices = (LinearLayout)view.findViewById(R.id.service_list);
        ImageView addButton = (ImageView)view.findViewById(R.id.add_service_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO move to detailed view
                Intent goToServiceDetailActivityIntent = new Intent(ctx, ServiceDetailActivity.class);
                goToServiceDetailActivityIntent.putExtra("originalActivity", ctx.getClass());
                //TODO add identifier for particular service
                goToServiceDetailActivityIntent.putExtra("pathOfFiles", pathToSaveFiles);
                goToServiceDetailActivityIntent.putExtra("contractor", contractor);
                goToServiceDetailActivityIntent.putExtra("isNew", true);
                ctx.startActivity(goToServiceDetailActivityIntent);
            }
        });

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
                    Intent goToServiceDetailActivityIntent = new Intent(ctx, ServiceDetailActivity.class);
                    goToServiceDetailActivityIntent.putExtra("originalActivity", ctx.getClass());
                    //TODO add identifier for particular service
                    goToServiceDetailActivityIntent.putExtra("pathOfFiles", pathToSaveFiles);
                    if(service != null) {
                        //TODO change ID to service id in database
                        goToServiceDetailActivityIntent.putExtra("serviceId", ""+services.indexOf(service));
                        goToServiceDetailActivityIntent.putExtra("contractor", service.getContractor());
                        goToServiceDetailActivityIntent.putExtra("description", service.getComments());
                        goToServiceDetailActivityIntent.putExtra("month", service.getMonth());
                        goToServiceDetailActivityIntent.putExtra("day", service.getDay());
                        goToServiceDetailActivityIntent.putExtra("year", service.getYear());
                    } else {
                        Log.e(TAG, "On view click service was null");
                    }

                    ctx.startActivity(goToServiceDetailActivityIntent);
                }
            });
            listOfServices.addView(v);
        }

        return view;
    }
}
