package com.schmidthappens.markd.AdapterClasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.Customer;

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
        Toast.makeText(context.getApplicationContext(), customerList.size() + "", Toast.LENGTH_SHORT).show();
        this.customerList = customerList;
        this.ctx = context;
    }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        Log.e(TAG, "onCreateViewHolder:");
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

        CustomerViewHolder(View v) {
            super(v);
            this.customerNameTextView = (TextView)v.findViewById(R.id.customer_list_name);
        }

        public void bindData(final Customer customer) {
            Log.e(TAG, customer.toString());
            customerNameTextView.setText(customer.toString()); //TODO: change?
        }
    }
}
