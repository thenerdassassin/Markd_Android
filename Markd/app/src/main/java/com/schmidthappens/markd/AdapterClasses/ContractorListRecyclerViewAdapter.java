package com.schmidthappens.markd.AdapterClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.Contractor;
import com.schmidthappens.markd.utilities.ContractorUpdater;
import com.schmidthappens.markd.view_initializers.ContractorFooterViewInitializer;

import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 11/11/17.
 */

public class ContractorListRecyclerViewAdapter extends RecyclerView.Adapter<ContractorListRecyclerViewAdapter.ContractorViewHolder>  {
    private final static String TAG = "ContractorListRecycler";

    private List<Contractor> contractorsList;
    private List<String> references;
    private Context ctx;
    private ContractorUpdater updater;

    public ContractorListRecyclerViewAdapter(Context context, List<Contractor> contractorsList, List<String> contractorsReferences, ContractorUpdater updater) {
        this.contractorsList = contractorsList;
        this.references = contractorsReferences;
        this.ctx = context;
        this.updater = updater;
    }

    @Override
    public ContractorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ContractorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContractorViewHolder holder, int position) {
        holder.bindData(contractorsList.get(position), references.get(position));
    }

    @Override
    public int getItemCount() {
        return (null != contractorsList ? contractorsList.size() : 0);
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.frame_layout_footer_view;
    }

    class ContractorViewHolder extends RecyclerView.ViewHolder {
        Contractor contractor;
        String contractorReference;
        FrameLayout footerFrame;

        ContractorViewHolder(View v) {
            super(v);
            if(v instanceof FrameLayout) {
                footerFrame = (FrameLayout)v;
            }
            v.setOnClickListener(setContractorClickListener);
        }

        void bindData(final Contractor contractor, final String reference) {
            this.contractor = contractor;
            this.contractorReference = reference;
            final String logoFilePath = "logos/" + reference + "/" + contractor.getLogoFileName();
            footerFrame.addView(ContractorFooterViewInitializer
                    .createFooterView(
                            (Activity)ctx,
                            contractor.getContractorDetails().getCompanyName(),
                            contractor.getContractorDetails().getTelephoneNumber(),
                            contractor.getContractorDetails().getWebsiteUrl(),
                            logoFilePath,
                            true));
        }

        private View.OnClickListener phoneClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL)
                        .setData(Uri.parse("tel:" + contractor.getContractorDetails().getTelephoneNumber()));
                ctx.startActivity(intent);
            }
        };

        private View.OnClickListener setContractorClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Send Password Reset");
                (new AlertDialog.Builder(ctx)
                        .setTitle("Update Contractor")
                        .setMessage("Are you sure you want your new contractor to be " + contractor.getContractorDetails().getCompanyName() + "?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                updater.update(contractorReference);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        })
                        .create())
                        .show();
            }
        };
    }
}
