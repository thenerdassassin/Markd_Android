package com.schmidthappens.markd.hvac_subactivities;

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
import com.schmidthappens.markd.data_objects.TempHvacData;
import com.schmidthappens.markd.menu_option_activities.HvacActivity;

import java.util.Calendar;

/**
 * Created by Josh on 6/6/2017.
 */

public class HvacEditActivity extends AppCompatActivity {
    //XML Objects
    EditText editManufacturer;
    EditText editModel;
    TextView editInstallDate;
    Button setInstallDateButton;
    EditText editLifeSpan;
    Button saveButton;

    private static final String TAG = "HvacEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hvac_edit_view);
        Intent intent = getIntent();

        if(intent != null && intent.hasExtra("title"))
            setTitle(intent.getStringExtra("title"));

        //Initialize XML Objects
        editManufacturer = (EditText)findViewById(R.id.hvac_edit_manufacturer);
        if(intent != null && intent.hasExtra("manufacturer"))
            editManufacturer.setText(intent.getStringExtra("manufacturer"));
        setEnterButtonToKeyboardDismissal(editManufacturer);

        editModel = (EditText)findViewById(R.id.hvac_edit_model);
        if(intent != null && intent.hasExtra("model"))
            editModel.setText(intent.getStringExtra("model"));
        setEnterButtonToKeyboardDismissal(editModel);

        editInstallDate = (TextView)findViewById(R.id.hvac_edit_install_date);
        if(intent != null && intent.hasExtra("installDate"))
            editInstallDate.setText(intent.getStringExtra("installDate"));

        setInstallDateButton = (Button)findViewById(R.id.hvac_set_install_date);
        setInstallDateButton.setOnClickListener(setInstallDateButtonClickListener);

        editLifeSpan = (EditText)findViewById(R.id.hvac_edit_life_span);
        if(intent != null && intent.hasExtra("lifespan"))
            editLifeSpan.setText(intent.getStringExtra("lifespan"));
        setEnterButtonToKeyboardDismissal(editLifeSpan);

        saveButton = (Button)findViewById(R.id.hvac_edit_save_button);
        saveButton.setOnClickListener(saveButtonClickListener);
    }

    //Mark:- Click Listener
    private View.OnClickListener saveButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard(HvacEditActivity.this.getCurrentFocus());
            saveHvacChanges();
            goBackToHvacActivity();
        }
    };

    private View.OnClickListener setInstallDateButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard(HvacEditActivity.this.getCurrentFocus());
            showDatePickerDialog(v);
        }
    };

    //Mark:- Helper Functions
    private void changeInstallDate(String newDate) {
        editInstallDate.setText(newDate);
    }

    private void saveHvacChanges(){
        //TODO change to http calls to save hvac changes
        TempHvacData hvacData = TempHvacData.getInstance();
        if(getTitle().equals("Air Handler")) {
            Log.i(TAG, "Save Air Handler Changes");
            hvacData.setAirHandlerManufacturer(editManufacturer.getText().toString());
            hvacData.setAirHandlerModel(editModel.getText().toString());
            hvacData.setAirHandlerInstallDate(editInstallDate.getText().toString());
            hvacData.setAirHandlerLifeSpan(editLifeSpan.getText().toString());
        } else if(getTitle().equals("Compressor")) {
            Log.i(TAG, "Save Compressor Changes");
            hvacData.setCompressorManufacturer(editManufacturer.getText().toString());
            hvacData.setCompressorModel(editModel.getText().toString());
            hvacData.setCompressorInstallDate(editInstallDate.getText().toString());
            hvacData.setCompressorLifeSpan(editLifeSpan.getText().toString());
        }
    }

    private void goBackToHvacActivity(){
        Intent hvacActivityIntent = new Intent(getApplicationContext(), HvacActivity.class);
        startActivity(hvacActivityIntent);
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
            return new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String newDate = Integer.toString(month+1) + "/" + dayOfMonth + "/" + year;
            ((HvacEditActivity)getActivity()).changeInstallDate(newDate);
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
