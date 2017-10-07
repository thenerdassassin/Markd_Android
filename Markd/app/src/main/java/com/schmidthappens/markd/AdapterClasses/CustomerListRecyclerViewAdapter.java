package com.schmidthappens.markd.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.customer_menu_activities.ElectricalActivity;
import com.schmidthappens.markd.customer_menu_activities.HvacActivity;
import com.schmidthappens.markd.customer_menu_activities.LandscapingActivity;
import com.schmidthappens.markd.customer_menu_activities.MainActivity;
import com.schmidthappens.markd.customer_menu_activities.PaintingActivity;
import com.schmidthappens.markd.customer_menu_activities.PlumbingActivity;
import com.schmidthappens.markd.data_objects.Customer;
import com.schmidthappens.markd.data_objects.TempCustomerData;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by joshua.schmidtibm.com on 9/30/17.
 * Help from: http://stacktips.com/tutorials/android/android-recyclerview-example
 */

public class CustomerListRecyclerViewAdapter extends RecyclerView.Adapter<CustomerListRecyclerViewAdapter.CustomerViewHolder> {
    private final static String TAG = "CustomerListRecycler";
    private List<Customer> customerList;
    private Context ctx;

    public CustomerListRecyclerViewAdapter(Context context, List<Customer> customerList) {
        this.customerList = customerList;
        this.ctx = context;
    }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerViewHolder holder, int position) {
        holder.bindData(customerList.get(position));
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

        void bindData(final Customer customer) {
            customerNameTextView.setText(customer.getName());
            customerAddressTextView.setText(customer.getAddress());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ctx, customer.getName(), Toast.LENGTH_SHORT).show();
                    goToCustomerPage();
                }
            });
        }

        private void goToCustomerPage() {
            SessionManager sessionManager = new SessionManager(ctx);
            String userType = sessionManager.getUserType();
            Context activityContext = ctx;
            Class contractorClass = getContractorActivityType(userType);
            Intent goToCustomerPage = new Intent(activityContext, contractorClass);
            ctx.startActivity(goToCustomerPage);
        }

        private Class getContractorActivityType(String userType) {
            switch (userType){
                case("plumber"):
                    return PlumbingActivity.class;
                case("electrician"):
                    return ElectricalActivity.class;
                case("painter"):
                    return PaintingActivity.class;
                case("hvac"):
                    return HvacActivity.class;
                case("landscaper"):
                    return LandscapingActivity.class;
                default:
                    Log.e(TAG, "No match for userType:" + userType);
                    return MainActivity.class;
            }
        }
    }
}
