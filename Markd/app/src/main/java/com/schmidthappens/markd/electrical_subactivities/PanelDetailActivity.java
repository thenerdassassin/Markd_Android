package com.schmidthappens.markd.electrical_subactivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.schmidthappens.markd.menu_option_activities.ViewPanelActivity;
import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.MainPanelAmperage;
import com.schmidthappens.markd.data_objects.PanelAmperage;
import com.schmidthappens.markd.data_objects.PanelManufacturer;
import com.schmidthappens.markd.data_objects.SubPanelAmperage;
import com.schmidthappens.markd.data_objects.TempPanelData;

/**
 * Created by Josh on 3/22/2017.
 */

public class PanelDetailActivity extends AppCompatActivity {
    //XML Objects
    CheckBox isSubPanel;
    Spinner amperageSpinner;
    Spinner manufacturerSpinner;
    Button savePanelButton;

    ArrayAdapter<String> mainPanelAmpAdapter;
    ArrayAdapter<String> subPanelAmpAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel_detail);

        //Initialize XML Objects
        isSubPanel = (CheckBox)findViewById(R.id.is_sub_panel);
        amperageSpinner = (Spinner)findViewById(R.id.panel_amperage_spinner);
        manufacturerSpinner = (Spinner)findViewById(R.id.panel_manufacturer_spinner);
        savePanelButton = (Button)findViewById(R.id.save_panel);


        //Initialize SpinnerAdapters for Amperages
        String[] mainPanelAmperages = MainPanelAmperage.getValues();
        String[] subPanelAmperages = SubPanelAmperage.getValues();

        mainPanelAmpAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mainPanelAmperages);
        mainPanelAmpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subPanelAmpAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subPanelAmperages);
        subPanelAmpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        //Add Manufacturer Adapter to Spinner
        String[] panelManufacturers = PanelManufacturer.getValues();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, panelManufacturers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        manufacturerSpinner.setAdapter(adapter);

        savePanelButton.setOnClickListener(onSaveClicked);

        //TODO  onClick Listener to Checkbox to change adapter
        isSubPanel.setOnClickListener(isSubPanelClick);
    }

    public void onStart() {
       super.onStart();

       Intent intentThatStartedThisActivity = getIntent();
       if(intentThatStartedThisActivity != null) {
           String amperageString = "";
           if(intentThatStartedThisActivity.hasExtra("panelAmperage")) {
               amperageString = intentThatStartedThisActivity.getStringExtra("panelAmperage");
           }

           if(intentThatStartedThisActivity.hasExtra("isMainPanel")) {
               boolean isMainPanel = intentThatStartedThisActivity.getBooleanExtra("isMainPanel", true);
               if(!isMainPanel) {
                   isSubPanel.setChecked(true);
                   amperageSpinner.setAdapter(subPanelAmpAdapter);
                   amperageSpinner.setSelection(SubPanelAmperage.fromString(amperageString).ordinal());
               } else {
                   isSubPanel.setChecked(false);
                   amperageSpinner.setAdapter(mainPanelAmpAdapter);
                   amperageSpinner.setSelection(MainPanelAmperage.fromString(amperageString).ordinal());
               }
           }

           if(intentThatStartedThisActivity.hasExtra("manufacturer")) {
               String manufacturer = intentThatStartedThisActivity.getStringExtra("manufacturer");
               manufacturerSpinner.setSelection(PanelManufacturer.fromString(manufacturer).ordinal());
           }
       }
    }

    // Mark:- Listeners
    private View.OnClickListener onSaveClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updatePanel();
            backToMain();
        }
    };

    private View.OnClickListener isSubPanelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v instanceof CheckBox) {
                CheckBox isSubPanel = (CheckBox)v;
                if(isSubPanel.isChecked()) {
                    amperageSpinner.setAdapter(subPanelAmpAdapter);
                }
                else {
                    amperageSpinner.setAdapter(mainPanelAmpAdapter);
                }
            }
        }
    };

    // Mark: Helper Functions
    private void updatePanel() {
        //TODO change to http call
        TempPanelData myPanels = TempPanelData.getInstance();

        //Get Info
        boolean isSubPanelChecked = isSubPanel.isChecked();
        String panelAmpString = (String)amperageSpinner.getItemAtPosition(amperageSpinner.getSelectedItemPosition());
        PanelAmperage amps = null;
        if(!isSubPanelChecked) {
            amps = MainPanelAmperage.fromString(panelAmpString);
        } else {
            amps = SubPanelAmperage.fromString(panelAmpString);
        }
        String manufacturerString = (String)manufacturerSpinner.getItemAtPosition(manufacturerSpinner.getSelectedItemPosition());
        PanelManufacturer manufacturer = PanelManufacturer.fromString(manufacturerString);

        //Send Update
        myPanels.updatePanel(!isSubPanelChecked, amps, manufacturer);
    }

    private void backToMain() {
        Context context = PanelDetailActivity.this;
        Class destinationClass = ViewPanelActivity.class;
        Intent intentToStartMainActivity = new Intent(context, destinationClass);
        intentToStartMainActivity.putExtra("source", "PanelDetailActivity");
        startActivity(intentToStartMainActivity);
    }
}
