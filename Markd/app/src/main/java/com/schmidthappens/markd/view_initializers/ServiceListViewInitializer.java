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
import com.schmidthappens.markd.customer_subactivities.ServiceDetailActivity;
import com.schmidthappens.markd.data_objects.Contractor;
import com.schmidthappens.markd.data_objects.ContractorService;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Josh on 4/28/2017.
 */

public class ServiceListViewInitializer {
    private static final String TAG = "ServiceListViewInit";
    public static View createServiceListView(final Context ctx, final List<ContractorService> services, final String contractor, final boolean isContractorViewing, final String uid) {
        Log.d(TAG, "isContractor:" + isContractorViewing);
        LayoutInflater viewInflater = LayoutInflater.from(ctx);
        View view = viewInflater.inflate(R.layout.view_service_list, null);
        LinearLayout listOfServices = (LinearLayout)view.findViewById(R.id.service_list);
        ImageView addButton = (ImageView)view.findViewById(R.id.add_service_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(services == null) {
                    ctx.startActivity(createServiceDetailIntent(ctx, contractor, isContractorViewing, uid, 0));
                } else {
                    ctx.startActivity(createServiceDetailIntent(ctx, contractor, isContractorViewing, uid, services.size()));
                }
            }
        });
        if(services == null || services.size() == 0) {
            View v = viewInflater.inflate(R.layout.list_row_service, null);
            TextView contractorTextView = v.findViewById(R.id.contractor_name);
            contractorTextView.setText("No services yet!");
            listOfServices.addView(v);
        } else {
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

                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ctx.startActivity(getServiceDetailActivityIntent(ctx, service, services.indexOf(service), isContractorViewing, uid));
                        }
                    });
                }
                listOfServices.addView(v);
            }
        }

        return view;
    }

    private static Intent getServiceDetailActivityIntent(Context ctx,  ContractorService service, int serviceId, boolean isContractor, String customerId) {
        Intent intentToReturn = new Intent(ctx, ServiceDetailActivity.class);
        if(service != null) {
            intentToReturn.putExtra("originalActivity", ctx.getClass());
            intentToReturn.putExtra("serviceId", serviceId);
            intentToReturn.putExtra("contractor", service.getContractor());
            intentToReturn.putExtra("description", service.getComments());
        }
        intentToReturn.putExtra("isContractor", isContractor);
        intentToReturn.putExtra("customerId", customerId);
        return intentToReturn;
    }
    private static Intent createServiceDetailIntent(Context ctx, String contractor, boolean isContractor, String customerId, int servicesLength) {
        Intent intentToReturn = new Intent(ctx, ServiceDetailActivity.class);
        intentToReturn.putExtra("originalActivity", ctx.getClass());
        intentToReturn.putExtra("contractor", contractor);
        intentToReturn.putExtra("isContractor", isContractor);
        intentToReturn.putExtra("customerId", customerId);
        intentToReturn.putExtra("servicesLength", servicesLength);
        return intentToReturn;
    }
}
