package com.schmidthappens.markd.plumbing_subactivities;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.TempPlumbingData;
import com.schmidthappens.markd.menu_option_activities.PlumbingActivity;
import com.schmidthappens.markd.utilities.StringUtilities;

import java.util.Calendar;

/**
 * Created by Josh on 6/6/2017.
 */

public class PlumbingEditActivity extends AppCompatActivity {
    //XML Objects
    EditText editManufacturer;
    EditText editModel;
    TextView editInstallDate;
    Button setInstallDateButton;
    EditText editLifeSpan;
    Button saveButton;

    private static final String TAG = "PlumbingEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_view_plumbing);

        SessionManager sessionManager = new SessionManager(PlumbingEditActivity.this);
        sessionManager.checkLogin();

        Intent intent = getIntent();

        if(intent != null && intent.hasExtra("title"))
            setTitle(intent.getStringExtra("title"));

        //Initialize XML Objects
        editManufacturer = (EditText)findViewById(R.id.plumbing_edit_manufacturer);
        if(intent != null && intent.hasExtra("manufacturer"))
            editManufacturer.setText(intent.getStringExtra("manufacturer"));
        setEnterButtonToKeyboardDismissal(editManufacturer);

        editModel = (EditText)findViewById(R.id.plumbing_edit_model);
        if(intent != null && intent.hasExtra("model"))
            editModel.setText(intent.getStringExtra("model"));
        setEnterButtonToKeyboardDismissal(editModel);

        editInstallDate = (TextView)findViewById(R.id.plumbing_edit_install_date);
        if(intent != null && intent.hasExtra("installDate"))
            editInstallDate.setText(intent.getStringExtra("installDate"));

        setInstallDateButton = (Button)findViewById(R.id.plumbing_set_install_date);
        setInstallDateButton.setOnClickListener(setInstallDateButtonClickListener);

        editLifeSpan = (EditText)findViewById(R.id.plumbing_edit_life_span);
        if(intent != null && intent.hasExtra("lifespan"))
            editLifeSpan.setText(intent.getStringExtra("lifespan"));
        setEnterButtonToKeyboardDismissal(editLifeSpan);

        saveButton = (Button)findViewById(R.id.plumbing_edit_save_button);
        saveButton.setOnClickListener(saveButtonClickListener);
    }

    //Mark:- Click Listener
    private View.OnClickListener saveButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard(PlumbingEditActivity.this.getCurrentFocus());
            savePlumbingChanges();
            goBackToPlumbingActivity();
        }
    };

    private View.OnClickListener setInstallDateButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard(PlumbingEditActivity.this.getCurrentFocus());
            showDatePickerDialog(v);
        }
    };

    //Mark:- Helper Functions
    private void changeInstallDate(String newDate) {
        editInstallDate.setText(newDate);
    }

    private void savePlumbingChanges(){
        //TODO change to http calls to save plumbing changes
        TempPlumbingData plumbingData = TempPlumbingData.getInstance();
        if(getTitle().equals("Domestic Hot Water")) {
            Log.i(TAG, "Save Hot Water Changes");
            plumbingData.setHotWaterManufacturer(editManufacturer.getText().toString());
            plumbingData.setHotWaterModel(editModel.getText().toString());
            plumbingData.setHotWaterInstallDate(editInstallDate.getText().toString());
            plumbingData.setHotWaterLifeSpan(editLifeSpan.getText().toString());
        } else if(getTitle().equals("Boiler")) {
            Log.i(TAG, "Save Boiler Changes");
            plumbingData.setBoilerManufacturer(editManufacturer.getText().toString());
            plumbingData.setBoilerModel(editModel.getText().toString());
            plumbingData.setBoilerInstallDate(editInstallDate.getText().toString());
            plumbingData.setBoilerLifeSpan(editLifeSpan.getText().toString());
        }
    }

    private void goBackToPlumbingActivity(){
        Intent plumbingActivityIntent = new Intent(getApplicationContext(), PlumbingActivity.class);
        startActivity(plumbingActivityIntent);
        finish();
    }

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
            ((PlumbingEditActivity)getActivity()).changeInstallDate(newDate);
        }
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
}
