package com.schmidthappens.markd.view_initializers;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
        ImageView footerLogo = (ImageView)v.findViewById(R.id.footer_logo);
        TextView contractorName = (TextView)v.findViewById(R.id.footer_contractor_name);
        TextView phoneNumber = (TextView)v.findViewById(R.id.footer_phone_number);
        TextView website = (TextView)v.findViewById(R.id.footer_website);

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

}
