package com.schmidthappens.markd.AdapterClasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.Contractor;

import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 11/11/17.
 */

public class ContractorListRecyclerViewAdapter extends RecyclerView.Adapter<ContractorListRecyclerViewAdapter.ContractorViewHolder>  {
    private final static String TAG = "ContractorListRecycler";
    private List<String> contractorReferenceList;
    private Context ctx;

    public ContractorListRecyclerViewAdapter(Context context, List<String> customerList) {
        this.contractorReferenceList = customerList;
        this.ctx = context;
    }

    @Override
    public ContractorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ContractorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContractorViewHolder holder, int position) {
        holder.bindData(contractorReferenceList.get(position));
    }

    @Override
    public int getItemCount() {
        return (null != contractorReferenceList ? contractorReferenceList.size() : 0);
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.list_row_contractor;
    }

    class ContractorViewHolder extends RecyclerView.ViewHolder {
        TextView contractorCompanyTextView;
        TextView contractorWebsite;

        ContractorViewHolder(View v) {
            super(v);
            contractorCompanyTextView = (TextView)v.findViewById(R.id.contractor_list_company);
            contractorWebsite = (TextView)v.findViewById(R.id.contractor_list_website);
        }

        void bindData(final String contractor) {
            DatabaseReference contractorReference = FirebaseDatabase.getInstance().getReference("users").child(contractor);
            contractorReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Contractor data = dataSnapshot.getValue(Contractor.class);
                    if(data != null) {
                        if(data.getContractorDetails() != null) {
                            contractorCompanyTextView.setText(data.getContractorDetails().getCompanyName());
                            contractorWebsite.setText(data.getContractorDetails().getWebsiteUrl());
                        } else {
                            //TODO
                        }
                    } else {
                        //TODO
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //TODO
                }
            });
        }
    }
}
