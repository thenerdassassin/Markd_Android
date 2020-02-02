package com.markd.applications.android.home.view_initializers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.markd.applications.android.home.R;
import com.markd.applications.android.home.customer_subactivities.ChangeContractorActivity;
import com.markd.applications.android.home.file_storage.DownloadUrlListener;
import com.markd.applications.android.home.file_storage.MarkdFirebaseStorage;
import com.squareup.picasso.Picasso;


/**
 * Created by Josh on 4/28/2017.
 */

public class ContractorFooterViewInitializer {
    private static final String TAG = "FooterViewInitializer";
    private static final int PLACE_HOLDER_IMAGE = R.drawable.ic_contractor_notification;

    public static View createFooterView(final Activity ctx, String contractor, String phone, final String websiteUrl, String photoPath) {
        return createFooterView(ctx, contractor, phone, websiteUrl, photoPath, false);
    }

    public static View createFooterView(
            final Activity ctx, String contractor, String phone,
            final String websiteUrl, String photoPath, boolean isRecyclerViewHolder) {
        LayoutInflater vi;
        vi = LayoutInflater.from(ctx);
        View v = vi.inflate(R.layout.view_footer, null);

        //Initialize XML Objects
        final ImageView footerLogo = v.findViewById(R.id.footer_logo);
        TextView contractorName = v.findViewById(R.id.footer_contractor_name);
        TextView phoneNumber = v.findViewById(R.id.footer_phone_number);
        TextView website = v.findViewById(R.id.footer_website);

        MarkdFirebaseStorage.getDownloadUrl(photoPath, new DownloadUrlListener() {
                    @Override
                    public void onStart() {
                        footerLogo.setImageResource(PLACE_HOLDER_IMAGE);
                    }

                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        Picasso.get()
                                .load(downloadUrl)
                                .resize(128, 128)
                                .placeholder(PLACE_HOLDER_IMAGE)
                                .into(footerLogo);
                    }

                    @Override
                    public void onCanceled() {
                        footerLogo.setImageResource(PLACE_HOLDER_IMAGE);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        footerLogo.setImageResource(PLACE_HOLDER_IMAGE);
                    }
                });

        contractorName.setText(contractor);
        phoneNumber.setText(phone);

        website.setText(websiteUrl);

        //Make Logo clickable to website
        footerLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                if (!websiteUrl.contains("http://")) {
                    intent.setData(Uri.parse("http://" + websiteUrl));
                } else {
                    intent.setData(Uri.parse(websiteUrl));
                }
                ctx.startActivity(intent);
            }
        });

        if(isRecyclerViewHolder) {
            v.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorLabel));
            contractorName.setTextColor(ContextCompat.getColor(ctx, R.color.black));
            phoneNumber.setTextColor(ContextCompat.getColor(ctx, R.color.black));
            website.setTextColor(ContextCompat.getColor(ctx, R.color.black));
        } else {
           Linkify.addLinks(phoneNumber, Linkify.PHONE_NUMBERS);
           Linkify.addLinks(website, Linkify.WEB_URLS);
           phoneNumber.setLinkTextColor(ContextCompat.getColor(ctx, R.color.hyperlink));
           website.setLinkTextColor(ContextCompat.getColor(ctx, R.color.hyperlink));
        }

        return v;
    }

    public static View createFooterView(final Context ctx, final String contractorType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View v = inflater.inflate(R.layout.view_default_footer, null);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Add Contractor Clicked");
                Class destinationClass = ChangeContractorActivity.class;
                Intent intentToStartChangeContractorActivity = new Intent(ctx, destinationClass);
                intentToStartChangeContractorActivity.putExtra("contractorType", contractorType);
                ctx.startActivity(intentToStartChangeContractorActivity);
            }
        });
        return v;
    }
}
