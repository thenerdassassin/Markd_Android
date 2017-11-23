package com.schmidthappens.markd.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.customer_menu_activities.ElectricalActivity;
import com.schmidthappens.markd.customer_menu_activities.HvacActivity;
import com.schmidthappens.markd.customer_menu_activities.LandscapingActivity;
import com.schmidthappens.markd.customer_menu_activities.MainActivity;
import com.schmidthappens.markd.customer_menu_activities.PaintingActivity;
import com.schmidthappens.markd.customer_menu_activities.PlumbingActivity;
import com.schmidthappens.markd.data_objects.Customer;

import java.io.StringBufferInputStream;
import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 9/30/17.
 * Help from: http://stacktips.com/tutorials/android/android-recyclerview-example
 */

public class CustomerListRecyclerViewAdapter extends RecyclerView.Adapter<CustomerListRecyclerViewAdapter.CustomerViewHolder> {
    private final static String TAG = "CustomerListRecycler";
    private List<Customer> customerList;
    private List<String> customerReferenceList;
    private String contractorType;
    private Context context;

    public CustomerListRecyclerViewAdapter(Context context, String contractorType, List<Customer> customerList, List<String> customerReferenceList) {
        this.context = context;
        this.contractorType = contractorType;
        this.customerList = customerList;
        this.customerReferenceList = customerReferenceList;
    }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerViewHolder holder, int position) {
        holder.bindData(customerList.get(position), customerReferenceList.get(position));
    }

    @Override
    public int getItemCount() {
        return (null != customerList ? customerList.size() : 0);
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.list_row_customer;
    }

    class CustomerViewHolder extends RecyclerView.ViewHolder {
        private TextView customerNameTextView;
        private TextView customerAddressTextView;

        CustomerViewHolder(View v) {
            super(v);
            this.customerNameTextView = (TextView)v.findViewById(R.id.customer_list_name);
            this.customerAddressTextView = (TextView)v.findViewById(R.id.customer_list_address);
        }

        void bindData(final Customer customer, final String customerId) {
            customerNameTextView.setText(customer.getName());
            customerAddressTextView.setText(customer.getAddress().toString());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, customer.getName(), Toast.LENGTH_SHORT).show();
                    goToCustomerPage(customerId);
                }
            });
        }

        private void goToCustomerPage(String customerId) {
            //TODO: implement go to customer page
            Class contractorClass = getContractorActivityType(contractorType);
            if(contractorClass == null) {
                return;
            }
            Intent goToCustomerPage = new Intent(context, contractorClass);
            goToCustomerPage.putExtra("isContractor", true);
            goToCustomerPage.putExtra("customerId", customerId);
            context.startActivity(goToCustomerPage);
        }

        private Class getContractorActivityType(String userType) {
            switch (userType){
                case("Plumber"):
                    return PlumbingActivity.class;
                case("Electrician"):
                    return ElectricalActivity.class;
                case("Painter"):
                    return PaintingActivity.class;
                case("Hvac"):
                    return HvacActivity.class;
                default:
                    Log.e(TAG, "No match for userType:" + userType);
                    return null;
            }
        }
    }
}
