package com.schmidthappens.markd.plumbing_subactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.schmidthappens.markd.R;

/**
 * Created by Josh on 6/6/2017.
 */

public class PlumbingEditActivity extends AppCompatActivity {
    //XML Objects
    EditText editManufacturer;
    EditText editModel;
    EditText editInstallDate;
    EditText editLifeSpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plumbing_edit_view);
        setTitle("Domestic Hot Water");

        Intent intent = getIntent();

        //Initialize XML Objects
        editManufacturer = (EditText)findViewById(R.id.plumbing_edit_manufacturer);
        if(intent != null && intent.hasExtra("manufacturer"))
            editManufacturer.setText(intent.getStringExtra("manufacturer"));
    }
}
