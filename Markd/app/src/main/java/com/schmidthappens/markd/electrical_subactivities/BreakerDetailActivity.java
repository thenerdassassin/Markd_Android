package com.schmidthappens.markd.electrical_subactivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.Breaker;
import com.schmidthappens.markd.data_objects.BreakerAmperage;
import com.schmidthappens.markd.data_objects.BreakerType;
import com.schmidthappens.markd.data_objects.TempPanelData;

/**
 * Created by Josh on 3/8/2017.
 */

public class BreakerDetailActivity extends AppCompatActivity {
    //XML objects
    private EditText breakerDetailEdit;
    private Spinner amperageSpinner;
    private Spinner breakerTypeSpinner;
    private Button deleteBreakerButton;
    private Button saveBreakerButton;


    private String breakerNumberString;
    private String breakerDescription;
    private String breakerType;
    private boolean isDoublePoleBottom;
    private String breakerAmperage;

    private final static String TAG = "BreakerDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_view_breaker);

        SessionManager sessionManager = new SessionManager(BreakerDetailActivity.this);
        sessionManager.checkLogin();

        //Set XML Objects
        breakerDetailEdit = (EditText)findViewById(R.id.electrical_breaker_description);
        amperageSpinner = (Spinner) findViewById(R.id.electrical_amperage_spinner);
        deleteBreakerButton = (Button)findViewById(R.id.electrical_delete_breaker_button);
        saveBreakerButton = (Button)findViewById(R.id.electrical_save_breaker_button);
        breakerTypeSpinner = (Spinner)findViewById(R.id.electrical_breaker_type_spinner);
        //Used to dismiss keyboard on enter pressed
        breakerDetailEdit.setOnEditorActionListener(editOnAction);

        //Initialze Spinner Values
        String[] amperages = BreakerAmperage.getValues();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, amperages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        amperageSpinner.setClickable(false);
        amperageSpinner.setAdapter(adapter);
        //Used to dismiss keyboard on touch
        amperageSpinner.setOnTouchListener(spinnerOnTouch);

        String[] breakerTypes = BreakerType.getValues();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, breakerTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        breakerTypeSpinner.setClickable(false);
        breakerTypeSpinner.setAdapter(arrayAdapter);
        //Used to dismiss keyboard on touch
        breakerTypeSpinner.setOnTouchListener(spinnerOnTouch);

        //Set Button Listeners
        this.setDeleteBreakerListener(deleteBreakerButton);
        this.setSaveBreakerListener(saveBreakerButton);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("breakerNumber")) {
                breakerNumberString = String.valueOf(intentThatStartedThisActivity.getIntExtra("breakerNumber", -1));
                setTitle("Breaker " + breakerNumberString);
            }

            if (intentThatStartedThisActivity.hasExtra("breakerDescription")) {
                breakerDescription = intentThatStartedThisActivity.getStringExtra("breakerDescription");
            }

            if (intentThatStartedThisActivity.hasExtra("breakerAmperage")) {
                breakerAmperage = intentThatStartedThisActivity.getStringExtra("breakerAmperage");
            }

            if (intentThatStartedThisActivity.hasExtra("breakerType")) {
                breakerType = intentThatStartedThisActivity.getStringExtra("breakerType");
                //Breaker is Bottom of Double Pole
                if (breakerType.equals(BreakerType.DoublePoleBottom.toString())) {
                    breakerType = BreakerType.DoublePole.toString();
                    isDoublePoleBottom = true;
                }
            }

            updateView();
        }
    }

    // Mark:- Button Listeners
    private void setDeleteBreakerListener(final Button deleteButton) {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteBreaker();
            }
        });
    }

    private void setSaveBreakerListener(final Button editButton) {
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: Change to http call to Server in Future
                TempPanelData panel = TempPanelData.getInstance();

                hideKeyboard();

                //TODO: Change to http call to Server in Future
                panel.updateBreaker(Integer.parseInt(breakerNumberString), makeBreaker());

                goBackToViewPanel();
            }
        });
    }

    //Mark: :- Helper functions
    //Used to Collect Updated Info
    private Breaker makeBreaker() {

        //Get BreakerType Value
        String breakerTypeString = (String)breakerTypeSpinner.getItemAtPosition(breakerTypeSpinner.getSelectedItemPosition());
        // Breaker was DoublePole Bottom and set to stay as double-pole
        if(isDoublePoleBottom && breakerTypeString.equals(BreakerType.DoublePole.toString())) {
            breakerTypeString = BreakerType.DoublePoleBottom.toString();
            breakerType = BreakerType.DoublePole.toString();
        } else {
            isDoublePoleBottom = false;
            breakerType = breakerTypeString;
        }
        //Get Breaker Amperage Value
        breakerAmperage = (String)amperageSpinner.getItemAtPosition(amperageSpinner.getSelectedItemPosition());

        //Get Breaker Description Value
        breakerDescription = breakerDetailEdit.getText().toString();

        return new Breaker(Integer.parseInt(breakerNumberString), breakerDescription, BreakerAmperage.fromString(breakerAmperage), BreakerType.fromString(breakerTypeString));
    }

    private void deleteBreaker() {
        Context context = BreakerDetailActivity.this;
        Class destinationClass = ViewPanelActivity.class;
        Intent intentToStartViewPanelActivity = new Intent(context, destinationClass);
        intentToStartViewPanelActivity.putExtra("actionType", "Delete Breaker");
        intentToStartViewPanelActivity.putExtra("breakerNumber", breakerNumberString);
        startActivity(intentToStartViewPanelActivity);
        finish();
    }

    private void updateView() {
        amperageSpinner.setSelection(BreakerAmperage.fromString(breakerAmperage).ordinal());
        breakerTypeSpinner.setSelection(BreakerType.fromString(breakerType).ordinal());
        breakerDetailEdit.setText(breakerDescription);
        breakerDetailEdit.setSelection(breakerDescription.length()); //Sets cursor to end of EditText
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void goBackToViewPanel() {
        Context context = BreakerDetailActivity.this;
        Class destinationClass = ViewPanelActivity.class;
        Intent intentToStartMainActivity = new Intent(context, destinationClass);
        startActivity(intentToStartMainActivity);
        finish();
    }

    private View.OnTouchListener spinnerOnTouch = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                hideKeyboard();
            }
            return false;
        }
    };

    private TextView.OnEditorActionListener editOnAction = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(breakerDetailEdit.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }
            return false;
        }
    };
}
