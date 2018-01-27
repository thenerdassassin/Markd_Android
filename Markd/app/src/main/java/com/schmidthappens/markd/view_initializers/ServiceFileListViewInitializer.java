package com.schmidthappens.markd.view_initializers;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.schmidthappens.markd.R;

import java.util.List;

/**
 * Created by joshua.schmidtibm.com on 1/27/18.
 */

public class ServiceFileListViewInitializer {
    private static final String TAG = "ServiceFileListViewInit";
    public static View createFileListView(final Context ctx, final List<String> files, final boolean isContractorViewing, final String uid) {
        Log.d(TAG, "isContractor:" + isContractorViewing);
        LayoutInflater viewInflater = LayoutInflater.from(ctx);
        View view = viewInflater.inflate(R.layout.view_file_list, null);
        LinearLayout listOfFiles = (LinearLayout)view.findViewById(R.id.file_list);
        ImageView addButton = (ImageView)view.findViewById(R.id.add_file_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: add button implementation
                Toast.makeText(ctx, "Add file", Toast.LENGTH_SHORT).show();
                //ctx.startActivity(createServiceDetailIntent(ctx, contractor, isContractorViewing, uid));
            }
        });
        if(files == null || files.size() == 0) {
            View v = viewInflater.inflate(R.layout.list_row_file, null);
            TextView contractorTextView = v.findViewById(R.id.file_name);
            contractorTextView.setText("No files yet!");
            contractorTextView.setTextColor(ContextCompat.getColor(ctx, R.color.black));
            listOfFiles.addView(v);
        } else {
            for (final String file : files) {
                View v = viewInflater.inflate(R.layout.list_row_file, null);

                if (file != null) {
                    TextView fileNameTextView = (TextView) v.findViewById(R.id.file_name);

                    if (fileNameTextView != null) {
                        fileNameTextView.setPaintFlags(fileNameTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        fileNameTextView.setText(file);
                        fileNameTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //TODO: go to file preview when clicked
                                Toast.makeText(ctx, "View file", Toast.LENGTH_SHORT).show();
                                //ctx.startActivity(getServiceDetailActivityIntent(ctx, service, ""+services.indexOf(service), isContractorViewing, uid));
                            }
                        });
                    }
                }
                listOfFiles.addView(v);
            }
        }

        return view;
    }
}
