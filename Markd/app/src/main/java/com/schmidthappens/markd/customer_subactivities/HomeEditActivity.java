package com.schmidthappens.markd.customer_subactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.customer_menu_activities.MainActivity;
import com.schmidthappens.markd.data_objects.TempCustomerData;

/**
 * Created by joshua.schmidtibm.com on 10/28/17.
 */

public class HomeEditActivity extends AppCompatActivity {
    private static final String TAG = "HomeEditActivity";
    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;

    private Boolean isNewAccount;
    EditText editStreet;
    EditText editCity;
    NumberPicker statePicker;
    EditText editZipCode;
    SeekBar bedroomSeekbar;
    EditText numberOfBedrooms;
    SeekBar bathroomSeekbar;
    EditText numberOfBathrooms;
    SeekBar squareFootageSeekbar;
    EditText squareFootage;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_view_home);
        authentication = new FirebaseAuthentication(this);
    }
    @Override
    public void onStart() {
        super.onStart();
        isNewAccount = !authentication.checkLogin();
        if(!isNewAccount) {
            customerData = new TempCustomerData(authentication, null);
            setTitle("Edit Home");
        } else {
            setUpActionBar();
        }

        initializeXMLObjects();
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

    //Mark:- Set up functions
    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.view_action_bar);
        //Set up actionBarButtons
        ImageView menuButton = (ImageView)findViewById(R.id.burger_menu);
        menuButton.setClickable(false);
        menuButton.setVisibility(View.GONE);
    }
    private void initializeXMLObjects() {
        editStreet = (EditText)findViewById(R.id.home_edit_address_street);
        editCity = (EditText)findViewById(R.id.home_edit_address_city);
        statePicker = (NumberPicker)findViewById(R.id.home_edit_state);
        statePicker.setMinValue(0);
        statePicker.setMaxValue(stateArray.length-1);
        statePicker.setDisplayedValues(stateArray);
        editZipCode = (EditText) findViewById(R.id.home_edit_address_zip_code);

        bedroomSeekbar = (SeekBar)findViewById(R.id.home_edit_bedroom_seekbar);
        bedroomSeekbar.setOnSeekBarChangeListener(bedroomSeekBarListener);
        numberOfBedrooms = (EditText)findViewById(R.id.home_edit_bedroom_number);
        numberOfBedrooms.setOnFocusChangeListener(bedroomFocusChange);

        bathroomSeekbar = (SeekBar)findViewById(R.id.home_edit_bathroom_seekbar);
        bathroomSeekbar.setOnSeekBarChangeListener(bathroomSeekBarListener);
        numberOfBathrooms = (EditText)findViewById(R.id.home_edit_bathroom_number);
        numberOfBathrooms.setOnFocusChangeListener(numberOfBathroomsFocusChange);

        squareFootageSeekbar = (SeekBar)findViewById(R.id.home_edit_square_footage_seekbar);
        squareFootageSeekbar.setOnSeekBarChangeListener(squareFootageSeekBarListener);
        squareFootage = (EditText)findViewById(R.id.home_edit_square_footage_number);
        squareFootage.setOnFocusChangeListener(squareFootageFocusChange);
        saveButton = (Button)findViewById(R.id.home_edit_save_button);
        saveButton.setOnClickListener(saveButtonClickListener);
    }
    private void processIntent(Intent intentToProcess) {
        if(intentToProcess != null && !isNewAccount) {
            if(intentToProcess.hasExtra("street")) {
                editStreet.setText(intentToProcess.getStringExtra("street"));
            }
            if(intentToProcess.hasExtra("city")) {
                editCity.setText(intentToProcess.getStringExtra("city"));
            }
            if(intentToProcess.hasExtra("state")) {
                setPicker(statePicker, intentToProcess.getStringExtra("state"), stateArray);
            }
            if(intentToProcess.hasExtra("zipcode")) {
                editZipCode.setText(intentToProcess.getStringExtra("zipcode"));
            }
            if(intentToProcess.hasExtra("bedrooms")) {
                String bedroomString = intentToProcess.getStringExtra("bedrooms");
                Double bedrooms = Double.valueOf(bedroomString)*2;
                bedroomSeekbar.setProgress(bedrooms.intValue());
                numberOfBedrooms.setText(bedroomString);
            } else {
                numberOfBedrooms.setText("0.0");
            }
            if(intentToProcess.hasExtra("bathrooms")) {
                String bathroomString = intentToProcess.getStringExtra("bathrooms");
                Double bathrooms = Double.valueOf(bathroomString)*2;
                bathroomSeekbar.setProgress(bathrooms.intValue());
                numberOfBathrooms.setText(bathroomString);
            } else {
                numberOfBathrooms.setText("0.0");
            }
            if(intentToProcess.hasExtra("squareFootage")) {
                String squareFootageString = intentToProcess.getStringExtra("squareFootage");
                squareFootageSeekbar.setProgress(Integer.valueOf(squareFootageString)/50);
                squareFootage.setText(squareFootageString);
            } else {
                squareFootage.setText("0");
            }
        }
    }

    //Mark:- Listeners
    View.OnClickListener saveButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            saveHome();
            closeActivity();
        }
    };
    SeekBar.OnSeekBarChangeListener bedroomSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            Double bedrooms = progress * 0.5;
            String value = String.valueOf(bedrooms);
            numberOfBedrooms.setText(value);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    View.OnFocusChangeListener bedroomFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if(!hasFocus) {
                if(!numberOfBedrooms.getText().toString().isEmpty()) {
                    Double value = Double.valueOf(numberOfBedrooms.getText().toString())*2;
                    bedroomSeekbar.setProgress(value.intValue());
                } else {
                    bedroomSeekbar.setProgress(0);
                }
            }
        }
    };
    SeekBar.OnSeekBarChangeListener bathroomSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            Double bathrooms = progress * 0.5;
            String value = String.valueOf(bathrooms);
            numberOfBathrooms.setText(value);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    View.OnFocusChangeListener numberOfBathroomsFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if(!hasFocus) {
                if(!numberOfBathrooms.getText().toString().isEmpty()) {
                    Double value = Double.valueOf(numberOfBathrooms.getText().toString())*2;
                    bathroomSeekbar.setProgress(value.intValue());
                } else {
                    bathroomSeekbar.setProgress(0);
                }
            }
        }
    };
    SeekBar.OnSeekBarChangeListener squareFootageSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            Integer squareFootageInteger = progress * 50;
            String value = String.valueOf(squareFootageInteger);
            squareFootage.setText(value);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    View.OnFocusChangeListener squareFootageFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if(!hasFocus) {
                if(!squareFootage.getText().toString().isEmpty()) {
                    Double value = Double.valueOf(squareFootage.getText().toString())/50;
                    squareFootageSeekbar.setProgress(value.intValue());
                } else {
                    squareFootageSeekbar.setProgress(0);
                }
            }
        }
    };

    //Mark:- Helper functions
    private void setPicker(NumberPicker picker, String value, String[] array) {
        for(int i = 0; i < array.length; i++) {
            if(array[i].equals(value)) {
                picker.setValue(i);
                break;
            }
        }
    }
    private void saveHome() {
        customerData.updateHome(
                editStreet.getText().toString(),
                editCity.getText().toString(),
                stateArray[statePicker.getValue()],
                editZipCode.getText().toString(),
                Double.valueOf(numberOfBedrooms.getText().toString()),
                Double.valueOf(numberOfBathrooms.getText().toString()),
                Integer.valueOf(squareFootage.getText().toString())
        );
    }
    private void closeActivity() {
        Intent goToMainActivity = new Intent(HomeEditActivity.this, MainActivity.class);
        startActivity(goToMainActivity);
        finish();
    }

    //Mark:- NumberPicker Arrays
    private static final String[] stateArray = {
            "AK", "AL", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
            "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
            "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
            "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
            "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
    };
}
