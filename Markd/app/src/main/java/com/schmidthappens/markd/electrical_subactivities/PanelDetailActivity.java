package com.schmidthappens.markd.electrical_subactivities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.Breaker;
import com.schmidthappens.markd.data_objects.MainPanelAmperage;
import com.schmidthappens.markd.data_objects.Panel;
import com.schmidthappens.markd.data_objects.PanelAmperage;
import com.schmidthappens.markd.data_objects.PanelManufacturer;
import com.schmidthappens.markd.data_objects.SubPanelAmperage;
import com.schmidthappens.markd.data_objects.TempPanelData;
import com.schmidthappens.markd.menu_option_activities.ViewPanelActivity;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Josh on 3/22/2017.
 */

public class PanelDetailActivity extends AppCompatActivity {
    //XML Objects
    EditText panelDescription;
    NumberPicker numberOfBreakers;
    CheckBox isSubPanel;
    TextView panelInstallDate;
    Button setPanelInstallDateButton;
    Spinner amperageSpinner;
    Spinner manufacturerSpinner;
    Button savePanelButton;

    ArrayAdapter<String> mainPanelAmpAdapter;
    ArrayAdapter<String> subPanelAmpAdapter;

    boolean isNewPanel = false;
    private static final String TAG = "PanelDetailActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_view_panel);

        SessionManager sessionManager = new SessionManager(PanelDetailActivity.this);
        sessionManager.checkLogin();

        //Initialize XML Objects
        panelDescription = (EditText)findViewById(R.id.electrical_panel_description);
        numberOfBreakers = (NumberPicker)findViewById(R.id.electrical_panel_number_of_breakers);
        isSubPanel = (CheckBox)findViewById(R.id.is_sub_panel);
        panelInstallDate = (TextView)findViewById(R.id.electrical_panel_install_date);
        setPanelInstallDateButton = (Button)findViewById(R.id.electrical_panel_set_install_date);
        amperageSpinner = (Spinner)findViewById(R.id.panel_amperage_spinner);
        manufacturerSpinner = (Spinner)findViewById(R.id.panel_manufacturer_spinner);
        savePanelButton = (Button)findViewById(R.id.electrical_save_panel_button);

        setEnterButtonToKeyboardDismissal(panelDescription);
        setUpNumberPicker(numberOfBreakers);
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
        isSubPanel.setOnClickListener(isSubPanelClick);
    }

    public void onStart() {
       super.onStart();

       Intent intentThatStartedThisActivity = getIntent();
       if(intentThatStartedThisActivity != null) {
           String amperageString = "";

           if(intentThatStartedThisActivity.hasExtra("newPanel")) {
               isNewPanel = true;
           } else {
               isNewPanel = false;
           }

           if(intentThatStartedThisActivity.hasExtra("panelDescription")) {
               String panelDescriptionString = intentThatStartedThisActivity.getStringExtra("panelDescription");
               panelDescription.setText(panelDescriptionString);
               panelDescription.setSelection(panelDescriptionString.length()); //Sets cursor to end of EditText
           }

           if(intentThatStartedThisActivity.hasExtra("numberOfBreakers")) {
               numberOfBreakers.setValue(intentThatStartedThisActivity.getIntExtra("numberOfBreakers", 1));
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
                   try {
                       amperageSpinner.setSelection(MainPanelAmperage.fromString(amperageString).ordinal());
                   } catch (NullPointerException e) {
                       amperageSpinner.setSelection(0);
                   }
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
            if (v instanceof CheckBox) {
                CheckBox isSubPanel = (CheckBox) v;
                if (isSubPanel.isChecked()) {
                    amperageSpinner.setAdapter(subPanelAmpAdapter);
                } else {
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
            Log.i(TAG, "Save Panel");
            savePanel();
            backToMain();
        }
    };

    //Mark:- Number Picker
    public void setUpNumberPicker(NumberPicker picker) {
        picker.setMinValue(1);
        picker.setMaxValue(52);
        picker.setWrapSelectorWheel(false);
    }

    //Mark:- Date Picker
    private void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of DatePickerDialog and return it
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            pickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            return pickerDialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            Calendar current = Calendar.getInstance();

            //Check to make sure date is not in future
            if(selected.getTime().after(current.getTime())) {
                Log.i(TAG, "Selected Invalid Date");
                Toast.makeText(getActivity().getApplicationContext(), "Install date must not be in future.", Toast.LENGTH_SHORT).show();
                return;
            }

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

    private void savePanel() {
        //TODO change to http call to update electrical panel
        TempPanelData myPanels = TempPanelData.getInstance();

        //Get Info
        String panelDescriptionString = panelDescription.getText().toString();
        int breakersNumber = numberOfBreakers.getValue();
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


        if(isNewPanel) {
            //TODO change to http call to add Panel
            myPanels.addPanel(new Panel(0, new ArrayList<Breaker>()));
        }
        //Send Update
        //TODO change to http call to update Panel
        myPanels.updatePanel(panelDescriptionString, breakersNumber, !isSubPanelChecked, panelInstallDateString, amps, manufacturer);

    }

    private void backToMain() {
        Context context = PanelDetailActivity.this;
        Class destinationClass = ViewPanelActivity.class;
        Intent intentToStartMainActivity = new Intent(context, destinationClass);
        intentToStartMainActivity.putExtra("source", "PanelDetailActivity");
        startActivity(intentToStartMainActivity);
    }
}
