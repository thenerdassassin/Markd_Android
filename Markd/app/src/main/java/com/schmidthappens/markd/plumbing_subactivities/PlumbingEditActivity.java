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

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.data_objects.TempPlumbingData;
import com.schmidthappens.markd.menu_option_activities.PlumbingActivity;

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
        setContentView(R.layout.plumbing_edit_view);
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
            Log.i(TAG, "Save Hot Water Changes");
            hideKeyboard(PlumbingEditActivity.this.getCurrentFocus());
            savePlumbingChanges();
            goBackToPlumbingActivity();
        }
    };

    private View.OnClickListener setInstallDateButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Set Install Date");
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
            plumbingData.setHotWaterManufacturer(editManufacturer.getText().toString());
            plumbingData.setHotWaterModel(editModel.getText().toString());
            plumbingData.setHotWaterInstallDate(editInstallDate.getText().toString());
            plumbingData.setHotWaterLifeSpan(editLifeSpan.getText().toString());
        } else if(getTitle().equals("Boiler")) {
            plumbingData.setBoilerManufacturer(editManufacturer.getText().toString());
            plumbingData.setBoilerModel(editModel.getText().toString());
            plumbingData.setBoilerInstallDate(editInstallDate.getText().toString());
            plumbingData.setBoilerLifeSpan(editLifeSpan.getText().toString());
        }
    }

    private void goBackToPlumbingActivity(){
        Intent plumbingActivityIntent = new Intent(getApplicationContext(), PlumbingActivity.class);
        startActivity(plumbingActivityIntent);
    }

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
            String newDate = Integer.toString(month+1) + "/" + dayOfMonth + "/" + year;
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
