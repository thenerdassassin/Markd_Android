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
import com.schmidthappens.markd.customer_subactivities.ServiceFileDetailActivity;
import com.schmidthappens.markd.file_storage.FirebaseFile;

import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 1/27/18.
 */

public class ServiceFileListViewInitializer {
    private static final String TAG = "ServiceFileListViewInit";
    public static View createFileListView(final Context ctx, final List<FirebaseFile> files, final String uid, final String serviceType, final int serviceId) {
        LayoutInflater viewInflater = LayoutInflater.from(ctx);
        View view = viewInflater.inflate(R.layout.view_file_list, null);
        LinearLayout listOfFiles = (LinearLayout)view.findViewById(R.id.file_list);
        ImageView addButton = (ImageView)view.findViewById(R.id.add_file_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Add file");
                ctx.startActivity(
                        createServiceFileIntent(ctx, uid, serviceType, serviceId)
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
            for(int fileId = 0; fileId < files.size(); fileId++) {
                final String fileName = files.get(fileId).getFileName();
                final int fileIdFinal = fileId;
                final String fileGuid = files.get(fileId).getGuid();
                View v = viewInflater.inflate(R.layout.list_row_file, null);

                if (fileName != null) {
                    TextView fileNameTextView = (TextView) v.findViewById(R.id.file_name);

                    if (fileNameTextView != null) {
                        fileNameTextView.setPaintFlags(fileNameTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        fileNameTextView.setText(fileName);
                        fileNameTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d(TAG, "View file:" + fileName);
                                ctx.startActivity(
                                        createServiceFileIntent(ctx, uid, serviceType, serviceId, fileIdFinal, fileName, fileGuid)
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

    private static Intent createServiceFileIntent(Context context, String uid, String serviceType, int serviceId) {
        Intent intentToCreateServiceFile = new Intent(context, ServiceFileDetailActivity.class);
        intentToCreateServiceFile.putExtra("customerId", uid);
        intentToCreateServiceFile.putExtra("serviceType", serviceType);
        intentToCreateServiceFile.putExtra("serviceId", serviceId);
        intentToCreateServiceFile.putExtra("fileId", -1);
        return  intentToCreateServiceFile;
    }

    private static Intent createServiceFileIntent(Context context, String uid, String serviceType, int serviceId, int fileId, String fileName, String fileGuid) {
        Intent intentToCreateServiceFile = new Intent(context, ServiceFileDetailActivity.class);
        intentToCreateServiceFile.putExtra("customerId", uid);
        intentToCreateServiceFile.putExtra("serviceType", serviceType);
        intentToCreateServiceFile.putExtra("serviceId", serviceId);
        intentToCreateServiceFile.putExtra("fileId", fileId);
        intentToCreateServiceFile.putExtra("fileName", fileName);
        intentToCreateServiceFile.putExtra("fileGuid", fileGuid);
        return  intentToCreateServiceFile;
    }
}
