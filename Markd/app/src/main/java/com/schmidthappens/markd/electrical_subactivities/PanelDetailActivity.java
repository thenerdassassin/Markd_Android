package com.schmidthappens.markd.electrical_subactivities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.MainPanelAmperage;
import com.schmidthappens.markd.data_objects.PanelAmperage;
import com.schmidthappens.markd.data_objects.PanelManufacturer;
import com.schmidthappens.markd.data_objects.SubPanelAmperage;
import com.schmidthappens.markd.data_objects.TempPanelData;
import com.schmidthappens.markd.menu_option_activities.ViewPanelActivity;

/**
 * Created by Josh on 3/22/2017.
 */

public class PanelDetailActivity extends AppCompatActivity {
    //XML Objects
    EditText panelDescription;
    CheckBox isSubPanel;
    TextView panelInstallDate;
    Button setPanelInstallDateButton;
    Spinner amperageSpinner;
    Spinner manufacturerSpinner;
    Button savePanelButton;

    ArrayAdapter<String> mainPanelAmpAdapter;
    ArrayAdapter<String> subPanelAmpAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel_detail_view);

        //Initialize XML Objects
        panelDescription = (EditText)findViewById(R.id.electrical_panel_description);
        panelInstallDate = (TextView)findViewById(R.id.electrical_panel_install_date) ;
        setPanelInstallDateButton = (Button)findViewById(R.id.electrical_panel_set_install_date);
        isSubPanel = (CheckBox)findViewById(R.id.is_sub_panel);
        amperageSpinner = (Spinner)findViewById(R.id.panel_amperage_spinner);
        manufacturerSpinner = (Spinner)findViewById(R.id.panel_manufacturer_spinner);
        savePanelButton = (Button)findViewById(R.id.electrical_save_panel_button);

        setEnterButtonToKeyboardDismissal(panelDescription);
        setPanelInstallDateButton.setOnClickListener(panelInstallDateButtonClickListener);

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

           if(intentThatStartedThisActivity.hasExtra("panelDescription")) {
               panelDescription.setText(intentThatStartedThisActivity.getStringExtra("panelDescription"));
           }

           if(intentThatStartedThisActivity.hasExtra("panelInstallDate")) {
               panelInstallDate.setText(intentThatStartedThisActivity.getStringExtra("panelInstallDate"));
           }

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
    private View.OnClickListener isSubPanelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard(v);
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

    private View.OnClickListener panelInstallDateButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard(PanelDetailActivity.this.getCurrentFocus());
            showDatePickerDialog(v);
        }
    };

    private View.OnClickListener onSaveClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard(v);
            updatePanel();
            backToMain();
        }
    };

    //Mark:- Date Picker
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePicker = new DatePickerDialog(getActivity(), this, 2014, 0, 17);
            return datePicker;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String newDate = "";
            if(month < 9) {
                newDate += "0";
            }
            newDate += Integer.toString(month+1) + ".";
            if(dayOfMonth < 10) {
                newDate += "0";
            }

            newDate += dayOfMonth + "." + (Integer.toString(year).substring(2));
            ((PanelDetailActivity)getActivity()).changeInstallDate(newDate);
        }
    }

    //Mark:- Helper Functions
    private void changeInstallDate(String newDate) {
        panelInstallDate.setText(newDate);
    }

    //Makes the enter button dismiss soft keyboard
    private void setEnterButtonToKeyboardDismissal(final EditText view) {
        view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard(v);
                    return true;
                }
                return false;
            }
        });
    }

    //Hides Keyboard
    private void hideKeyboard(View v) {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void updatePanel() {
        //TODO change to http call to update electrical panel
        TempPanelData myPanels = TempPanelData.getInstance();

        //Get Info
        String panelDescriptionString = panelDescription.getText().toString();
        boolean isSubPanelChecked = isSubPanel.isChecked();
        String panelInstallDateString = panelInstallDate.getText().toString();
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
        myPanels.updatePanel(panelDescriptionString, !isSubPanelChecked, panelInstallDateString, amps, manufacturer);
    }

    private void backToMain() {
        Context context = PanelDetailActivity.this;
        Class destinationClass = ViewPanelActivity.class;
        Intent intentToStartMainActivity = new Intent(context, destinationClass);
        intentToStartMainActivity.putExtra("source", "PanelDetailActivity");
        startActivity(intentToStartMainActivity);
    }
}
