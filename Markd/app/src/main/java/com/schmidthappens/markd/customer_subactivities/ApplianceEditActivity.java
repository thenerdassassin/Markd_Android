package com.schmidthappens.markd.customer_subactivities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.data_objects.AirHandler;
import com.schmidthappens.markd.data_objects.Boiler;
import com.schmidthappens.markd.data_objects.Compressor;
import com.schmidthappens.markd.data_objects.HotWater;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.customer_menu_activities.HvacActivity;
import com.schmidthappens.markd.customer_menu_activities.PlumbingActivity;
import com.schmidthappens.markd.utilities.NumberPickerUtilities;
import com.schmidthappens.markd.utilities.StringUtilities;

import java.util.Calendar;

/**
 * Created by Josh on 6/6/2017.
 */

public class ApplianceEditActivity extends AppCompatActivity {
    //XML Objects
    EditText editManufacturer;
    EditText editModel;
    TextView editInstallDate;
    Button setInstallDateButton;
    NumberPicker lifeSpanIntegerPicker;
    NumberPicker lifeSpanUnits;
    Button saveButton;

    private static final String TAG = "ApplianceEditActivity";
    private static final String[] unitsArray = {
            "days",
            "months",
            "years"
    };

    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;
    private boolean isContractorEditingPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_view_plumbing);
        authentication = new FirebaseAuthentication(this);
        initializeXMLObjects();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!authentication.checkLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        customerData = new TempCustomerData(authentication, null);
        processIntent(getIntent());
    }

    @Override
    public void onStop() {
        super.onStop();
        authentication.detachListener();
        if(customerData != null) {
            customerData.removeListeners();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    //Mark:- Click Listener
    private View.OnClickListener saveButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard();
            savePlumbingChanges();
        }
    };

    private View.OnClickListener setInstallDateButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard();
            showDatePickerDialog(v);
        }
    };

    //Mark:- Helper Functions
    private void initializeXMLObjects() {
        editManufacturer = (EditText)findViewById(R.id.plumbing_edit_manufacturer);
        setEnterButtonToKeyboardDismissal(editManufacturer);
        editModel = (EditText)findViewById(R.id.plumbing_edit_model);
        setEnterButtonToKeyboardDismissal(editModel);
        editInstallDate = (TextView)findViewById(R.id.plumbing_edit_install_date);
        setInstallDateButton = (Button)findViewById(R.id.plumbing_set_install_date);
        setInstallDateButton.setOnClickListener(setInstallDateButtonClickListener);

        lifeSpanIntegerPicker = (NumberPicker)findViewById(R.id.plumbing_edit_lifespan_number_picker);
        lifeSpanIntegerPicker.setMinValue(0);
        lifeSpanIntegerPicker.setMaxValue(365);
        lifeSpanIntegerPicker.setWrapSelectorWheel(false);
        setKeyboardDismissal(lifeSpanIntegerPicker);

        lifeSpanUnits = (NumberPicker)findViewById(R.id.plumbing_edit_life_span_units);
        lifeSpanUnits.setMinValue(0);
        lifeSpanUnits.setMaxValue(unitsArray.length-1);
        setKeyboardDismissal(lifeSpanUnits);
        lifeSpanUnits.setDisplayedValues(unitsArray);

        saveButton = (Button)findViewById(R.id.plumbing_edit_save_button);
        saveButton.setOnClickListener(saveButtonClickListener);
    }
    private void processIntent(Intent intent) {
        if(intent != null) {
            if(intent.hasExtra("title")) {
                setTitle(intent.getStringExtra("title"));
            }
            if(intent.hasExtra("manufacturer")) {
                editManufacturer.setText(intent.getStringExtra("manufacturer"));
            }
            if(intent.hasExtra("model")) {
                editModel.setText(intent.getStringExtra("model"));
            }
            if(StringUtilities.isNullOrEmpty(intent.getStringExtra("installDate"))) {
                editInstallDate.setText(StringUtilities.getCurrentDateString());
            } else {
                editInstallDate.setText(intent.getStringExtra("installDate"));
            }
            lifeSpanIntegerPicker.setValue(intent.getIntExtra("lifespanInteger", 0));
            if(intent.hasExtra("units")) {
                NumberPickerUtilities.setPicker(lifeSpanUnits, intent.getStringExtra("units"), unitsArray);
            }
            isContractorEditingPage = intent.getBooleanExtra("isContractor", false);
            if(isContractorEditingPage && intent.hasExtra("customerId")) {
                customerData = new TempCustomerData(intent.getStringExtra("customerId"), null);
            }
        }
    }
    private void changeInstallDate(String newDate) {
        editInstallDate.setText(newDate);
    }

    private void savePlumbingChanges() {
        String manufacturer = editManufacturer.getText().toString();
        String model        = editModel.getText().toString();
        String installDate  = editInstallDate.getText().toString();
        Integer lifeSpanInteger = lifeSpanIntegerPicker.getValue();
        String units = unitsArray[lifeSpanUnits.getValue()];

        Class activityToGoTo = null;
        if(getTitle().equals("Domestic Hot Water")) {
            Log.i(TAG, "Save Hot Water Changes");
            HotWater hotWater = new HotWater(manufacturer, model, installDate, lifeSpanInteger, units);
            customerData.updateHotWater(hotWater);
            activityToGoTo = PlumbingActivity.class;
        } else if(getTitle().equals("Boiler")) {
            Log.i(TAG, "Save Boiler Changes");
            Boiler boiler = new Boiler(manufacturer, model, installDate, lifeSpanInteger, units);
            customerData.updateBoiler(boiler);
            activityToGoTo = PlumbingActivity.class;
        } else if(getTitle().equals("Compressor")) {
            Log.i(TAG, "Save Compressor Changes");
            Compressor compressor = new Compressor(manufacturer, model, installDate, lifeSpanInteger, units);
            customerData.updateCompressor(compressor);
            activityToGoTo = HvacActivity.class;
        } else if(getTitle().equals("Air Handler")) {
            Log.i(TAG, "Save Air Handler Changes");
            AirHandler airHandler = new AirHandler(manufacturer, model, installDate, lifeSpanInteger, units);
            customerData.updateAirHandler(airHandler);
            activityToGoTo = HvacActivity.class;
        }
        goBackToActivity(activityToGoTo);
    }

    private void goBackToActivity(Class destinationClass){
        Intent activityIntent = new Intent(this, destinationClass);
        if(isContractorEditingPage) {
            activityIntent.putExtra("isContractor", true);
            activityIntent.putExtra("customerId", customerData.getUid());
        }
        startActivity(activityIntent);
        finish();
    }

    private void setKeyboardDismissal(NumberPicker picker) {
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                hideKeyboard();
            }
        });
        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
    }

    //Mark:- DatePickerFragment
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
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

            String newDate = StringUtilities.getDateString(month+1, dayOfMonth, year);
            ((ApplianceEditActivity)getActivity()).changeInstallDate(newDate);
        }
    }

    //Makes the enter button dismiss soft keyboard
    private void setEnterButtonToKeyboardDismissal(final EditText view) {
        view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });
    }

    //Hides Keyboard
    private void hideKeyboard() {
        View v = ApplianceEditActivity.this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
