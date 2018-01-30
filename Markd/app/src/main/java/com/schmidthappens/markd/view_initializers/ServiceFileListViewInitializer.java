package com.schmidthappens.markd.view_initializers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.customer_subactivities.ServiceImageActivity;

import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 1/27/18.
 */

public class ServiceFileListViewInitializer {
    private static final String TAG = "ServiceFileListViewInit";
    public static View createFileListView(final Context ctx, final List<String> files, final boolean isContractorViewing, final String uid, final int serviceId, final Class originalActivity) {
        Log.d(TAG, "isContractor:" + isContractorViewing);
        LayoutInflater viewInflater = LayoutInflater.from(ctx);
        View view = viewInflater.inflate(R.layout.view_file_list, null);
        LinearLayout listOfFiles = (LinearLayout)view.findViewById(R.id.file_list);
        ImageView addButton = (ImageView)view.findViewById(R.id.add_file_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Add file");
                ctx.startActivity(
                        createServiceImageIntent(ctx, isContractorViewing, uid, serviceId, originalActivity)
                );
            }
        });
        if(files == null || files.size() == 0) {
            View v = viewInflater.inflate(R.layout.list_row_file, null);
            TextView contractorTextView = v.findViewById(R.id.file_name);
            contractorTextView.setText("No files yet!");
            contractorTextView.setTextColor(ContextCompat.getColor(ctx, R.color.black));
            listOfFiles.addView(v);
        } else {
            for(int i = 0; i < files.size(); i++) {
                final String file = files.get(i);
                final int j = i;
                View v = viewInflater.inflate(R.layout.list_row_file, null);

                if (file != null) {
                    TextView fileNameTextView = (TextView) v.findViewById(R.id.file_name);

                    if (fileNameTextView != null) {
                        fileNameTextView.setPaintFlags(fileNameTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        fileNameTextView.setText(file);
                        fileNameTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG, "View file:" + file);
                                ctx.startActivity(
                                        createServiceImageIntent(ctx, isContractorViewing, uid, file, serviceId, originalActivity, j)
                                );
                            }
                        });
                    }
                }
                listOfFiles.addView(v);
            }
        }

        return view;
    }

    private static Intent createServiceImageIntent(Context context, boolean isContractorViewing, String uid, int serviceId, Class originalActivity) {
        Intent intentToCreateServiceImage = new Intent(context, ServiceImageActivity.class);
        intentToCreateServiceImage.putExtra("isContractor", isContractorViewing);
        intentToCreateServiceImage.putExtra("isNew", true);
        intentToCreateServiceImage.putExtra("customerId", uid);
        intentToCreateServiceImage.putExtra("serviceId", serviceId);
        intentToCreateServiceImage.putExtra("originalActivity", originalActivity);
        //TODO: pass Contractor and Comments
        return  intentToCreateServiceImage;
    }

    private static Intent createServiceImageIntent(Context context, boolean isContractorViewing, String uid, String fileName, int serviceId, Class originalActivity, int fileId) {
        Intent intentToCreateServiceImage = new Intent(context, ServiceImageActivity.class);
        intentToCreateServiceImage.putExtra("isContractor", isContractorViewing);
        intentToCreateServiceImage.putExtra("isNew", false);
        intentToCreateServiceImage.putExtra("fileName", fileName);
        intentToCreateServiceImage.putExtra("customerId", uid);
        intentToCreateServiceImage.putExtra("serviceId", serviceId);
        intentToCreateServiceImage.putExtra("originalActivity", originalActivity);
        intentToCreateServiceImage.putExtra("fileId", fileId);
        //TODO: pass Contractor and Comments
        return  intentToCreateServiceImage;
    }
}
