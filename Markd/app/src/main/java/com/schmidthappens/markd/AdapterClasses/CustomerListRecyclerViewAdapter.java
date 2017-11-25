package com.schmidthappens.markd.AdapterClasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.Customer;
import com.schmidthappens.markd.utilities.CustomerSelectedInterface;

import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 9/30/17.
 * Help from: http://stacktips.com/tutorials/android/android-recyclerview-example
 */

public class CustomerListRecyclerViewAdapter extends RecyclerView.Adapter<CustomerListRecyclerViewAdapter.CustomerViewHolder> {
    private final static String TAG = "CustomerListRecycler";
    private Context context;
    private List<Customer> customerList;
    private List<String> customerReferenceList;

    public CustomerListRecyclerViewAdapter(Context context, List<Customer> customerList, List<String> customerReferenceList) {
        this.context = context;
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
            if(context instanceof CustomerSelectedInterface)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "customer selected:" + customer.getName());
                    if(context instanceof CustomerSelectedInterface) {
                        ((CustomerSelectedInterface) context).onCustomerSelected(customerId);
                    } else {
                        Log.e(TAG, "Context does not support CustomerSelectedInterface");
                    }
                }
            });
        }
    }
}
