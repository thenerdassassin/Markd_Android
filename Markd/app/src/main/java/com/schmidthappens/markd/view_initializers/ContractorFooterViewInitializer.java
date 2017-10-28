package com.schmidthappens.markd.view_initializers;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.schmidthappens.markd.R;


/**
 * Created by Josh on 4/28/2017.
 */

public class ContractorFooterViewInitializer {
    public static View createFooterView(final Context ctx, Drawable logo, String contractor, String phone, final String websiteUrl) {
        LayoutInflater vi;
        vi = LayoutInflater.from(ctx);
        View v = vi.inflate(R.layout.view_footer, null);

        //Initialize XML Objects
        ImageView footerLogo = v.findViewById(R.id.footer_logo);
        TextView contractorName = v.findViewById(R.id.footer_contractor_name);
        TextView phoneNumber = v.findViewById(R.id.footer_phone_number);
        TextView website = v.findViewById(R.id.footer_website);

        footerLogo.setImageDrawable(logo);
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
                intent.setData(Uri.parse(websiteUrl));
                ctx.startActivity(intent);
            }
        });

        return v;
    }

    public static View createFooterView(final Context ctx) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View v = inflater.inflate(R.layout.view_default_footer, null);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx, "Add Contractor Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }
}
