package com.schmidthappens.markd.customer_subactivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.schmidthappens.markd.account_authentication.FirebaseAuthentication;
import com.schmidthappens.markd.account_authentication.LoginActivity;
import com.schmidthappens.markd.account_authentication.SessionManager;
import com.schmidthappens.markd.data_objects.Breaker;
import com.schmidthappens.markd.data_objects.Panel;
import com.schmidthappens.markd.data_objects.TempCustomerData;
import com.schmidthappens.markd.data_objects.TempPanelData;

/**
 * Created by Josh on 3/8/2017.
 */

public class BreakerDetailActivity extends AppCompatActivity {
    private static final String TAG = "BreakerDetailActivity";
    private FirebaseAuthentication authentication;
    private TempCustomerData customerData;
    private boolean isContractorEditingPage;

    //XML objects
    private EditText breakerDetailEdit;
    private Spinner amperageSpinner;
    private Spinner breakerTypeSpinner;
    private Button deleteBreakerButton;
    private Button saveBreakerButton;

    private int panelId;
    private String breakerNumberString;
    private String breakerDescription;
    private String breakerType;
    private boolean isDoublePoleBottom;
    private String breakerAmperage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_view_breaker);
        authentication = new FirebaseAuthentication(this);
        initializeXMLObjects();
    }
    @Override
    protected void onStart() {
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
    protected void onStop() {
        super.onStop();
        authentication.detachListener();
        if(customerData != null) {
            customerData.removeListeners();
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        Log.i(TAG, "Navigate Up");
        goBackToViewPanel();
        return true;
    }
    @Override
    public void onBackPressed()
    {
        Log.i(TAG, "Back Pressed");
        goBackToViewPanel();
    }
    private void initializeXMLObjects() {
        breakerDetailEdit = (EditText)findViewById(R.id.electrical_breaker_description);
        amperageSpinner = (Spinner) findViewById(R.id.electrical_amperage_spinner);
        deleteBreakerButton = (Button)findViewById(R.id.electrical_delete_breaker_button);
        saveBreakerButton = (Button)findViewById(R.id.electrical_save_breaker_button);
        breakerTypeSpinner = (Spinner)findViewById(R.id.electrical_breaker_type_spinner);
        //Used to dismiss keyboard on enter pressed
        breakerDetailEdit.setOnEditorActionListener(editOnAction);
        setDeleteBreakerListener(deleteBreakerButton);
        setSaveBreakerListener(saveBreakerButton);
        setUpSpinners();
    }
    //TODO: change spinner to number picker
    private void setUpSpinners() {
        String[] amperages = Breaker.getAmperageValues();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, amperages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        amperageSpinner.setClickable(false);
        amperageSpinner.setAdapter(adapter);
        //Used to dismiss keyboard on touch
        amperageSpinner.setOnTouchListener(spinnerOnTouch);

        String[] breakerTypes = Breaker.getBreakerTypeValues();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, breakerTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        breakerTypeSpinner.setClickable(false);
        breakerTypeSpinner.setAdapter(arrayAdapter);
        //Used to dismiss keyboard on touch
        breakerTypeSpinner.setOnTouchListener(spinnerOnTouch);
    }
    private void processIntent(Intent intentThatStartedThisActivity) {
        if(intentThatStartedThisActivity != null) {
            isContractorEditingPage = intentThatStartedThisActivity.getBooleanExtra("isContractor", false);
            if(intentThatStartedThisActivity.hasExtra("customerId")) {
                customerData = new TempCustomerData(intentThatStartedThisActivity.getStringExtra("customerId"), null);
            } else {
                Log.e(TAG, "Empty customer id");
            }
            if(intentThatStartedThisActivity.hasExtra("panelId")) {
                panelId = intentThatStartedThisActivity.getIntExtra("panelId", -1);
            }
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
                if (breakerType.equals(Breaker.DoublePoleBottom)) {
                    breakerType = Breaker.DoublePole;
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
                hideKeyboard();
                Panel panel = customerData.getPanels().get(panelId);
                panel = panel.editBreaker(Integer.parseInt(breakerNumberString), makeBreaker());
                Log.d(TAG, "Made panel:" + panel.toString() + "with breakers:" + panel.getNumberOfBreakers());
                customerData.updatePanel(panelId, panel);
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
        if(isDoublePoleBottom && breakerTypeString.equals(Breaker.DoublePole)) {
            breakerTypeString = Breaker.DoublePoleBottom;
            breakerType = Breaker.DoublePole;
        } else {
            isDoublePoleBottom = false;
            breakerType = breakerTypeString;
        }
        //Get Breaker Amperage Value
        breakerAmperage = (String)amperageSpinner.getItemAtPosition(amperageSpinner.getSelectedItemPosition());

        //Get Breaker Description Value
        breakerDescription = breakerDetailEdit.getText().toString();
        Breaker newBreaker = new Breaker(Integer.parseInt(breakerNumberString), breakerDescription, breakerAmperage, breakerTypeString);

        Log.d(TAG, "Breaker Made:" + newBreaker.toString());
        return newBreaker;
    }
    private void deleteBreaker() {
        hideKeyboard();
        Panel panel = customerData.getPanels().get(panelId);
        panel.deleteBreaker(Integer.parseInt(breakerNumberString));
        customerData.updatePanel(panelId, panel);
        goBackToViewPanel();
    }
    private void updateView() {
        setSpinner(amperageSpinner, Breaker.getAmperageValues(), breakerAmperage);
        setSpinner(breakerTypeSpinner, Breaker.getBreakerTypeValues(), breakerType);
        breakerDetailEdit.setText(breakerDescription);
        breakerDetailEdit.setSelection(breakerDescription.length()); //Sets cursor to end of EditText
    }
    private void setSpinner(Spinner spinner, String[] values, String selectedValue) {
        for(int i = 0; i<values.length; i++) {
            String value = values[i];
            if(value.equals(selectedValue)) {
                spinner.setSelection(i);
            }
        }
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
        intentToStartMainActivity.putExtra("isContractor", isContractorEditingPage);
        intentToStartMainActivity.putExtra("panelId", panelId);
        intentToStartMainActivity.putExtra("customerId", customerData.getUid());
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
